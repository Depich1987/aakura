package com.j1987.aakura.domain;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(table = "J_USER")
public class JUser {

    @NotNull
    @Column(name = "USER_NAME", unique = true)
    private String userName;

    /**
     */
    private String firstName;

    /**
     */
    private String lastName;

    /**
     */
    private String phoneNumber;

    /**
     */
    @Email
    private String email;

    //For the view
    private String roleName;
    
    @Transient
    private String activityNames;

    @ManyToMany
    private List<JRole> roles;

    @ManyToMany
    private List<JCompany> companies = new ArrayList<JCompany>();

    @ManyToMany
    private List<JActivity> activities = new ArrayList<JActivity>();

    /**
     */
    @NotNull
    private String password;
    
    /**
     */
    @Transient
    private String confirmPassword;
}
