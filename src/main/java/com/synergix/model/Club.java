package com.synergix.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "club")
@Data
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Member mentor;

}
