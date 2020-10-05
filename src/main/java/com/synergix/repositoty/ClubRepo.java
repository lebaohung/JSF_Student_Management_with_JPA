package com.synergix.repositoty;

import com.synergix.model.Club;
import com.synergix.model.Member;
import com.synergix.model.MemberClub;

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

    public void saveMemberClubIntoClub(Club club) {
        try {
            em.getTransaction().begin();
            em.merge(club);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception " + e.getMessage());
        }
    }

    public MemberClub getMemberClubById(Integer memberId) {
        return em.find(MemberClub.class, memberId);
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
            MemberClub memberClub = this.getMemberClubById(memberId);
            club.getMemberClubs().remove(memberClub);
            em.merge(club);
            em.getTransaction().commit();
        } catch  (Exception e) {
            logger.log(Level.SEVERE, "Exception " + e.getMessage());
        }
    }
}
