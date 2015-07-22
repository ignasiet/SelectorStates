import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import readers.ExprList;
import readers.PDDLParser;
import readers.PDDLParser.Expr;
import readers.PDDLParser.ParseException;
import readers.PDDLTokenizer;

/**
 * 
 */

/**
 * @author ignasi
 *
 */
public class Parser {

	/**
	 * 
	 */
	private String _Path;
	private ExprList _Problem;
	private Domain _Domain = new Domain();
	
	public Parser(String path) {
		_Path = path;
		_Problem = parseDomain();
		for(Expr e : _Problem){
			String clean_string = e.toString().replaceAll("[()]", "").trim(); 
			parseType(e.toString(), clean_string.split(" ")[0].replace(":", ""), e);
		}
	}
	
	public ExprList parsedProblem(){
		return _Problem;
	}
	
	public Domain getDomain(){
		return _Domain;
	}
	
	private void parseType(String element, String type, Expr e){
		//System.out.println("=============================");
		//System.out.println("Tipo: " + type);
		//System.out.println(element);
	    switch (type) {
		case "domain":
			_Domain.Name = element.toString().replaceAll("[()]", "").trim().replace(type, "").trim();
			break;
		case "predicates":
			String cleanedElement = element.replace(type, "").trim();
			cleanedElement = cleanedElement.substring(2, cleanedElement.length()).trim().replace("\n", "");
			_Domain.parsePredicates(cleanedElement);
			break;
		case "constants":
			cleanedElement = element.trim().replace("\n", "");
			_Domain.extract(cleanedElement.substring(2, cleanedElement.length()-1));
			break;
		case "action":
			cleanedElement = element.trim().replace("\n", "");
			cleanedElement = cleanedElement.replaceAll("\\s+", " ");
			String[] splitted_String = cleanedElement.substring(1, cleanedElement.length()-1).trim().split(":");
			Action a = parseAction(splitted_String);
			_Domain.addActions(a);
			break;
		default:
			break;
		}
	}
	
	private Action parseAction(String[] splitted_String) {
		Action a = new Action();
		for(String token : splitted_String){
			if(token.length()>0){
				String[] line = token.split(" ");
				String predicates_list = token.replaceAll("and", "").replace(line[0], "").trim();
				predicates_list = predicates_list.substring(1, predicates_list.length()-1);
				if(line[0].equals("action")){
					a.Name = line[1];
				}else if(line[0].equals("parameters")){
					//predicates_list = predicates_list.substring(1, predicates_list.length()-1);
					a.parseParameters(predicates_list);					
				}else if(line[0].equals("precondition")){
					predicates_list = predicates_list.substring(1, predicates_list.length()-1);
					a.parsePreconditions(predicates_list);					
				}else if(line[0].equals("effect")){
					a.parseEffects(predicates_list);
				}else if(line[0].equals("observe")){
					a.IsObservation = true;
					a.parseEffects(predicates_list);
				}
			}
		}
		return a;
	}

	private ExprList parseDomain(){
		Scanner scan;
		try {
			scan = new Scanner(new File(_Path));
			String content1 = scan.useDelimiter("\\Z").next();
			scan.close();
			PDDLTokenizer tzr = new PDDLTokenizer(content1);
			PDDLParser parser = new PDDLParser(tzr);
			Expr result = parser.parseExpr();
			ExprList domain = (ExprList) result;
			return domain;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;	
	}
	
	private Domain buildDomainProblem(){
		Domain dom = new Domain();
		return dom;
	}

}
