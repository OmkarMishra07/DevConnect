package com.example.MergeX.model;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table
public class College {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(nullable = true, unique = true)
    private String name;
    @OneToMany(mappedBy = "college")
    private List<User> students;


}