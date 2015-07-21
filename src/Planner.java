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


public class Planner {
	public static Domain domain = new Domain();
	public static ArrayList<String> plan = new ArrayList<String>();
	public static int num_replans = 0;
	public static int actions_executed = 0;
	public static int actions_left = 0;
	private static Hashtable<String, Integer> _actionsApplied = new Hashtable<String, Integer>();
	private static Hashtable<String, Integer> observations_Hash = new Hashtable<String, Integer>();
	
	public static void startPlanner(){

		/*String path = "C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\Problemas\\";
		String path_Plan = "C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\plan.txt";
		String path_problem = "C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\";
		String path_planner = "C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\Planners\\";*/
		String path = "/home/ignasi/Dropbox/USP/Replanner/Problemas/";
		String path_Plan = "/home/ignasi/Dropbox/USP/Replanner/Planners/plan.txt";
		String path_problem = "/home/ignasi/Dropbox/USP/Replanner/";
		String path_planner = "/home/ignasi/Dropbox/USP/Replanner/Planners/";
		
		init();
		domain.ground_all_actions();
		//System.out.println("Done grounding.");
		//String problem = "pW" + randInt(1, 7) + ".pddl";
		String problem = "pW5-2States1.pddl";
		//System.out.println("Printing");
		//Printer.Printer(domain);
		parseInit(path + problem);
		String hidden = "hidden5Complete7.pddl";
		//System.out.println("Done parsing initial state.");
		//String hidden = "hidden" + i + ".pddl";
		System.out.println("Problem real: " + hidden);
		parseHidden(path + hidden);	
		
		/*Process entry*/
		domain.getInvariantPredicates();
		domain.eliminateInvalidActions();
		
		/*Time measure: translation*/
		long startTime = System.currentTimeMillis();
		Translator tr = new Translator(domain);
		long endTime = System.currentTimeMillis();
		System.out.println("Translation time: " + (endTime - startTime) + " Milliseconds");
		
		/*Size measure*/
		//System.out.println(domain.predicates_grounded.size() + " " + tr.domain_translated.predicates_grounded.size());
		//Printer.Printer(tr.domain_translated);
		//Searcher aStar = new Searcher();
		
		/*Get landmarks*/
		Landmarker lm = new Landmarker(domain.state, domain.list_actions, domain.goalState, domain.predicates_invariants);
		
		/*Time measure: search*/
		startTime = System.currentTimeMillis();
		//aStar.searchPlan(tr.domain_translated);
		endTime = System.currentTimeMillis();
		System.out.println("Time: " + (endTime - startTime) + " Milliseconds");

		/*Execute and verify plan*/
		//executor(aStar);		
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
				Translator tr = new Translator(domain);
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

	private static void init() {
		domain.Name = "wumpus";
		domain.ProblemInstance = "wumpus-5";
		domain.parsePredicates("(adj ?i ?j - pos) (at ?i - pos) (safe ?i - pos) (wumpus-at ?x - pos) (alive) (stench ?i - pos)"
				+ " (gold-at ?i - pos) (got-the-treasure) (breeze ?i - pos) (pit-at ?p - pos)");
		domain.extract("constants p1-1 p1-2 p1-3 p1-4 p1-5 p1-6 p1-7 p1-8 p1-9 p1-10 p2-1 p2-2 p2-3 p2-4 p2-5 p2-6 p2-7 p2-8 p2-9 p2-10 p3-1 p3-2 p3-3 p3-4 p3-5 p3-6 p3-7 p3-8 p3-9 p3-10 p4-1 p4-2 p4-3 p4-4 p4-5 p4-6 p4-7 p4-8 p4-9 p4-10 p5-1 p5-2 p5-3 p5-4 p5-5 p5-6 p5-7 p5-8 p5-9 p5-10 p6-1 p6-2 p6-3 p6-4 p6-5 p6-6 p6-7 p6-8 p6-9 p6-10 p7-1 p7-2 p7-3 p7-4 p7-5 p7-6 p7-7 p7-8 p7-9 p7-10 p8-1 p8-2 p8-3 p8-4 p8-5 p8-6 p8-7 p8-8 p8-9 p8-10 p9-1 p9-2 p9-3 p9-4 p9-5 p9-6 p9-7 p9-8 p9-9 p9-10 p10-1 p10-2 p10-3 p10-4 p10-5 p10-6 p10-7 p10-8 p10-9 p10-10 - pos");
		
		/*Action Move*/
		Action a = new Action();
		a.Name = "move";
		a.parseParameters("?i - pos ?j - pos");
		a.parsePreconditions("(adj ?i ?j) (at ?i) (alive) (safe ?j) ");
		a.parseEffects("(not (at ?i)) (at ?j)");
		domain.addActions(a);
		
		/*Action smell*/
		Action b = new Action();
		b.Name = "smell_wumpus";
		b.parseParameters("?pos - pos");
		b.parsePreconditions("(alive) (at ?pos)");
		b.parseEffects("(stench ?pos)");
		b.IsObservation = true;
		domain.addActions(b);
		
		/*Action feel*/
		/*Action c = new Action();
		c.Name = "feel-breeze";
		c.parseParameters("?pos - pos");
		c.parsePreconditions("(alive) (at ?pos)");
		c.parseEffects("(breeze ?pos)");
		c.IsObservation = true;
		domain.addActions(c);*/
		
		/*Action grab*/
		Action d = new Action();
		d.Name = "grab";
		d.parseParameters("?i - pos");
		d.parsePreconditions("(at ?i) (gold-at ?i) (alive)");
		d.parseEffects("(got-the-treasure) (not (gold-at ?i))");
		domain.addActions(d);
		
		domain.parseGoalState("(got-the-treasure) (alive)");
		//domain.addInitialState();
		System.out.println("Done parsing.");
	}
	
	public static String cleanString(String a){
		a = a.replace("and", "").replaceAll("\\n", "").replaceAll("[()]", "").replace("not ", "~").replace(" ", "_").trim();
		return a;
	}
	
	private static void parseInit(String path){
		Scanner scan;
		try {
			scan = new Scanner(new File(path));
			String content1 = scan.useDelimiter("\\Z").next();
			scan.close();
			int init = content1.indexOf("(:init");
			int fin = content1.indexOf(";;; Safes");
			if(init > 0 && fin > 0){
				String init_predicates = content1.substring(init + 6, fin);
				if(init_predicates.contains("(and")){
					init_predicates = init_predicates.replace("(and", "");
				}
				domain.addInitialState(init_predicates);
			}
			extractDeductiveRules(content1);
			extractSensingRules(content1);
			extractRules(content1);
			//extractExclusionRules(content1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	private static void extractDeductiveRules(String content1){
		int init_safes = content1.indexOf(";;; Safes");
		int fin_safes = content1.indexOf(";;; Exclution");
		int counter = 1;
		if(init_safes > 0 && fin_safes > 0){
			String text_deductive_actions = content1.substring(init_safes + 9, fin_safes);
			text_deductive_actions = text_deductive_actions.replace("\t\n", "");
			String[] text_splited = text_deductive_actions.trim().split("\n");
			for(String formula : text_splited){
				Action a = new Action();
				formula = formula.replace("(or ", "");
				formula = formula.substring(0, formula.lastIndexOf(")"));
				Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(formula);
				boolean isFirst = true;
			    while(m.find()) {
			    	String aux = Planner.cleanString(m.group(1));
			    	if(isFirst){
			    		if(aux.startsWith("~")){
			    			aux = aux.substring(1);
			    		}
			    		else{
			    			aux = "~" + aux;
			    		}
			    		a._precond.add(aux);
			    		isFirst = false;
			    	}else{
			    		if(aux.startsWith("~")){
			    			a.Name = counter + "-deduct-not-" + aux.substring(1);
			    			a._Negative_effects.add(aux.substring(1));
			    		}
			    		else{
			    			a.Name = counter + "-deduct-" + aux;
			    			a._Positive_effects.add(aux);
			    		}
			    		
			    	}
			    	if(!domain.predicates_count.containsKey(aux.trim())){
						domain.predicates_grounded.add(aux.trim());
						domain.predicates_count.put(aux.trim(), 1);
					}
			    	
			    }
			    //a._Positive_effects.add("lock");
			    a.deductive_action = true;
			    domain.list_actions.put(a.Name, a);
			    counter++;
			}			
		}
	}
	
	private static void extractRules(String content1){
		int init_wumpus = content1.indexOf(";;; Wumpus");
		int fin_wumpus = content1.indexOf(";;; stenchs");
		if(init_wumpus > 0 && fin_wumpus > 0){
			String text_deductive_actions = content1.substring(init_wumpus + 10, fin_wumpus);
			text_deductive_actions = text_deductive_actions.replace("\t\n", "");
			String[] text_splited = text_deductive_actions.trim().split("\n");
			for(String formula : text_splited){
				Action a = new Action();
				formula = formula.replace("(or ", "");
				formula = formula.substring(0, formula.lastIndexOf(")"));
				Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(formula);
				boolean isFirst = true;
			    while(m.find()){
			    	String aux = Planner.cleanString(m.group(1));
			    	if(isFirst){
			    		if(aux.startsWith("~")){
			    			aux = aux.substring(1);
			    		}
			    		else{
			    			aux = "~" + aux;
			    		}
			    		a.Name = "not-" + aux.replace("~", "");
			    		a._precond.add(aux);
			    		isFirst = false;
			    	}else{
			    		if(aux.startsWith("~")){
			    			//a.Name = "Stench-not-" + aux.substring(1);
			    			a._Negative_effects.add(aux.substring(1));
			    		}
			    		else{
			    			a.Name = "stench-" + aux;
			    			a._Positive_effects.add(aux);
			    		}
			    	}
			    }
			    //a._Positive_effects.add("lock");
			    a.deductive_action = true;
			    domain.list_actions.put(a.Name, a);
			}			
		}		
	}
	
	private static void extractSensingRules(String content1){
		int init_wumpus = content1.indexOf(";;; stenchs");
		int fin_wumpus = content1.indexOf(";;; end safes");
		if(init_wumpus > 0 && fin_wumpus > 0){
			String text_deductive_actions = content1.substring(init_wumpus + 11, fin_wumpus);
			text_deductive_actions = text_deductive_actions.replace("\t\n", "");
			String[] text_splited = text_deductive_actions.trim().split("\n");
			for(String formula : text_splited){
				Action a = new Action();
				Action a_opposed = new Action();
				formula = formula.replace("(or ", "");
				formula = formula.substring(0, formula.lastIndexOf(")"));
				Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(formula);
			    while(m.find()){
			    	String aux = Planner.cleanString(m.group(1));
			    	if(aux.startsWith("~")){
			    		a_opposed._precond.add(aux);
			    		aux = aux.substring(1);
			    		a._precond.add(aux);
			    	}
			    	else{
			    		a._Positive_effects.add(aux);
			    		a_opposed._Positive_effects.add("~"+aux);
			    		a.Name = "deduct-stench-" + aux;
			    		a_opposed.Name = "deduct-not-stench-" + aux;
			    	}
			    }
			    //a._Positive_effects.add("lock");
			    a.deductive_action = true;
			    a_opposed.deductive_action = true;
			    domain.list_actions.put(a.Name, a);
			    domain.list_actions.put(a_opposed.Name, a_opposed);
			}			
		}
	}
	
	private static void extractExclusionRules(String content1){
		int init_wumpus = content1.indexOf(";;; Exclution");
		int fin_wumpus = content1.indexOf(";;; Wumpus");
		if(init_wumpus > 0 && fin_wumpus > 0){
			String text_deductive_actions = content1.substring(init_wumpus + 13, fin_wumpus);
			text_deductive_actions = text_deductive_actions.replace("\t\n", "");
			String[] text_splited = text_deductive_actions.trim().split("\n");
			for(String formula : text_splited){
				Action a = new Action();
				formula = formula.replace("(or ", "");
				formula = formula.substring(0, formula.lastIndexOf(")"));
				Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(formula);
			    while(m.find()){
			    	String aux = Planner.cleanString(m.group(1));
			    	if(aux.startsWith("~")){
			    		aux = aux.substring(1);
			    		a._Negative_effects.add(aux);
			    	}
			    	else{
			    		a._Positive_effects.add(aux);
			    		a.Name = "deduct-presence-" + aux;
			    	}
			    }
			    domain.list_actions.put(a.Name, a);
			}			
		}		
	}
}
