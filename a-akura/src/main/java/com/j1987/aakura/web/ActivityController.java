package com.j1987.aakura.web;
import com.j1987.aakura.domain.JActivity;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.gvnix.web.report.roo.addon.GvNIXReports;

@RequestMapping("/jactivitys")
@Controller
@RooWebScaffold(path = "jactivitys", formBackingObject = JActivity.class)
//@GvNIXReports({ "activitylistreport|pdf,xls" })
public class ActivityController {
}
