package com.j1987.aakura.web.form;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class AssignRoleForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -682316289171938329L;
	
	@NotNull
	private String userName;
	
	@NotNull
	private String roleName;

	public AssignRoleForm() {
		// TODO Auto-generated constructor stub
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	
}
