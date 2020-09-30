package com.synergix.controller;

import com.synergix.model.Member;
import com.synergix.repositoty.MemberRepo;

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
public class MemberBean implements Serializable {

    @Inject
    private MemberRepo memberRepo;

    public MemberRepo getMemberRepo() {
        return memberRepo;
    }

    private static final String MANAGER_PAGE = "showManagerPage";
    private static final String DETAIL_PAGE = "showDetailPage";
    private static final int MINIMUM_LENGTH_NAME = 2;
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "^0\\d{9}$";

    private String navigateMemberPage;
    private List<Member> members = new ArrayList<>();
    private Map<Integer, Boolean> selectedMemberMap = new HashMap<>();
    private Member tempMember;

    public static String getManagerPage() {
        return MANAGER_PAGE;
    }

    public static String getDetailPage() {
        return DETAIL_PAGE;
    }

    public String getNavigateMemberPage() {
        return navigateMemberPage;
    }

    public void setNavigateMemberPage(String navigateMemberPage) {
        this.navigateMemberPage = navigateMemberPage;
    }

    public Member getTempMember() {
        return tempMember;
    }

    public void setTempMember(Member tempMember) {
        this.tempMember = tempMember;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public Map<Integer, Boolean> getSelectedMemberMap() {
        return selectedMemberMap;
    }

    public void setSelectedMemberMap(Map<Integer, Boolean> selectedMemberMap) {
        this.selectedMemberMap = selectedMemberMap;
    }

    @PostConstruct
    public void initNavigator() {
        this.getAll();
        this.navigateMemberPage = MANAGER_PAGE;
    }

    public void getAll() {
        this.members = memberRepo.getAll();
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

    public void create() {
        tempMember = new Member();
        this.getAll();
        members.add(tempMember);
    }

    public void cancelAdd() {
        this.clearTempMember();
        this.getAll();
    }

    public void clearTempMember() {
        tempMember = null;
    }

    public boolean save(Member member) {
        boolean result = memberRepo.save(member);
        this.cancelAdd();
        return result;
    }

    public List<Integer> getSelectedMemberList() {
        return this.getSelectedMemberMap().entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public boolean deleteSelectedMember() {
        boolean result = true;
        for (Integer memberId : this.getSelectedMemberList()) {
            result = memberRepo.delete(memberId);
        }
        this.getAll();
        this.selectedMemberMap.clear();
        if (result) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Check log----------------------");
        }
        return result;
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

    public void moveToDetailPage(Member member) {
        tempMember = member;
        this.navigateMemberPage = DETAIL_PAGE;
    }

    public boolean update(Member member) {
        return memberRepo.update(member);
    }
}
