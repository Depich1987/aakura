package com.j1987.aakura.web;
import com.j1987.aakura.domain.JPayment;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/jpayments")
@Controller
@RooWebScaffold(path = "jpayments", formBackingObject = JPayment.class)
public class PaymentController {
}
