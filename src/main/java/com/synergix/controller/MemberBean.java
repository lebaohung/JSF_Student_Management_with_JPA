package com.synergix.controller;

import com.synergix.model.Member;
import com.synergix.repositoty.MemberRepo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ConversationScoped
public class MemberBean implements Serializable {

    @Inject
    private Conversation conversation;

    public Conversation getConversation() {
        return conversation;
    }

    @Inject
    private MemberRepo memberRepo;

    public MemberRepo getMemberRepo() {
        return memberRepo;
    }

    private static final String MANAGER_PAGE = "showManagerPage";
    private static final String DETAIL_PAGE = "showDetailPage";

    private String navigateMemberPage;
    private List<Member> members = new ArrayList<>();
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
        System.out.println("ready conversation");
        if (!conversation.isTransient()) {
            System.out.println("now is transient");
            conversation.end();
        }
        System.out.println("now is longrunning");
        conversation.begin();
    }

    @PostConstruct
    public void initNavigator() {
        System.out.println("construct");
        this.getAll();
        this.navigateMemberPage = MANAGER_PAGE;
    }

    @PreDestroy
    public void testDestroy() {
        System.out.println("destroy");
    }

    public void getAll() {
        this.members = memberRepo.getAll();
    }


}
