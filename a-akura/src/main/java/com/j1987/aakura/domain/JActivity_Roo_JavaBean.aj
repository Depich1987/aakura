// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.j1987.aakura.domain;

import com.j1987.aakura.domain.JActivity;
import com.j1987.aakura.domain.JCompany;
import com.j1987.aakura.domain.JUser;
import java.util.List;

privileged aspect JActivity_Roo_JavaBean {
    
    public String JActivity.getCode() {
        return this.code;
    }
    
    public void JActivity.setCode(String code) {
        this.code = code;
    }
    
    public String JActivity.getName() {
        return this.name;
    }
    
    public void JActivity.setName(String name) {
        this.name = name;
    }
    
    public String JActivity.getDescription() {
        return this.description;
    }
    
    public void JActivity.setDescription(String description) {
        this.description = description;
    }
    
    public JCompany JActivity.getCompany() {
        return this.company;
    }
    
    public void JActivity.setCompany(JCompany company) {
        this.company = company;
    }
    
    public List<JUser> JActivity.getUsers() {
        return this.users;
    }
    
    public void JActivity.setUsers(List<JUser> users) {
        this.users = users;
    }
    
}