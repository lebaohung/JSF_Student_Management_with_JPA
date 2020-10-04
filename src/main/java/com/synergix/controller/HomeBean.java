package com.synergix.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Locale;

@Named
@SessionScoped
public class HomeBean implements Serializable {

    @Inject
    private Conversation conversation;

    private static final String SHOW_MEMBER_MANAGEMENT = "listMember";
    private static final String SHOW_CLUB_MANAGEMENT = "listClub";
    private static final String SHOW_MAIN_MANAGER = "managerTemplate";

    private String navigateHomePage;
    private Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();

    public String getShowMemberManagement() {
        return SHOW_MEMBER_MANAGEMENT;
    }

    public String getShowClubManagement() {
        return SHOW_CLUB_MANAGEMENT;
    }

    public String getShowMainManager() {
        return SHOW_MAIN_MANAGER;
    }

    public String getNavigateHomePage() {
        return navigateHomePage;
    }

    public void setNavigateHomePage(String navigateHomePage) {
        this.navigateHomePage = navigateHomePage;
    }

    public Locale getLocale() {
        return locale;
    }

    @PostConstruct
    public void init() {
        this.navigateHomePage = SHOW_MAIN_MANAGER;
    }

    public String getLanguage() {
        return locale.getLanguage();
    }

    public void changeLanguage(String language) {
        locale = new Locale(language);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(language));
    }

    public void initConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
        conversation.begin();
    }

    public void showMemberManagement() {
        initConversation();
        this.navigateHomePage = SHOW_MEMBER_MANAGEMENT;
    }

    public void showClubManagement() {
        initConversation();
        this.navigateHomePage = SHOW_CLUB_MANAGEMENT;
    }

    public void backToHomePage() {
        if (!conversation.isTransient()) conversation.end();
        this.navigateHomePage = SHOW_MAIN_MANAGER;
    }
}