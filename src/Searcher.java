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
	private Hashtable<Hashtable<String, Integer>, Integer> _VisitedNodes = new Hashtable<Hashtable<String, Integer>, Integer>();
	private Integer _GeneratedNodes = 0;
	private Integer i = 0;
	private ArrayList<Tupla> tupla_Solutions = new ArrayList<Searcher.Tupla>();
	//private graphplanner gp;
	
	public Searcher(){
		
	}
	
	private PriorityQueue<SearchNode> initFringe(){
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
			/*System.out.println("Plano: " + gp.last_layer);
			System.out.println("Valor heuristico: " + gp.heuristicValue());
			for(int i=1;i<=gp.last_layer;i+=2){
				System.out.println("Action level: " + gp._ActionPlan.get(i));
			}*/
			initialState.heuristicValue = gp.heuristicValue();
			initialState.fCost = initialState.heuristicValue + initialState.pathCost;
			PriorityQueue<SearchNode> fringe = initFringe();
			initialState._ActionsApplied = new Hashtable<String, Integer>();
			fringe.add(initialState);
			//aStarSearch(fringe);
			//aStar(initialState);
			//recursiveBFS(domain, initialState, Integer.MAX_VALUE);
			AndOrSearch(domain, initialState);
		}
	}
	
	public void aStarSearch(PriorityQueue<SearchNode> fringe){
		while(!fringe.isEmpty()){
			//node <- selectFrom(fringe)
			SearchNode node = fringe.poll();
			if(!_VisitedNodes.containsKey(node.state)){
				_VisitedNodes.put(node.state, 1);
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
				if(!node._ActionsApplied.containsKey(action.Name)){
					fringe.add(expand(node, action));
				}
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
			if(!_VisitedNodes.containsKey(next_node.state)){
				_VisitedNodes.put(next_node.state, 1);
			}
			else{
				continue;
			}
			if(goalTest(next_node)){
				returnPath(next_node);
				return;
			}
			if(next_node.isObservationNode){
				ArrayList<SearchNode> list_results_observations = next_node.transformObservation(next_node.generatedBy);
				boolean isFirst = true;
				for(SearchNode n : list_results_observations){
					graphplanner gp = new graphplanner(n.getState(), domain_translated.list_actions, domain_translated.goalState);
					n.heuristicValue = gp.heuristicValue();
					for(AbstractAction action : matchApplicableActions(n)){
						_GeneratedNodes++;
						if(!n._ActionsApplied.containsKey(action.Name)){
							SearchNode node_created = expand(n, action);
							if(!_VisitedNodes.containsKey(node_created.state)){
								fringe.add(node_created);
							}
						}						
					}
					_VisitedNodes.put(n.state, 1);
					SearchNode topNode = fringe.poll();
					if(topNode != null){
						while(_VisitedNodes.containsKey(topNode.state)){
							topNode = fringe.poll();
						}
						if(isFirst){
							System.out.println("Forking 1: " + n.generatedBy.Name + " cost: " + n.fCost);
							isFirst = false;
						}else{
							System.out.println("Forking 2: " + n.generatedBy.Name + " cost: " + n.fCost);
						}
						aStar(topNode);
					}
				}
				return;
			}
			//Else expand node
			for(AbstractAction action : matchApplicableActions(next_node)){
				if(!next_node._ActionsApplied.containsKey(action.Name)){
					SearchNode node_created = expand(next_node, action);
					if(!_VisitedNodes.containsKey(node_created.state)){
						_GeneratedNodes++;
						fringe.add(node_created);
					}
				}				
			}
		}
	}
	
	/** 
	 * Retorna solução ou palha e um novo limite f-custo
    */
	public ArrayList<Tupla> recursiveBFS(Domain domain, SearchNode node, Integer limit){
		//Verify if is goal
		if(goalTest(node)){
			System.out.println("This branch reached goal!");
			returnPath(node);
			Tupla return_t = new Tupla(node, 0);
			ArrayList<Tupla> tupla_return = new ArrayList<Tupla>();
			tupla_return.add(return_t);			
			return tupla_return;
		}
		//Fringe init
		PriorityQueue<SearchNode> fringe = initFringe();
		PriorityQueue<SearchNode> sucessor = initFringe();
		//Expand node
		for(AbstractAction action : matchApplicableActions(node)){
			if(!node._ActionsApplied.containsKey(action.Name)){
				SearchNode node_created = expand(node, action);
				if(!_VisitedNodes.containsKey(node_created.state)){
					_GeneratedNodes++;
					fringe.add(node_created);
				}
			}
		}
		if(fringe.isEmpty()){
			System.out.println("This branch failed because there are no more applicable actions!");
			return null;
		}
		for(SearchNode s : fringe){
			s.fCost = max(s.fCost, node.fCost);
			sucessor.add(s);
		}
		while(true){
			//Order successors according to f
			SearchNode best = sucessor.peek();
			//SearchNode best = sucessor.poll();
			if(best.fCost > limit){
				Tupla return_t = new Tupla(null, best.fCost);
				ArrayList<Tupla> tupla_return = new ArrayList<Tupla>();
				tupla_return.add(return_t);
				System.out.println("This branch failed!");
				return tupla_return;
			}
			Integer alternativa;
			if(sucessor.size()>1){
				//Segundo melhor fCost...o atual?
				//Eliminamos o primero da lista...e depois o colocamos de novo-> not good
				PriorityQueue<SearchNode> copy_Queue = new PriorityQueue<SearchNode>(sucessor);
				SearchNode first_best = copy_Queue.poll();
				SearchNode alternative_best = copy_Queue.peek();
				//sucessor.add(best);
				alternativa = alternative_best.fCost;
				//alternativa = sucessor.peek().fCost;
			}
			else{
				alternativa = Integer.MAX_VALUE;
			}
			if(best.isObservationNode){
				System.out.println("Observation Node selected: " + best.generatedBy.Name);
				ArrayList<SearchNode> list_results_observations = best.transformObservation(best.generatedBy);
				ArrayList<Tupla> tuplas_list = new ArrayList<Tupla>();
				for(SearchNode best_derived : list_results_observations){
					best_derived = calculateHeuristic(best_derived);
					System.out.println("Call " + i);
					i++;
					ArrayList<Tupla> tupla_return = recursiveBFS(domain, best_derived, min(limit, alternativa));
					for(Tupla t : tupla_return){
						if(t.node == null){
							best = sucessor.poll();
							best.fCost = t.limit;
							sucessor.add(best);
						}
					}
					tuplas_list.addAll(tupla_return);
				}
				for(int iter_tupla = 0; iter_tupla< tuplas_list.size(); iter_tupla++){
					Tupla t = tuplas_list.get(iter_tupla);
					if(t.node != null){
						tupla_Solutions.add(t);
						tuplas_list.remove(iter_tupla);
					}
				}
			}else{
				ArrayList<Tupla> tupla_return = recursiveBFS(domain, best, min(limit, alternativa));
				for(int iter_tupla = 0; iter_tupla< tupla_return.size(); iter_tupla++){
					Tupla t = tupla_return.get(iter_tupla);
					if(t.node != null){
						return tupla_return;
					}
				}
				//return tupla_return;
				/*if(tupla.node != null){
					ArrayList<Tupla> tupla_return = new ArrayList<Tupla>();
					tupla_return.add(tupla);
					return tupla_return;
				}*/
			}			
		}
	}
	
	public void AndOrSearch(Domain domain, SearchNode initialState){
		orSearch(domain, initialState);
	}
	
	private void orSearch(Domain domain, SearchNode node) {
		if(goalTest(node)){
			returnPath(node);
			return;
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
	
	private ArrayList<SearchNode> expandObservation(SearchNode node, AbstractAction a) {
		ArrayList<SearchNode> nodes_sucessor = new ArrayList<SearchNode>(node.expandObservation(a));
		for(SearchNode node_created : nodes_sucessor){
			if(node_created != null){
				graphplanner gp = new graphplanner(node_created.getState(), domain_translated.list_actions, domain_translated.goalState);
				if(!gp.fail){
					node_created.heuristicValue = gp.heuristicValue();
					node_created.pathCost = node.pathCost + 1;
					node_created.fCost = node_created.heuristicValue + node_created.pathCost;
				}
			}
		}
		return nodes_sucessor;
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

	private SearchNode calculateHeuristic(SearchNode n){
		graphplanner gp = new graphplanner(n.getState(), domain_translated.list_actions, domain_translated.goalState);
		n.heuristicValue = gp.heuristicValue();
		n.fCost = n.pathCost + n.heuristicValue;
		return n;
	}
	
	private Integer max(Integer i1, Integer i2){
		if(i1 >= i2){
			return i1;
		}else{
			return i2;
		}
	}
	
	private Integer min(Integer i1, Integer i2){
		if(i1 <= i2){
			return i1;
		}else{
			return i2;
		}
	}
	
	class Tupla{
		SearchNode node;
		Integer limit;
		
		Tupla(SearchNode s, Integer a){
	        this.node = s;
	        this.limit = a;
	    }
	}
	
	
}
