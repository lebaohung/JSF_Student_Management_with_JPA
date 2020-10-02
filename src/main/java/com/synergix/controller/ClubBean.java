package com.synergix.controller;

import com.synergix.model.Club;
import com.synergix.model.Member;
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
    private Member middleMember;
    private Club middleClub;
    private Map<Integer, Boolean> selectedClubMap = new HashMap<>();
    private Map<Integer, Boolean> selectedMemberMap = new HashMap<>();
    private List<Integer> membersIdListOfClub = new ArrayList<>();
    private List<Member> memberListOfClub = new ArrayList<>();
    private List<Club> clubs = new ArrayList<>();

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
        middleClub.setMembers(new ArrayList<>());
        clubs.add(middleClub);
    }

    public void cancelAdd() {
        this.clearMiddleClub();
        this.clubs.remove(clubs.size()-1);
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
        this.middleMember = null;
        this.clearSelectedMemberMap();
        this.memberListOfClub = this.middleClub.getMembers();
        this.getMembersIdListOfClub();
        this.navigateClubPage = DETAIL_PAGE;
    }

    public void update(Club club) {
        clubRepo.update(club);
    }

    public void clearMemberListOfClub() {
        this.memberListOfClub.clear();
    }

    public void clearSelectedMemberMap() {
        this.selectedMemberMap.clear();
    }

    public void createMember() {
        middleMember = new Member();
        this.memberListOfClub.add(middleMember);
    }

    public void cancelAddMember() {
        this.middleMember = null;
        this.memberListOfClub.remove(this.memberListOfClub.size() -1);
    }

    public void updateMentorClub(Club club) {
        clubRepo.updateMentorClub(club);
    }

    public void saveMemberIntoClub(Club club, Member member) {
        clubRepo.saveMemberIntoClub(club, member);
        this.middleMember = null;
        this.memberListOfClub = club.getMembers();
    }

    public List<Integer> getMembersIdListOfClub() {
        return this.memberListOfClub
                .stream()
                .map(Member::getId)
                .collect(Collectors.toList());
    }

    public void selectAllMembers() {
        for (Integer memberId : this.getMembersIdListOfClub()) {
            selectedMemberMap.put(memberId, true);
        }
    }

    public void unselectAllMembers() {
        for (Integer memberId : this.getMembersIdListOfClub()) {
            selectedMemberMap.put(memberId, false);
        }
    }

    public List<Integer> getSelectedMembersId() {
        return this.selectedMemberMap.entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void deleteSelectedMember(Club club) {
        for (Integer memberId : this.getSelectedMembersId()) {
            clubRepo.deleteMemberInClub(club.getId(), memberId);
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
                if (s.isEmpty() || s == null) return new Member();
                try {
                    mentorId = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    return null;
                }
                return clubRepo.getMemberById(mentorId);
            }

            @Override
            public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
                Member mentor = (Member) o;
                if (mentor.getId() == null) return null;
                return ((Member) o).getId().toString();
            }
        };
    }

    public Validator mentorValidator() {
        return new Validator() {
            @Override
            public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
                Member member = (Member) value;
                if (value == null || (!member.equals(new Member()) && !memberListOfClub.contains(member))) {
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
                if (member.getId() == null) return null;
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
                if (value != null && memberListOfClub.contains(member)) {
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
                for (int i = 0; i < s.length(); i ++) {
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
