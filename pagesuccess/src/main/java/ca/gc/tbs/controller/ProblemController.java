package ca.gc.tbs.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ca.gc.tbs.domain.Problem;
import ca.gc.tbs.repository.ProblemRepository;

@Controller
public class ProblemController {

	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
	private static final Logger LOG = LoggerFactory.getLogger(ProblemController.class);
	public static final String COLLECTION_PROBLEM = "problem";

	@Autowired
	private ProblemRepository repository;

	@CrossOrigin(origins = "*")
	@PostMapping(value = "/addProblem")
	public @ResponseBody String addProblem(HttpServletRequest request) {

		try {
			Problem problem = new Problem(System.currentTimeMillis() + "", request.getParameter("url"),
					format.format(new Date()), request.getParameter("problem"), request.getParameter("problemDetails"),
					"Health Canada", request.getParameter("language"), "", "", "");
			repository.save(problem);
			return "Problem added.";
		} catch (Exception e) {
			return "Error:" + e.getMessage();
		}
	}

	@PostMapping(value = "/updateProblem")
	public @ResponseBody String updateProblem(HttpServletRequest request) {
		try {
			Optional<Problem> opt = repository.findById(request.getParameter("id"));
			Problem problem = opt.get();
			problem.setResolution(request.getParameter("resolution"));
			problem.setResolutionDate(format.format(new Date()));
			this.repository.save(problem);
			return problem.getResolutionDate();
		} catch (Exception e) {
			return "Error:" + e.getMessage();
		}
	}

	@GetMapping(value = "/deleteProblem")
	public @ResponseBody String deleteProblem(HttpServletRequest request) {
		try {
			this.repository.deleteById(request.getParameter("id"));
			return "deleted";
		} catch (Exception e) {
			return "Error:" + e.getMessage();
		}
	}

	public String getData() {

		String returnData = "";
		try {
			StringBuilder builder = new StringBuilder();
			List<Problem> problems = this.repository.findAll();
			for (Problem problem : problems) {
				builder.append("<tr><td>" + problem.getDepartment() + "</td>");
				builder.append("<td>" + problem.getLanguage() + "</td>");
				builder.append("<td>" + problem.getUrl() + "</td>");
				builder.append("<td>" + problem.getProblem() + "</td>");
				builder.append("<td>" + problem.getProblemDetails() + "</td>");
				builder.append("<td>" + problem.getProblemDate() + "</td>");
				try {
					builder.append("<td>" + problem.getResolution() + "</td>");
					builder.append("<td>" + problem.getResolutionDate() + "</td>");
				} catch (Exception e) {
					builder.append("<td></td>");
					builder.append("<td></td>");
				}
				builder.append("<td><div class='btn-group'><button id='resolve" + problem.getId()
						+ "' class='btn btn-xs resolveBtn'>Resolve</button><button id='delete" + problem.getId()
						+ "' class='btn btn-xs deleteBtn'>Delete</button></div></td>");
				builder.append("</tr>");
			}
			returnData = builder.toString();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return returnData;
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
