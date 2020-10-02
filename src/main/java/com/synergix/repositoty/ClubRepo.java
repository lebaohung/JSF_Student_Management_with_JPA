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
    
    public List<Club> getAll() {
        TypedQuery<Club> getAllClubs = em.createQuery("select distinct c from Club c left join fetch c.mentor left join fetch c.members order by c.id ", Club.class);
        return getAllClubs.getResultList();
    }

    public Club getById(Integer clubId) {
        return em.find(Club.class, clubId);
    }

    public void refreshClub(Club club) {
        em.refresh(club);
    }

    public void save(Club club) {
        try {
            em.getTransaction().begin();
            em.persist(club);
            em.getTransaction().commit();
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Exception " + e.getMessage());
        }
    }

    public void update(Club club) {
        try {
            em.getTransaction().begin();
            em.merge(club);
            em.getTransaction().commit();
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Exception " + e.getMessage());
        }
    }

    public void delete(Integer clubId) {
        try {
            em.getTransaction().begin();
            Club club = this.getById(clubId);
            em.remove(club);
            em.getTransaction().commit();
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Exception " + e.getMessage());
        }
    }

    public List<Member> getMembersListByClubId(Integer clubId) {
        Club club = this.getById(clubId);
        return club.getMembers();
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
            Logger.getAnonymousLogger().log(Level.SEVERE, "Exception " + e.getMessage());
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

    public boolean deleteMemberInClub(Integer clubId, Integer memberId) {
        Club club = this.getById(clubId);
        Member member = this.getMemberById(memberId);
        if (club == null || member == null) return false;
        em.getTransaction().begin();
        club.getMembers().remove(member);
        em.merge(club);
        em.getTransaction().commit();
        return true;
    }
}
