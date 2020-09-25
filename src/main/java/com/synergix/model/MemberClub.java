package com.synergix.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "member_club")
@Data
public class MemberClub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Club club;
}
