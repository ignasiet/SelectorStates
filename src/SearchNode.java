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
	public int heuristicValue;
	public AbstractAction generatedBy = null;
	
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
		node_sucessor.generatedBy = a;
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

}
