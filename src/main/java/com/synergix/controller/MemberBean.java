package com.synergix.controller;

import com.synergix.model.Member;
import com.synergix.repositoty.MemberRepo;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Named
@ConversationScoped
@Getter
@Setter
public class MemberBean implements Serializable {

    @Inject
    private MemberRepo memberRepo;

    private static final String MANAGER_PAGE = "showManagerPage";
    private static final String DETAIL_PAGE = "showDetailPage";
    private static final int MINIMUM_LENGTH_NAME = 2;
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "^0\\d{9}$";

    private String navigateMemberPage;
    private transient List<Member> members = new ArrayList<>();
    private Map<Integer, Boolean> selectedMemberMap = new HashMap<>();
    private transient Member tempMember;

    public String getManagerPage() {
        return MANAGER_PAGE;
    }

    public String getDetailPage() {
        return DETAIL_PAGE;
    }

    @PostConstruct
    public void initNavigator() {
        this.tempMember = null;
        this.getAll();
        this.navigateMemberPage = MANAGER_PAGE;
    }

    public void getAll() {
        this.members = memberRepo.getAll();
    }

    public void create() {
        tempMember = new Member();
        members.add(tempMember);
    }

    public void cancelAdd() {
        this.clearTempMember();
        members.remove(members.size()-1);
    }

    public void clearTempMember() {
        tempMember = null;
    }

    public void save(Member member) {
        if (!memberRepo.save(member)) {
            Logger.getAnonymousLogger().log(Level.SEVERE, () -> "Cannot save member ID " + member.getId());
        }
        this.clearTempMember();
        this.getAll();
    }

    public void selectAll() {
        for (Member member : this.getMembers()) {
            selectedMemberMap.put(member.getId(), true);
        }
    }

    public void unselectAll() {
        for (Member member : this.getMembers()) {
            selectedMemberMap.put(member.getId(), false);
        }
    }

    public List<Integer> getSelectedMemberList() {
        return this.getSelectedMemberMap().entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void deleteSelectedMember() {
        boolean result = true;
        for (Integer memberId : this.getSelectedMemberList()) {
            result = memberRepo.delete(memberId);
        }
        this.getAll();
        this.selectedMemberMap.clear();
        if (!result) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Cannot delete all selected member!");
        }
    }

    public void moveToDetailPage(Member member) {
        tempMember = member;
        this.navigateMemberPage = DETAIL_PAGE;
    }

    public void update(Member member) {
        if (!memberRepo.update(member)) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Cannot save member!");
        };
    }

    public void validateName(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        String sName = value.toString();
        if (sName.length() < MINIMUM_LENGTH_NAME) {
            FacesMessage facesMessage = new FacesMessage("Minimum name length is 2. Please enter again!");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            tempMember = new Member();
            throw new ValidatorException(facesMessage);
        }
    }

    public void validateEmail(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String email = value.toString();
        if (!email.isEmpty() && !email.matches(EMAIL_REGEX)) {
            FacesMessage facesMessage = new FacesMessage("  Invalid email! Please enter email again!");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
    }

    public void validatePhone(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String phone = value.toString();
        if (!phone.isEmpty() && !phone.matches(PHONE_REGEX)) {
            FacesMessage facesMessage = new FacesMessage("Phone length is 10, starts with 0. Please enter again!");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
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
