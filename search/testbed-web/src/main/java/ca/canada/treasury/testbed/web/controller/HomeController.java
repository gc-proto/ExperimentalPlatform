package ca.canada.treasury.testbed.web.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {

    @GetMapping("/error")
    public String error(Model model) {
        return "error";
    }
    @GetMapping(value = "/")
    public RedirectView index() {
        return new RedirectView("/home", true);
    }

    @GetMapping(value= "/home")
    public String home() throws IOException {
        return "home";
    }
}