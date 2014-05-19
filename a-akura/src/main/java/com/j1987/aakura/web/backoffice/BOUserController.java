package com.j1987.aakura.web.backoffice;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.j1987.aakura.domain.JActivity;
import com.j1987.aakura.domain.JAuditLog;
import com.j1987.aakura.domain.JCompany;
import com.j1987.aakura.domain.JEvent;
import com.j1987.aakura.domain.JEventCategory;
import com.j1987.aakura.domain.JRole;
import com.j1987.aakura.domain.JUser;
import com.j1987.aakura.fwk.ActivityNotFoundException;
import com.j1987.aakura.fwk.JUtils;
import com.j1987.aakura.fwk.UserNotFoundException;
import com.j1987.aakura.services.dao.ActivityService;
import com.j1987.aakura.services.dao.CompanyService;
import com.j1987.aakura.services.dao.UserService;
import com.j1987.aakura.services.security.JSecurityService;
import com.j1987.aakura.web.form.AssignActivityForm;
import com.j1987.aakura.web.form.AssignRoleForm;

@Controller
@RequestMapping(value = "/backoffice/users")
public class BOUserController {
	
	private static final String CREATE_VIEW = "backoffice/users/create";
	private static final String LIST_VIEW = "backoffice/users/list";
	private static final String UPDATE_VIEW = "backoffice/users/update";
	private static final String SHOW_VIEW = "backoffice/users/show";
	
	private static final String ROLE_ASSIGMENT_VIEW = "backoffice/users/assignrole";
	private static final String ACTIVITY_ASSIGNMENT_VIEW = "backoffice/users/assignactivity";
	
	private static Logger logger = Logger.getLogger(BOUserController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private ActivityService activityService;
	
	
	@Autowired
	JSecurityService securityService;
	
	
	public BOUserController() {
		// TODO Auto-generated constructor stub
	}
	
	
    /**
     * Validate User's login by AJAX request
     * @param userName
     * @return boolean
     */
    @RequestMapping(value = "/checkusername", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody boolean validateUserName(@RequestParam("userName")String userName){
    	boolean valid =  false;
    	
    	List<JUser> users = userService.findUserByUserNameEquals(userName);
    	
    	if(users.isEmpty()){
    		valid = true;
    	}
    	
    	return valid;
    }
	
	/**
	 * Create a new {@link JUser} in a database.
	 * @param user
	 * @param bindingResult
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
 	@RequestMapping(value = "/create", method = RequestMethod.POST, produces = "text/html")
 	public String create(@Valid JUser user, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        
	 if (bindingResult.hasErrors()) {
		 
            populateEditForm(uiModel, user);
            logger.debug("create() - the user object is invalid. Redirect to create view");
            return CREATE_VIEW;
        }
	 
        uiModel.asMap().clear();
        
        String passpwd = user.getPassword();
        String encodedPwd = securityService.encodePassword(passpwd);
        user.setPassword(encodedPwd);
        
        userService.persist(user);
        
        JAuditLog auditlog = new JAuditLog(securityService.currentUser(), JUtils.AUDIT_MSG_CREATE_USER,
    			JEventCategory.PARAMETRAGE, 
    			JEvent.NEW_USER, 
    			user.getFirstName() +" "+user.getLastName()+" [ "+user.getUserName()+" ]",
    			httpServletRequest.getRemoteAddr());
        
        String eventDateAsString = JUtils.DATE_TIME_FORMAT.format(auditlog.getEventDate());
        auditlog.setEventDateAsString(eventDateAsString);
        auditlog.setIpAddress(httpServletRequest.getRemoteAddr());
        auditlog.setHostClient(httpServletRequest.getRemoteHost());
        auditlog.persist();
        
        logger.debug("create()- a new user has been created with success !");
        
        return "redirect:/backoffice/users/" + encodeUrlPathSegment(user.getId().toString(), httpServletRequest);
    }
    
 	
 	/**
 	 * Display the create's view for creating {@link JUser}
 	 * @param uiModel
 	 * @return
 	 */
    @RequestMapping(value = "/create", params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        
    	populateEditForm(uiModel, new JUser());
        return CREATE_VIEW;
    }
    
    
    
    /**
     * Display the details's view by given {@link JUser} id.
     * @param id
     * @param uiModel
     * @return
     * @throws UserNotFoundException
     */
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) throws UserNotFoundException {
        
    	JUser user = userService.findUserById(id);
    	
    	if(user == null){

	    	String errMsg = ">>>> show() - Failed finding User with id : " + id  ;
	   	   	 logger.error(errMsg);
	   	   	 throw new UserNotFoundException("Aucun utilisateur avec l'identifiant [ "+id+" ] au sein de votre compagnie "); 

    	}
    	
    	List<JUser> users = new ArrayList<JUser>();
    	
    	users.add(user);
    	
    	users = retrieveEnrichedUsers(users);
    	
    	uiModel.addAttribute("user", user);
        uiModel.addAttribute("itemId", id);
        
        return SHOW_VIEW;
    }
    
    
    /**
     * Display the list view of {@link JUser}
     * @param page
     * @param size
     * @param uiModel
     * @return
     */
    @RequestMapping(value = "/list", produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        
    	if (page != null || size != null) {
    		
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("users", retrieveEnrichedUsers(userService.findUserEntriesWithoutFirstUser(firstResult, sizeNo)));
            
            float nrOfPages = (float) userService.countUsers() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
        	
            uiModel.addAttribute("users", retrieveEnrichedUsers(userService.findAllUsersWithoutFirstUser()));
        }
        return LIST_VIEW;
    }
    
    
    /**
     * Update {@link JUser}
     * @param user
     * @param bindingResult
     * @param uiModel
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "text/html")
    public String update(@Valid JUser user, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
       
    	if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, user);
            return UPDATE_VIEW;
        }
    	
        uiModel.asMap().clear();
        
        String passpwd = user.getPassword();
        String encodedPwd = securityService.encodePassword(passpwd);
        user.setPassword(encodedPwd);
        
        userService.update(user);
        
        JAuditLog auditlog = new JAuditLog(
        		securityService.currentUser(), 
        		JUtils.AUDIT_MSG_UPDATE_USER_DETAILS,
    			JEventCategory.PARAMETRAGE, 
    			JEvent.UPDATE_USER, 
    			user.getUserName(),
    			user.getFirstName() + " " + user.getLastName()
    			);
        
        String eventDateAsString = JUtils.DATE_TIME_FORMAT.format(auditlog.getEventDate());
        auditlog.setEventDateAsString(eventDateAsString);
        auditlog.setIpAddress(httpServletRequest.getRemoteAddr());
        auditlog.setHostClient(httpServletRequest.getRemoteHost());
        auditlog.persist();
        
        return "redirect:/backoffice/users/" + encodeUrlPathSegment(user.getId().toString(), httpServletRequest);
    }
    
    
    /**
     * Display the update view for given user's id
     * @param id
     * @param uiModel
     * @return
     */
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        
    	populateEditForm(uiModel, userService.findUserById(id));
        return UPDATE_VIEW;
    }
    
    
    @RequestMapping(value =  "/assignrole", method = RequestMethod.POST, produces = "text/html")
    public String assignRole(@Valid AssignRoleForm  assignRoleForm, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
    	
        if (bindingResult.hasErrors() || StringUtils.isEmpty(assignRoleForm.getRoleName())) {
        	
        	populateAssignRoleForm(assignRoleForm.getUserName(), uiModel);
            logger.error(">>>> create() - Failed assigning Role. Invalid values. Role Name might have not been selected");
            return ROLE_ASSIGMENT_VIEW;
        
        }
        
        uiModel.asMap().clear();
        
        String userName = assignRoleForm.getUserName();
        List<JUser> users = userService.findUserByUserNameEquals(userName);
        if (!users.isEmpty()) {
        	JUser user = users.get(0);
			
			Map<String, String> roleNamesMap = JUtils.dbRoleNamesMap();
			
			String roleName = assignRoleForm.getRoleName();
			String dbRoleName = roleNamesMap.get(roleName);
			List<JRole> roles = userService.findRoleByRoleNameEquals(dbRoleName);
			
			if (!roles.isEmpty()) {
				JRole role = roles.get(0);
				// TODO - REVIEW - Replace the role ??
				user.getRoles().clear();
				user.getRoles().add(role);
				
				role.getUsers().add(user);
				user.setRoleName(roleName);
				
				if(roleName.equals(JUtils.DB_UI_ROLE_ADMIN) || roleName.equals(JUtils.DB_UI_ROLE_SUPERVISOR)){
					JCompany company = companyService.findCompanyById(Long.valueOf("1"));
					user.getCompanies().add(company);
					company.getUsers().add(user);
				}
				
				userService.merge(user);
				
		        JAuditLog auditlog = new JAuditLog(securityService.currentUser() , JUtils.AUDIT_MSG_ASSIGNROLE_USER,
		    			JEventCategory.PARAMETRAGE, 
		    			JEvent.NEW_ROLE, 
		    			userName,
		    			dbRoleName);
		        
		        String eventDateAsString = JUtils.DATE_TIME_FORMAT.format(auditlog.getEventDate());
		        auditlog.setEventDateAsString(eventDateAsString);
		        auditlog.setIpAddress(httpServletRequest.getRemoteAddr());
		        auditlog.setHostClient(httpServletRequest.getRemoteHost());
		        auditlog.persist();
			}
		}
        
        return "redirect:/backoffice/users/list?size=10";
    }
    
    
    
    /**
     * display the assignment form for role's assignment
     * @param userName
     * @param uiModel
     * @return
     */
    @RequestMapping(value = "/assignrole/{userName}", params = "form", produces = "text/html")
    public String assignRoleForm(@PathVariable("userName")String userName,Model uiModel) {
    	populateAssignRoleForm(userName, uiModel);
        return ROLE_ASSIGMENT_VIEW;
    }
    
    
    /**
     * Assign activity to a given {@link JUser}
     * @param assignActivityForm
     * @param bindingResult
     * @param uiModel
     * @param httpServletRequest
     * @return
     * @throws UserNotFoundException
     * @throws ActivityNotFoundException
     */
    @RequestMapping(value = "/assignactivity", method = RequestMethod.POST, produces = "text/html")
    public String assignActivity(@Valid AssignActivityForm assignActivityForm, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) throws UserNotFoundException, ActivityNotFoundException{
       
    	if (bindingResult.hasErrors()) {
        	List<JUser> candidates = userService.findUserByUserNameEquals(assignActivityForm.getUserName());
        	if(candidates.isEmpty()){
        		
            	JUser user = candidates.get(0);
            	populateEditAssignActvityForm(uiModel, user ,assignActivityForm);
                logger.error("assignActivity() - >>>>  FAILED : The AssignActvityForm is NOT valid");
        	}
        	
          return ACTIVITY_ASSIGNMENT_VIEW;
    	}
    	
    	List<JUser> candidates = userService.findUserByUserNameEquals(assignActivityForm.getUserName());
    	
    	if(candidates.isEmpty()){
    		String errMsg = "assignActivity () - >>>> show() - Failed finding User with username : " + assignActivityForm.getUserName()  ;
	   	   	 logger.error(errMsg);
	   	   	 throw new UserNotFoundException("Aucun utilisateur avec le pseudo [ "+assignActivityForm.getUserName()+" ] au sein de votre compagnie "); 
    	}
    	
    	List<JActivity> activities = activityService.findActivitiesByCodeEquals(assignActivityForm.getActivityCode());
    	
    	if(activities.isEmpty()){
    		String errMsg = "assignActivity () -  >>>> show() - Failed finding Actvity with code : " + assignActivityForm.getActivityCode()  ;
	   	   	 logger.error(errMsg);
	   	   	 throw new ActivityNotFoundException("Aucune activity avec le code [ "+assignActivityForm.getActivityCode()+" ] au sein de votre compagnie ");
    	}
    
    	JActivity activity = activities.get(0);
    	JUser user = candidates.get(0);
    	
    	List<JActivity> userActivities = user.getActivities();
    	
    	if (!userActivities.contains(activity)){
    		user.getActivities().add(activity);
    		
    		userService.merge(user);
    		
    		JAuditLog auditlog = new JAuditLog(securityService.currentUser() , JUtils.AUDIT_MSG_ASSIGNACTIVITY_USER,
	    			JEventCategory.PARAMETRAGE, 
	    			JEvent.ASSIGNACTIVITY_USER, 
	    			user.getUserName(),
	    			null);
	        
	        String eventDateAsString = JUtils.DATE_TIME_FORMAT.format(auditlog.getEventDate());
	        auditlog.setEventDateAsString(eventDateAsString);
	        auditlog.setIpAddress(httpServletRequest.getRemoteAddr());
	        auditlog.setHostClient(httpServletRequest.getRemoteHost());
	        auditlog.persist();
	        
    		logger.debug("assignActivity () - Activity assignment has been done successfully!");
    	}
    	
    	return "redirect:/backoffice/users/list?size=10";
    }
    
    
    /**
     * display the assignment form for actvity's assignment
     * @param userName
     * @param uiModel
     * @return
     */
    @RequestMapping(value = "/assignactivity/{userName}",  params = "form", produces = "text/html")
    public String assignActvityForm(@PathVariable("userName")String userName, Model uiModel){
    	
    	List<JUser> candidates = userService.findUserByUserNameEquals(userName);
    	if(!candidates.isEmpty()){
        	AssignActivityForm assignActivityForm = new AssignActivityForm();
        	JUser user = candidates.get(0);
        	
        	populateEditAssignActvityForm(uiModel,user,assignActivityForm);
    	}

    	return ACTIVITY_ASSIGNMENT_VIEW;

    }
    
    
    /**
     * Unassigns all privileges to the current {@link JUser} by given userName.
     * @param userName
     * @param httpServletRequest
     * @return
     * @throws UserNotFoundException
     */
    @RequestMapping(value = "/unassign/{userName}", method = RequestMethod.GET, produces = "text/html")
    public String unassignUserPrivileges(@PathVariable("userName")String userName, ServletRequest httpServletRequest) throws UserNotFoundException{
    	
    	List<JUser> candidates = userService.findUserByUserNameEquals(userName);
    	if(candidates.isEmpty()){
    		String errMsg = "assignActivity () - >>>> show() - Failed finding User with username : " + userName ;
	   	   	 logger.error(errMsg);
	   	   	 throw new UserNotFoundException("Aucun utilisateur avec le pseudo [ "+userName+" ] au sein de votre compagnie "); 
    	}
    	
    	JUser user = candidates.get(0);
    	
    	if(user != null){
    		
    		//remove all companies of current user.
    		List<JCompany> userCompanies = user.getCompanies();
    		if(!userCompanies.isEmpty()){
    			
    			for (JCompany jCompany : userCompanies) {
					jCompany.getUsers().remove(user);
				}
    		}
    		
    		
    		// remove all activities of current user.
    		List<JActivity> userActivities = user.getActivities();
    		if(!userActivities.isEmpty()){
    			
    			for (JActivity jActivity : userActivities) {
    				jActivity.getUsers().remove(user);
				}
    		}
    		
    		//set the user's companies and activities to null
    		user.setCompanies(null);
    		user.setActivities(null);
    		
        	userService.merge(user);
    		logger.debug("unassignUserPrivileges() -  the unassignment has been done successfully!");
    		
    		JAuditLog auditlog = new JAuditLog(securityService.currentUser() , JUtils.AUDIT_MSG_UNASSIGN_USER,
        			JEventCategory.PARAMETRAGE, 
        			JEvent.UNASSIGN_USER, 
        			user.getUserName(),
        			httpServletRequest.getRemoteAddr());
            
            String eventDateAsString = JUtils.DATE_TIME_FORMAT.format(auditlog.getEventDate());
            auditlog.setEventDateAsString(eventDateAsString);
            auditlog.setIpAddress(httpServletRequest.getRemoteAddr());
            auditlog.setHostClient(httpServletRequest.getRemoteHost());
            auditlog.persist();
    		
    	}
    	

    	return  "redirect:/backoffice/users/list?size=10";
    	
    }
    
    void populateEditAssignActvityForm(Model uiModel, JUser user, AssignActivityForm assignActivityForm){

    	assignActivityForm.setUserName(user.getUserName());
    	
    	String activityCode =  null;
        JActivity activity = null;
        List<JActivity> userActivities = user.getActivities();
        
        if (!userActivities.isEmpty()) {
        	activity = userActivities.get(0);
        	activityCode = activity.getCode();
        	
		}

        assignActivityForm.setActivityCode(activityCode);
        
        List<JActivity> activities =  new ArrayList<JActivity>();

        activities.addAll(activityService.findAllActivities());

    	uiModel.addAttribute("assignActivityForm", assignActivityForm);
    	uiModel.addAttribute("activities", activities);
    	uiModel.addAttribute("itemId",user.getId());
    	uiModel.addAttribute("currentNav", "user");
    }
    
    
    /**
     * 
     * */
    private void populateAssignRoleForm(String userName, Model uiModel) {
        List<String> roleNames = new ArrayList<String>();
        Map<String, String> roleNamesMap = JUtils.uiRoleNamesMap();
        roleNames.add(roleNamesMap.get(JUtils.DB_ROLE_ADMIN));
        roleNames.add(roleNamesMap.get(JUtils.DB_ROLE_SUPERVISOR));
        roleNames.add(roleNamesMap.get(JUtils.DB_ROLE_MANAGER));
        
        AssignRoleForm assignRoleForm = new AssignRoleForm();
        assignRoleForm.setUserName(userName);
        
        uiModel.addAttribute("assignRoleForm", assignRoleForm);
        uiModel.addAttribute("roleNames", roleNames);

        uiModel.addAttribute("currentNav", "user");
    }
    
    
    /**
     * delete a given id.
     * @param id
     * @param page
     * @param size
     * @param uiModel
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        JUser JUser_ = JUser.findJUser(id);
        JUser_.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/jusers";
    }
    
    private List<JUser> retrieveEnrichedUsers(List<JUser> users) {
    	
    	List<JUser> enrichedUsers = new ArrayList<JUser>();
    	for (JUser dtUser : users) {
    		List<JRole> roles = dtUser.getRoles();
    		
    		if (!roles.isEmpty()) {
    			
    			JRole role = roles.get(0);
				Map<String, String> namesMap = JUtils.uiRoleNamesMap();
				String roleName = namesMap.get(role.getName());
				dtUser.setRoleName(roleName);
				
				
				
				if(roleName.equals(JUtils.DB_UI_ROLE_ADMIN) || roleName.equals(JUtils.DB_UI_ROLE_SUPERVISOR)){
					
					if(!dtUser.getCompanies().isEmpty()){
						dtUser.setActivityNames("*");
					}
				}
				
				List<JActivity> activities = dtUser.getActivities();
				
				if (!activities.isEmpty()) {
					String activityNames =  JUtils.retrieveObjectPropertiesAsString(activities);
					dtUser.setActivityNames(activityNames);
				}
    		}
    		
    		enrichedUsers.add(dtUser);
    	}
    	
    	return enrichedUsers;
    }
    
    void populateEditForm(Model uiModel, JUser user) {
        uiModel.addAttribute("user", user);
    }
    
    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }

}
