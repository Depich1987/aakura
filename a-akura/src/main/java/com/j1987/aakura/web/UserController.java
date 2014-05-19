package com.j1987.aakura.web;
import com.j1987.aakura.domain.JUser;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/jusers")
@Controller
@RooWebScaffold(path = "jusers", formBackingObject = JUser.class)
public class UserController {
}
