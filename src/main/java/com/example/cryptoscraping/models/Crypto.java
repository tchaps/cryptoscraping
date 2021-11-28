package com.example.cryptoscraping.models;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "crypto")
@Getter
@Setter
public class Crypto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    private String coinName;
    @Column(nullable = false)
    private String price;
    private String volume;
    private String marketcap;
    private String age;
    private String holders;
    @Transient
    private String linkCmc;
}
