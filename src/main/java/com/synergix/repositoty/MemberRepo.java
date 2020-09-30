package com.synergix.repositoty;

import com.synergix.model.Member;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.persistence.*;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
public class MemberRepo implements Serializable {

    private static final String SELECT_ALL_MEMBERS = "SELECT m FROM Member m ORDER BY m.id ASC";
    private static final String INSERT_MEMBER = "INSERT INTO public.MEMBER(\n" +
            "\tname, email, phone, birthday)\n" +
            "\tVALUES (?, ?, ?, ?);";
    private static final String SELECT_MEMBER_BY_ID = "SELECT m FROM Member m WHERE m.id = :memberId";
    private static final String UPDATE_MEMBER = "UPDATE public.MEMBER\n" +
            "\tSET name=?, email=?, phone=?, birthday=? WHERE id = ?";
    private static final String DELETE_MEMBER_ID_IN_CLASS = "DELETE FROM MEMBER_club WHERE MEMBER_id = ?;";
    private static final String DELETE_MEMBER = "DELETE FROM public.MEMBER WHERE id=?;";
    private static final String COUNT_MEMBERS = "SELECT COUNT(id) FROM MEMBER;";
    private static final String GET_ALL_MEMBERS_ID = "SELECT id FROM Member m";

    private EntityManager em = Persistence.createEntityManagerFactory("com.synergix").createEntityManager();
    private Member member = null;

    @SuppressWarnings("unchecked")
    public List<Member> getAll() {
        TypedQuery<Member> getAllQuery = em.createQuery("FROM Member", Member.class);
        System.out.println("repo1");
        List<Member> members = getAllQuery.getResultList();
        member = em.find(Member.class, 1);
        System.out.println("repo2");
        return members;
    }

    public Member getById(Integer memberId) {
        return em.find(Member.class, memberId);
    }

    public boolean save(Member member) {
        if (member == null) return false;
        em.getTransaction().begin();
        em.persist(member);
        em.getTransaction().commit();
        return true;
    }

    public void test() {
//        em.find(Member.class, 1);
//        em.getReference(Member.class, 1);
        System.out.println(em.contains(member));
    }


}
