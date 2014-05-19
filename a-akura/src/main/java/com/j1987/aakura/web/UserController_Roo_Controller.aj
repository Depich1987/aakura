// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.j1987.aakura.web;

import com.j1987.aakura.domain.JActivity;
import com.j1987.aakura.domain.JCompany;
import com.j1987.aakura.domain.JRole;
import com.j1987.aakura.domain.JUser;
import com.j1987.aakura.web.UserController;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect UserController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String UserController.create(@Valid JUser JUser_, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, JUser_);
            return "jusers/create";
        }
        uiModel.asMap().clear();
        JUser_.persist();
        return "redirect:/jusers/" + encodeUrlPathSegment(JUser_.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", produces = "text/html")
    public String UserController.createForm(Model uiModel) {
        populateEditForm(uiModel, new JUser());
        return "jusers/create";
    }
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String UserController.show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("juser_", JUser.findJUser(id));
        uiModel.addAttribute("itemId", id);
        return "jusers/show";
    }
    
    @RequestMapping(produces = "text/html")
    public String UserController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("jusers", JUser.findJUserEntries(firstResult, sizeNo));
            float nrOfPages = (float) JUser.countJUsers() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("jusers", JUser.findAllJUsers());
        }
        return "jusers/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String UserController.update(@Valid JUser JUser_, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, JUser_);
            return "jusers/update";
        }
        uiModel.asMap().clear();
        JUser_.merge();
        return "redirect:/jusers/" + encodeUrlPathSegment(JUser_.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String UserController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, JUser.findJUser(id));
        return "jusers/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String UserController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        JUser JUser_ = JUser.findJUser(id);
        JUser_.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/jusers";
    }
    
    void UserController.populateEditForm(Model uiModel, JUser JUser_) {
        uiModel.addAttribute("JUser_", JUser_);
        uiModel.addAttribute("jactivitys", JActivity.findAllJActivitys());
        uiModel.addAttribute("jcompanys", JCompany.findAllJCompanys());
        uiModel.addAttribute("jroles", JRole.findAllJRoles());
    }
    
    String UserController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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