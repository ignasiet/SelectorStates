import java.util.ArrayList;
import java.util.Iterator;

import readers.ExprList;
import readers.PDDLParser;
import readers.PDDLParser.Expr;
import readers.PDDLParser.ParseException;
import readers.PDDLTokenizer;

/**
 * @author ignasi
 *
 */
public class Effect {
	public ArrayList<String> _Effects = new ArrayList<String>();
	public ArrayList<String> _Condition = new ArrayList<String>();
	
	public Effect(){}
	
	public Effect(String conditionalEffect){
		conditionalEffect = conditionalEffect.replaceAll("and", "").replaceAll("\\s+", " ").trim();
		ExprList eList = new ExprList();
		if((eList = itemize(conditionalEffect)) != null){
			/* Parsing conditional effect: 3 elements
			 * 1- "when"
			 * 2- Conditions: index 1
			 * 3- Effects: index 2
			 * */
			String el = eList.get(1).toString().trim();
			el = el.substring(1, el.length()-1);
			_Effects.add(el.trim());
			el = eList.get(2).toString().trim();
			el = el.substring(1, el.length()-1);
			_Condition.add(el);
		}
	}
	
	private ExprList itemize(String predicate){
		PDDLTokenizer tzr = new PDDLTokenizer(predicate);
		PDDLParser parser = new PDDLParser(tzr);
		Expr result;
		try {
			result = parser.parseExpr();
			ExprList eList = (ExprList) result;
			ExprList elements = (ExprList) eList.get(0);
			return elements;
		} catch (ParseException e) {
			//e.printStackTrace();
			return null;
		}		
	}
	
	
}
