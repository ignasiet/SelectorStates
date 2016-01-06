package main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import planner.Planner;
import simulators.Simulator;


public class Main {
	
	//private static Hashtable<String, ArrayList> constantes = new Hashtable<String, ArrayList>();
	//private static Hashtable<String, Action> list_actions = new Hashtable<String, Action>();
	private static ArrayList<String> _Plan = new ArrayList<>();
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
				callClgPlanner();
				Simulator.Wumpus(Planner.domain);
				Simulator.simulate(Planner.domain, _Plan);
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
	
	@SuppressWarnings("unused")
	private static void callClgPlanner(){
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
			
			//Process proc = Runtime.getRuntime().exec("./clg -a 1 -c 1 -v 1 -k 1 -p ./ -o Kdomain.pddl -f Kproblem.pddl");
			
			/*proc.redirectOutput(new File("plan.txt"));
			*/
			
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
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
		    	_Plan.add(aux);
		    	//System.out.println(aux);
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
