package com.j1987.aakura.fwk.web;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.j1987.aakura.domain.JActivity;
import com.j1987.aakura.domain.JCompany;
import com.j1987.aakura.domain.JUser;
import com.j1987.aakura.fwk.DataImporter;
import com.j1987.aakura.fwk.JUtils;
import com.j1987.aakura.services.dao.UserService;
import com.j1987.aakura.services.properties.PropertiesService;

public class CommonInterceptor extends HandlerInterceptorAdapter {
	
	/**
	 * Logging system
	 * */
	private static Logger logger = Logger.getLogger(CommonInterceptor.class); 
	
	@Autowired
	private PropertiesService propertiesService;
	
	@Autowired
	private UserService userService;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
//		DataImporter.getInstance(
//				propertiesService.getSqlScript(), 
//				propertiesService.getDbDriverClassName(), 
//				propertiesService.getDbURL(), 
//				propertiesService.getDbUserName(), 
//				propertiesService.getDbPassword());
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication != null) {
			String authUser = authentication.getName();
			
			if ( !JUtils.SECURITY_UNAUTHENTICATED_USER.equals(authUser) ) {
				
				JUser user = null;
				Collection<? extends GrantedAuthority> authorities =  authentication.getAuthorities();
				for (GrantedAuthority authority : authorities) {
					
					// if the current user is a MANAGER
					if (JUtils.DB_ROLE_MANAGER.equals(authority.getAuthority())) {
						
						HttpSession session = request.getSession();
						if (session != null && (session.getAttribute(JUtils.HTTP_SESSION_ACTIVITY_CODE) == null)) {
							
							logger.debug("preHandle() - setting 'useractivitycode' attribute to the session");
							
							if (user == null) {
								List<JUser> users = userService.findUserByUserNameEquals(authUser);
								user = users.get(0);
							}
							
							//Retrieves the activity of current MANAGER
							List<JActivity> activities = user.getActivities();
							
							if( !activities.isEmpty() && activities.size() <= 1){
								JActivity activity = activities.get(0);
								
								if (session.getAttribute(JUtils.HTTP_SESSION_ACTIVITY_CODE) == null) {
									
									session.setAttribute(JUtils.HTTP_SESSION_ACTIVITY_CODE, activity.getCode());
									session.setAttribute(JUtils.HTTP_SESSION_ACTIVITY_NAME, activity.getName());
									
									logger.debug("preHandle() - actvity's session variables has been set");
								}
								
								if (session.getAttribute(JUtils.HTTP_SESSION_COMPANY_CODE) == null) {
									
									JCompany company = activity.getCompany();
									
									session.setAttribute(JUtils.HTTP_SESSION_COMPANY_CODE, company.getId());
									session.setAttribute(JUtils.HTTP_SESSION_COMPANY_NAME, company.getName());
									
									logger.debug("preHandle() - company's session variables has been set");
								}
							}
							
						}
						
					}else if(JUtils.DB_ROLE_SUPERVISOR.equals(authority.getAuthority()) || JUtils.DB_ROLE_ADMIN.equals(authority.getAuthority())){
						// if the current user is a SUPERVISOR
						
						HttpSession session = request.getSession();
						if (session != null && (session.getAttribute(JUtils.HTTP_SESSION_COMPANY_CODE) == null)) {
							
							logger.debug("preHandle() - setting 'userCompanyCode' attribute to the session");
							if (user == null) {
								List<JUser> users = userService.findUserByUserNameEquals(authUser);
								user = users.get(0);
							}

							//Retrieves the companies of current SUPERVISOR
							List<JCompany> companies = user.getCompanies();
							
							if(!companies.isEmpty() && companies.size() <=1){
								
								if (session.getAttribute(JUtils.HTTP_SESSION_COMPANY_CODE) == null) {
									
									JCompany company = companies.get(0);
									
									session.setAttribute(JUtils.HTTP_SESSION_COMPANY_CODE, company.getId());
									session.setAttribute(JUtils.HTTP_SESSION_COMPANY_NAME, company.getName());
									
									logger.debug("preHandle() - company's session variables has been set");
								}
								
							}
							
						}
						
					}
					
				}
				
			}
			
		}
		
		
		return super.preHandle(request, response, handler);
	}

}
