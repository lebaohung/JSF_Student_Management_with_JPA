package com.synergix.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "member_club")
@Data
public class MemberClub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
