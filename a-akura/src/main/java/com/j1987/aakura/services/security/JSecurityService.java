package com.j1987.aakura.services.security;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface JSecurityService extends UserDetailsService {

	/**
	 * Access rights
	 * */
	public static final String SECURITY_ROLE_SUPER_ADMIN	= "ROLE_SUPER_ADMIN";
	public static final String SECURITY_ROLE_ADMIN_NETWORK 			= "ROLE_ADMIN_NETWORK";
	public static final String SECURITY_ROLE_ORGANIZATION 	= "ROLE_CASHIER";
	
	public static final String SECURITY_UNAUTHENTICATED_USER 	= "anonymousUser";
	/**
	 * Password encoding as specified in 'asuivre.properties' 
	 * */
	public static final String PASSWD_ENCODING_MD5 		= "Md5";
	public static final String PASSWD_ENCODING_SHA 		= "Sha";
	public static final String PASSWD_ENCODING_SHA256 	= "Sha-256";

	/**
	 * Encodes a password for authentication based on the value specified 
	 * for 'password.encoding' in 'asuivre.properties'. 
	 * Encoding type defaults to Sha-256
	 * */
	public String encodePassword(String rawPassword);
	
	/**
	 * Retrieves the current user's 'username'
	 * @return The login name of the current user
	 * */
	public String currentUser();
	
}
