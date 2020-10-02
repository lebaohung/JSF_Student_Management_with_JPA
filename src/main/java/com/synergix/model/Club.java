package com.synergix.model;

import lombok.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;


@Entity
@Table(name = "club")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "mentor_id")
    private Member mentor;

    @OneToMany(orphanRemoval = true)
    @JoinTable(name = "member_club", joinColumns = {@JoinColumn(name = "club_id")}, inverseJoinColumns = {@JoinColumn(name = "member_id")})
    private List<Member> members;
}
