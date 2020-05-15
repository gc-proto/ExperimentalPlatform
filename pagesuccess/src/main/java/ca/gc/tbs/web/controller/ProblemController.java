package ca.gc.tbs.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProblemController {

	public static final String SOLR_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final SimpleDateFormat format = new SimpleDateFormat(SOLR_DATE_FORMAT);
	private static final Logger LOG = LoggerFactory.getLogger(ProblemController.class);
	public static final String COLLECTION_PROBLEM = "problem";

	@CrossOrigin(origins = "*")
	@PostMapping(value = "/addProblem")
	public @ResponseBody String addProblem(HttpServletRequest request) {
		try {
			return "Problem added.";
		} catch (Exception e) {
			return "Error:" + e.getMessage();
		}
	}

	@PostMapping(value = "/updateProblem")
	public @ResponseBody String updateProblem(HttpServletRequest request) {
		try {
			return "";
		} catch (Exception e) {
			return "Error:" + e.getMessage();
		}
	}

	@GetMapping(value = "/deleteProblem")
	public @ResponseBody String deleteProblem(HttpServletRequest request) {
		return "";
	}

	public String getData() throws Exception {
		StringBuilder builder = new StringBuilder();
		String xml = "";
		Document document = DocumentHelper.parseText(xml);
		List<Node> results = document.selectNodes("//doc");
		for (Node elem : results) {
			builder.append("<tr><td>" + elem.selectSingleNode("str[@name='department']").getText() + "</td>");
			builder.append("<td>" + elem.selectSingleNode("str[@name='language']").getText() + "</td>");
			builder.append("<td>" + elem.selectSingleNode("str[@name='url']").getText() + "</td>");
			builder.append("<td>" + elem.selectSingleNode("str[@name='problem']").getText() + "</td>");
			builder.append("<td>" + elem.selectSingleNode("str[@name='problemDetails']").getText() + "</td>");
			builder.append("<td>" + elem.selectSingleNode("date[@name='date']").getText() + "</td>");
			try {
				builder.append("<td>" + elem.selectSingleNode("str[@name='resolution']").getText() + "</td>");
				builder.append("<td>" + elem.selectSingleNode("date[@name='resolutionDate']").getText() + "</td>");
			} catch (Exception e) {
				builder.append("<td></td>");
				builder.append("<td></td>");
			}
			builder.append("<td><div class='btn-group'><button id='resolve"
					+ elem.selectSingleNode("str[@name='id']").getText()
					+ "' class='btn btn-xs resolveBtn'>Resolve</button><button id='delete"
					+ elem.selectSingleNode("str[@name='id']").getText()
					+ "' class='btn btn-xs deleteBtn'>Delete</button></div></td>");
			builder.append("</tr>");
		}
		return builder.toString();
	}

	@GetMapping(value = "/dashboard")
	public ModelAndView dashboard() throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.addObject("data", this.getData());
		mav.setViewName("problemDashboard");
		return mav;
	}

	@GetMapping(value = "/testForm")
	public String testForm() {
		return "testForm";
	}
}
