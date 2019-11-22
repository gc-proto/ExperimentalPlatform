package ca.tbssct.ep.web;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ca.tbssct.ep.EvironmentCreator;

@Controller
public class EPVerificationController {
	
		@GetMapping("/verification")
		public String greetingForm(String id) throws Exception {
			XMLDecoder decoder=null;
			try {
				decoder=new XMLDecoder(new BufferedInputStream(new FileInputStream(EPRequestController.SERVER+id)));
			} catch (FileNotFoundException e) {
				System.out.println("ERROR: File dvd.xml not found");
			}
			EPRequest request=(EPRequest)decoder.readObject();
			new EvironmentCreator().create("full", request.getDomainNamePrefix(), request.getPassword(), request.getEmailAddress());
		
			return "verified";
		}
}
