import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author ignasi
 *
 */
public class Translator {

	/**
	 * 
	 */
	protected Hashtable<String,String> _Tags = new Hashtable<String,String>();
	private ArrayList<String> _TagList = new ArrayList<String>();
	protected ArrayList<String> predicates_opposed;
	protected Domain domain_translated = new Domain();
	
	public Translator(Domain domain_to_translate) {
		//TODO: 1 - Translate predicates (all)
		translatePredicates(domain_to_translate.predicates_grounded, domain_to_translate.predicates_uncertain);
		// 2-Translate initial state
		translateInitialState(domain_to_translate.state);
		// 3 - Translate goal
		translateGoal(domain_to_translate.goalState);
		// 4 - Translate actions
		translateActions(domain_to_translate.list_actions);
		// 5 - Add aditional actions
		addContingentMergeActions(domain_to_translate);
		// 6 - Add tag refutation
		addTagRefutation(domain_to_translate);
		// 7 - Add deductive actions
		addDeductiveActions(domain_to_translate);
	}

	private void addDeductiveActions(Domain domain_to_translate) {
		for(String predicate : predicates_opposed){
			Action a = new Action();
			a.Name = "Opposed-tag-" + predicate;
			a._Positive_effects.add("K" + predicate);
			for(String p_opposed : predicates_opposed){
				if(!p_opposed.equals(predicate)){
					a._precond.add("K~" + p_opposed);
				}
			}
			domain_translated.list_actions.put(a.Name, a);
		}
	}

	private void addTagRefutation(Domain domain_to_translate) {
		for(String predicate : domain_to_translate.predicates_grounded){
			if(!domain_to_translate.predicates_invariants_grounded.containsKey(predicate)){
				for(String tag : _TagList){
					Action a = new Action();
					a.Name = "Tag-Refutation-" + predicate + "-" + tag;
					a._precond.add("K"+ predicate + "-" + _Tags.get(tag));
					a._precond.add("K~"+ predicate);
					a._Positive_effects.add("K~-" + _Tags.get(tag));
					//domain_translated.list_actions.put(a.Name, a);
				}
			}
		}
	}

	private void addContingentMergeActions(Domain domain_to_translate) {
		for(String predicate : domain_to_translate.predicates_grounded){
			if(!domain_to_translate.predicates_invariants_grounded.containsKey(predicate)){
				Action a = new Action();
				a.Name = "Contingent-Merge-" + predicate;
				a._Positive_effects.add("K" + predicate);
				for(String tag : _TagList){
					a._precond.add("K" + predicate + "-" + _Tags.get(tag) + "^" + "K~-" + _Tags.get(tag));
				}
				//domain_translated.list_actions.put(a.Name, a);
			}
		}
	}

	private void translateActions(Hashtable<String, AbstractAction> list_actions) {
		Enumeration e = list_actions.keys();
		while(e.hasMoreElements()){
			AbstractAction a = list_actions.get(e.nextElement().toString());
			if(a.IsObservation){
				Action a_translated = new Action();
				a_translated.IsObservation = true;
				a_translated.Name = a.Name;
				for(String precondition : a._precond){
					a_translated._precond.add("K" + precondition);
				}
				for(String positive_effect : a._Positive_effects){
					a_translated._Positive_effects.add("K" + positive_effect);
					a_translated._Positive_effects.add("K~" + positive_effect);
				}
				domain_translated.list_actions.put(a_translated.Name, a_translated);
			}else{
				Action a_translated = new Action();
				a_translated.IsObservation = false;
				a_translated.Name = a.Name;
				for(String precondition : a._precond){
					a_translated._precond.add("K" + precondition);
				}
				for(String positive_effect : a._Positive_effects){
					a_translated._Positive_effects.add("K" + positive_effect);
				}
				for(String negat_effect : a._Negative_effects){
					if(negat_effect.startsWith("~")){
						negat_effect = negat_effect.substring(1);
					}
					a_translated._Negative_effects.add("~K" + negat_effect);
					a_translated._Positive_effects.add("K~" + negat_effect);
				}
				domain_translated.list_actions.put(a_translated.Name, a_translated);
			}
		}
	}

	private void translateGoal(ArrayList<String> goalState) {
		for(String predicate_goal : goalState){
			domain_translated.goalState.add("K" + predicate_goal);
		}
	}

	private void translatePredicates(ArrayList<String> predicates_grounded, ArrayList<String> predicates_uncertain) {
		Integer number_tag = 1;
		predicates_opposed = new ArrayList<String>(predicates_uncertain);
		for(String tag : predicates_uncertain){
			_TagList.add(tag);
			_Tags.put(tag, "t" + number_tag);
			number_tag++;
			domain_translated.predicates_grounded.add("K" + tag + "-t" + number_tag);
			domain_translated.predicates_grounded.add("K-t" + number_tag);
			domain_translated.predicates_grounded.add("K~-t" + number_tag);
		}
		//1- predicates without tags
		for(String predicate : predicates_grounded){
			//KL
			domain_translated.predicates_grounded.add("K" + predicate);
			//ML
			//domain_translated.predicates_grounded.add("M" + predicate);
			//K not L
			domain_translated.predicates_grounded.add("K~" + predicate);
			//M not L
			//domain_translated.predicates_grounded.add("M~" + predicate);
			for(String tag : _TagList){
				//KL-tagi
				domain_translated.predicates_grounded.add("K" + predicate + "-" + _Tags.get(tag));
				//K not L-tagi
				domain_translated.predicates_grounded.add("K~" + predicate + "-" + _Tags.get(tag));
			}			
		}
	}

	private void translateInitialState(Hashtable<String, Integer> state) {
		Enumeration e = state.keys();
		//1-Add state 
		while(e.hasMoreElements()){
			String key_state = e.nextElement().toString();
			domain_translated.state.put("K" + key_state, 1);
			//domain_translated.state.put("M" + key_state, 1);
		}
		//2-Add tag-states
		for(String tag : _TagList){
			//2.1 - KL-tag K
			domain_translated.state.put("K" + tag + "-" + _Tags.get(tag), 1);
			//2.2 - K not L - tag opposed
			for(String tag_opposed : predicates_opposed){
				if(!tag_opposed.equals(tag)){
					domain_translated.state.put("K~" + tag + "-" + _Tags.get(tag_opposed), 1);
				}
			}
		}
	}
	
}
