package com.j1987.aakura.web.form;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class AssignActivityForm implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1484053414632375199L;


	@NotNull
	private String userName;
	
	
	private String activityCode;

	public AssignActivityForm() {
		// TODO Auto-generated constructor stub
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	
}
