import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Domain {
	
	public String Name;
	public String wumpus;
	public ArrayList<Action> action_list = new ArrayList<Action>();
	public ArrayList<String> predicates = new ArrayList<String>();
	public ArrayList<String> predicates_grounded = new ArrayList<String>();
	public ArrayList<String> predicates_uncertain = new ArrayList<String>();
	public Hashtable<String, ArrayList> constantes = new Hashtable<String, ArrayList>();
	public Hashtable<String, AbstractAction> list_actions = new Hashtable<String, AbstractAction>();
	public Hashtable<String, Integer> state = new Hashtable<String, Integer>();
	public Hashtable<String, Integer> hidden_state = new Hashtable<String, Integer>();
	public Hashtable<String, Integer> predicates_count = new Hashtable<String, Integer>();
	public Hashtable<String, Integer> predicates_invariants = new Hashtable<String, Integer>();
	public Hashtable<String, Integer> predicates_invariants_grounded = new Hashtable<String, Integer>();
	public ArrayList<String> goalState = new ArrayList<String>();
	public String ProblemInstance;
		
	
	public void parsePredicates(String predicates_list){
		Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(predicates_list);
	    while(m.find()) {	    	
	    	predicates.add(m.group(1));
	    }
	    //predicates.add("lock");
	}
	
	public void addActions(Action a){
		//list_actions.put(a.Name, a);
		/*if(a.IsObservation){
			a._Negative_effects.add("lock");
		}else{
			a._precond.add("lock");
		}*/
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
		Enumeration e = list_actions.keys();
		while(e.hasMoreElements()){
			AbstractAction a = list_actions.get(e.nextElement().toString());
			if(!a.IsObservation){
				for(String effect : a._Positive_effects){
					/*if(effect.indexOf("?") > 0){
						predicates_variants.put(effect.substring(0, effect.indexOf("?")), 1);
					}else{
						predicates_variants.put(effect, 1);
					}
				}
				for(String effect : a._Negative_effects){
					if(effect.indexOf("?") > 0){
						predicates_variants.put(effect.substring(0, effect.indexOf("?")), 1);
					}else{
						predicates_variants.put(effect, 1);
					}*/
					if(effect.contains("_")){
						predicates_variants.put(effect.substring(0, effect.indexOf("_")), 1);
					}else{
						predicates_variants.put(effect, 1);
					}
				}
			}
		}
		for(Action a : action_list){
			for(String predicate : a._precond){
				/*if(predicate.indexOf("?") > 0){
					String aux = predicate.substring(0, predicate.indexOf("?"));
					if(!predicates_variants.containsKey(aux)){
						predicates_invariants.put(aux, 1);
						//System.out.println("Invariant: " + aux);
					}
				}else{
					if(!predicates_variants.containsKey(predicate)){
						predicates_invariants.put(predicate, 1);
						//System.out.println("Invariant: " + predicate);
					}
				}*/
				String auxPredicate = predicate;
				if(predicate.contains("_")){
					auxPredicate = predicate.substring(0, predicate.indexOf("_"));
				}
				if(!predicates_variants.containsKey(auxPredicate)){
					predicates_invariants.put(auxPredicate, 1);
				}
			}
		}
	}
	
	public void eliminateInvalidActions(){
		Enumeration e = list_actions.keys();
		ArrayList<String> actions_to_be_removed = new ArrayList<String>();
		while(e.hasMoreElements()){
			String action_name = e.nextElement().toString();
			AbstractAction a = list_actions.get(action_name);
			for(String precond : a._precond){
				String predicate_name = precond;
				if(precond.contains("_")){
					predicate_name = precond.substring(0, precond.indexOf("_"));
				}
				if(predicates_invariants.containsKey(predicate_name)){
					predicates_invariants_grounded.put(precond, 1);
					//Verificar si acontece no estado inicial
					if(!state.containsKey(precond)){
						actions_to_be_removed.add(action_name);
						break;
					}
				}
			}
		}
		for(String deleteAction : actions_to_be_removed){
			list_actions.remove(deleteAction);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
			if(action.IsObservation){
				act_grounded.IsObservation = true;
			}
			act_grounded.Name = action.Name + "_" + combination.replace(";", "_");
			ArrayList<String> lista_objetos = new ArrayList<String>(Arrays.asList(combination.split(";")));
			int i = 0;
			String posit_eff = action._Positive_effects.toString().replace("[", "").replace("]", "");
			String negat_eff = action._Negative_effects.toString().replace("[", "").replace("]", "");
			String precond = action._precond.toString().replace("[", "").replace("]", "");
			for(String parameter : action._parameters){
				//String parameter = e.nextElement().toString();
				posit_eff = posit_eff.replace(parameter, lista_objetos.get(i));
				negat_eff = negat_eff.replace(parameter, lista_objetos.get(i));
				precond = precond.replace(parameter, lista_objetos.get(i));
				i++;
			}
			ArrayList<String> lista_efeitos_positivos = new ArrayList<String>();
			ArrayList<String> lista_efeitos_negativos = new ArrayList<String>();
			for(String item : Arrays.asList(posit_eff.split(","))){
				lista_efeitos_positivos.add(item.trim());
				if(!predicates_count.containsKey(item.trim())){
					predicates_grounded.add(item.trim());
					predicates_count.put(item.trim(), 1);
				}
			}
			for(String item : Arrays.asList(negat_eff.split(","))){
				lista_efeitos_negativos.add(item.trim());
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
			act_grounded._Positive_effects = lista_efeitos_positivos;
			act_grounded._Negative_effects = lista_efeitos_negativos;
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
		//TODO: extract oneof first
		if(initial_state.contains("(oneof")){
			int index_oneof = initial_state.indexOf("(oneof") + 6;
			String oneof_string = initial_state.substring(index_oneof);
			Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(oneof_string);
		    while(m.find()) {
		    	String aux = Planner.cleanString(m.group(1));
		    	predicates_uncertain.add(aux);
		    }
		    initial_state = initial_state.substring(0, index_oneof);
		    addDeductiveOneOfAction();
		}
		Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(initial_state);
	    while(m.find()) {
	    	String auxString = Planner.cleanString(m.group(1));
	    	if(!predicates_count.containsKey(auxString)){
	    		predicates_count.put(auxString, 1);
	    		predicates_grounded.add(auxString);
	    		if(auxString.contains("wumpus")){
	    			wumpus = auxString;
	    			System.out.println("Wumpus escolhido em: " + auxString);
	    		}
	    	}
	    	state.put(auxString, 1);
	    }
	    //state.put("lock", 1);
	}
	
	private void addDeductiveOneOfAction() {
		for(String pred : predicates_uncertain){
			Action a = new Action();
			a.Name = "OneOf-" + pred;
			a._Negative_effects.add(pred);
			for(String otherPred : predicates_uncertain){
				if(!otherPred.equals(pred)){
					a._precond.add(otherPred);
				}				
			}
			//a._Positive_effects.add("lock");
			a.deductive_action = true;
			list_actions.put(a.Name, a);
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
			System.out.println("Action " + action_name + " not found. Possibly deductive action.");
			return true;
		}else{
			AbstractAction a = list_actions.get(action_name.toLowerCase());
			if(!a.deductive_action){
				System.out.println("Executing: " + action_name);
			}else{
				System.out.println("Deducting: " + action_name);
			}
			if(isActionApplicable(a) && isActionReallyApplicable(a)){
				applyEffects(a);
				return true;
			}
			else{
				getInfosBeforeReplanning(a);
				return false;
			}
		}		
	}
	
	public String sensingAction(String action_name){
		String observation = "";
		AbstractAction a = list_actions.get(action_name.toLowerCase());
		String predicate_observed = a._Positive_effects.get(0);
		if(hidden_state.containsKey(predicate_observed)){
			observation = predicate_observed;
		}else{
			observation = "~" + predicate_observed;
		}
		if(observation.startsWith("~")){
			state.remove(observation.substring(1));
			hidden_state.remove(observation.substring(1));
		}else{
			state.put(observation, 1);
			hidden_state.put(observation, 1);
		}		
		return observation;
	}
	
	private void getInfosBeforeReplanning(AbstractAction a) {
		for(String precond : a._precond){
			if(!hidden_state.containsKey(precond)){
				state.remove(precond);
			}
		}	
	}

	private void applyEffects(AbstractAction a) {
		for(String effect : a._Positive_effects){
			state.put(effect, 1);
			hidden_state.put(effect, 1);
		}
		for(String effect : a._Negative_effects){
			effect = effect.replace("~", "");
			state.remove(effect);
			hidden_state.remove(effect);
		}
	}
	
	/**Verify if the action is applicable*/
	private boolean isActionApplicable(AbstractAction a){
		for(String precondition : a._precond){
			if(precondition.startsWith("~")){
				if(state.containsKey(precondition.substring(1))){
					System.out.println("Action not applicable: " + a.Name);
					//System.out.println("Precondition negated" + precondition + " not found.");
					//System.out.println("Found negated " + precondition.substring(1) + " precondition.");
					return false;
				}
			}
			else{
				if(!state.containsKey(precondition)){
					//System.out.println("Action not applicable: " + a.Name);
					//System.out.println("Precondition " + precondition + " not found.");
					return false;
				}
			}			
		}
		return true;
	}
	
	/**Verify in the hidden world if the action is applicable*/
	private boolean isActionReallyApplicable(AbstractAction a){
		for(String precondition : a._precond){
			if(precondition.startsWith("~")){
				if(hidden_state.containsKey(precondition.substring(1))){
					return false;
				}
			}
			else{
				if(!hidden_state.containsKey(precondition)){
					//System.out.println("Action not applicable: " + a.Name);
					//System.out.println("Precondition " + precondition + " not found.");
					return false;
				}
			}			
		}
		return true;
	}
	
}
