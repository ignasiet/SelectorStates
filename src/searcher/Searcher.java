package searcher;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Queue;

import pddlElements.Action;
import pddlElements.Domain;
import planner.graphplanner;


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
	
	public void clearHash(){
		_VisitedNodes = new Hashtable<Hashtable<String, Integer>, Integer>();
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
		initialState._ActionsApplied = new Hashtable<String, Integer>();
		//debuggerTester(initialState);
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
	
	public void replan(Domain domain, Hashtable<String, Integer> _actionsApplied, Hashtable<String, Integer> observations){
		//Add initial state with heuristic to the fringe
		domain_translated = domain;
		SearchNode initialState = new SearchNode(domain.state);
		observations_made = observations;
		//debuggerTester(initialState);
		graphplanner gp = new graphplanner(initialState.getState(), domain.list_actions, domain.goalState);
		if(!gp.fail){
			initialState.heuristicValue = gp.heuristicValue();
			initialState.fCost = initialState.heuristicValue + initialState.pathCost;
			Queue<SearchNode> fringe = initFringe();
			initialState._ActionsApplied = new Hashtable<String, Integer>(_actionsApplied);
			fringe.add(initialState);
			searcherContingentPlan(fringe, domain);
		}
	}
	
	public void searcherContingentPlan(Queue<SearchNode> fringe, Domain domain){
		aStarSearch(fringe);
		_VisitedNodes.clear();
		while(!nodes_toReplan.isEmpty()){
			SearchNode node_r = nodes_toReplan.get(0);
			nodes_toReplan.remove(0);
			fringe.clear();
			fringe.add(node_r);
			//_VisitedNodes.clear();
			aStarSearch(fringe);
		}
		System.out.println("Number of solutions found: " + solution_nodes.size());
		printSolution();
		//System.out.println("Path created.");
	}	
	
	private void printSolution() {
		String next_son = "";
		TreeNode last_node = new TreeNode("root");
		for(ArrayList<SearchNode> path : solution_path){
			if(!solutionTree.hasRoot()){
				solutionTree.root = last_node;
			}else{
				last_node = solutionTree.root;
			}
			for(SearchNode nodeTreated : path){
				//System.out.println(last_node.name);
				if(!last_node.name.equals(nodeTreated.generatedBy.Name)){
					if(!last_node.hasChild(nodeTreated.generatedBy.Name)){
						TreeNode tNode = new TreeNode(nodeTreated.generatedBy.Name);
						/*if(last_node.left_sucessor != null && last_node.right_sucessor != null){
							//System.out.println(last_node.name);
						}*/
						last_node.addNode(tNode, next_son);
						next_son = "";
						last_node = tNode;
					}else{
						TreeNode tNode = last_node.getChildren(nodeTreated.generatedBy.Name);
						last_node = tNode;
					}
				}
				else{
					//System.out.println("Divisor: " + nodeTreated.observation_divisor);
					if(nodeTreated.observation_divisor.contains("~")){
						next_son = "right";
					}
					else{
						next_son = "left";
					}
				}
			}
		}
		System.out.println("Path created.");
		//solutionTree.printTree(domain_translated.list_actions);
	}
	
	public void debuggerTester(SearchNode node){
		while(!goalTest(node)){
			System.out.println("Choose action (wisely!): ");
			ArrayList<Action> applicable_action_list = matchApplicableActions(node);
			ArrayList<SearchNode> list_states = new ArrayList<SearchNode>();
			int i = 0;
			for(Action action : applicable_action_list){
				SearchNode node_created = expand(node, action);
				list_states.add(i, node_created);
				System.out.println(i + " Action: " + action.Name + " Heuristic: " + node_created.heuristicValue + " Fvalue: " + node_created.fCost);
				i++;
			}
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
	        String input;
			try {
				input = bufferedReader.readLine();
				int number = Integer.parseInt(input);
				node = list_states.get(number);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	        
		}
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
				//System.out.println("Visited State!");
				//System.out.println("Number of states: " + _VisitedNodes.get(node.state));
				continue;
			}
			//If node is Goal, return Path
			if(goalTest(node)){
				returnPath(node);
				//success = true;
				break;
			}
			//Else expand node
			ArrayList<Action> applicable_action_list = matchApplicableActions(node);			
			for(Action action : applicable_action_list){
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
	
	private ArrayList<Action> matchApplicableActions(SearchNode node){
		ArrayList<Action> return_list = new ArrayList<Action>();
		Enumeration<String> e = domain_translated.list_actions.keys();
		while(e.hasMoreElements()){
			Action a = domain_translated.list_actions.get(e.nextElement().toString());
			if(node.canApply(a)){
				return_list.add(a);
			}
		}
		return return_list;
	}
	
	private SearchNode expand(SearchNode node, Action a) {
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
					discardPlan = true;
					//Create the 2 observations:
					ArrayList<SearchNode> list_transformed = node_plan.transformObservation(node_plan.generatedBy);
					for(SearchNode node_trans : list_transformed){
						node_trans = calculateHeuristic(node_trans);
						nodes_toReplan.add(node_trans);
					}
				}
				//Create the 2 observations:
				/*ArrayList<SearchNode> list_transformed = node_plan.transformObservation(node_plan.generatedBy);
				for(SearchNode node_trans : list_transformed){
					node_trans = calculateHeuristic(node_trans);
					nodes_toReplan.add(node_trans);
				}*/
				//discardPlan = true;
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
