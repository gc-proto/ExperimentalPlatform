package ca.tbssct.ep.web;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import ca.tbssct.ep.EnvironmentCreator;
import ca.tbssct.ep.Util;

@Controller
public class EPVerificationController {

	public static final String REQUEST_PATH = Util.getRequestPath();
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping("/v-v")
	public ModelAndView handleTerms(String id) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("termsandconditions");
		mav.addObject("id", id);
		return mav;
	}

	@PostMapping("/termsAccepted")
	public View handleTermsAccepted(@ModelAttribute Terms terms) throws Exception {
		String id = terms.getRequestId();
		String path = REQUEST_PATH + id;
		try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(path)))) {
			EPRequest request = (EPRequest) decoder.readObject();
			logger.info("Request found and read..." + request.getExperimentName());
			if (Util.isDemoMode()) {
				new EnvironmentCreator().create(request);
			} else {
				
			}
			return new RedirectView("verified");
		} catch (FileNotFoundException e) {
			return Util.handleError("ERROR: " + e.getMessage(), id, logger);
		}
	}

	@GetMapping("verified")
	public String handleVerification() throws Exception {
		return "verified";
	}
}
