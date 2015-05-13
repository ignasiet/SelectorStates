import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * 
 */

/**
 * @author Ignasi
 *
 */
public class Searcher {
	
	private int nMax = 20;
	PriorityQueue<SearchNode> pQueue;
	
	public Searcher(){
		//init Priority queue
		pQueue = new PriorityQueue<SearchNode>(nMax, 
				new Comparator<SearchNode>(){
					public int compare(SearchNode lhs, SearchNode rhs) {
						return lhs.heuristicValue - rhs.heuristicValue;
		    }
		});
	}
	
	public void searchPlan(Domain domain){
		//Add initial state with heuristic to the fringe
		SearchNode initialState = new SearchNode(domain.state);
		graphplanner gp = new graphplanner(initialState.getState(), domain.list_actions, domain.goalState);
		System.out.println(gp._Plan.toString());
	}

}
