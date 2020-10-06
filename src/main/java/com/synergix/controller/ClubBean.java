package com.synergix.controller;

import com.synergix.model.Club;
import com.synergix.model.Member;
import com.synergix.model.MemberClub;
import com.synergix.repositoty.ClubRepo;
import com.synergix.repositoty.MemberRepo;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named(value = "clubBean")
@ConversationScoped
@Getter
@Setter
public class ClubBean implements Serializable {

    @Inject
    private ClubRepo clubRepo;

    @Inject
    private MemberRepo memberRepo;

    private static final String MANAGER_PAGE = "showManagerPage";
    private static final String DETAIL_PAGE = "showDetailPage";
    private static final int MINIMUM_LENGTH_NAME = 2;
    private static final String NOT_FOUND_MEMBER = "Not Found Member!";

    private String navigateClubPage;
    private transient Club middleClub;
    private Map<Integer, Boolean> selectedClubMap = new HashMap<>();
    private Map<Integer, Boolean> selectedMemberClubMap = new HashMap<>();
    private List<Integer> memberIdList = new ArrayList<>();
    private transient List<MemberClub> memberClubsList = new ArrayList<>();
    private transient List<Club> clubs = new ArrayList<>();

    public String getManagerPage() {
        return MANAGER_PAGE;
    }

    public String getDetailPage() {
        return DETAIL_PAGE;
    }

    @PostConstruct
    public void initNavigator() {
        this.clearMiddleClub();
        this.unselectAll();
        this.getAll();
        this.navigateClubPage = MANAGER_PAGE;
    }

    public void getAll() {
        this.clubs = clubRepo.getAll();
    }

    public void create() {
        middleClub = new Club();
        clubs.add(middleClub);
    }

    public void cancelAdd() {
        this.clearMiddleClub();
        this.clubs.remove(clubs.size() - 1);
    }

    public void clearMiddleClub() {
        this.middleClub = null;
    }

    public void save(Club club) {
        clubRepo.save(club);
        this.clearMiddleClub();
        this.getAll();
    }

    public void selectAll() {
        for (Club club : this.getClubs()) {
            selectedClubMap.put(club.getId(), true);
        }
    }

    public void unselectAll() {
        for (Club club : this.getClubs()) {
            selectedClubMap.put(club.getId(), false);
        }
    }

    public List<Integer> getSelectedClubList() {
        return this.getSelectedClubMap().entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void deleteSelectedClub() {
        for (Integer clubId : this.getSelectedClubList()) {
            clubRepo.delete(clubId);
        }
        this.getAll();
        this.unselectAll();
    }

    public void moveToDetailPage(Club club) {
        this.middleClub = club;
        this.clearSelectedMemberMap();
        this.memberClubsList = this.middleClub.getMemberClubs();
        this.memberIdList = this.getMemberIdList();
        this.navigateClubPage = DETAIL_PAGE;
    }

    public void update(Club club) {
        clubRepo.update(club);
    }

    public void clearSelectedMemberMap() {
        this.selectedMemberClubMap.clear();
    }

    public void createMemberClub(Club club) {
        MemberClub memberClub = new MemberClub();
        memberClub.setClub(club);
        this.memberClubsList.add(memberClub);
    }

    public void cancelAddMember() {
        this.memberClubsList.remove(this.memberClubsList.size() - 1);
    }

    public void updateMentorClub(Club club) {
        clubRepo.update(club);
    }

    public void saveMemberClubIntoClub(Club club) {
        clubRepo.update(club);
        this.memberClubsList = club.getMemberClubs();
        this.memberIdList = this.getMemberIdList();
    }

    public List<Integer> getMemberIdList() {
        return this.memberClubsList
                .stream()
                .map(item -> item.getMember().getId())
                .collect(Collectors.toList());
    }

    public void selectAllMemberClub() {
        for (MemberClub memberClub : this.memberClubsList) {
            selectedMemberClubMap.put(memberClub.getId(), true);
        }
    }

    public void unselectAllMemberClub() {
        for (MemberClub memberClub : this.memberClubsList) {
            selectedMemberClubMap.put(memberClub.getId(), false);
        }
    }

    public List<Integer> getSelectedMemberClubId() {
        return this.selectedMemberClubMap.entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void deleteSelectedMember(Club club) {
        for (Integer memberClubId : this.getSelectedMemberClubId()) {
            clubRepo.deleteMemberClubInClub(club, memberClubId);
        }
        this.moveToDetailPage(club);
    }

    public Validator nameValidator() {
        return new Validator() {
            @Override
            public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
                String sName = o.toString();
                if (sName.length() < MINIMUM_LENGTH_NAME) {
                    FacesMessage facesMessage = new FacesMessage("Minimum name length is 2. Please enter again!");
                    facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
                    throw new ValidatorException(facesMessage);
                }
            }
        };
    }

    public Converter mentorConverter() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
                Integer mentorId = null;
                if (s.isEmpty() || s == null) return null;
                try {
                    mentorId = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    EditableValueHolder editableValueHolder = (EditableValueHolder) uiComponent;
                    editableValueHolder.resetValue();
                    FacesMessage notValidMemberId = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Not valid member ID!", null);
                    throw new ConverterException((notValidMemberId));
                }
                Member member = clubRepo.getMemberById(mentorId);
                if (member == null) {
                    EditableValueHolder editableValueHolder = (EditableValueHolder) uiComponent;
                    editableValueHolder.resetValue();
                    FacesMessage cannotConvertToMember = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot convert to member!", null);
                    throw new ConverterException((cannotConvertToMember));
                }
                return member;
            }

            @Override
            public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
                Member mentor = (Member) o;
                if (mentor == null) return null;
                return ((Member) o).getId().toString();
            }
        };
    }

    public Validator mentorValidator() {
        return new Validator() {
            @Override
            public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
                Member member = (Member) value;
                if (value != null && !memberIdList.contains(member.getId())) {
                    EditableValueHolder editableValueHolder = (EditableValueHolder) component;
                    editableValueHolder.resetValue();
                    FacesMessage notFoundIdMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, NOT_FOUND_MEMBER, null);
                    throw new ValidatorException(notFoundIdMsg);
                }
            }
        };
    }

    public Converter memberIntoClubConverter() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.isEmpty() || value == null) return null;
                Integer memberId = null;
                try {
                    memberId = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return null;
                }
                return clubRepo.getMemberById(memberId);
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                Member member = (Member) value;
                if (member == null) return null;
                return member.getId().toString();
            }
        };
    }

    public Validator memberIntoClubValidator() {
        return new Validator() {
            @Override
            public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
                Member member = (Member) value;
                List<Member> memberList = memberRepo.getAll();
                if (value != null && memberIdList.contains(member.getId())) {
                    cancelAddMember();
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Member existed in club", null));
                } else if (value == null || !memberList.contains(member)) {
                    cancelAddMember();
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, NOT_FOUND_MEMBER, null));
                }
            }
        };
    }

    public Converter nameConverter() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
                s = s.toLowerCase();
                StringBuilder value2 = new StringBuilder();
                Character ch = ' ';
                for (int i = 0; i < s.length(); i++) {
                    if (ch == ' ' && s.charAt(i) != ' ') {
                        value2.append(Character.toUpperCase(s.charAt(i)));
                    } else {
                        value2.append(s.charAt(i));
                    }
                    ch = s.charAt(i);
                }
                return value2.toString().trim();
            }

            @Override
            public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
                return o.toString();
            }
        };
    }

    public Converter emailConverter() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
                return s.trim().toLowerCase();
            }

            @Override
            public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
                return o.toString();
            }
        };
    }
}
