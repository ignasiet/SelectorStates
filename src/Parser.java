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
	public Parser() {
		// TODO Auto-generated constructor stub
	}
	
	private ExprList parseDomain(String path){
		Scanner scan;
		try {
			scan = new Scanner(new File(path));
			String content1 = scan.useDelimiter("\\Z").next();
			scan.close();
			PDDLTokenizer tzr = new PDDLTokenizer(content1);
			PDDLParser parser = new PDDLParser(tzr);
			Expr result = parser.parseExpr();
			ExprList domain = (ExprList) result;
			return domain;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;			
		
	}

}
