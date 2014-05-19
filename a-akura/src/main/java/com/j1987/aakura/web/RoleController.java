package com.j1987.aakura.web;
import com.j1987.aakura.domain.JRole;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/jroles")
@Controller
@RooWebScaffold(path = "jroles", formBackingObject = JRole.class)
public class RoleController {
}
