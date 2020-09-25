package com.synergix.controller;

import com.synergix.model.Member;
import com.synergix.repositoty.MemberRepo;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ConversationScoped
public class MemberBean implements Serializable {

    private static final String MANAGER_PAGE = "showManagerPage";
    private static final String DETAIL_PAGE = "showDetailPage";

    private String navigateMemberPage;
    private List<Member> members = new ArrayList<>();

    @Inject
    private Conversation conversation;

    @Inject
    private MemberRepo memberRepo;

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

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public void initConversation() {
        if (conversation.isTransient()) conversation.begin();
    }

    public void endConversation() {
        if (!conversation.isTransient()) conversation.end();
    }

    @PostConstruct
    public void initNavigator() {
        this.getAll();
        this.navigateMemberPage = MANAGER_PAGE;
    }

    public void getAll() {
        this.endConversation();
        this.initConversation();
        this.members = memberRepo.getAll();
    }
}
