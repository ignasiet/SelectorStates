package planner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import algorithm.BDDSearcher;
import algorithm.Searcher;
import landmarker.Landmarker;
import parser.Parser;
import parser.ParserHelper;
import pddlElements.Domain;
import pddlElements.Printer;
import translating.LinearTranslation;
import translating.Translation;
import translating.Translator_Kt;


public class Planner {
	public static Domain domain = new Domain();
	public static Domain domain_translated = new Domain();
	//public static ArrayList<String> plan = new ArrayList<String>();
	public static int num_replans = 0;
	public static int actions_executed = 0;
	public static int actions_left = 0;
	private static String outputPath = "";
	//private static Hashtable<String, Integer> _actionsApplied = new Hashtable<String, Integer>();
	//private static Hashtable<String, Integer> observations_Hash = new Hashtable<String, Integer>();
	private static ArrayList<String> _Plan = new ArrayList<>();
	private static Hashtable<String, String> _ObservationSelected = new Hashtable<String, String>();
	
	public static void startPlanner(String domain_file_path, String problem_file_path, String hidden_file, String file_out_path){
		/*Define problem*/
		outputPath = file_out_path;
		long startTime = System.currentTimeMillis();
		domain = initParsing(domain_file_path, problem_file_path);
		//init();
		/*Ground conditional effects*/
		domain.ground_all_actions();
		parseHidden(hidden_file);
		
		/*Process entry*/
		domain.getInvariantPredicates();
		domain.eliminateInvalidActions();
		long endTime = System.currentTimeMillis();
		System.out.println("Preprocessing time: " + (endTime - startTime) + " milliseconds");
		
		//TODO: Print domain, to test for errors: show Thiago
		// to create a domain generator
		//Printer.print(path_print + "d-balls.pddl", domain);		
		
		/*Time measure: translation*/
		domain = ParserHelper.cleanProblem(domain);
		startTime = System.currentTimeMillis();
		//Translator_Kt tr = new Translator_Kt(domain);
		LinearTranslation tr = new LinearTranslation(domain);
		endTime = System.currentTimeMillis();
		System.out.println("Translation time: " + (endTime - startTime) + " Milliseconds");
		
		//Non deterministic planner
		//Searcher s = new Searcher();
		//s.lcdp(tr.getDomainTranslated(), 1f);
		//System.out.println("Planner time: " + s.totalTime + " Milliseconds");
		
		BDDSearcher b = new BDDSearcher(tr.getDomainTranslated());
		
		//LANDMARKS
		//@SuppressWarnings("unused")
		//Landmarker l = new Landmarker(tr.domain_translated.state, tr.domain_translated.list_actions, tr.domain_translated.goalState);
		
		/*Size measure*/
		//System.out.println(domain.predicates_grounded.size() + " " + tr.domain_translated.predicates_grounded.size());
		/*Print domain*/
		//printDomain(tr);
		/**/
	}
	
	private static void printDomain(Translation tr) {
		tr.getDomainTranslated().hidden_state = domain.hidden_state;
		domain_translated = tr.getDomainTranslated();
		long startTime = System.currentTimeMillis();
		Printer.print(outputPath + "Kdomain.pddl", outputPath + "Kproblem.pddl", tr.getDomainTranslated());
		long endTime = System.currentTimeMillis();
		System.out.println("Printing time: " + (endTime - startTime) + " Milliseconds");
	}

	public static void replan(){
		//Replanning:
		//1- clean current plan:
		_Plan.clear();
		//2- translate again! (updated initial state)
		//Translator_Kt tr = new Translator_Kt(domain);
		Printer.print(outputPath + "Kdomain.pddl", outputPath + "Kproblem.pddl", domain_translated);
	}
	
	public static int randInt(int min, int max) {
	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	public static Domain initParsing(String pathDomain, String pathProblem){
		Parser p = new Parser(pathDomain, pathProblem);
		Domain domain_completed = p.getDomain();
		return domain_completed;
	}
	
	private static void parseHidden(String path){
		Scanner scan;
		try {
			scan = new Scanner(new File(path));
			String content1 = scan.useDelimiter("\\Z").next();
			scan.close();
			domain.addHiddenState(content1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	public static void callClgPlanner(){
		// Run a java app in a separate system process
		//./clg -a 1 -c 1 -v 1 -k 1 -p ./ -o Kdomain.pddl -f Kproblem.pddl | grep '[0-9][0-9]*:\s'
		try {
			//String programName = "./clg";
			String programName = "/home/ignasi/workspace/CLG_cluster/clg";
			String commandA = "-a";
			String commandC = "-c";
			String commandV = "-v";
			String commandK = "-k";
			String valueTrue = "1";
			String valueFalse = "0";
			String commandPath = "-p";
			String path = "./";
			
			String operatorFile = "-o";
			String domainPathFile = "Kdomain.pddl";
			String factFile = "-f";
			String problemPathFile = "Kproblem.pddl";
			//Pipe Grep commands: | grep '[0-9][0-9]*:\s'
			/*String pipe = "|";
			String grepCommand = "grep";
			String regexString = "'[0-9][0-9]*:'";*/
			
			String[] CMD_ARRAY = { programName, commandA, valueTrue , commandC, 
					valueTrue, commandV, valueFalse, commandK, valueTrue, 
					commandPath, path, operatorFile, domainPathFile, factFile, problemPathFile};
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(CMD_ARRAY);
			// Then retrieve the process output
			builder.redirectOutput(new File("plan.txt"));
			builder.redirectError(new File("plan.txt"));
			//System.out.println("" + builder.command());
			Process p = builder.start();
			InputStream in = p.getInputStream();
			InputStream err = p.getErrorStream();
		    p.waitFor();//here as there is some snipped code that was causing a different
		                // exception which stopped it from getting processed

		    //missing these was causing the mass amounts of open 'files'
		    p.getInputStream().close();
		    p.getOutputStream().close();
		    p.getErrorStream().close();
		    //Store the plan:
			Readplan();
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static String readFile(String path, Charset encoding) throws IOException{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	private static void Readplan() {
		try {
			String content1 = readFile("plan.txt", Charset.defaultCharset());
			//System.out.println("Actions in plan:");
			//^(http|https|ftp)://.*$
			Matcher m = Pattern.compile("(?m)^([0-9][0-9]*):(.*)$").matcher(content1);			
		    while(m.find()) {
		    	String aux = m.group(2).trim();
		    	getPlan().add(aux);
		    	//System.out.println(aux);
		    }
		    //Read observations selected:
		    //Observation selected after action
		    Matcher obs = Pattern.compile("Observation selected after action (.*):\\n.*\\.\\.\\s(K((N_)?.*))\\(\\)").matcher(content1);			
		    while(obs.find()) {
		    	String act = obs.group(1).trim();
		    	String selected = obs.group(2).trim();
		    	//System.out.println("Action: " + act + " observed: " + selected);
		    	//TODO: using K-predicates beware of regex!
		    	/*if(selected.startsWith("N_")){
		    		getObservationSelected().put(act.toLowerCase(), "~" + selected.substring(2).toLowerCase());
		    	}else{
		    		getObservationSelected().put(act.toLowerCase(), selected.toLowerCase());
		    	}*/
		    	getObservationSelected().put(act.toLowerCase(), selected.toLowerCase());
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> getPlan() {
		return _Plan;
	}

	public static void setPlan(ArrayList<String> _Plan) {
		Planner._Plan = _Plan;
	}

	public static Hashtable<String, String> getObservationSelected() {
		return _ObservationSelected;
	}

	public static void setObservationSelected(Hashtable<String, String> _ObservationSelected) {
		Planner._ObservationSelected = _ObservationSelected;
	}
}
