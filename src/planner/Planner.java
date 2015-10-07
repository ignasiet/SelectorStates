package planner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parsing.Parser;
import pddlElements.Action;
import pddlElements.Domain;
import readers.ExprList;
import readers.PDDLParser.Expr;
import searcher.Searcher;
import searcher.SolutionTree;
import searcher.TreeNode;
import translating.Printer;
import translating.Translator_Kt;
import landmarker.*;
import pddlElements.*;


public class Planner {
	public static Domain domain = new Domain();
	public static ArrayList<String> plan = new ArrayList<String>();
	public static int num_replans = 0;
	public static int actions_executed = 0;
	public static int actions_left = 0;
	private static Hashtable<String, Integer> _actionsApplied = new Hashtable<String, Integer>();
	private static Hashtable<String, Integer> observations_Hash = new Hashtable<String, Integer>();
	
	@SuppressWarnings("unused")
	public static void startPlanner(){

		/*String path = "C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\Problemas\\";
		String path_Plan = "C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\plan.txt";
		String path_problem = "C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\";
		String path_planner = "C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\Planners\\";*/
		String path = "/home/ignasi/Dropbox/USP/Replanner/Problemas/";
		String path_Plan = "/home/ignasi/Dropbox/USP/Replanner/Planners/plan.txt";
		String path_problem = "/home/ignasi/Dropbox/USP/Replanner/Dominios/";
		String path_planner = "/home/ignasi/Dropbox/USP/Replanner/Planners/";
		
		/*Define problem*/
		String problem = "p-balls.pddl";
		domain = initParsing(path_problem + "d-balls.pddl", path + problem);
		//init();
		/*Ground conditional effects*/
		domain.ground_all_actions();
		/*Select hidden file*/
		String hidden = "hidden5Complete7.pddl";
		System.out.println("Problem real: " + hidden);
		parseHidden(path + hidden);	
		
		/*Process entry*/
		domain.getInvariantPredicates();
		domain.eliminateInvalidActions();
		
		/*Time measure: translation*/
		long startTime = System.currentTimeMillis();
		Translator_Kt tr = new Translator_Kt(domain);
		long endTime = System.currentTimeMillis();
		System.out.println("Translation time: " + (endTime - startTime) + " Milliseconds");
		
		/*Size measure*/
		//System.out.println(domain.predicates_grounded.size() + " " + tr.domain_translated.predicates_grounded.size());
		/*Print domain*/
		Printer.Printer(tr.domain_translated);
		/*Start search*/
		Searcher aStar = new Searcher();
		
		/*Get landmarks*/
		//Landmarker lm = new Landmarker(tr.domain_translated.state, tr.domain_translated.list_actions, tr.domain_translated.goalState, tr.domain_translated.predicates_invariants);
		
		/*Time measure: search*/
		startTime = System.currentTimeMillis();
		aStar.searchPlan(tr.domain_translated);
		endTime = System.currentTimeMillis();
		System.out.println("Time: " + (endTime - startTime) + " Milliseconds");

		/*Execute and verify plan*/
		//executor(aStar);		
	}
	
	private static void callFFPlanner(){
		// Run a java app in a separate system process
				//Process proc = Runtime.getRuntime().exec("java -jar regressionGUI.jar problem.xml propplan 900000 90000");
				// Then retreive the process output
				//InputStream in = proc.getInputStream();
				//InputStream err = proc.getErrorStream();
				
				//TODO: Select a set of possible initial states
				//dom.generateInitialState();
				
				//Translate problem to PDDL
				//Reader rd = new Reader(dom);
				
				
				
				//Call planner: must have FF-planner (see config)
				//Process proc = Runtime.getRuntime().exec("./ff -o Kdomain.pddl -f Kproblem.pddl");
				// Then retrieve the process output
				//InputStream in = proc.getInputStream();
				//InputStream err = proc.getErrorStream();
				
				// read the output from the command
				//BufferedReader bri = new BufferedReader(new InputStreamReader(in));
				//Plan solution = new Plan();
				//Executer exec = new Executer(dom);
	}

	private static void executor(Searcher aStar){
		boolean success = false;
		while(!success){
			_actionsApplied.clear();
			observations_Hash.clear();
			if(tryPlan(aStar.getSolution())){
				success = true;
				System.out.println("Success!!!!");
			}else{
				System.out.println("Need to replan!");
				long startTime = System.currentTimeMillis();
				Translator_Kt tr = new Translator_Kt(domain);
				long endTime = System.currentTimeMillis();
				System.out.println("Translation time: " + (endTime - startTime) + " Milliseconds");
				aStar = new Searcher();
				startTime = System.currentTimeMillis();
				aStar.clearHash();
				aStar.replan(tr.domain_translated, _actionsApplied, observations_Hash);
				endTime = System.currentTimeMillis();
				System.out.println("Time: " + (endTime - startTime) + " Milliseconds");
			}
		}
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
	
	private static boolean tryPlan(SolutionTree treePlan){
		boolean result = true;
		TreeNode action_node = treePlan.root;
		if(action_node.name.equals("root")){
			action_node = action_node.left_sucessor;
		}
		try{
			while(action_node.left_sucessor != null){			
				if(domain.list_actions.get(action_node.name).IsObservation){
					String observation = domain.sensingAction(action_node.name);
					System.out.println("Observing: " + action_node.name + "(" + observation + ")");
					//System.out.println("Observed: " + observation);
					observations_Hash.put(action_node.name, 1);
					_actionsApplied.put(action_node.name, 1);
					action_node = treePlan.getObservationNode(action_node, observation);					
				}else{				
					if(domain.applyAction(action_node.name)){
						actions_executed++;
						actions_left--;
						_actionsApplied.put(action_node.name, 1);
						//System.out.println("Action : " + action + " is possible");
					}else{
						System.out.println("Action error.");
						return false;
					}
					action_node = action_node.left_sucessor;
				}			
			}
		}
		catch(NullPointerException e){
			System.out.println("Error on plan.");
			result = false;
		}
		return result;		
	}
	
	private static boolean testPlan(){
		actions_left = plan.size();
		for(String action : plan){
			if(domain.applyAction(action)){
				actions_executed++;
				actions_left--;
				//System.out.println("Action : " + action + " is possible");
			}else{
				System.out.println("Action error.");
				return false;
			}
		}
		return true;
	}

	private static void loadPlan(String path) {
		try {
			path = "plan.txt";
			BufferedReader in = new BufferedReader(new FileReader(path));
			String line;
			//System.out.println("Loading plan...");
			while (in.ready()) {
				line = in.readLine();
				plan.add(line);
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Domain initParsing(String pathDomain, String pathProblem){
		Parser p = new Parser(pathDomain, pathProblem);
		Domain domain_completed = p.getDomain();
		return domain_completed;
	}
		
	
	/*public static String cleanString(String a){
		a = a.replace("and", "").replaceAll("\\n", "").replaceAll("[()]", "").replace("not ", "~").replace(" ", "_").trim();
		return a;
	}*/
		
	
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
