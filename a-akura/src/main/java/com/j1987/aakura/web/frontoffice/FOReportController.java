package com.j1987.aakura.web.frontoffice;

import java.math.MathContext;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import net.sf.jasperreports.engine.JRParameter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.j1987.aakura.domain.JActivity;
import com.j1987.aakura.domain.JPayment;
import com.j1987.aakura.domain.JUser;
import com.j1987.aakura.fwk.JUtils;
import com.j1987.aakura.services.dao.ActivityService;
import com.j1987.aakura.services.dao.PaymentService;
import com.j1987.aakura.services.dao.UserService;
import com.j1987.aakura.services.security.JSecurityService;
import com.j1987.aakura.web.form.ReportFilterForm;
import com.j1987.aakura.web.statistics.ActivityStatistics;

@Controller
@RequestMapping(value = "/frontoffice/reports")
public class FOReportController {
	
	private static final String ACTIVITYREPORTPARAMS_VIEW = "frontoffice/reports/activityreportparams";
	private static final String ACTIVITYREPORT_VIEW = "frontoffice/reports/activityreport";
	
	/**
	 * Report views to generate PDF/XLS files
	 * */ 
    private static final String GENERATED_ACTIVITYREPORT_VIEW = "payment_activityreport"; 

	
	private static Logger logger = Logger.getLogger(FOReportController.class);
	
	@Autowired
	private ActivityService activityService;
	
	@Autowired
	JSecurityService securityService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	PaymentService paymentService;

	public FOReportController() {
		// TODO Auto-generated constructor stub
	}
	
	
	@RequestMapping(value = "/activityreport",  method = RequestMethod.GET , produces = "text/html")
	public String activityReportFilter(@Valid ReportFilterForm reportFilterForm,
			BindingResult bindingResult, 
			Model uiModel,
			 @RequestParam(value = "page", required = false) Integer page,
			 @RequestParam(value = "size", required = false) Integer size,
			HttpServletRequest httpServletRequest){
		
    	if (bindingResult.hasErrors()) {
    		populateEditActivityReportForm(uiModel, reportFilterForm);
    		logger.debug("activityReportFilter() - the reportFilterForm object is invalid. Redirect to filterform view");
            return ACTIVITYREPORTPARAMS_VIEW;
        }
    	
    	uiModel.asMap().clear();
    	
    	Date startDateFormatted;
    	Date endDateFormatted;
    	
		try {
			startDateFormatted = JUtils.DATE_FORMAT.parse(reportFilterForm.getStartDate());
			endDateFormatted = JUtils.DATE_FORMAT.parse(reportFilterForm.getEndDate());
			
		} catch (ParseException e) {
			
    		populateEditActivityReportForm(uiModel, reportFilterForm);
    		logger.debug("activityReportFilter() - the reportFilterForm date format is invalid. Redirect to filterform view"+e.getMessage());
            return ACTIVITYREPORTPARAMS_VIEW;
//			e.printStackTrace();
		}
    	
    	
    	List<String> paymentTypes = paymentTypeByReportFilterForm(reportFilterForm);
    	List<JActivity> activities = activityService.findActivitiesByActivityCodeList(reportFilterForm.getActivityCodeList());
    	
    	
    	if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            
            List<JPayment> payments = paymentService.findPaymentsByActivityListAndPaymentDateBetween(activities, paymentTypes,startDateFormatted, endDateFormatted);
            
            ActivityStatistics statistics = new ActivityStatistics();
            statistics.setSelectedActivities(JUtils.retrieveObjectPropertiesAsString(activities));
            
        	uiModel.addAttribute("statistics",getActivityStatistics(statistics,payments));
            
            uiModel.addAttribute("payments", paymentService.findPaymentsByActivityListAndPaymentDateBetweenEntries(activities, paymentTypes,startDateFormatted, endDateFormatted, firstResult, sizeNo));
            float nrOfPages = (float)  payments.size() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
            
        } else {
        	
        	List<JPayment> payments = paymentService.findPaymentsByActivityListAndPaymentDateBetween(activities, paymentTypes,startDateFormatted, endDateFormatted);
           
        	ActivityStatistics statistics = new ActivityStatistics();
            statistics.setSelectedActivities(JUtils.retrieveObjectPropertiesAsString(activities));
            
        	uiModel.addAttribute("statistics",getActivityStatistics(statistics,payments));
        	uiModel.addAttribute("payments", payments);
        }
    	
    	String[] reportFormats =  {"pdf","xls"};
        Collection<String> reportFormatsList = Arrays.asList(reportFormats);
        uiModel.addAttribute("report_formats", reportFormatsList);
        
        uiModel.addAttribute("reportFilterForm", reportFilterForm);
        

        
        
        

		return ACTIVITYREPORT_VIEW;
		
	}

	@RequestMapping(value = "/activityreportparams",  params = "form", produces = "text/html")
	public String activityReportFilterForm( Model uiModel, HttpServletRequest httpServletRequest){
		
		populateEditActivityReportForm(uiModel, new ReportFilterForm());
		
		return ACTIVITYREPORTPARAMS_VIEW;
		
	}

	
	@RequestMapping(value = "/download/activityreport",  method = RequestMethod.GET , produces = "text/html")
	public String generateActivityReportFilter(
			@Valid ReportFilterForm reportFilterForm,
//			@RequestParam(value = "format", required = true) String format,
			BindingResult bindingResult, 
			Model uiModel, HttpServletRequest httpServletRequest){
		
    	if (bindingResult.hasErrors()) {
    		populateEditActivityReportForm(uiModel, reportFilterForm);
    		logger.debug("generateActivityReportFilter() - the reportFilterForm object is invalid. Redirect to filterform view");
            return ACTIVITYREPORT_VIEW;
        }
		
		
    	if ( null == reportFilterForm.getFormat() || reportFilterForm.getFormat().length() <= 0 ) {
            uiModel.addAttribute("error", "message_format_required");
            return ACTIVITYREPORT_VIEW;
            
    	}
	
    	final String REGEX = "(pdf|xls)";
    
	    Pattern pattern = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(reportFilterForm.getFormat());
    
	    if ( !matcher.matches() ) {
	            uiModel.addAttribute("error", "message_format_invalid");
	            return ACTIVITYREPORT_VIEW;
	    }
	    
	    uiModel.asMap().clear();
    	
    	Date startDateFormatted;
    	Date endDateFormatted;
    	
		try {
			startDateFormatted = JUtils.DATE_FORMAT.parse(reportFilterForm.getStartDate());
			endDateFormatted = JUtils.DATE_FORMAT.parse(reportFilterForm.getEndDate());
			
		} catch (ParseException e) {
			
    		populateEditActivityReportForm(uiModel, reportFilterForm);
    		logger.debug("activityReportFilter() - the reportFilterForm date format is invalid. Redirect to filterform view"+e.getMessage());
            return ACTIVITYREPORT_VIEW;
//			e.printStackTrace();
		}
    	
    	
    	List<String> paymentTypes = paymentTypeByReportFilterForm(reportFilterForm);
    	List<JActivity> activities = activityService.findActivitiesByActivityCodeList(reportFilterForm.getActivityCodeList());
		
    	Collection<JPayment> dataSource = new ArrayList<JPayment>();
    	
    	dataSource = paymentService.findPaymentsByActivityListAndPaymentDateBetween(activities, paymentTypes,startDateFormatted, endDateFormatted);
    	
        if (dataSource.isEmpty()) {
            uiModel.addAttribute("error", "message_emptyresults_noreportgeneration");
            return ACTIVITYREPORT_VIEW;
        }
        
        uiModel.addAttribute("activityNames",JUtils.retrieveObjectPropertiesAsString(activities));
        uiModel.addAttribute("startDate", reportFilterForm.getStartDate());
        uiModel.addAttribute("endDate", reportFilterForm.getEndDate());
        uiModel.addAttribute("format", reportFilterForm.getFormat());
        
        uiModel.addAttribute("title", "RAPPORT_ACTIVITE" +" "+ reportFilterForm.getStartDate() + " "+reportFilterForm.getEndDate());
        
        HttpSession session = httpServletRequest.getSession();
        String companyName = (String)session.getAttribute(JUtils.HTTP_SESSION_COMPANY_NAME);
        
        if(companyName != null){
        	uiModel.addAttribute("companyName", companyName);
        }
        
        uiModel.addAttribute("paymentActivityReportList", dataSource);
        
        Locale locale = new Locale("fr", "FR");
    	uiModel.addAttribute(JRParameter.REPORT_LOCALE, locale);
    	
    	return GENERATED_ACTIVITYREPORT_VIEW;
		
	}
	
    void populateEditActivityReportForm(Model uiModel, ReportFilterForm reportFilterForm) {
    	
    	List<JActivity> activities = new ArrayList<JActivity>();
    	
    	List<JUser> users = userService.findUserByUserNameEquals(securityService.currentUser());
    	if(! users.isEmpty()){
    		JUser user = users.get(0);
    		
    		if(user.getRoleName().equals(JUtils.DB_UI_ROLE_ADMIN) || user.getRoleName().equals(JUtils.DB_UI_ROLE_SUPERVISOR)){
    			activities =  activityService.findAllActivities();
    			
    		}
    		
    		if(user.getRoleName().equals(JUtils.DB_UI_ROLE_MANAGER)){
    			activities = user.getActivities();
    		}
    	}
        
    	uiModel.addAttribute("activities", activities);
        uiModel.addAttribute("reportFilterForm", reportFilterForm);

    }
    
    private List<String> paymentTypeByReportFilterForm(ReportFilterForm reportFilterForm){
    	List<String> paymentTypes = new ArrayList<String>();
    	
    	if(reportFilterForm.getPaymentIn()){
    		paymentTypes.add(JUtils.DB_PAYMENT_IN);
    	}
    	
    	if(reportFilterForm.getPaymentOut()){
    		paymentTypes.add(JUtils.DB_PAYMENT_OUT);
    	}
    	
    	return paymentTypes;
    }
    
    private ActivityStatistics getActivityStatistics( ActivityStatistics statistics, List<JPayment> payments){
    	
    	Long cptIn = Long.valueOf("0");
    	Long cptOut = Long.valueOf("0");
    	
    	for (JPayment jPayment : payments) {
			
    			if(jPayment.getPaymentType().equals(JUtils.DB_PAYMENT_IN)){
    				cptIn ++;
    				
    				statistics.setAggregatePaymentIn(statistics.getAggregatePaymentIn().add(jPayment.getAmount()));
    			}
    			
    			if(jPayment.getPaymentType().equals(JUtils.DB_PAYMENT_OUT)){
    				cptOut++ ;
    				
    				statistics.setAggregatePaymentOut(statistics.getAggregatePaymentOut().add(jPayment.getAmount()));
    			}
		}
    	
    	statistics.setCountPaymentIn(cptIn);
    	statistics.setCountPaymentOut(cptOut);
    	
    	statistics.setBalanceAmount(statistics.getAggregatePaymentIn().subtract(statistics.getAggregatePaymentOut(), new MathContext(2)));
    	return statistics;
    	
    }
}
