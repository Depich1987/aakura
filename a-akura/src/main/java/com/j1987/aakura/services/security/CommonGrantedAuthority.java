package com.j1987.aakura.services.security;

import org.springframework.security.core.GrantedAuthority;


/**
 * <code>CommonGrantedAuthority</code> is an implementation of a <code>GrantedAuthority</code> 
 * Spring object and is a part of a customized authentication provider to be used by Spring Security.
 * 
 * @author cbalde
 *
 */
public class CommonGrantedAuthority implements GrantedAuthority {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5053731337244981587L;
	private String authority;
	
	public CommonGrantedAuthority(String authority) {
		this.authority = authority;
	}
	

	/* (non-Javadoc)
	 * @see org.springframework.security.core.GrantedAuthority#getAuthority()
	 */
	@Override
	public String getAuthority() {
		return authority;
	}

}
