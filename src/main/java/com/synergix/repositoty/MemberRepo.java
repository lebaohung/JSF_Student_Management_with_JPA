package com.synergix.repositoty;

import com.synergix.model.Member;

import javax.inject.Named;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Named
public class MemberRepo implements Serializable {

    private EntityManager em = Persistence.createEntityManagerFactory("com.synergix").createEntityManager();

    public List<Member> getAll() {
        TypedQuery<Member> getAllQuery = em.createQuery("FROM Member", Member.class);
        return getAllQuery.getResultList();
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

    public boolean update(Member member) {
        if (member == null) return false;
        em.getTransaction().begin();
        em.merge(member);
        em.getTransaction().commit();
        return true;
    }

    public boolean delete(Integer memberId) {
        Member member = em.find(Member.class, memberId);
        if (member == null) return false;
        em.getTransaction().begin();
        em.remove(member);
        em.getTransaction().commit();
        return true;
    }
}
