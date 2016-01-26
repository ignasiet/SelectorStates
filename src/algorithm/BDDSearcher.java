package algorithm;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import bdd.BDDAction;
import bdd.BDDUtils;
import net.sf.javabdd.BDD;
import pddlElements.Action;
import pddlElements.Domain;

public class BDDSearcher {
	
	private Domain _Domain;
	private BDDUtils utils = new BDDUtils();
	private Integer lastUsed = 0;
	private Hashtable<String, Integer> var2num = new Hashtable<String, Integer>();
	private Hashtable<Integer, String> num2var = new Hashtable<Integer, String>();
	private ArrayList<BDDAction> actions = new ArrayList<BDDAction>();
	
	public BDDSearcher(Domain d) {
		_Domain = d;
		translateDomain2BDD();
	}
	private void translateDomain2BDD() {
		Enumeration<String> e = _Domain.list_actions.keys();
		while(e.hasMoreElements()){
			String actionName = e.nextElement().toString();
			Action a = _Domain.list_actions.get(actionName);
			actions.add(translateAction(a));
		}
		System.out.println("Actions ready.");
	}
	
	private BDDAction translateAction(Action a){
		BDD prec = utils.one();
		for(String precondition : a._precond){
			setVar(precondition);
			BDD precond = utils.createTrueBDD(var2num.get(precondition));
			prec = prec.and(precond);
		}
		BDDAction actionBDD = new BDDAction();
		actionBDD.preconditions = prec;
		return actionBDD;
	}
	
	private void setVar(String var){
		if(!var2num.containsKey(var)){
			var2num.put(var, lastUsed);
			num2var.put(lastUsed, var);
			lastUsed++;
		}
	}

}
