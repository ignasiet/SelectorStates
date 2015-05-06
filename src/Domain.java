import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Domain {
	
	public String Name;
	public ArrayList<Action> action_list = new ArrayList<Action>();
	public ArrayList<String> predicates = new ArrayList<String>();
	public ArrayList<String> predicates_grounded = new ArrayList<String>();
	public Hashtable<String, ArrayList> constantes = new Hashtable<String, ArrayList>();
	public Hashtable<String, Action> list_actions = new Hashtable<String, Action>();
	public Hashtable<String, Integer> state = new Hashtable<String, Integer>();
	public Hashtable<String, Integer> hidden_state = new Hashtable<String, Integer>();
	public Hashtable<String, Integer> predicates_count = new Hashtable<String, Integer>();
	public Hashtable<String, Integer> predicates_invariants = new Hashtable<String, Integer>();
	public ArrayList<String> goalState = new ArrayList<String>();
	public String ProblemInstance;
		
	
	public void parsePredicates(String predicates_list){
		Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(predicates_list);
	    while(m.find()) {	    	
	    	predicates.add(m.group(1));
	    }
	}
	
	public void addActions(Action a){
		//list_actions.put(a.Name, a);
		action_list.add(a);
	}
	
	public void extract(String objects){
		String[] splited_objects = objects.split(" ");
		String last_object = "";
		ArrayList<String> lista_objetos = new ArrayList<String>(Arrays.asList(splited_objects));
		ArrayList<String> lista_predicados = new ArrayList<String>();
		lista_objetos.remove(0);
		for(String predicate : lista_objetos){
			if(last_object.equals("-")){
				lista_predicados.remove(lista_predicados.size()-1);
				ArrayList<String> lista_b = new ArrayList<String>(lista_predicados);
				constantes.put(predicate, lista_b);
				lista_predicados.clear();
			}
			else{
				lista_predicados.add(predicate);				
			}
			last_object = predicate;
		}
	}
	
	public static ArrayList<String> product(ArrayList<String> list1, ArrayList<String> list2){
		if(list2.isEmpty()){
			return list1;
		}
		else{
			ArrayList<String> result = new ArrayList<String>();
			for(String element1 : list1){
				for(String element2: list2){
					if(!element1.equals(element2)){
						result.add(element1 + ";" + element2);
					}
				}
			}
			return result;
		}
	}

	public void ground_all_actions() {
		for(Action a : action_list){
			ground_actions(a);
		}
	}
	
	public void getInvariantPredicates(){
		Hashtable<String, Integer> predicates_variants = new Hashtable<String, Integer>();
		for(Action a : action_list){
			for(String effect : a._effect){
				if(effect.indexOf("?") > 0){
					predicates_variants.put(effect.substring(0, effect.indexOf("?")), 1);
				}else{
					predicates_variants.put(effect, 1);
				}
			}
		}
		for(Action a : action_list){
			for(String predicate : a._precond){
				if(predicate.indexOf("?") > 0){
					String aux = predicate.substring(0, predicate.indexOf("?"));
					if(!predicates_variants.containsKey(aux)){
						predicates_invariants.put(aux, 1);
						System.out.println("Invariant: " + aux);
					}
				}else{
					if(!predicates_variants.containsKey(predicate)){
						predicates_invariants.put(predicate, 1);
						System.out.println("Invariant: " + predicate);
					}
				}
			}
		}
	}
	
	private boolean isValidCombination(String combination){
		return true;
	}
	
	public void ground_actions(Action action){
		ArrayList<String> result = new ArrayList<String>();
		//Hashtable<String, String> substitution = new Hashtable<String, String>();
		Enumeration e = action.action_parameters.keys();
		Enumeration en = action.parameters_type.keys();
		while(en.hasMoreElements()){
			String parameter = en.nextElement().toString();
			result = product(constantes.get(action.parameters_type.get(parameter)), result);
		}
		for(String combination : result){
			boolean validAction = true;
			Action act_grounded = new Action();
			act_grounded.Name = action.Name + "_" + combination.replace(";", "_");
			ArrayList<String> lista_objetos = new ArrayList<String>(Arrays.asList(combination.split(";")));
			int i = 0;
			String eff = action._effect.toString().replace("[", "").replace("]", "");
			String precond = action._precond.toString().replace("[", "").replace("]", "");
			for(String parameter : action._parameters){
				//String parameter = e.nextElement().toString();
				eff = eff.replace(parameter, lista_objetos.get(i));
				precond = precond.replace(parameter, lista_objetos.get(i));
				i++;
			}
			ArrayList<String> lista_efeitos = new ArrayList<String>();
			for(String item : Arrays.asList(eff.split(","))){
				lista_efeitos.add(item.trim());
				if(!predicates_count.containsKey(item.trim())){
					predicates_grounded.add(item.trim());
					predicates_count.put(item.trim(), 1);
				}
			}
			ArrayList<String> lista_precond = new ArrayList<String>();
			for(String item : Arrays.asList(precond.split(","))){
				lista_precond.add(item.trim());
				if(!predicates_count.containsKey(item.trim())){
					predicates_grounded.add(item.trim());
					predicates_count.put(item.trim(), 1);
				}
			}
			act_grounded._effect = lista_efeitos;				
			act_grounded._precond = lista_precond;
			if(validAction){
				list_actions.put(act_grounded.Name, act_grounded);
			}
		}
	}
	
	public void parseGoalState(String goal_state){
		Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(goal_state);
	    while(m.find()) {	    	
	    	goalState.add(Planner.cleanString(m.group(1)));
	    }
	}
	
	public void addInitialState(String initial_state){
		Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(initial_state);
	    while(m.find()) {
	    	String auxString = Planner.cleanString(m.group(1));
	    	if(!predicates_count.containsKey(auxString)){
	    		predicates_count.put(auxString, 1);
	    		predicates_grounded.add(auxString);
	    	}
	    	state.put(auxString, 1);
	    }
	}
	
	public void addHiddenState(String initial_state){
		Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(initial_state);
	    while(m.find()) {
	    	String auxString = Planner.cleanString(m.group(1));
	    	hidden_state.put(auxString, 1);
	    }
	}

	public boolean applyAction(String action_name){
		if(!list_actions.containsKey(action_name.toLowerCase())){
			System.out.println("Error: action " + action_name + " not found.");
		}
		Action a = list_actions.get(action_name.toLowerCase());
		if(isActionApplicable(a) && isActionReallyApplicable(a)){
			applyEffects(a);
			return true;
		}
		else{
			return false;
		}
	}
	
	private void applyEffects(Action a) {
		for(String effect : a._effect){
			if(effect.startsWith("~")){
				effect = effect.replace("~", "");
				state.remove(effect);
			}
			else{
				state.put(effect, 1);
			}
		}
	}
	
	/**Verify if the action is applicable*/
	private boolean isActionApplicable(Action a){
		for(String precondition : a._precond){
			if(precondition.startsWith("~")){
				if(state.containsKey(precondition.substring(1))){
					System.out.println("Action not applicable: " + a.Name);
					System.out.println("Precondition negated" + precondition + " not found.");
					System.out.println("Found negated " + precondition.substring(1) + " precondition.");
					return false;
				}
			}
			else{
				if(!state.containsKey(precondition)){
					System.out.println("Action not applicable: " + a.Name);
					System.out.println("Precondition " + precondition + " not found.");
					return false;
				}
			}			
		}
		return true;
	}
	
	/**Verify in the hidden world if the action is applicable*/
	private boolean isActionReallyApplicable(Action a){
		for(String precondition : a._precond){
			if(precondition.startsWith("~")){
				if(hidden_state.containsKey(precondition.substring(1))){
					System.out.println("Action failed in real world: " + a.Name);
					System.out.println("Precondition negated" + precondition + " not found.");
					System.out.println("Found negated " + precondition.substring(1) + " precondition.");
					return false;
				}
			}
			else{
				if(!hidden_state.containsKey(precondition)){
					System.out.println("Action not applicable: " + a.Name);
					System.out.println("Precondition " + precondition + " not found.");
					return false;
				}
			}			
		}
		return true;
	}
	
}
