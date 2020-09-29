package com.synergix.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "club")
@Data
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Member mentor;

    @OneToMany
    @JoinTable(name = "member_club", joinColumns = {@JoinColumn(name = "club_id")}, inverseJoinColumns = {@JoinColumn(name = "member_id")})
    private List<Member> members;
}
