package com.j1987.aakura.services.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.j1987.aakura.domain.JActivity;
import com.j1987.aakura.domain.JAuditLog;
import com.j1987.aakura.domain.JCompany;
import com.j1987.aakura.domain.JEvent;
import com.j1987.aakura.domain.JEventCategory;
import com.j1987.aakura.domain.JRole;
import com.j1987.aakura.domain.JUser;
import com.j1987.aakura.fwk.JUtils;
import com.j1987.aakura.services.dao.UserService;
import com.j1987.aakura.services.properties.PropertiesService;

/**
 * <code>DTSecurityServiceImpl</code> is an implementation of a <code>UserDetailsService</code> 
 *  Spring interface and is a part of a customized authentication provider to be used by Spring Security.
 * 
 * @author cbalde
 *
 */
public class JSecurityServiceImpl implements JSecurityService {
	
	private static Logger logger = Logger.getLogger(JSecurityServiceImpl.class);
	
	@Autowired
	private PropertiesService propertiesService;
	
	@Autowired
	private UserService userService;

	/**
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Transactional
	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {
		
		JUser user = null;
		List<JUser> users = userService.findUserByUserNameEquals(userName);
		if (!users.isEmpty()) {
			user = users.get(0);
		}
		
		handlePreconditionsAccess(user, userName);
		
		// 1. Create user details
		UserDetails userDetails = retrieveUserDetails(user); 
		
		// 2. Audit log
        JAuditLog auditlog = new JAuditLog(user.getUserName()  , JUtils.AUDIT_MSG_LOGIN,
    			JEventCategory.CONNEXION, 
    			JEvent.LOGIN_USER, 
    			user.getRoleName(),
    			null);
        
        String eventDateAsString = JUtils.DATE_TIME_FORMAT.format(auditlog.getEventDate());
        auditlog.setEventDateAsString(eventDateAsString);
        auditlog.persist();
		
		logger.info("loadUserByUsername() - User ["+user.getUserName()+"] - successful login");
        
		return userDetails;
	}

	/**
	 * Encodes a password for authentication based on the value specified 
	 * for 'password.encoding' in 'asuivre.properties'. 
	 * Encoding type defaults to Sha-256
	 * */
	@Override
	public String encodePassword(String rawPassword) {
		String encodedPassword = null;
		
		String pwdEncoding = propertiesService.getPasswordEncoding();
		if (pwdEncoding != null) {
			if (PASSWD_ENCODING_MD5.equals(pwdEncoding)) {
				PasswordEncoder encoder = new Md5PasswordEncoder();
				encodedPassword = encoder.encodePassword(rawPassword, null);
						
			} else if (PASSWD_ENCODING_SHA.equals(pwdEncoding)) {
				PasswordEncoder encoder = new ShaPasswordEncoder();
				encodedPassword = encoder.encodePassword(rawPassword, null);
				
			} else if (PASSWD_ENCODING_SHA256.equals(pwdEncoding)) {
				PasswordEncoder encoder = new ShaPasswordEncoder(256);
				encodedPassword = encoder.encodePassword(rawPassword, null);
				
			} else {
				PasswordEncoder encoder = new ShaPasswordEncoder(256);
				encodedPassword = encoder.encodePassword(rawPassword, null);
			}
			
		} else {
			PasswordEncoder encoder = new ShaPasswordEncoder(256);
			encodedPassword = encoder.encodePassword(rawPassword, null);
		}
		
		return encodedPassword;
	}

	
	@Override
	public String currentUser() {
		String userName = null;
		SecurityContext context = SecurityContextHolder.getContext();
		if (context != null) {
			Authentication authentication = context.getAuthentication();
			if (authentication != null) {
				Object principal = authentication.getPrincipal();
				if (principal != null && principal instanceof org.springframework.security.core.userdetails.User) {
					userName = ((org.springframework.security.core.userdetails.User) principal).getUsername();
				}
			}
		}
		return userName;		
	}

	/**
	 * 
	 * */
	private void handlePreconditionsAccess(JUser user, String userName)  throws UsernameNotFoundException {
		
		if (user == null) {
			String errMsg = "handlePreconditionsAccess() - Could not find user ["+userName+"]";
			logger.error(errMsg, new UsernameNotFoundException(errMsg));
			throw new UsernameNotFoundException(errMsg);
			
		} else if ( user.getRoles().isEmpty()) {
			String errMsg = "handlePreconditionsAccess() - User ["+userName+"] doesnt have any role assigned yet. Please ask your administrator";
			logger.error(errMsg, new UsernameNotFoundException(errMsg));
			throw new UsernameNotFoundException(errMsg);
			
		} else {
			Map<String, String> dbRoles = JUtils.dbRoleNamesMap();
			String roleName = user.getRoleName();
			String dbRoleName = dbRoles.get(roleName);
			
			if (dbRoleName.equals(JUtils.DB_ROLE_ADMIN)) {
				List<JCompany> companies = user.getCompanies();
				if (companies.isEmpty()) {
					String errMsg = "handlePreconditionsAccess() - User ["+userName+"] with network admin role doesnt have any company assigned yet. Please ask your administrator";
					logger.error(errMsg, new UsernameNotFoundException(errMsg));
					throw new UsernameNotFoundException(errMsg);
				} 
			}
			
			if (dbRoleName.equals(JUtils.DB_ROLE_SUPERVISOR)) {
				List<JCompany> companies = user.getCompanies();
				if (companies.isEmpty()) {
					String errMsg = "handlePreconditionsAccess() - User ["+userName+"] with network supervisor role doesnt have any company assigned yet. Please ask your administrator";
					logger.error(errMsg, new UsernameNotFoundException(errMsg));
					throw new UsernameNotFoundException(errMsg);
				} 
			}
			
			if (dbRoleName.equals(JUtils.DB_ROLE_MANAGER)) {
				List<JActivity> activities = user.getActivities();
				if (activities.isEmpty()) {
					String errMsg = "handlePreconditionsAccess() - User ["+userName+"] with agency chief role doesnt have any agency assigned yet. Please ask your administrator";
					logger.error(errMsg, new UsernameNotFoundException(errMsg));
					throw new UsernameNotFoundException(errMsg);
				} 
			}

			
		}
	}
	
	/**
	 * 
	 * */
	private UserDetails retrieveUserDetails(JUser user) {
		
		// TODO - HACK - ALL flags are set to true first
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;
		
		List<GrantedAuthority> authorities = retrieveGrantedAuthorities(user);
		
		UserDetails userDetails = new org.springframework.security.core.userdetails.User(
				user.getUserName(), user.getPassword(), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		
		return userDetails;
	}
	
	/**
	 * 
	 * */
	private List<GrantedAuthority> retrieveGrantedAuthorities(JUser user) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		// Groups where the given user is a member of.
		
		List<JRole> roles = user.getRoles();
		for (JRole role : roles) {
			GrantedAuthority grantedAuthority = new CommonGrantedAuthority(role.getName());
			grantedAuthorities.add(grantedAuthority);
		}
		
		return grantedAuthorities;
	}
	
}
