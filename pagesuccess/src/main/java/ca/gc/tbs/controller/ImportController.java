package ca.gc.tbs.controller;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import ca.gc.tbs.domain.Problem;
import ca.gc.tbs.repository.ProblemRepository;

@Controller
public class ImportController {

	@Autowired
	ProblemRepository problemRepository;

	// Mon May 18 2020 23:02:28 GMT+0000 (Coordinated Universal Time)
	SimpleDateFormat INPUT_FORMAT = new SimpleDateFormat("EEE MMM dd yyyy");
	SimpleDateFormat OUTPUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	@GetMapping(value = "/importcsv")
	public View importData() throws Exception {
		final Reader reader = new InputStreamReader(new URL(
				"https://docs.google.com/spreadsheets/d/1tTNrPJqKyNNkJo1UaCoSp1RMpSz3dJsRKmieDglSAOU/export?format=csv")
						.openConnection().getInputStream(),
				"UTF-8");
		final CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
		try {
			for (final CSVRecord record : parser) {
				try {
					if (record.get("Y/N").equals("No")) {
						Problem problem = new Problem();
						problem.setId(record.get("Ref Number").replace("/",""));
						INPUT_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
						String sDate = record.get("Date/time received");
						sDate = sDate.substring(0,sDate.indexOf("("));
						Date date = INPUT_FORMAT.parse(sDate);
						problem.setProblemDate(OUTPUT_FORMAT.format(date));
						problem.setTitle(record.get("Page Title"));
						problem.setUrl(record.get("Page URL"));
						problem.setProblem(record.get("What's wrong"));
						problem.setProblemDetails(record.get("Details"));
						problem.setTags(Arrays.asList(record.get("Topic").split(",")));
						problem.setResolution(record.get("Notes"));
						problem.setResolutionDate("");
						problem.setDepartment("Health");
						if (problem.getUrl().contains("/en/")) {
							problem.setLanguage("en");	
						} else {
							problem.setLanguage("fr");
						}
						
						this.problemRepository.save(problem);
						
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}

		} finally {
			parser.close();
			reader.close();
		}
		return new RedirectView("/dashboard");
	}
}
