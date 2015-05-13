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
	
	public SearchNode(Hashtable<String, Integer> state_copy){
		state = new Hashtable<String, Integer>(state_copy);
	}
	
	public Hashtable<String, Integer> getState(){
		return state;
	}

}
