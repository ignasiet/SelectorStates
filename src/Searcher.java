import java.util.PriorityQueue;
import java.util.Comparator;

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
		if(!gp.fail){
			System.out.println("Plano: " + gp.last_layer);
			System.out.println("Valor heuristico: " + gp.heuristicValue());
			for(int i=1;i<=gp.last_layer;i+=2){
				System.out.println("Action level: " + gp._ActionPlan.get(i));
			}
		}
	}

}
