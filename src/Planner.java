import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Planner {
	public static Domain domain = new Domain();
	public static ArrayList<String> plan = new ArrayList<String>();
	
	public static void startPlanner(){
		String path = "C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\Problemas\\";
		String path_Plan = "C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\plan.txt";
		String path_problem = "C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\";
		String path_planner = "C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\Planners\\";
		/*String path = "/home/ignasi/Dropbox/USP/Replanner/Problemas/";
		String path_Plan = "/home/ignasi/Dropbox/USP/Replanner/Planners/plan.txt";
		String path_problem = "/home/ignasi/Dropbox/USP/Replanner/";
		String path_planner = "/home/ignasi/Dropbox/USP/Replanner/Planners/";*/
		boolean success = false;
		init();
		domain.ground_all_actions();
		System.out.println("Done grounding.");
		String problem = "pW" + randInt(1, 7) + ".pddl";
		parseInit(path + problem);
		parseHidden(path + "hidden.pddl");
		System.out.println("Done parsing initial state.");
		domain.getInvariantPredicates();
		System.out.println("Printing");
		Printer.Printer(domain);
		createPlan(path_planner, path_problem);
		loadPlan(path_Plan);
		while(!success){
			if(!testPlan()){
				System.out.println("Need to replan!");
				System.out.println("Printing");
				Printer.Printer(domain);
				createPlan(path_planner, path_problem);
				plan.clear();
				loadPlan(path_Plan);
			}else{
				success = true;
				System.out.println("Success!!!!");
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
	
	@SuppressWarnings("unused")
	private static void createPlan(String path, String problems){
		//Call planner: must have FF-planner (see config)
		Process proc;
		try {
			String exec_string = path + "ff -o " + problems + "Cdomain.pddl -f " + problems + "Cproblem.pddl";
			proc = Runtime.getRuntime().exec(exec_string);
			//Then retrieve the process output
			InputStream in = proc.getInputStream();
			InputStream err = proc.getErrorStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static boolean testPlan(){
		for(String action : plan){
			if(domain.applyAction(action)){
				System.out.println("Action : " + action + " is possible");
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
			System.out.println("Loading plan...");
			while (in.ready()) {
				line = in.readLine();
				plan.add(line);
				System.out.println("Action loaded: " + line);
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
		domain.extract("constants p1-1 p1-2 p1-3 p2-1 p2-2 p2-3 p3-1 p3-2 p3-3 - pos");
		
		/*Action Move*/
		Action a = new Action();
		a.Name = "move";
		a.parseParameters("?i - pos ?j - pos");
		a.parsePreconditions("(adj ?i ?j) (at ?i) (alive) (safe ?j) ");
		a.parseEffects("(not (at ?i)) (at ?j)");
		domain.addActions(a);
		
		/*Action Move*/
		Action b = new Action();
		b.Name = "smell_wumpus";
		b.parseParameters("?pos - pos");
		b.parsePreconditions("(alive) (at ?pos)");
		b.parseEffects("(stench ?pos)");
		domain.addActions(b);
		
		/*Action Move*/
		Action c = new Action();
		c.Name = "feel-breeze";
		c.parseParameters("?pos - pos");
		c.parsePreconditions("(alive) (at ?pos)");
		c.parseEffects("(breeze ?pos)");
		domain.addActions(c);
		
		/*Action Move*/
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
			    			a.Name = "deduct-not-" + aux.substring(1);
			    		}
			    		else{
			    			a.Name = "deduct-" + aux;
			    		}
			    		a._effect.add(aux);
			    	}
			    	if(!domain.predicates_count.containsKey(aux.trim())){
						domain.predicates_grounded.add(aux.trim());
						domain.predicates_count.put(aux.trim(), 1);
					}
			    }
			    domain.list_actions.put(a.Name, a);
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
			    		a._precond.add(aux);
			    		isFirst = false;
			    	}else{
			    		if(aux.startsWith("~")){
			    			a.Name = "deduct-not-wumpus-" + aux.substring(1);
			    		}
			    		else{
			    			a.Name = "deduct-wumpus-" + aux;
			    		}
			    		a._effect.add(aux);
			    	}
			    }
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
				formula = formula.replace("(or ", "");
				formula = formula.substring(0, formula.lastIndexOf(")"));
				Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(formula);
			    while(m.find()){
			    	String aux = Planner.cleanString(m.group(1));
			    	if(aux.startsWith("~")){
			    		aux = aux.substring(1);
			    		a._precond.add(aux);
			    	}
			    	else{
			    		a._effect.add(aux);
			    		a.Name = "deduct-presence-" + aux;
			    	}
			    }
			    domain.list_actions.put(a.Name, a);
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
			    		a._precond.add(aux);
			    	}
			    	else{
			    		a._effect.add(aux);
			    		a.Name = "deduct-presence-" + aux;
			    	}
			    }
			    domain.list_actions.put(a.Name, a);
			}			
		}		
	}
}
