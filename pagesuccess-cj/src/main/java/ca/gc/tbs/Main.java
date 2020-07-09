package ca.gc.tbs;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import com.sybit.airtable.Airtable;
import com.sybit.airtable.Base;
import com.sybit.airtable.Table;

import ca.gc.tbs.domain.Problem;
import ca.gc.tbs.repository.ProblemRepository;

import static java.lang.System.exit;

@SpringBootApplication
@ComponentScan(basePackages = {"ca.gc.tbs.domain","ca.gc.tbs.repository"})
public class Main implements CommandLineRunner {

	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat INPUT_FORMAT = new SimpleDateFormat("EEE MMM dd yyyy");

	@Autowired
	private ProblemRepository problemRepository;
	
	@Value("${airtable.key}")
	private String airtableKey;
	
	@Value("${airtable.base}")
	private String airtableBase;
	
	@Value("${airtable.tab}")
	private String airtableTab;

	public static void main(String args[]) throws Exception {
		new SpringApplicationBuilder(Main.class).web(WebApplicationType.NONE) // .REACTIVE, .SERVLET
				.run(args);
	}

	public Main() throws Exception {

	}
	
	

	@Override
	public void run(String... args) throws Exception {
		//this.resetAirTableFlag();
		this.airTableSync();
	}
	
	public void resetAirTableFlag() {
		List<Problem> pList = this.problemRepository.findByAirTableSync("true");
		for (Problem problem : pList) {
			problem.setAirTableSync("false");
			this.problemRepository.save(problem);
		}
	}
	
	public void airTableSync() throws Exception {
		System.out.println("Awake...");
		Airtable airtable = new Airtable().configure(this.airtableKey);
		Base base = airtable.base(this.airtableBase);
		@SuppressWarnings("unchecked")
		Table<AirTableProblem> problemTable = base.table(this.airtableTab, AirTableProblem.class);
		System.out.println("Connected to Airtable");
		List<Problem> pList = this.problemRepository.findByAirTableSync(null);
		pList.addAll(this.problemRepository.findByAirTableSync("false"));
		System.out.println("Connected to MongoDB");
		System.out.println("Found "+pList.size() + " records that need to by added.");
		for (Problem problem : pList) {
			try {
				AirTableProblem airProblem = new AirTableProblem();
				airProblem.setUniqueID(problem.getId());
				airProblem.setDate(DATE_FORMAT.format(INPUT_FORMAT.parse(problem.getProblemDate())));
				airProblem.setURL(problem.getUrl());
				airProblem.setPageTitle(problem.getTitle());
				airProblem.setLang(problem.getLanguage().toUpperCase());
				airProblem.setWhatswrong(problem.getProblem());
				airProblem.setDetails(problem.getProblemDetails());
				airProblem.setYesno(problem.getYesno());
				airProblem.setTopic(problem.getTopic());
				airProblem.setPII(problem.getPersonalInfoYN());
				airProblem.setPIIType(problem.getPersonalInfoTypes());
				airProblem.setTags(String.join(",",problem.getTags()));
				airProblem.setId(null);
				problemTable.create(airProblem);
				problem.setAirTableSync("true");
				this.problemRepository.save(problem);
			} catch (Exception e) {
				System.out.println(e.getMessage()+ " Could not process record: " + problem.getId()+ " URL:"+problem.getUrl());
			}
		}
		System.out.println("Finished processiing...");
		exit(0);
	}

	public ProblemRepository getProblemRepository() {
		return problemRepository;
	}

	public void setProblemRepository(ProblemRepository problemRepository) {
		this.problemRepository = problemRepository;
	}
}