package com.synergix.repositoty;

import com.synergix.model.Club;
import com.synergix.model.Member;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

@Named(value = "clubRepo")
public class ClubRepo implements Serializable {
    
    @Inject
    private MemberRepo memberRepo;

    private EntityManager em = Persistence.createEntityManagerFactory("com.synergix").createEntityManager();
    
    public List<Club> getAll() {
        TypedQuery<Club> getAllClubs = em.createQuery("select distinct c from Club c left join fetch c.mentor left join fetch c.members order by c.id ", Club.class);
        return getAllClubs.getResultList();
    }

    public Club getById(Integer clubId) {
//        TypedQuery<Club> getClubQuery = em.createQuery("select c from Club c join fetch c.mentor join fetch c.members where c.id = ?1", Club.class);
//        getClubQuery.setParameter(1, clubId);
//        return getClubQuery.getSingleResult();
        return em.find(Club.class, clubId);
    }

    public boolean save(Club club) {
        if (club == null) return false;
        em.getTransaction().begin();
        em.persist(club);
        em.getTransaction().commit();
        return true;
    }

    public boolean update(Club club) {
        if (club == null) return false;
        em.getTransaction().begin();
        em.merge(club);
        em.getTransaction().commit();
        return true;
    }

    public boolean delete(Integer clubId) {
        Club club = this.getById(clubId);
        if (club == null) return false;
        em.getTransaction().begin();
        em.remove(club);
        em.getTransaction().commit();
        return true;
    }

    public List<Member> getMembersListByClubId(Integer clubId) {
        Club club = this.getById(clubId);
        return club.getMembers();
    }

    public boolean saveMemberIntoClub(Club club, Member member) {
        if (club == null || member == null) return false;
        List<Member> members = club.getMembers();
        members.set(members.size() - 1, member);
        club.setMembers(members);
        em.getTransaction().begin();
        em.merge(club);
        em.getTransaction().commit();
        return true;
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
        Member member = memberRepo.getById(memberId);
        if (club == null || member == null) return false;
        em.getTransaction().begin();
        club.getMembers().remove(member);
        em.merge(club);
        em.getTransaction().commit();
        return true;
    }
}
