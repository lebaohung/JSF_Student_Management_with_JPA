package com.synergix.repositoty;

import com.synergix.model.Club;
import com.synergix.model.Member;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named(value = "clubRepo")
public class ClubRepo implements Serializable {

    private EntityManager em = Persistence.createEntityManagerFactory("com.synergix").createEntityManager();
    private Logger logger = Logger.getAnonymousLogger();

    public List<Club> getAll() {
        TypedQuery<Club> getAllClubs = em.createQuery("from Club ", Club.class);
        return getAllClubs.getResultList();
    }

    public Club getById(Integer clubId) {
        return em.find(Club.class, clubId);
    }

    public void save(Club club) {
        try {
            em.getTransaction().begin();
            em.persist(club);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception " + e.getMessage());
        }
    }

    public void update(Club club) {
        try {
            em.getTransaction().begin();
            em.merge(club);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception " + e.getMessage());
        }
    }

    public void delete(Integer clubId) {
        try {
            em.getTransaction().begin();
            Club club = this.getById(clubId);
            em.remove(club);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception " + e.getMessage());
        }
    }

    public void saveMemberIntoClub(Club club, Member member) {
        try {
            em.getTransaction().begin();
            List<Member> newMembers = club.getMembers();
            newMembers.set(newMembers.size() - 1, member);
            club.setMembers(newMembers);
            em.merge(club);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception " + e.getMessage());
        }
    }

    public Member getMemberById(Integer memberId) {
        return em.find(Member.class, memberId);
    }

    public void updateMentorClub(Club club) {
        em.getTransaction().begin();
        em.merge(club);
        em.getTransaction().commit();
    }

    public void deleteMemberInClub(Integer clubId, Integer memberId) {
        try {
            em.getTransaction().begin();
            Club club = this.getById(clubId);
            Member member = this.getMemberById(memberId);
            club.getMembers().remove(member);
            em.merge(club);
            em.getTransaction().commit();
        } catch  (Exception e) {
            logger.log(Level.SEVERE, "Exception " + e.getMessage());
        }
    }
}
