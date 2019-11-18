package ca.tbssct.ep.web;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EPRequestController {

	@GetMapping("/request")
	public String greetingForm(Model model) {
		model.addAttribute("request", new Request());
		return "index";
	}

	@PostMapping("/requestPost")
	public String greetingSubmit(@ModelAttribute Request request) {
		return "result";
	}
}
