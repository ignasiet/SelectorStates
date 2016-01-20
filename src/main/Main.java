package main;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import planner.Planner;
import simulators.Wumpus;


public class Main {
	
	//private static Hashtable<String, ArrayList> constantes = new Hashtable<String, ArrayList>();
	//private static Hashtable<String, Action> list_actions = new Hashtable<String, Action>();
	//private static ArrayList<String> _Plan = new ArrayList<>();
	private static Options options = new Options();
	
	public static void main(String[] args){
		// create Options object

		// add options
		options.addOption("h", "help", false, "Show help.");
		options.addOption("on", "online", false, "Performs an online search (default is false)");
		options.addOption("o", "output",  true, "Output folder for translated problems.");
		options.addOption("d", "domain", true, "Domain file.");
		options.addOption("p", "problem", true, "Problem file.");
		options.addOption("r", "hidden", true, "Real World file.");
		
		try {
	        // parse the command line arguments
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);
			// get c option value			
			if (cmd.hasOption("h")){
				help();
			}else{
				if(!cmd.hasOption("d") | !cmd.hasOption("o") | !cmd.hasOption("p") | !cmd.hasOption("r")){
					System.out.println("Incorrect call. See help:");
					help();
				}
				String domainfile = cmd.getOptionValue("d");
				String problemfile = cmd.getOptionValue("p");
				String outputfile = cmd.getOptionValue("o");
				String hiddenfile = cmd.getOptionValue("r");
				Planner.startPlanner(domainfile, problemfile, hiddenfile, outputfile);
				/*Planner.callClgPlanner();				
				//Wumpus wumpusInstance = new Wumpus(Planner.domain);
				Wumpus wumpusInstance = new Wumpus(Planner.domain_translated);
				wumpusInstance.predictedObservations(Planner.getObservationSelected());
				//Execute while it can and replan if not:
				int numReplan = 0;
				while(wumpusInstance.simulate(Planner.domain_translated, Planner.getPlan()) < 0){
					Planner.replan();
					numReplan++;
					Planner.callClgPlanner();
				}
				System.out.println("Number of replans: " + numReplan);*/
			}
	    }catch (ParseException e) {
	    	// oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
			e.printStackTrace();
		}		
	}
	
	private static void help() {
		// This prints out some help
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp("Main", options);
		System.exit(0);
	}
}
	
