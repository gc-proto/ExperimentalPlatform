package ca.tbssct.ep.web;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import ca.tbssct.ep.EvironmentCreator;

@Controller
public class EPVerificationController {

	public static final String REQUEST_PATH = "/home/requests/";
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping("/verification")
	public RedirectView handleVerification(String id) throws Exception {
		XMLDecoder decoder = null;
		try {
			String path = REQUEST_PATH + id;
			decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(path)));
		} catch (FileNotFoundException e) {
			logger.error("ERROR: "+e.getMessage());
		}
		EPRequest request = (EPRequest) decoder.readObject();
		logger.info("Request found and read...");
		new EvironmentCreator().create(request);

		return new RedirectView("verified");
	}

	@GetMapping("verified")
	public String handleVerification() throws Exception {
		return "verified";
	}
}
