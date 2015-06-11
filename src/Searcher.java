import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Queue;

/**
 * @author Ignasi
 *
 */
public class Searcher {
	
	private int nMax = 20;
	private Domain domain_translated;
	private Hashtable<Hashtable<String, Integer>, Integer> _VisitedNodes = new Hashtable<Hashtable<String, Integer>, Integer>();
	private Integer _GeneratedNodes = 0;
	private Integer i = 0;
	private ArrayList<ArrayList<SearchNode>> solution_path = new ArrayList<ArrayList<SearchNode>>();
	private ArrayList<SearchNode> solution_nodes = new ArrayList<SearchNode>();
	private ArrayList<SearchNode> nodes_toReplan = new ArrayList<SearchNode>();
	private Hashtable<String, Integer> observations_made = new Hashtable<String, Integer>();
	private SolutionTree solutionTree = new SolutionTree();
	
	public Searcher(){
		
	}
	
	public SolutionTree getSolution(){
		return solutionTree;
	}
	
	private Queue<SearchNode> initFringe(){
		//init Priority queue
		PriorityQueue<SearchNode> fringe = new PriorityQueue<SearchNode>(nMax, 
				new Comparator<SearchNode>(){
			public int compare(SearchNode lhs, SearchNode rhs) {
				return lhs.fCost - rhs.fCost;
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
			initialState.heuristicValue = gp.heuristicValue();
			initialState.fCost = initialState.heuristicValue + initialState.pathCost;
			Queue<SearchNode> fringe = initFringe();
			initialState._ActionsApplied = new Hashtable<String, Integer>();
			fringe.add(initialState);
			searcherContingentPlan(fringe, domain);
		}
	}
	
	public void searcherContingentPlan(Queue<SearchNode> fringe, Domain domain){
		aStarSearch(fringe);
		while(!nodes_toReplan.isEmpty()){
			SearchNode node_r = nodes_toReplan.get(0);
			nodes_toReplan.remove(0);
			fringe.clear();
			fringe.add(node_r);
			aStarSearch(fringe);
		}
		System.out.println("Number of solutions found: " + solution_nodes.size());
		printSolution();
		//System.out.println("Path created.");
	}
	
	private void printSolution() {		
		TreeNode last_node = new TreeNode("root");
		for(ArrayList<SearchNode> path : solution_path){
			if(!solutionTree.hasRoot()){
				solutionTree.root = last_node;
			}else{
				last_node = solutionTree.root;
			}
			for(SearchNode nodeTreated : path){
				if(!last_node.name.equals(nodeTreated.generatedBy.Name)){
					if(!last_node.hasChild(nodeTreated.generatedBy.Name)){
						TreeNode tNode = new TreeNode(nodeTreated.generatedBy.Name);
						last_node.addNode(tNode);
						last_node = tNode;
					}else{
						TreeNode tNode = last_node.getChildren(nodeTreated.generatedBy.Name);
						last_node = tNode;
					}
				}
			}
		}
		System.out.println("Path created.");
		//solutionTree.printTree();
	}

	public void aStarSearch(Queue<SearchNode> fringe){
		Hashtable<String, Integer> Actions_in_fringe = new Hashtable<String, Integer>();
		while(!fringe.isEmpty()){
			//System.out.println("Fringe size: " + fringe.size());
			//node <- selectFrom(fringe)
			SearchNode node = fringe.poll();
			if(node.generatedBy != null){
				//System.out.println("Treating node: " + node.generatedBy.Name);
			}
			//
			if(!_VisitedNodes.containsKey(node.state)){
				_VisitedNodes.put(node.state, 1);
			}else{
				continue;
			}
			//If node is Goal, return Path
			if(goalTest(node)){
				returnPath(node);
				//success = true;
				break;
			}
			//Else expand node
			ArrayList<AbstractAction> applicable_action_list = matchApplicableActions(node);			
			for(AbstractAction action : applicable_action_list){
				_GeneratedNodes++;
				//not expand what it is in the fringe
				if(!Actions_in_fringe.containsKey(action.Name)){
					Actions_in_fringe.put(action.Name, 1);
					if(!(action.IsObservation && node._ActionsApplied.containsKey(action.Name))){
						SearchNode node_created = expand(node, action);
						if(!_VisitedNodes.containsKey(node_created.state)){
							_GeneratedNodes++;
							fringe.add(node_created);
							Actions_in_fringe.remove(action.Name);
						}
					}
					else{
						//System.out.println("Invalid Actions: " + action.Name);
					}
				}
			}
		}
		if(fringe.isEmpty()){
			//System.out.println("SEARCH FAILED!");
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
			if(node_sucessor != null){
			graphplanner gp = new graphplanner(node_sucessor.getState(), domain_translated.list_actions, domain_translated.goalState);
			if(!gp.fail){
				node_sucessor.heuristicValue = gp.heuristicValue();
				node_sucessor.pathCost = node.pathCost + 1;
				node_sucessor.fCost = node_sucessor.heuristicValue + node_sucessor.pathCost;
			}
			return node_sucessor;
		}
		return null;
	}
	
	private void returnPath(SearchNode node) {
		ArrayList<SearchNode> path = new ArrayList<SearchNode>();
		solution_nodes.add(node);
		SearchNode node_plan = node;
		path.clear();
		boolean discardPlan = false;
		while(!(node_plan.Parent_node == null)){
			if(node_plan.isObservationNode){
				if(!observations_made.containsKey(node_plan.generatedBy.Name)){
					observations_made.put(node_plan.generatedBy.Name, 1);
					//Create the 2 observations:
					ArrayList<SearchNode> list_transformed = node_plan.transformObservation(node_plan.generatedBy);
					for(SearchNode node_trans : list_transformed){
						node_trans = calculateHeuristic(node_trans);
						nodes_toReplan.add(node_trans);
					}
					discardPlan = true;
				}
			}
			path.add(0, node_plan);
			String action_done = node_plan.generatedBy.Name;			
			node_plan = node_plan.Parent_node;
			node_plan._applyAction = action_done;
		}
		//path.add(0, node_plan);
		//Iterate solution_nodes
		for(SearchNode solutionNode : path){
			if(solutionNode.observation_divisor != null){
				solutionTree.put_observation(solutionNode._applyAction, solutionNode.observation_divisor);
			}
		}
		if(!discardPlan){
			solution_path.add(path);
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

	private SearchNode calculateHeuristic(SearchNode n){
		graphplanner gp = new graphplanner(n.getState(), domain_translated.list_actions, domain_translated.goalState);
		n.heuristicValue = gp.heuristicValue();
		n.fCost = n.pathCost + n.heuristicValue;
		return n;
	}
	
}
