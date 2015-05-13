import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * @author Ignasi
 *
 */
public class Searcher {
	
	private int nMax = 20;
	private PriorityQueue<SearchNode> fringe;
	private Domain domain_translated;
	private Hashtable<SearchNode, Integer> _VisitedNodes = new Hashtable<SearchNode, Integer>();
	private graphplanner gp;
	
	public Searcher(){
		//init Priority queue
		fringe = new PriorityQueue<SearchNode>(nMax, 
				new Comparator<SearchNode>(){
					public int compare(SearchNode lhs, SearchNode rhs) {
						return lhs.heuristicValue - rhs.heuristicValue;
		    }
		});
	}
	
	public void searchPlan(Domain domain){
		//Add initial state with heuristic to the fringe
		domain_translated = domain;
		SearchNode initialState = new SearchNode(domain.state);
		gp = new graphplanner(initialState.getState(), domain.list_actions, domain.goalState);
		if(!gp.fail){
			System.out.println("Plano: " + gp.last_layer);
			System.out.println("Valor heuristico: " + gp.heuristicValue());
			for(int i=1;i<=gp.last_layer;i+=2){
				System.out.println("Action level: " + gp._ActionPlan.get(i));
			}
			initialState.heuristicValue =  gp.heuristicValue();
			fringe.add(initialState);
			aStarSearch(fringe);
		}
	}
	
	public void aStarSearch(PriorityQueue<SearchNode> pQueue2){
		while(!fringe.isEmpty()){
			//node <- selectFrom(fringe)
			SearchNode node = fringe.poll();
			//If node is Goal, return Path
			if(goalTest(node)){
				returnPath(node);
			}
			//Else expand node
			expand(node);
		}
	}
	
	private void expand(SearchNode node) {
		Enumeration<String> e = domain_translated.list_actions.keys();
		while(e.hasMoreElements()){
			AbstractAction a = domain_translated.list_actions.get(e.nextElement().toString());
			if(node.canApply(a)){
				SearchNode node_sucessor = node.applyAction(a);				
				gp = new graphplanner(node_sucessor.getState(), domain_translated.list_actions, domain_translated.goalState);
				if(!gp.fail){
					node_sucessor.heuristicValue = gp.heuristicValue();
					fringe.add(node_sucessor);
				}
			}
		}
	}

	private void returnPath(SearchNode node) {
		System.out.println("GOAL!");
	}

	private boolean goalTest(SearchNode node){
		for(String predicate : domain_translated.goalState){
			if(!node.contains(predicate)){
				return false;
			}
		}
		return true;
	}

}
