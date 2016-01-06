package planner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;

import parser.Parser;
import parser.ParserHelper;
import pddlElements.Domain;
import pddlElements.Printer;
import translating.Translator_Kt;


public class Planner {
	public static Domain domain = new Domain();
	public static ArrayList<String> plan = new ArrayList<String>();
	public static int num_replans = 0;
	public static int actions_executed = 0;
	public static int actions_left = 0;
	private static Hashtable<String, Integer> _actionsApplied = new Hashtable<String, Integer>();
	private static Hashtable<String, Integer> observations_Hash = new Hashtable<String, Integer>();
	
	@SuppressWarnings("unused")
	public static void startPlanner(String domain_file_path, String problem_file_path, String hidden_file, String file_out_path){

		/*String path = "C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\Problemas\\";
		String path_Plan = "C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\plan.txt";
		String path_problem = "C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\";
		String path_planner = "C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\Planners\\";*/
		//String path = "/home/ignasi/Dropbox/USP/Replanner/Problemas/";
		//String path_print = "/home/ignasi/workspace/CLG_cluster/";
		//String path_Plan = "/home/ignasi/Dropbox/USP/Replanner/Planners/plan.txt";
		//String path_problem = "/home/ignasi/Dropbox/USP/Replanner/Dominios/";
		//String path_planner = "/home/ignasi/Dropbox/USP/Replanner/Planners/";
		
		/*Define problem*/
		//String problem = "pW.pddl";
		//String dom_file_name = "dW.pddl";
		long startTime = System.currentTimeMillis();
		domain = initParsing(domain_file_path, problem_file_path);
		//init();
		/*Ground conditional effects*/
		domain.ground_all_actions();
		parseHidden(hidden_file);
		/*Select hidden file*/
		//String hidden = "hidden5Complete7.pddl";
		//System.out.println("Problem real: " + hidden);
		//parseHidden(path + hidden);	
		
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
		Translator_Kt tr = new Translator_Kt(domain);
		endTime = System.currentTimeMillis();
		System.out.println("Translation time: " + (endTime - startTime) + " Milliseconds");
		
		/*Size measure*/
		//System.out.println(domain.predicates_grounded.size() + " " + tr.domain_translated.predicates_grounded.size());
		/*Print domain*/
		startTime = System.currentTimeMillis();
		Printer.print(file_out_path + "Kdomain.pddl", file_out_path + "Kproblem.pddl", tr.domain_translated);
		endTime = System.currentTimeMillis();
		System.out.println("Printing time: " + (endTime - startTime) + " Milliseconds");
		//domain = tr.domain_translated;
		/*Start search*/
		//Searcher aStar = new Searcher();
		
		/*Get landmarks*/
		//Landmarker lm = new Landmarker(tr.domain_translated.state, tr.domain_translated.list_actions, tr.domain_translated.goalState, tr.domain_translated.predicates_invariants);
		
		/*Time measure: search*/
		/*startTime = System.currentTimeMillis();
		aStar.searchPlan(tr.domain_translated);
		endTime = System.currentTimeMillis();
		System.out.println("Time: " + (endTime - startTime) + " Milliseconds");*/	
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
	
	private static void createPlan(String path, String problems){
		//Call planner: must have FF-planner (see config)
		Process proc;
		try {
			String exec_string = path + "ff -o Cdomain.pddl -f Cproblem.pddl";
			proc = Runtime.getRuntime().exec(exec_string);
			try {
			    proc.waitFor();
			    System.out.println("FF finished");//this will only be seen after +- 10 seconds and process has finished

			} catch (InterruptedException ex) {
			   ex.printStackTrace(); 
			}
			//Then retrieve the process output
			InputStream in = proc.getInputStream();
			InputStream err = proc.getErrorStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
}
