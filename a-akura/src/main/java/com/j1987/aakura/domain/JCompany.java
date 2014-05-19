package com.j1987.aakura.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(table = "J_COMPANY")
public class JCompany {

    /**
     */
    @NotNull
    private String name;

    /**
     */
    private String description;

    /**
     */
    @OneToMany(cascade = CascadeType.ALL)
    private Set<JActivity> activities = new HashSet<JActivity>();
    
    @ManyToMany
    private List<JUser> users = new ArrayList<JUser>();
}
