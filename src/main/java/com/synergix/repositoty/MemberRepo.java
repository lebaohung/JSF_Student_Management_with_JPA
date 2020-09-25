package com.synergix.repositoty;

import com.synergix.model.Member;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.persistence.*;
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

    private static final EntityManager em = Persistence.createEntityManagerFactory("Club-Persistence-Unit").createEntityManager();
    private static final EntityTransaction transaction = em.getTransaction();

    @SuppressWarnings("unchecked")
    public List<Member> getAll() {
        Query getAllQuery = em.createQuery(SELECT_ALL_MEMBERS);
        List<Member> members = getAllQuery.getResultList();
        if (members != null && members.size() > 0) {
            return members;
        }
        return null;
    }

    public Member getById(Integer memberId) {
        Member member = new Member();
        Query getByIdQuery = em.createQuery(SELECT_MEMBER_BY_ID);
        getByIdQuery.setParameter("memberId", memberId);
        try {
            member = em.find(com.synergix.model.Member.class, memberId);
        } catch (NoResultException e) {
            Logger.getAnonymousLogger().log(Level.INFO, e.getMessage());
        }
        System.out.println("member " + member.getId());
        return member;
    }

    public void save(Member member) {

    }


}
