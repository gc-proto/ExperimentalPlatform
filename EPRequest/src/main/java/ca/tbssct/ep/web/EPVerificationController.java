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

	public static final String REQUEST_PATH = "/home/hyma/ExperimentalPlatform/requests/";

	@GetMapping("/verification")
	public String greetingForm(String id) throws Exception {
		XMLDecoder decoder = null;
		try {
			String path = REQUEST_PATH + id;
			decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(path)));
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File dvd.xml not found");
		}
		EPRequest request = (EPRequest) decoder.readObject();
		new EvironmentCreator().create("assignIP",request);

		return "verified";
	}
}
