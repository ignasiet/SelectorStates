package searcher;
import java.util.ArrayList;
import java.util.Hashtable;

import pddlElements.Action;
import pddlElements.Effect;
import planner.Step;

/**
 * 
 */

/**
 * @author Ignasi
 *
 */
public class SearchNode {
	
	public Hashtable<String, Integer> state;
	public Hashtable<String, Integer> _ActionsApplied;
	public int heuristicValue;
	public int pathCost = 0;
	public int fCost = 0;
	public Action generatedBy = null;
	public String _applyAction;
	public SearchNode Parent_node = null;
	public boolean isObservationNode = false;
	public String observation_divisor;
	public ArrayList<SearchNode> sucessor_node = new ArrayList<SearchNode>();
	
	public SearchNode(Hashtable<String, Integer> state_copy){
		state = new Hashtable<String, Integer>(state_copy);
	}
	
	public Hashtable<String, Integer> getState(){
		return state;
	}

	public boolean contains(String predicate) {		
		return state.containsKey(predicate);
	}

	public boolean canApply(Action a) {
		for(String precondition : a._precond){
			if(!state.containsKey(precondition)){
				return false;
			}
		}
		return true;
	}

	public SearchNode applyAction(Action a) {
		SearchNode node_sucessor = new SearchNode(state);
		if(a.IsObservation){
			node_sucessor.isObservationNode = true;
		}
		node_sucessor.generatedBy = a;
		node_sucessor.Parent_node = this;
		node_sucessor._ActionsApplied = new Hashtable<String, Integer>(this._ActionsApplied);
		node_sucessor._ActionsApplied.put(a.Name, 1);
		//1-Apply positive effects:
		/*for(String effect_positive : a._Positive_effects){
			node_sucessor.state.put(effect_positive, 1);
		}
		//2 - Apply negative effects:
		for(String effect_negative : a._Negative_effects){
			if(effect_negative.startsWith("~")){
				effect_negative = effect_negative.substring(1);
			}
			node_sucessor.state.remove(effect_negative);
		}*/
		//3 - Apply conditional effects:
		for(Effect conditionalEffect : a._Effects){
			if(isEffectApplicable(conditionalEffect)){
				for(String effectC : conditionalEffect._Effects){
					if(effectC.startsWith("~")){
						node_sucessor.state.remove(effectC.substring(1));
					}else{
						node_sucessor.state.put(effectC, 1);
					}
				}
			}
		}			
		_applyAction = a.Name;
		return node_sucessor;
	}
	
	/**Verify if the conditional effect is applied*/
	private boolean isEffectApplicable(Effect e){
		if(e._Condition.isEmpty()){
			return true;
		}else{
			for(String precondition : e._Condition){
				if(!precondition.startsWith("~")){
					if(!state.containsKey(precondition)){
						//System.out.println(a.Name);
						return false;
					}
				}else {
					if(state.containsKey(precondition.substring(1))){
						return false;
					}
				}
			}
			return true;
		}		
	}
	
	public ArrayList<SearchNode> expandObservation(Action a){
		ArrayList<SearchNode> list_return = new ArrayList<SearchNode>();
		//Node 1
		SearchNode node_sucessor_positive = new SearchNode(state);
		node_sucessor_positive.isObservationNode = true;
		node_sucessor_positive.generatedBy = a;
		node_sucessor_positive.Parent_node = this;
		node_sucessor_positive._ActionsApplied = new Hashtable<String, Integer>(this._ActionsApplied);
		node_sucessor_positive._ActionsApplied.put(a.Name, 1);
		//Node 2
		SearchNode node_sucessor_negative = new SearchNode(state);
		node_sucessor_negative.isObservationNode = true;
		node_sucessor_negative.generatedBy = a;
		node_sucessor_negative.Parent_node = this;
		node_sucessor_negative._ActionsApplied = new Hashtable<String, Integer>(this._ActionsApplied);
		node_sucessor_negative._ActionsApplied.put(a.Name, 1);
		//For every effect
		//TODO: As commented before, single effect for observations?
		boolean isFirst = true;
		for(Effect effect : a._Effects){
			String effect_positive = effect._Effects.get(0);
			if(isFirst){
				node_sucessor_positive.state.put(effect_positive, 1);
				node_sucessor_negative.state.remove(effect_positive);
			}
			else{
				node_sucessor_positive.state.remove(effect_positive);
				node_sucessor_negative.state.put(effect_positive, 1);
			}
		}
		list_return.add(node_sucessor_positive);
		list_return.add(node_sucessor_negative);
		return list_return;
	}

 	public ArrayList<SearchNode> transformObservation(Action a) {
		SearchNode node_sucessor_positive = new SearchNode(state);
		SearchNode node_sucessor_negative = new SearchNode(state);
		node_sucessor_positive.generatedBy = a;
		node_sucessor_positive.Parent_node = this;
		node_sucessor_negative.generatedBy = a;
		node_sucessor_negative.Parent_node = this;
		node_sucessor_negative._ActionsApplied = new Hashtable<String, Integer>(this._ActionsApplied);
		node_sucessor_positive._ActionsApplied = new Hashtable<String, Integer>(this._ActionsApplied);
		//1-Apply positive effects:
		boolean isFirst = true;
		//for(String effect_positive : a._Positive_effects){
		//TODO: verify expansion observation
		for(Effect eff : a._Effects){
			if(isFirst){
				node_sucessor_positive.state.put(eff._Effects.get(0), 1);
				node_sucessor_positive.observation_divisor = eff._Effects.get(0);
				node_sucessor_negative.state.remove(eff._Effects.get(0));
				isFirst = false;
			}else{
				node_sucessor_negative.state.put(eff._Effects.get(0), 1);
				node_sucessor_positive.state.remove(eff._Effects.get(0));
				node_sucessor_negative.observation_divisor = eff._Effects.get(0);
			}			
		}
		ArrayList<SearchNode> list_return = new ArrayList<SearchNode>();
		node_sucessor_negative.heuristicValue = this.heuristicValue;
		node_sucessor_negative.pathCost = this.pathCost;
		node_sucessor_negative.fCost = this.fCost;
		node_sucessor_positive.heuristicValue = this.heuristicValue;
		node_sucessor_positive.pathCost = this.pathCost;
		node_sucessor_positive.fCost = this.fCost;
		list_return.add(node_sucessor_positive);
		list_return.add(node_sucessor_negative);
		return list_return;
	}
	
}
