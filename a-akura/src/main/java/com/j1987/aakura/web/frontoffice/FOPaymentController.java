package com.j1987.aakura.web.frontoffice;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.j1987.aakura.domain.JActivity;
import com.j1987.aakura.domain.JAuditLog;
import com.j1987.aakura.domain.JEvent;
import com.j1987.aakura.domain.JEventCategory;
import com.j1987.aakura.domain.JPayment;
import com.j1987.aakura.domain.JUser;
import com.j1987.aakura.fwk.ActivityNotFoundException;
import com.j1987.aakura.fwk.AkuraGenericException;
import com.j1987.aakura.fwk.JUtils;
import com.j1987.aakura.fwk.PaymentNotFoundException;
import com.j1987.aakura.services.dao.ActivityService;
import com.j1987.aakura.services.dao.PaymentService;
import com.j1987.aakura.services.dao.UserService;
import com.j1987.aakura.services.security.JSecurityService;
import com.j1987.aakura.web.form.FilterPayment;
import com.j1987.aakura.web.form.PaymentForm;

@Controller
@RequestMapping(value = "/frontoffice/payments")
public class FOPaymentController {
	
	private static final String CREATE_VIEW = "frontoffice/payments/create";
	private static final String LIST_VIEW = "frontoffice/payments/list";
	private static final String UPDATE_VIEW = "frontoffice/payments/update";
	private static final String SHOW_VIEW = "frontoffice/payments/show";
	
	private static Logger logger = Logger.getLogger(FOPaymentController.class);
	
	@Autowired
	private ActivityService activityService;
	
	@Autowired
	JSecurityService securityService;
	
	@Autowired
	PaymentService paymentService;
	
	@Autowired
	UserService userService;
	

	public FOPaymentController() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	@RequestMapping(value = "/create", method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid PaymentForm paymentForm, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) throws ActivityNotFoundException {
        
		if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, paymentForm);
            logger.debug("create() - the paymentForm object is invalid. Redirect to create view");
            return CREATE_VIEW;
        }
		
        uiModel.asMap().clear();
        
        HttpSession session = httpServletRequest.getSession();
        String activityCode = null;
        
        if(paymentForm.getActivityCode() != null){
        	activityCode = paymentForm.getActivityCode();
        }else{
        	 
        	activityCode = (String)session.getAttribute(JUtils.HTTP_SESSION_ACTIVITY_CODE);
        }
        
        List<JActivity>  activities = activityService.findActivitiesByCodeEquals(activityCode);
        
        if(activities.isEmpty()){
    	    	String errMsg = ">>>> create() - Failed finding Activity with code : " + paymentForm.getActivityCode();
    	   	   	 logger.error(errMsg);
    	   	   	 throw new ActivityNotFoundException("Aucune activite avec le code [ "+ paymentForm.getActivityCode() +" ] au sein de votre activite "); 

        }
        
        JPayment payment =  new JPayment();
        
        JActivity activity = activities.get(0);
        
        payment.setActivity(activity);
        payment.setReference(paymentForm.getReference());
        payment.setAmount(paymentForm.getAmount());
        payment.setDescription(paymentForm.getDescription());
        
		payment.setCreatedBy(securityService.currentUser());
        payment.setCreationDate(new Date());
        
        try {
			Date paymentDate = JUtils.DATE_FORMAT.parse(paymentForm.getPaymentDate());
			
			if(paymentDate != null){
				payment.setPaymentDate(new Date());
			}
		} catch (ParseException e) {

			populateEditForm(uiModel, paymentForm);
			logger.debug("create() - message :"+e.getMessage());
			return CREATE_VIEW;
		}
        
        
        String auditMessage = JUtils.DB_PAYMENT_IN;
        if(paymentForm.getPaymentType().equals(JUtils.DB_PAYMENT_IN)){
        	payment.setPaymentType(JUtils.DB_PAYMENT_IN);
        }else if(paymentForm.getPaymentType().equals(JUtils.DB_PAYMENT_OUT)){
        	payment.setPaymentType(JUtils.DB_PAYMENT_OUT);
        	auditMessage = JUtils.DB_PAYMENT_OUT;
        }
        
        paymentService.persist(payment);
        
        JAuditLog auditlog = new JAuditLog(securityService.currentUser(), auditMessage,
    			JEventCategory.TRANSACTIONNEL, 
    			JEvent.NEW_PAYMENT,
    			payment.getActivity().getName(),
    			payment.getReference());
        
        String eventDateAsString = JUtils.DATE_TIME_FORMAT.format(auditlog.getEventDate());
        auditlog.setEventDateAsString(eventDateAsString);
        auditlog.setIpAddress(httpServletRequest.getRemoteAddr());
        auditlog.setHostClient(httpServletRequest.getRemoteHost());
        auditlog.persist();
        
        logger.debug("create()- a new payment has been created with success !");
        
        return "redirect:/frontoffice/payments/" + encodeUrlPathSegment(payment.getId().toString(), httpServletRequest);
    }
    
	
	
	
    @RequestMapping(value = "/create", params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
    	
    	PaymentForm paymentForm = new PaymentForm();
    	
    	String referenceGenerated = RandomStringUtils.random(8, true, true);
    	paymentForm.setReference(referenceGenerated.toUpperCase());
    	
        populateEditForm(uiModel, paymentForm);
        return CREATE_VIEW;
    }
    
    
    
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel, HttpServletRequest httpServletRequest) throws PaymentNotFoundException {
    	
    	HttpSession session = httpServletRequest.getSession();
    	String activityCode = (String)session.getAttribute(JUtils.HTTP_SESSION_ACTIVITY_CODE);
    	
    	if(activityCode != null){
    		JPayment payment = paymentService.findPayment(id);
    		
    		if(!payment.getActivity().getCode().equals(activityCode) || payment == null){
    			
    			String errMsg = ">>>> show() - Failed trying to access an other activity's payment with id : " + payment.getId();
   	   	   	 	logger.error(errMsg);
   	   	   	 	throw new PaymentNotFoundException("Aucun Flux Financier avec cet identifiant [ "+ payment.getId() +" ] au sein de votre activite "); 
    		}
    		
            uiModel.addAttribute("payment", payment);
            uiModel.addAttribute("itemId", id);
            
            return  SHOW_VIEW;
    	}
    	
       JPayment payment = paymentService.findPayment(id);

       if(payment == null){
    	   String errMsg = ">>>> show() - Failed trying to access an activity's payment with id : " + id;
   	   	 	logger.error(errMsg);
   	   	 	throw new PaymentNotFoundException("Aucun Flux Financier avec cet identifiant [ "+ id +" ] au sein de votre entreprise "); 
       }
       
       uiModel.addAttribute("payment", payment);
       uiModel.addAttribute("itemId", id);
        
        return  SHOW_VIEW;
    }
    
    
    
    
    @RequestMapping(value = "/list", produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel, HttpServletRequest httpServletRequest) throws ActivityNotFoundException {
        
    	HttpSession session = httpServletRequest.getSession();
    	String activityCode = (String)session.getAttribute(JUtils.HTTP_SESSION_ACTIVITY_CODE);
    	
    	List<JActivity> activities = new ArrayList<JActivity>();
    	
    	if(activityCode != null){
    		
    		activities = activityService.findActivitiesByCodeEquals(activityCode);
        	
            if(activities.isEmpty()){
    	    	String errMsg = ">>>> list() - Failed finding Activity with code : " + activityCode;
    	   	   	 logger.error(errMsg);
    	   	   	 throw new ActivityNotFoundException("Aucune activite avec le code [ "+ activityCode +" ] au sein de votre activite "); 

            }
    	}

        JActivity activity = null;
        
        if(!activities.isEmpty()){
        	activity = activities.get(0);
        }
    	
    	if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            
            List<JPayment> payments = new ArrayList<JPayment>();
            long nbr = 0;
            
            if(activityCode != null){
            	payments = paymentService.findPaymentsByActivityEqualsEntries(activity, firstResult, sizeNo);
            	nbr = paymentService.findAllPaymentsByActivityEquals(activity).size();
            }else{
            	payments = paymentService.findPaymentEntries(firstResult, sizeNo);
            	nbr = paymentService.countPayments();
            }
            
            uiModel.addAttribute("payments", payments);
            float nrOfPages = (float)  nbr / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
            
        } else {
        	List<JPayment> payments = new ArrayList<JPayment>();
        	
            if(activityCode != null){
            	payments = paymentService.findAllPaymentsByActivityEquals(activity);
            	
            }else{
            	payments = paymentService.findAllPayments();
            	
            }
        	
            uiModel.addAttribute("payments", payments);
        }
    	
        addDateTimeFormatPatterns(uiModel);
        
    	FilterPayment filterPayment = new FilterPayment();
    	uiModel.addAttribute("filterPayment", filterPayment);
    	uiModel.addAttribute("activities", activityService.findAllActivities());
        
        return LIST_VIEW;
    }
    
    
    @Secured(value = {"ROLE_ADMIN","ROLE_SUPERVISOR"})
    @RequestMapping(value = "/list/filter", produces = "text/html")
    public String listByFilter(@RequestParam(value = "activityFilter", required = false)String activityFilter, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) throws ActivityNotFoundException{
    	
    	if(activityFilter == null){
    		
    		String errMsg = ">>>> listByFilter() - Failed finding Activity with code : " + activityFilter;
	   	   	 logger.error(errMsg);
	   	   	 throw new ActivityNotFoundException("Aucune activite avec le code [ "+ activityFilter +" ] au sein de votre activite "); 
    	}
    	
    	List<JActivity> activities = activityService.findActivitiesByCodeEquals(activityFilter);
    	
        if(activities.isEmpty()){
	    	String errMsg = ">>>> listByFilter() - Failed finding Activity with code : " + activityFilter;
	   	   	 logger.error(errMsg);
	   	   	 throw new ActivityNotFoundException("Aucune activite avec le code [ "+ activityFilter +" ] au sein de votre activite "); 

        }
    	
        JActivity activity = activities.get(0);
        
    	if (page != null || size != null) {
    		int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("payments", paymentService.findPaymentsByActivityEqualsEntries(activity, firstResult, sizeNo));
            float nrOfPages = (float) paymentService.findAllPaymentsByActivityEquals(activity).size() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
            
    	}else {
            uiModel.addAttribute("payments", paymentService.findAllPaymentsByActivityEquals(activity));
        }
    	
    	uiModel.addAttribute("activities", activityService.findAllActivities());
    	
    	FilterPayment filterPayment = new FilterPayment();
    	if(activityFilter != null){
    		filterPayment.setActivityFilter(activityFilter);
    	}
    	
    	uiModel.addAttribute("filterPayment", filterPayment);
    	
    	return LIST_VIEW;
    }
    
    
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "text/html")
    public String update(@Valid PaymentForm paymentForm, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) throws ActivityNotFoundException, PaymentNotFoundException {
        
		if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, paymentForm);
            logger.debug("update() - the paymentForm object is invalid. Redirect to create view");
            return CREATE_VIEW;
        }
		
        uiModel.asMap().clear();
        
        HttpSession session = httpServletRequest.getSession();
        String activityCode = null;
        
        if(paymentForm.getActivityCode() != null){
        	activityCode = paymentForm.getActivityCode();
        }else{
        	 
        	activityCode = (String)session.getAttribute(JUtils.HTTP_SESSION_ACTIVITY_CODE);
        }
        
        List<JActivity>  activities = activityService.findActivitiesByCodeEquals(activityCode);
        
        if(activities.isEmpty()){
    	    	String errMsg = ">>>> update() - Failed finding Activity with code : " + paymentForm.getActivityCode();
    	   	   	 logger.error(errMsg);
    	   	   	 throw new ActivityNotFoundException("Aucune activite avec le code [ "+ paymentForm.getActivityCode() +" ] au sein de votre activite "); 

        }
        
        JPayment payment =  paymentService.findPayment(paymentForm.getId());
        
        if(payment == null){
     	   String errMsg = ">>>> update() - Failed trying to access an activity's payment ";
    	   	 	logger.error(errMsg);
    	   	 	throw new PaymentNotFoundException("Aucun Flux Financier avec cet identifiant au sein de votre entreprise "); 
        }
        
        JActivity activity = activities.get(0);
        
        payment.setActivity(activity);
        payment.setAmount(paymentForm.getAmount());
        payment.setDescription(paymentForm.getDescription());
        
		payment.setModificatedBy(securityService.currentUser());
        payment.setModificationDate(new Date());
        
        try {
			Date paymentDate = JUtils.DATE_FORMAT.parse(paymentForm.getPaymentDate());
			
			if(paymentDate != null){
				payment.setPaymentDate(new Date());
			}
		} catch (ParseException e) {

			populateEditForm(uiModel, paymentForm);
			logger.debug("create() - message :"+e.getMessage());
			return UPDATE_VIEW;
		}
        
        
        String auditMessage = JUtils.AUDIT_MSG_UPDATE_PAYMENT_IN;
        if(paymentForm.getPaymentType().equals(JUtils.DB_PAYMENT_IN)){
        	payment.setPaymentType(JUtils.DB_PAYMENT_IN);
        }else if(paymentForm.getPaymentType().equals(JUtils.DB_PAYMENT_OUT)){
        	payment.setPaymentType(JUtils.DB_PAYMENT_OUT);
        	auditMessage = JUtils.AUDIT_MSG_UPDATE_PAYMENT_OUT;
        }
        
        paymentService.update(payment);
        
        JAuditLog auditlog = new JAuditLog(securityService.currentUser(), auditMessage,
    			JEventCategory.TRANSACTIONNEL, 
    			JEvent.UPDATE_PAYMENT,
    			payment.getActivity().getName(),
    			payment.getReference());
        
        String eventDateAsString = JUtils.DATE_TIME_FORMAT.format(auditlog.getEventDate());
        auditlog.setEventDateAsString(eventDateAsString);
        auditlog.setIpAddress(httpServletRequest.getRemoteAddr());
        auditlog.setHostClient(httpServletRequest.getRemoteHost());
        auditlog.persist();
        
        logger.debug("update()- a payment has been updated with success !");
        
        return "redirect:/frontoffice/payments/" + encodeUrlPathSegment(payment.getId().toString(), httpServletRequest);
    }
    
    
    
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel, HttpServletRequest httpServletRequest) throws AkuraGenericException {
        
    	HttpSession session = httpServletRequest.getSession();
    	String activityCode = (String)session.getAttribute(JUtils.HTTP_SESSION_ACTIVITY_CODE);
    	
    	if(activityCode != null){
    		JPayment payment = paymentService.findPayment(id);
    		
    		if(!payment.getActivity().getCode().equals(activityCode) || payment == null){
    			
    			String errMsg = ">>>> updateForm() - Failed trying to access an other activity's payment with id : " + payment.getId();
   	   	   	 	logger.error(errMsg);
   	   	   	 	throw new PaymentNotFoundException("Aucun Flux Financier avec cet identifiant [ "+ payment.getId() +" ] au sein de votre activite "); 
    		}
    		
    		
	       if( !compareDates(payment.getCreationDate())){
    	    	   String errorMessage = ">>>> updateForm() - Failed trying to access an activity's payment which payment date before current date : " + payment.getCreationDate() +" and today "+ new Date();
    	  	   	 	logger.error(errorMessage);
    	  	   	 	throw new AkuraGenericException("Vous n'avez pas les droits requis pour modifier ou supprimer ce paiement. Veuillez contacter un administrateur du systeme ou un superviseur pour cette tache!");
	       }
    		
	    	PaymentForm paymentForm = new PaymentForm();
	    	
	    	paymentForm.setActivityCode(payment.getActivity().getCode());
	    	paymentForm.setAmount(payment.getAmount());
	    	paymentForm.setDescription(payment.getDescription());
	    	paymentForm.setPaymentType(payment.getPaymentType());
	    	paymentForm.setPaymentDate(payment.getDescription());
	    	paymentForm.setReference(payment.getReference());
	    	paymentForm.setId(payment.getId());
	    	
	    	populateEditForm(uiModel,paymentForm );
            
            return  UPDATE_VIEW;
    	}
    	
       JPayment payment = paymentService.findPayment(id);

       if(payment == null){
    	   String errMsg = ">>>> updateForm() - Failed trying to access an activity's payment with id : " + id;
   	   	 	logger.error(errMsg);
   	   	 	throw new PaymentNotFoundException("Aucun Flux Financier avec cet identifiant [ "+ id +" ] au sein de votre entreprise "); 
       }

    	PaymentForm paymentForm = new PaymentForm();
    	
    	paymentForm.setActivityCode(payment.getActivity().getCode());
    	paymentForm.setAmount(payment.getAmount());
    	paymentForm.setDescription(payment.getDescription());
    	paymentForm.setPaymentType(payment.getPaymentType());
    	paymentForm.setPaymentDate(payment.getDescription());
    	paymentForm.setReference(payment.getReference());
    	paymentForm.setId(payment.getId());
    	
    	populateEditForm(uiModel,paymentForm );
        return UPDATE_VIEW;
        
    }
    
    private boolean compareDates(Date creationDate){
    	
    	boolean valid = false;

    	if(DateUtils.isSameDay(creationDate, new Date())){
    		valid = true;
    	}
    	
    	return valid;
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel, HttpServletRequest httpServletRequest) throws PaymentNotFoundException {
        
        JPayment payment = paymentService.findPayment(id);

        if(payment == null){
     	   String errMsg = ">>>> delete() - Failed trying to access an activity's payment with id : " + id;
    	   	 	logger.error(errMsg);
    	   	 	throw new PaymentNotFoundException("Aucun Flux Financier avec cet identifiant [ "+ id +" ] au sein de votre entreprise "); 
        }
        
        HttpSession session = httpServletRequest.getSession();
    	String activityCode = (String)session.getAttribute(JUtils.HTTP_SESSION_ACTIVITY_CODE);
    	
    	if(activityCode != null){
        
	       if( !compareDates(payment.getCreationDate())){
		    	   String errorMessage = ">>>> delete() - Failed trying to access an activity's payment which payment date before current date : " + payment.getCreationDate() +" and today "+ new Date();
		  	   	 	logger.error(errorMessage);
		  	   	 	throw new PaymentNotFoundException("Vous n'avez pas les droits requis pour modifier ou supprimer ce paiement. Veuillez contacter un administrateur du systeme ou un superviseur pour cette tache!");
	       }
	       
    	}
        
        String auditMessage = JUtils.AUDIT_MSG_DELETE_PAYMENT_IN;

       if(payment.getPaymentType().equals(JUtils.DB_PAYMENT_OUT)){
        	auditMessage = JUtils.AUDIT_MSG_DELETE_PAYMENT_OUT;
        }
       
       JAuditLog auditlog = new JAuditLog(securityService.currentUser(), auditMessage,
   			JEventCategory.TRANSACTIONNEL, 
   			JEvent.DELETE_PAYMENT,
   			payment.getActivity().getName(),
   			payment.getReference());
       
        
        paymentService.remove(payment.getId());
        

        String eventDateAsString = JUtils.DATE_TIME_FORMAT.format(auditlog.getEventDate());
        auditlog.setEventDateAsString(eventDateAsString);
        auditlog.setIpAddress(httpServletRequest.getRemoteAddr());
        auditlog.setHostClient(httpServletRequest.getRemoteHost());
        auditlog.persist();
        
        logger.debug("delete()- a payment has been removed from the database with success !");
        
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        
        return "redirect:/frontoffice/payments/list";
    }
    
    
    
    
    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("JPayment__creationdate_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
        uiModel.addAttribute("JPayment__paymentdate_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
        uiModel.addAttribute("JPayment__modificationdate_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
    }
    
    
    
    
    void populateEditForm(Model uiModel, PaymentForm paymentForm) {
    	
    	List<String> paymentTypes = new ArrayList<String>();
    	
    	paymentTypes.add(JUtils.DB_PAYMENT_IN);
    	paymentTypes.add(JUtils.DB_PAYMENT_OUT);
    	
    	List<JUser> users = userService.findUserByUserNameEquals(securityService.currentUser());
    	if(! users.isEmpty()){
    		JUser user = users.get(0);
    		
    		if(user.getRoleName().equals(JUtils.DB_UI_ROLE_ADMIN) || user.getRoleName().equals(JUtils.DB_UI_ROLE_SUPERVISOR)){
    			List<JActivity> activities = activityService.findAllActivities();
    			uiModel.addAttribute("activities", activities);
    			
    		}
    	}
    	
    	uiModel.addAttribute("paymentTypes", paymentTypes);
        uiModel.addAttribute("paymentForm", paymentForm);
        addDateTimeFormatPatterns(uiModel);
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
