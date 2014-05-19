package com.j1987.aakura.web.backoffice;

import java.text.ParseException;
import java.util.Date;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.j1987.aakura.domain.JAuditLog;
import com.j1987.aakura.domain.JEventCategory;
import com.j1987.aakura.fwk.JUtils;
import com.j1987.aakura.web.form.AuditLogForm;

@Controller
@RequestMapping(value = "/backoffice/auditlogs")
public class BOAuditLogController {
	
	private static final String ALLLIST_VIEW = "backoffice/auditlogs/list";
	private static final String CONNECTION_LIST_VIEW = "backoffice/auditlogs/connections/list";
	private static final String SETTING_LIST_VIEW = "backoffice/auditlogs/settings/list";
	private static final String TRANSACTION_LIST_VIEW = "backoffice/auditlogs/transactions/list";
	
	private static Logger logger = Logger.getLogger(BOAuditLogController.class);

	public BOAuditLogController() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Display all {@link JAuditLog} type CONNEXION
	 * @param auditLogForm
	 * @param page
	 * @param size
	 * @param uiModel
	 * @return
	 */
	@RequestMapping(value = "/connections/list", produces = "text/html")
    public String listConnectionsAuditLogs(@Valid AuditLogForm auditLogForm, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		
		boolean hasErrors = parseAuditLogForm(auditLogForm, JEventCategory.CONNEXION, uiModel, page, size);
		if (hasErrors) {
	        return CONNECTION_LIST_VIEW;
		}
		populateAuditLog(auditLogForm, JEventCategory.CONNEXION, uiModel, "auditconnections", page, size);

        return CONNECTION_LIST_VIEW;
    }
	
	
	/**
	 * Display all {@link JAuditLog} type SETTINGS
	 * @param auditLogForm
	 * @param page
	 * @param size
	 * @param uiModel
	 * @return
	 */
	@RequestMapping(value = "/settings/list", produces = "text/html")
    public String listSettingsAuditLogs(@Valid AuditLogForm auditLogForm, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		
		boolean hasErrors = parseAuditLogForm(auditLogForm, JEventCategory.PARAMETRAGE, uiModel, page, size);
		if (hasErrors) {
	        return SETTING_LIST_VIEW;
		}
		populateAuditLog(auditLogForm, JEventCategory.PARAMETRAGE, uiModel, "auditsettings", page, size);

        return SETTING_LIST_VIEW;
    }
	
	@RequestMapping(value = "/transactions/list", produces = "text/html")
    public String listTransactionsAuditLogs(@Valid AuditLogForm auditLogForm, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		
		boolean hasErrors = parseAuditLogForm(auditLogForm, JEventCategory.TRANSACTIONNEL, uiModel, page, size);
		if (hasErrors) {
	        return TRANSACTION_LIST_VIEW;
		}
		populateAuditLog(auditLogForm, JEventCategory.TRANSACTIONNEL, uiModel, "audittransctions", page, size);

        return TRANSACTION_LIST_VIEW;
    }
	
        
/**
 * 
 * */
private boolean parseAuditLogForm(AuditLogForm auditLogForm, JEventCategory eventCategory, Model uiModel, Integer page, Integer size) {
	
	boolean hasParseErrors = false;
	String sDate =  auditLogForm.getStartDateAsString();
	String eDate =  auditLogForm.getEndDateAsString();
	try {
		
		if (!StringUtils.isEmpty(sDate)) {
			Date startDate = JUtils.DATE_FORMAT.parse(StringUtils.trim(sDate));
			auditLogForm.setStartDate(startDate);
		}
		// Include endDate 
		if (!StringUtils.isEmpty(eDate)) {
			Date endDate = JUtils.DATE_FORMAT.parse(StringUtils.trim(eDate));
			if (endDate != null) {
				DateTime dateTime = new DateTime(endDate);
				dateTime = dateTime.plusDays(1);				
				auditLogForm.setEndDate(dateTime.toDate());
			}
		}
		
	} catch (ParseException e) {
		logger.error(">>> Failed parsing date.", e);
        uiModel.addAttribute("error_msg", "Saisie invalide. Le format de date est incorrect.");
		populateAuditLog(new AuditLogForm(), eventCategory, uiModel, "auditconnections", page, size);
		hasParseErrors = true;
	}
	
	return hasParseErrors;
}

/**
 * 
 * */
private void populateAuditLog(AuditLogForm auditLogForm, JEventCategory eventCategory, Model uiModel, String currentNav, Integer page, Integer size) {

    if (page != null || size != null) {
        int sizeNo = size == null ? 10 : size.intValue();
        final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
        uiModel.addAttribute("dtauditlogs", JAuditLog
        		.findJAuditLogsByEventCategory(eventCategory,firstResult, sizeNo, auditLogForm.getStartDate(), auditLogForm.getEndDate()));
        float nrOfPages = (float) JAuditLog.countJAuditLogs(eventCategory) / sizeNo;
        uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
    } else {
        uiModel.addAttribute("dtauditlogs", JAuditLog.findAllJAuditLogs(eventCategory));
    }
    
    String sDate = auditLogForm.getStartDateAsString();
    String eDate = auditLogForm.getEndDateAsString();
    
    if ( !StringUtils.isEmpty(sDate)) {
        uiModel.addAttribute("startDateAsString", sDate);
	}
    if ( !StringUtils.isEmpty(sDate)) {
        uiModel.addAttribute("endDateAsString", eDate);
	}
    
    uiModel.addAttribute("startDate", auditLogForm.getStartDate());
    uiModel.addAttribute("endDate", auditLogForm.getEndDate());
    
//    uiModel.addAttribute("report_formats", DTUtils.reportFormatsList());
    uiModel.addAttribute("auditLogForm", new AuditLogForm());
    uiModel.addAttribute("currentNav", currentNav);
	
}

}
