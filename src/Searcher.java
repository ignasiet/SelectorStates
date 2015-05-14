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
	//private PriorityQueue<SearchNode> fringe;
	private Domain domain_translated;
	private Hashtable<SearchNode, Integer> _VisitedNodes = new Hashtable<SearchNode, Integer>();
	private Integer _GeneratedNodes = 0;
	//private graphplanner gp;
	
	public Searcher(){
		
	}
	
	private PriorityQueue<SearchNode> initFringe(){
		//init Priority queue
		PriorityQueue<SearchNode> fringe = new PriorityQueue<SearchNode>(nMax, 
				new Comparator<SearchNode>(){
			public int compare(SearchNode lhs, SearchNode rhs) {
				return lhs.heuristicValue - rhs.heuristicValue;
			}
		});
		return fringe;
	}
	
	public void searchPlan(Domain domain){
		//Add initial state with heuristic to the fringe
		domain_translated = domain;
		SearchNode initialState = new SearchNode(domain.state);
		graphplanner gp = new graphplanner(initialState.getState(), domain.list_actions, domain.goalState);
		if(!gp.fail){
			System.out.println("Plano: " + gp.last_layer);
			System.out.println("Valor heuristico: " + gp.heuristicValue());
			for(int i=1;i<=gp.last_layer;i+=2){
				System.out.println("Action level: " + gp._ActionPlan.get(i));
			}
			initialState.heuristicValue = gp.heuristicValue();
			PriorityQueue<SearchNode> fringe = initFringe();
			fringe.add(initialState);
			//aStarSearch(fringe);
			aStar(initialState);
		}
	}
	
	public void aStarSearch(PriorityQueue<SearchNode> fringe){
		while(!fringe.isEmpty()){
			//node <- selectFrom(fringe)
			SearchNode node = fringe.poll();
			if(!_VisitedNodes.containsKey(node)){
				_VisitedNodes.put(node, 1);
			}
			//If node is Goal, return Path
			if(goalTest(node)){
				returnPath(node);
				break;
			}
			if(node.isObservationNode){
				ArrayList<SearchNode> list_results_observations = node.transformObservation(node.generatedBy);
				PriorityQueue<SearchNode> fringe1 = new PriorityQueue<SearchNode>(fringe);
				PriorityQueue<SearchNode> fringe2 = new PriorityQueue<SearchNode>(fringe);
				boolean isFirst = true;
				for(SearchNode n : list_results_observations){
					if(isFirst){
						fringe.add(n);
						isFirst = false;
					}else{
						fringe2.add(n);
						//aStarSearch(fringe2);
					}
				}
				break;
			}
			//Else expand node
			for(AbstractAction action : matchApplicableActions(node)){
				_GeneratedNodes++;
				fringe.add(expand(node, action));
			}
		}
	}
	
	public void aStar(SearchNode node){
		if(goalTest(node)){
			returnPath(node);
			return;
		}
		PriorityQueue<SearchNode> fringe = initFringe();
		fringe.add(node);
		while(!fringe.isEmpty()){
			SearchNode next_node = fringe.poll();
			if(!_VisitedNodes.containsKey(next_node)){
				_VisitedNodes.put(next_node, 1);
			}
			else{
				continue;
			}
			if(goalTest(next_node)){
				returnPath(next_node);
				break;
			}
			if(next_node.isObservationNode){
				ArrayList<SearchNode> list_results_observations = next_node.transformObservation(next_node.generatedBy);
				boolean isFirst = true;
				for(SearchNode n : list_results_observations){
					graphplanner gp = new graphplanner(n.getState(), domain_translated.list_actions, domain_translated.goalState);
					n.heuristicValue = gp.heuristicValue();
					if(isFirst){
						for(AbstractAction action : matchApplicableActions(n)){
							_GeneratedNodes++;
							fringe.add(expand(n, action));
						}
						System.out.println("Forking 1: " + n.generatedBy.Name);
						aStar(fringe.poll());
						isFirst = false;
					}else{
						for(AbstractAction action : matchApplicableActions(n)){
							_GeneratedNodes++;
							fringe.add(expand(n, action));
						}
						System.out.println("Forking 2: " + n.generatedBy.Name);
						aStar(fringe.poll());
					}
				}
				break;
			}
			//Else expand node
			for(AbstractAction action : matchApplicableActions(next_node)){
				_GeneratedNodes++;
				fringe.add(expand(next_node, action));
			}
		}
	}
	
	private ArrayList<AbstractAction> matchApplicableActions(SearchNode node){
		ArrayList<AbstractAction> return_list = new ArrayList<AbstractAction>();
		Enumeration<String> e = domain_translated.list_actions.keys();
		while(e.hasMoreElements()){
			AbstractAction a = domain_translated.list_actions.get(e.nextElement().toString());
			if(node.canApply(a)){
				return_list.add(a);
			}
		}
		return return_list;
	}

	
	private SearchNode expand(SearchNode node, AbstractAction a) {
		SearchNode node_sucessor = node.applyAction(a);
		graphplanner gp = new graphplanner(node_sucessor.getState(), domain_translated.list_actions, domain_translated.goalState);
		if(!gp.fail){
			node_sucessor.heuristicValue = gp.heuristicValue();
		}
		return node_sucessor;
	}

	private void returnPath(SearchNode node) {
		System.out.println("GOAL!");
		System.out.println("Found plan: ");
		SearchNode node_plan = node;
		while(!(node_plan.Parent_node == null)){
			System.out.println(node_plan.generatedBy.Name);
			node_plan = node_plan.Parent_node;
		}
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
