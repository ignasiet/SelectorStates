import java.util.ArrayList;
import java.util.Hashtable;

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
	public AbstractAction generatedBy = null;
	public SearchNode Parent_node = null;
	public boolean isObservationNode = false;
	
	public SearchNode(Hashtable<String, Integer> state_copy){
		state = new Hashtable<String, Integer>(state_copy);
	}
	
	public Hashtable<String, Integer> getState(){
		return state;
	}

	public boolean contains(String predicate) {		
		return state.containsKey(predicate);
	}

	public boolean canApply(AbstractAction a) {
		for(String precondition : a._precond){
			if(!state.containsKey(precondition)){
				return false;
			}
		}
		return true;
	}

	public SearchNode applyAction(AbstractAction a) {
		SearchNode node_sucessor = new SearchNode(state);
		if(a.IsObservation){
			node_sucessor.isObservationNode = true;
		}
		node_sucessor.generatedBy = a;
		node_sucessor.Parent_node = this;
		node_sucessor._ActionsApplied = new Hashtable<String, Integer>(this._ActionsApplied);
		node_sucessor._ActionsApplied.put(a.Name, 1);
		//1-Apply positive effects:
		for(String effect_positive : a._Positive_effects){
			node_sucessor.state.put(effect_positive, 1);
		}
		//2 - Apply negative effects:
		for(String effect_negative : a._Negative_effects){
			if(effect_negative.startsWith("~")){
				effect_negative = effect_negative.substring(1);
			}
			node_sucessor.state.remove(effect_negative);
		}
		return node_sucessor;
	}
	
	public ArrayList<SearchNode> expandObservation(AbstractAction a){
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
		boolean isFirst = true;
		for(String effect_positive : a._Positive_effects){
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

 	public ArrayList<SearchNode> transformObservation(AbstractAction a) {
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
		for(String effect_positive : a._Positive_effects){
			if(isFirst){
				node_sucessor_positive.state.put(effect_positive, 1);
				node_sucessor_negative.state.remove(effect_positive);
				isFirst = false;
			}else{
				node_sucessor_negative.state.put(effect_positive, 1);
				node_sucessor_positive.state.remove(effect_positive);
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
