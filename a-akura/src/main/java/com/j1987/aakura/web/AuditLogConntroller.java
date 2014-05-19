package com.j1987.aakura.web;
import com.j1987.aakura.domain.JAuditLog;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/jauditlogs")
@Controller
@RooWebScaffold(path = "jauditlogs", formBackingObject = JAuditLog.class)
public class AuditLogConntroller {
}
