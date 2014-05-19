package com.j1987.aakura.web.backoffice;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

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
import com.j1987.aakura.fwk.ActivityNotFoundException;
import com.j1987.aakura.fwk.JUtils;
import com.j1987.aakura.services.dao.ActivityService;
import com.j1987.aakura.services.dao.CompanyService;
import com.j1987.aakura.services.security.JSecurityService;


/**
 * An Implementation of Activity Controller related to back office.There we can create, list, update or delete {@link JCompany} items.
 * @author Franck Janel Agah
 *
 */
@Controller
@RequestMapping(value = "/backoffice/activities")
public class BOActivityController {
	
	private static final String CREATE_VIEW = "backoffice/activities/create";
	private static final String LIST_VIEW = "backoffice/activities/list";
	private static final String UPDATE_VIEW = "backoffice/activities/update";
	private static final String SHOW_VIEW = "backoffice/activities/show";
	
	private static final String GENERATED_ACTIVITYDAILY_REPORT_VIEW = "activitylist_report";
	
	private static Logger logger = Logger.getLogger(BOActivityController.class);
	
	@Autowired
	CompanyService companyService;
	
	@Autowired
	private ActivityService activityService;
	
	@Autowired
	JSecurityService securityService;
	
	public BOActivityController() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Create a new activity in the database.
	 * @param activity
	 * @param bindingResult
	 * @param uiModel
	 * @param httpServletRequest
	 * @return 
	 */
	 @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "text/html")
	    public String create(@Valid JActivity activity, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
	        
		 	if (bindingResult.hasErrors()) {
	            populateEditForm(uiModel, activity);
	            logger.debug("create() - the activity object is invalid. Redirect to create view");
	            return CREATE_VIEW;
	        }
		 	
	        uiModel.asMap().clear();
	        
	        HttpSession session = httpServletRequest.getSession();
		   	Long companyCode = (Long)session.getAttribute(JUtils.HTTP_SESSION_COMPANY_CODE);
		   	
		   	if(companyCode != null){
		   		JCompany company =  companyService.findCompanyById(Long.valueOf(companyCode));
		   		
		   		if (company != null ){
		   			activity.setCompany(company);
		   		}
		   	}
		   	
	        activityService.persist(activity);
	        
	        JAuditLog auditlog = new JAuditLog(securityService.currentUser(), JUtils.AUDIT_MSG_CREATE_ACTIVITY,
	    			JEventCategory.PARAMETRAGE, 
	    			JEvent.NEW_ACTIVITY, 
	    			activity.getName(),
	    			httpServletRequest.getRemoteAddr());
	        
	        String eventDateAsString = JUtils.DATE_TIME_FORMAT.format(auditlog.getEventDate());
	        auditlog.setEventDateAsString(eventDateAsString);
	        auditlog.setIpAddress(httpServletRequest.getRemoteAddr());
	        auditlog.setHostClient(httpServletRequest.getRemoteHost());
	        auditlog.persist();
	        
	        logger.debug("create()- a new activity has been created with success !");
	        
	        return "redirect:/backoffice/activities/" + encodeUrlPathSegment(activity.getId().toString(), httpServletRequest);
	    }
	    
	 
	 /**
	  * Display the view for creating a new activity
	  * @param uiModel
	  * @return
	  */
	    @RequestMapping(value = "/create", params = "form", produces = "text/html")
	    public String createForm(Model uiModel) {
	        populateEditForm(uiModel, new JActivity());
	        return CREATE_VIEW;
	    }
	    
	    /**
	     * Display the details's view
	     * @param id
	     * @param uiModel
	     * @return
	     * @throws ActivityNotFoundException 
	     */
	    @RequestMapping(value = "/{id}", produces = "text/html")
	    public String show(@PathVariable("id") Long id, Model uiModel) throws ActivityNotFoundException {
	    	
	    	JActivity activity = activityService.findActivity(id);
	    	
	    	if(activity == null){
	    		String errMsg = ">>>> show() - Failed finding Activity with id : " + id  ;
	   	   	 logger.error(errMsg);
	   	   	 throw new ActivityNotFoundException("Aucune activite avec l'identifiant [ "+id+" ] au sein de votre compagnie "); 
	    	}
	    	
	        uiModel.addAttribute("activity", activity);
	        uiModel.addAttribute("itemId", id);
	        
	        return SHOW_VIEW;
	    }
	    
	    
	    /**
	     * Display the view of all Activities
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
	            
	            uiModel.addAttribute("activities", activityService.findActivityEntries(firstResult, sizeNo));
	            float nrOfPages = (float) activityService.countActivities() / sizeNo;
	            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
	        
	    	} else {
	            uiModel.addAttribute("activities", activityService.findAllActivities());
	        }
	        return LIST_VIEW;
	    }
	    
	    /**
	     * Update an activity
	     * @param activity
	     * @param bindingResult
	     * @param uiModel
	     * @param httpServletRequest
	     * @return
	     */
	    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "text/html")
	    public String update(@Valid JActivity activity, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
	       
	    	if (bindingResult.hasErrors()) {
	            populateEditForm(uiModel, activity);
	            return UPDATE_VIEW;
	        }
	    	
	        uiModel.asMap().clear();
	        
	        HttpSession session = httpServletRequest.getSession();
		   	Long companyCode = (Long)session.getAttribute(JUtils.HTTP_SESSION_COMPANY_CODE);
		   	
		   	if(companyCode != null){
		   		JCompany company =  companyService.findCompanyById(Long.valueOf(companyCode));
		   		
		   		if (company != null ){
		   			activity.setCompany(company);
		   		}
		   	}
	        
	        
	        activityService.update(activity);
	        
	        JAuditLog auditlog = new JAuditLog(securityService.currentUser(), JUtils.AUDIT_MSG_UPDATE_ACTIVITY,
	    			JEventCategory.PARAMETRAGE, 
	    			JEvent.UPDATE_ACTIVITY, 
	    			activity.getName(),
	    			httpServletRequest.getRemoteAddr());
	        
	        String eventDateAsString = JUtils.DATE_TIME_FORMAT.format(auditlog.getEventDate());
	        auditlog.setEventDateAsString(eventDateAsString);
	        auditlog.setIpAddress(httpServletRequest.getRemoteAddr());
	        auditlog.setHostClient(httpServletRequest.getRemoteHost());
	        auditlog.persist();
	        
	        logger.debug("update()- the activity has been updated with success !");
	        
	        return "redirect:/backoffice/activities/" + encodeUrlPathSegment(activity.getId().toString(), httpServletRequest);
	    }
	    
	    /**
	     * display the update view for given id
	     * @param id
	     * @param uiModel
	     * @return
	     * @throws ActivityNotFoundException 
	     */
	    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
	    public String updateForm(@PathVariable("id") Long id, Model uiModel) throws ActivityNotFoundException {
	        
	    	JActivity activity = activityService.findActivity(id);
	    	
			if(activity == null){
	    		String errMsg = ">>>> show() - Failed finding Activity with id : " + id  ;
	   	   	 logger.error(errMsg);
	   	   	 throw new ActivityNotFoundException("Aucune activite avec l'identifiant [ "+id+" ] au sein de votre compagnie "); 
	    	}
	    	
	    	populateEditForm(uiModel,activity);
	    	
	        return UPDATE_VIEW;
	    }
	    
	    
	    /**
	     * Check if the activity code is already used. It's an implementation of UNIQUELESS Pattern
	     * @param activityCode
	     * @return
	     */
	    @RequestMapping(value = "/checkcode", method = RequestMethod.POST, produces = "application/json")
	    public @ResponseBody boolean checkActivityCode(@RequestParam(value = "activityCode", required=true)String activityCode){
			
	    	if (activityCode == null){
	    		return false;
	    	}
	    	
	    	List<JActivity> activities = activityService.findActivitiesByCodeEquals(activityCode);
	    	
	    	if( activities.isEmpty()) {
	    		return true;
	    	}

	    	return false;
	    	
	    }
	    
	    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
	    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel, ServletRequest httpServletRequest) {
	        
	    	JActivity activity = activityService.findActivity(id);
	    	activityService.remove(activity.getId());
	    	
	        JAuditLog auditlog = new JAuditLog(securityService.currentUser(), JUtils.AUDIT_MSG_DELETE_ACTIVITY,
	    			JEventCategory.PARAMETRAGE, 
	    			JEvent.DELETE_ACTIVITY, 
	    			activity.getName(),
	    			httpServletRequest.getRemoteAddr());
	        
	        String eventDateAsString = JUtils.DATE_TIME_FORMAT.format(auditlog.getEventDate());
	        auditlog.setEventDateAsString(eventDateAsString);
	        auditlog.setIpAddress(httpServletRequest.getRemoteAddr());
	        auditlog.setHostClient(httpServletRequest.getRemoteHost());
	        auditlog.persist();
	        
	        uiModel.asMap().clear();
	        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
	        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
	        
	        return "redirect:/backoffice/activities/list";
	    }
	    
	    @RequestMapping(value = "/generate/activitylistreport", method = RequestMethod.GET)
	    public String generateActivityListReport(@RequestParam(value = "format", required = true) String format, Model uiModel) {
	        
	    	if ( null == format || format.length() <= 0 ) {
	                uiModel.addAttribute("error", "message_format_required");
	                return LIST_VIEW;
	                
	        }
	    	
	        final String REGEX = "(pdf|xls)";
	        
	        Pattern pattern = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
	        Matcher matcher = pattern.matcher(format);
	        
	        if ( !matcher.matches() ) {
	                uiModel.addAttribute("error", "message_format_invalid");
	                return LIST_VIEW;
	        }
	        
	        Collection<JActivity> dataSource = activityService.findAllActivities();
	        
	        if (dataSource.isEmpty()) {
	                uiModel.addAttribute("error", "message_emptyresults_noreportgeneration");
	                return LIST_VIEW;
	        }
	        uiModel.addAttribute("format", format);
	        uiModel.addAttribute("title", "ACTIVITYLISTREPORT");
	        uiModel.addAttribute("activitylistreportList", dataSource);
	        
	        return GENERATED_ACTIVITYDAILY_REPORT_VIEW;
	    }
	    
	    void populateEditForm(Model uiModel, JActivity activity) {
	        uiModel.addAttribute("activity", activity);
	        uiModel.addAttribute("companies", companyService.findAllCompanies());

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
