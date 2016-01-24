package translating;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import parser.ParserHelper;
import pddlElements.Action;
import pddlElements.Axiom;
import pddlElements.Branch;
import pddlElements.Disjunction;
import pddlElements.Domain;
import pddlElements.Effect;

/**
 * @author ignasi
 *
 */
public class LinearTranslation extends Translation{

	/**
	 * 
	 */
	private ArrayList<Disjunction> list_disjunctions;
	private Domain domain_translated = new Domain();
	
	public LinearTranslation(Domain domain_to_translate) {
		// 0 - Copy domain metadata
		list_disjunctions = domain_to_translate.list_disjunctions;
		domain_translated.Name = domain_to_translate.Name;
		domain_translated.ProblemInstance = domain_to_translate.ProblemInstance;
		// 1 - Translate predicates (all)
		translatePredicates(domain_to_translate.predicates_grounded, domain_to_translate.predicates_invariants_grounded);
		// 2-Translate initial state
		translateInitialState(domain_to_translate.state);
		// 3 - Translate goal
		translateGoal(domain_to_translate.goalState);
		// 4 - Translate actions
		translateActions(domain_to_translate.list_actions);
		// 5 - Add aditional actions
		//addContingentMergeActions(domain_to_translate);
		// 6 - Add tag refutation
		//addTagRefutation(domain_to_translate);
		// 7 - Add deductive actions
		addDeductiveActions(domain_to_translate);
		// 8 - Add axioms
		addAxiomsActions(domain_to_translate);
		// 9 - Translate invariants
		translateInvariants(domain_to_translate);
		// 10 - Add tag maximal effects: called when actions are translated
		//addTagMaximalEffects(domain_to_translate);
	}

	private void translateInvariants(Domain domain_to_translate) {
		Enumeration<String> e = domain_to_translate.predicates_invariants.keys();
		while(e.hasMoreElements()){
			String invariant_pred = e.nextElement().toString();
			domain_translated.predicates_invariants.put("K" + invariant_pred, 1);
		}
	}

	private void addAxiomsActions(Domain domain_to_translate) {
		int i = 1;
		for(Axiom ax : domain_to_translate._Axioms){
			Action a = new Action();
			Axiom kAx = new Axiom();
			for(String prec : ax._Body){
				//Normal axiom action
				a._precond.add("K" + prec);
				kAx._Body.add("K" + prec);
				addPredicate("K" + prec);
			}
			for(String h : ax._Head){
				//Normal axiom action
				a._Effects.add(newEffect("K" + h));
				kAx._Head.add("K" + h);
				addPredicate("K" + h);
				if(h.startsWith("~")){
					a.Name = i + "_deduct_not_" + h.substring(1);
				}else{
					a.Name = i + "_deduct_" + h;
				}
			}
			domain_translated.list_actions.put(a.Name, a);
			domain_translated._Axioms.add(kAx);
			i++;
		}
	}
	
	private Effect newEffect(String eff){
		Effect e = new Effect();
		e._Effects.add(eff);
		return e;
	}

	private void addDeductiveActions(Domain domain_to_translate) {
		/*Action a = new Action();
		a.Name = "Closure-merge-K";
		for(Disjunction disj: domain_to_translate.list_disjunctions){
			for(String predicate : disj.getIterator()){
				Effect kEffect = new Effect();
				Effect kInvertedEffect = new Effect();
				//a._Effects.add(newEffect("K" + predicate));
				for(String p_opposed : disj.getIterator()){
					if(!p_opposed.equals(predicate)){
						kEffect._Condition.add("K~" + p_opposed);
						kInvertedEffect._Effects.add("K~" + p_opposed);
					}
				}
				addPredicate("K" + predicate);
				addPredicate("K~" + predicate);
				kInvertedEffect._Condition.add("K" + predicate);
				kEffect._Effects.add("K" + predicate);
				a._Effects.add(kEffect);
				a._Effects.add(kInvertedEffect);
			}
		}
		a.deductive_action = true;
		domain_translated.disjunctionAction = a;
		domain_translated.list_actions.put(a.Name, a);*/
		for(Disjunction disj: domain_to_translate.list_disjunctions){
			for(String predicate : disj.getIterator()){
				Action a = new Action();
				a.Name = "Closure-oneof-" + predicate;
				Effect kEffect = new Effect();
				kEffect._Effects.add("K" + predicate);
				for(String p_opposed : disj.getIterator()){
					if(!p_opposed.equals(predicate)){
						a._precond.add("K~" + p_opposed);
					}
				}
				a._Effects.add(kEffect);
				a.deductive_action = true;
				domain_translated.list_actions.put(a.Name, a);
			}
		}
	}

	/*private void addTagRefutation(Domain domain_to_translate) {
		for(String predicate : domain_to_translate.predicates_grounded){
			if(!domain_to_translate.predicates_invariants_grounded.containsKey(predicate)){
				for(String tag : _TagList){
					Action a = new Action();
					a.Name = "Tag-Refutation-" + predicate + tag;
					a._precond.add("K"+ predicate + _Tags.get(tag));
					a._precond.add("K~"+ predicate);
					a._Effects.add(newEffect("K~-" + _Tags.get(tag)));
					//domain_translated.list_actions.put(a.Name, a);
				}
			}
		}
	}*/

	/*private void addContingentMergeActions(Domain domain_to_translate) {
		Enumeration<String> e = taggedEffects.keys();
		while(e.hasMoreElements()){
			String predicate = e.nextElement().toString();
			ArrayList<String> list_tags = taggedEffects.get(predicate);
			Action a = new Action();
			a.deductive_action = true;
			a.Name = "Contingent-Merge-" + predicate;
			Effect eff = new Effect();
			eff._Effects.add("K" + predicate);
			for(String tag : list_tags){
				eff._Condition.add("K" + predicate + _Tags.get(tag));
				eff._Effects.add("~K" + ParserHelper.complement(predicate) + _Tags.get(tag));
			}
			a._Effects.add(eff);
			domain_translated.list_actions.put(a.Name, a);
		}
	}*/

	protected void translateActions(Hashtable<String, Action> list_actions) {
		Enumeration<String> e = list_actions.keys();
		while(e.hasMoreElements()){
			Action a = list_actions.get(e.nextElement().toString());
			if(a.IsObservation){
				translateObservations(a);
			}else{
				Action a_translated = new Action();
				a_translated.IsObservation = false;
				a_translated.Name = a.Name;
				for(String precondition : a._precond){
					//Preconditions now are conditions of conditionals effects
					//TODO: add M-predicates
					a_translated._precond.add("K" + precondition);
				}
				if(a.deductive_action){
					a_translated.deductive_action = true;
				}
				if(a._IsConditionalEffect){
					a_translated._IsConditionalEffect = true;
				}
				for(Effect eff : a._Effects){
					a_translated._Effects.addAll(translateEffects(eff, a._precond));
				}
				domain_translated.list_actions.put(a_translated.Name, a_translated);
			}
		}
	}
	
	private Disjunction needsEffectsTagMaximal(String predicate){
		Disjunction return_disj = new Disjunction();
		for(Disjunction disj : list_disjunctions){
			if(disj.contains(predicate)){
				return_disj = disj;
				return return_disj;
			}
		}
		return null;
	}
	
	private ArrayList<Effect> translateEffects(Effect eff, ArrayList<String> _precond){
		ArrayList<Effect> returnList = new ArrayList<Effect>();
		Effect supportRule = new Effect();
		Effect cancelRule = new Effect();
		for(String prec : _precond){
			supportRule._Condition.add("K" + prec);
			cancelRule._Condition.add("~K" + ParserHelper.complement(prec));
		}
		for(String condition : eff._Condition){			
			supportRule._Condition.add("K" + condition);
			addPredicate("K" + condition);
			cancelRule._Condition.add("~K" + ParserHelper.complement(condition));
			addPredicate("K" + ParserHelper.complement(condition));
		}
		for(String effect : eff._Effects){
			supportRule._Effects.add("K" + effect);
			//TODO: eliminate effects starting with ~:
			if(effect.startsWith("~")){
				supportRule._Effects.add("~K" + effect.substring(1));
			}else{
				supportRule._Effects.add("~K~" + effect);
			}
			addPredicate("K" + effect);
			cancelRule._Effects.add("~K" + ParserHelper.complement(effect));
			addPredicate("K" + ParserHelper.complement(effect));
		}
		returnList.add(supportRule);
		returnList.add(cancelRule);
		return returnList;
	}

	private void translateObservations(Action a) {
		//TODO: Add branches
		Action a_translated = new Action();
		a_translated.IsObservation = true;
		a_translated.Name = a.Name;
		Effect e = new Effect();
		Branch b = new Branch();
		for(String precondition : a._precond){
			a_translated._precond.add("K" + precondition);
			e._Condition.add("K" + precondition);
			e._Condition.add("~K" + ParserHelper.complement(precondition));
		}
		for(Effect e_action : a._Effects){
			Branch branch1 = new Branch();
			Branch branch2 = new Branch();
			for(String effect_result : e_action._Effects){
				a_translated._precond.add("~K" + ParserHelper.complement(effect_result));
				a_translated._precond.add("~K" + effect_result);
				e._Effects.add("K" + effect_result);
				addPredicate("K" + effect_result);
				addPredicate("K" + ParserHelper.complement(effect_result));
				e._Effects.add("K" + ParserHelper.complement(effect_result));
				branch1._Branches.add("K" + effect_result);
				branch1._Branches.add("~K" + ParserHelper.complement(effect_result));
				branch2._Branches.add("~K" + effect_result);
				branch2._Branches.add("K" + ParserHelper.complement(effect_result));
			}
			a_translated._Effects.add(e);
			a_translated._Branches.add(branch1);
			a_translated._Branches.add(branch2);
		}
		domain_translated.list_actions.put(a_translated.Name, a_translated);
	}

	protected void translateGoal(ArrayList<String> goalState) {
		for(String predicate_goal : goalState){
			domain_translated.goalState.add("K" + predicate_goal);
			addPredicate("K" + predicate_goal);
		}
	}

	protected void translatePredicates(ArrayList<String> predicates_grounded, Hashtable<String, Integer> predicates_invariants_grounded) {
		//1- predicates without tags
		for(String predicate : predicates_grounded){
			if(!predicates_invariants_grounded.containsKey(predicate)){
				//KL
				addPredicate("K" + predicate);
				//K not L
				addPredicate("K~" + predicate);
			}					
		}
	}
	
	private void addPredicate(String predicate){
		if(!domain_translated.predicates_count.containsKey(predicate)){
			domain_translated.predicates_grounded.add(predicate);
			domain_translated.predicates_count.put(predicate, 1);
		}
	}
	
	/*private void addTaggedEffect(String effect, String tag){
		effect = effect.replace("~", "");
		if(!taggedEffects.containsKey(effect)){
			ArrayList<String> list_tags = new ArrayList<String>();
			list_tags.add(tag);
			taggedEffects.put(effect, list_tags);
		}else{
			ArrayList<String> list_tags = taggedEffects.get(effect);
			if(!list_tags.contains(tag)){
				list_tags.add(tag);
			}
			taggedEffects.put(effect, list_tags);
		}
	}*/

	protected void translateInitialState(Hashtable<String, Integer> state) {
		Enumeration<String> e = state.keys();
		//1-Add state 
		while(e.hasMoreElements()){
			String key_state = e.nextElement().toString();
			domain_translated.state.put("K" + key_state, 1);
			addPredicate("K" + key_state);
		}
	}

	public Domain getDomainTranslated() {
		return domain_translated;
	}
	
}
