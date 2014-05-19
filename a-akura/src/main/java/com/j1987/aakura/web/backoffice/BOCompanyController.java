package com.j1987.aakura.web.backoffice;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.j1987.aakura.domain.JAuditLog;
import com.j1987.aakura.domain.JCompany;
import com.j1987.aakura.domain.JEvent;
import com.j1987.aakura.domain.JEventCategory;
import com.j1987.aakura.fwk.JUtils;
import com.j1987.aakura.services.dao.CompanyService;
import com.j1987.aakura.services.security.JSecurityService;

/**
 * An Implementation of company controller.It's used for general settings because there will be only one company
 * @author Franck Janel AGAH
 *
 */
@Controller
@RequestMapping(value = "/backoffice/generalsettings")
public class BOCompanyController{
	
	private static final String UPDATE_VIEW = "backoffice/generalsettings/update";
	private static final String SHOW_VIEW = "backoffice/generalsettings/show";
	
	private static Logger logger = Logger.getLogger(BOCompanyController.class);
	
	@Autowired
	CompanyService companyService;
	
	@Autowired
	JSecurityService securityService;
	
	public BOCompanyController() {
		// TODO Auto-generated constructor stub
	}
	
	
    @RequestMapping(value = "/details", produces = "text/html")
    public String show(Model uiModel) {
    	
    	JCompany company = companyService.findCompanyById(Long.valueOf("1"));
        uiModel.addAttribute("company", company);
        uiModel.addAttribute("itemId", company.getId());
        
        return SHOW_VIEW;
    }
    
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "text/html")
    public String update(@Valid JCompany company, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
       
    	if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, company);
            return UPDATE_VIEW;
        }
    	
        uiModel.asMap().clear();
        companyService.update(company);
        
        JAuditLog auditlog = new JAuditLog(securityService.currentUser(), JUtils.AUDIT_MSG_UPDATE_COMPANY,
    			JEventCategory.PARAMETRAGE, 
    			JEvent.UPDATE_COMPANY, 
    			company.getName(),
    			httpServletRequest.getRemoteAddr());
        
        String eventDateAsString = JUtils.DATE_TIME_FORMAT.format(auditlog.getEventDate());
        auditlog.setEventDateAsString(eventDateAsString);
        
        auditlog.setIpAddress(httpServletRequest.getRemoteAddr());
        auditlog.setHostClient(httpServletRequest.getRemoteHost());
        
        auditlog.persist();
        
        logger.debug("update()- the company's details has been updated!");
        
        return "redirect:/backoffice/generalsettings/details";
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        
    	populateEditForm(uiModel, companyService.findCompanyById(id));
        return UPDATE_VIEW;
    }
    
    void populateEditForm(Model uiModel, JCompany company) {
        uiModel.addAttribute("company", company);
        
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