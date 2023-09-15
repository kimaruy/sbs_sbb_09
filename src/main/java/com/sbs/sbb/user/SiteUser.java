package com.sbs.sbb.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private String new_password;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String cellphoneNo;

    private String birth;

    private String name;

    @Column(unique = true)
    private String nickname;

}