package ca.tbssct.ep.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import ca.tbssct.ep.Util;

@Controller
public class ErrorController {

	@GetMapping("e")
	public ModelAndView error() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("adminEmail", Util.getAdminEmail());
		mav.setViewName("error");
		return mav;
	}
	
}
