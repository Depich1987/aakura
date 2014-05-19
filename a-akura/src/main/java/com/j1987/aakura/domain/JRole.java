package com.j1987.aakura.domain;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(table = "J_ROLE")
public class JRole {
	
	 /**
     */
    @NotNull
    private String name;

    /**
     */
    private String description;

    /**
     */
    @ManyToMany(cascade = CascadeType.MERGE)
    private List<JUser> users = new ArrayList<JUser>();
}
