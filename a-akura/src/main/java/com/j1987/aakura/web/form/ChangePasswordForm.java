package com.j1987.aakura.web.form;

import java.io.Serializable;

public class ChangePasswordForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 722899255016812835L;

	public ChangePasswordForm() {
		// TODO Auto-generated constructor stub
	}
	
	private String oldPassword;
	
	private String newPassword;
	
	private String confirmNewPassword;

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}

	public void setConfirmNewPassword(String confirmNewPassword) {
		this.confirmNewPassword = confirmNewPassword;
	}

}
