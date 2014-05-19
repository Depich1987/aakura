package com.j1987.aakura.domain;
import java.util.ArrayList;
import java.util.List;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import javax.validation.constraints.NotNull;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(table = "J_ACTIVITY", finders = { "findJActivitysByCodeEquals" })
public class JActivity {

    @NotNull
    @Column(unique = true)
    private String code;

    /**
     */
    @NotNull
    private String name;

    /**
     */
    private String description;

    /**
     */
    @ManyToOne
    private JCompany company;
    
    @ManyToMany
    private List<JUser> users = new ArrayList<JUser>();
}
