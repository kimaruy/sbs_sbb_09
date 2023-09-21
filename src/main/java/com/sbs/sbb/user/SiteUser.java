package com.sbs.sbb.user;

import com.sbs.sbb.consulting.Consulting;
import com.sbs.sbb.consulting_answer.Consulting_Answer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @ManyToMany
    Set<SiteUser> voter;

    @ManyToOne
    private SiteUser author;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<UserRole> authorities;

    public boolean hasRole(UserRole role) {
        return authorities.contains(role);
    }

    public boolean isAdmin() {
        return hasRole(UserRole.ADMIN);
    }

}