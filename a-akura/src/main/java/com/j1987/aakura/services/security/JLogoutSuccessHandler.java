package com.j1987.aakura.services.security;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.j1987.aakura.domain.JAuditLog;
import com.j1987.aakura.domain.JEvent;
import com.j1987.aakura.domain.JEventCategory;
import com.j1987.aakura.domain.JUser;
import com.j1987.aakura.fwk.JUtils;
import com.j1987.aakura.services.dao.UserService;

public class JLogoutSuccessHandler  extends SimpleUrlLogoutSuccessHandler {

	private static final String TARGET_URL_HOME_DEFAULT = "a-akura/";
	
	private static Logger logger = Logger.getLogger(JLogoutSuccessHandler.class); 
	
	@Autowired
	private UserService userService;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		
		// Audit Log 
		if (authentication != null) {
			User user =  (User)authentication.getPrincipal();
			String userName = user.getUsername();
			List<JUser> users = userService.findUserByUserNameEquals(userName);
			if (!users.isEmpty()) {
				JUser uzer = users.get(0);
				
		        JAuditLog auditlog = new JAuditLog(userName, JUtils.AUDIT_MSG_LOGOUT,
		    			JEventCategory.CONNEXION, 
		    			JEvent.LOGOUT_USER, 
		    			uzer.getRoleName(),
		    			null);
		        
		        String eventDateAsString = JUtils.DATE_TIME_FORMAT.format(auditlog.getEventDate());
		        auditlog.setEventDateAsString(eventDateAsString);
		        auditlog.persist();
				logger.info("onLogoutSuccess() - User ["+userName+"] - successful logout");
				
			} else {
				logger.error("onLogoutSuccess() - User [] not found in the database !?!?");
			}
		}
		
		HttpSession session = request.getSession();
		if (session != null) {
			session.invalidate();
		}
		
		String targUrl = determineTargetUrl(request, response);
		response.sendRedirect(targUrl + TARGET_URL_HOME_DEFAULT);
	}
	
}
