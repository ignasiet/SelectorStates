import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Planner {
	public static Domain domain = new Domain();
	public static ArrayList<String> plan = new ArrayList<String>();
	
	public static void startPlanner(){
		init();
		domain.ground_all_actions();
		System.out.println("Done grounding.");
		parseInit("C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\Problemas\\pW2.pddl");
		System.out.println("Done parsing initial state.");
		System.out.println("Printing");
		Printer.Printer(domain);
		loadPlan("C:\\Users\\Ignasi\\Dropbox\\USP\\Replanner\\plan.txt");
		if(!testPlan()){
			System.out.println("Need to replan!");
		}else{
			System.out.println("Success!!!!");
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
			BufferedReader in = new BufferedReader(new FileReader(path));
			String line;
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
		domain.extract("constants p1-1 p1-2 p1-3 p1-4 p1-5 p2-1 p2-2 p2-3 p2-4 p2-5 p3-1 p3-2 p3-3 p3-4 p3-5 p4-1 p4-2 p4-3 p4-4 p4-5"
				+ " p5-1 p5-2 p5-3 p5-4 p5-5 - pos");
		
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
			int fin = content1.indexOf(")\n    )\n");
			if(init > 0 && fin > 0){
				String init_predicates = content1.substring(init + 6, fin);
				if(init_predicates.contains("(and")){
					init_predicates = init_predicates.replace("(and", "");
				}
				domain.addInitialState(init_predicates);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
