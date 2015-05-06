import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

public class Printer {

	/**
	 * 
	 */
	private static Domain domain;
	private static String negateString = "n_";
	public static void Printer(Domain dom) {
		domain = dom;
		printDomainFile();
		printProblemFile();
	}
	
	private static void printDomainFile(){
		try {
			// Print problem as an XML File
			String path = "Cdomain.pddl";
			File file = new File("Cdomain.pddl");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(printDomain());
			bw.close();

			System.out.println("Done exporting file to " + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void printProblemFile(){
		try {
			// Print problem as an XML File
			String path = "Cproblem.pddl";
			File file = new File("Cproblem.pddl");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(printProblem());
			bw.close();

			System.out.println("Done exporting file to " + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String printDomain() {
		String auxStr = "";
		auxStr = "(define (domain " + domain.Name + ")\n";
		auxStr = auxStr + printTranslatedPredicates();	
		auxStr = auxStr + printActions();
		auxStr = auxStr + "\n)\n";
		return auxStr;
	}
	
	private static String printActions() {
		String auxStr = "";
		for(Action action : domain.action_list){
			auxStr = auxStr + "(:action " + action.Name;
			auxStr = auxStr + "\n:precondition (";
			if(action._precond.size()>1){
				auxStr = auxStr + "and ";
			}
			for(String precond : action._precond){				
				auxStr = auxStr + "(" + precond + ")";
			}
			auxStr = auxStr + ")\n";
		}
		auxStr = auxStr + ")\n";
		return auxStr;
	}

	private static String printTranslatedPredicates() {
		// Print the predicates
		String auxStr = "\n(:predicates ";
		for(String pred : domain.predicates_grounded){
			if(!pred.startsWith("~")){
				auxStr = auxStr + "\n\t(" + pred.replaceAll("~", negateString) + ")";
			}
		}
		auxStr = auxStr + ")\n";
		return auxStr;
	}
	
	
	private static String printProblem() {
		String auxStr = "";
		auxStr = "(define (problem " + domain.ProblemInstance + ")\n";
		auxStr = auxStr + "(:domain " + domain.Name + ")\n";
		//auxStr = auxStr + printInitSituation();
		auxStr = auxStr + printGoalSituation();
		auxStr = auxStr + "\n)\n";
		return auxStr;
	}
	
	private static String printGoalSituation() {
		String auxStr = "\n(:goal (and ";
		for(String pred : domain.goalState){
			if (pred.startsWith("~")) {
				auxStr = auxStr + "(not (" + pred.substring(1).replaceAll("~_", negateString) + ")) ";
			} else {
				auxStr = auxStr + "(" + pred.replaceAll("~_", negateString) + ") ";
			}
		}
		auxStr = auxStr + ")\n)";
		return auxStr;
		
	}	
	
	

}
