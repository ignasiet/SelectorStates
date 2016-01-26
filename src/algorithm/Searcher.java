/**
 * 
 */
package algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import elements.Goal;
import elements.State;
import landmarker.Graphplan;
import pddlElements.Action;
import pddlElements.Branch;
import pddlElements.Domain;
import pddlElements.Effect;


/**
 * @author ignasi
 *
 */
public class Searcher {
	
	private Domain _Domain;
	private Goal goal;
	private int nMax = 200;
	private Queue<State> _Fringe;
	private float w;
	public long totalTime = 0;
	private State lastVisited;
	private Hashtable<HashSet<String>, String> policy = new Hashtable<HashSet<String>, String>();
	private HashSet<HashSet<String>> statesHash = new HashSet<HashSet<String>>();
	
	public void lcdp(Domain domain, float weight){
		_Domain = domain;
		w = weight;
		goal = getGoalConditions(domain.goalState);
		State initialState = getInitialState(domain.state);
		//matchApplicableActions(initialState);
		_Fringe = initFringe();
		State s = initialState;
		long startTime = System.currentTimeMillis();
		while(!initialState.solved){
			s = lcdpTrial(s);
			if(s == null){
				s = markFailed(lastVisited);
			}else{
				s = checkSolved(s);
			}
		}
		long endTime = System.currentTimeMillis();
		totalTime = endTime - startTime;
		extractPolicy(initialState);
	}
	
	private void extractPolicy(State initialState) {
		if(policy.containsKey(initialState._State)){
			System.out.println("Best initial action: " + policy.get(initialState._State));
		}
		State s = applyAction(_Domain.list_actions.get(policy.get(initialState._State)), initialState);
		while(s != null){			
			if(!_Domain.list_actions.containsKey(policy.get(s._State))){
				System.out.println("Observations non consistent (randomly generated), lead to a dead-end:");
				System.out.println(s._State);
				break;
			}
			Action a = _Domain.list_actions.get(policy.get(s._State));
			System.out.println("Action: " + a.Name);
			if(!a._Branches.isEmpty()){
				System.out.println("Applying observation: ");
				Branch b = a._Branches.get(randInt());
				s = s.applyBranch(b);
			}else{
				s = applyAction(a, s);
			}
			if(goalTest(s)){
				System.out.println("GOAL reached");
				break;
			}
		}
	}
	
	public int randInt() {

	    // NOTE: This will (intentionally) not run as written so that folks
	    // copy-pasting have to think about how to initialize their
	    // Random instance.  Initialization of the Random instance is outside
	    // the main scope of the question, but some decent options are to have
	    // a field that is initialized once and then re-used as needed or to
	    // use ThreadLocalRandom (if using at least Java 1.7).
	    Random rand= new Random();
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((1 - 0) + 1) + 0;
	    return randomNum;
	}

	private Queue<State> initFringe(){
		//init Priority queue
		PriorityQueue<State> fringe = new PriorityQueue<State>(nMax, 
				new Comparator<State>(){
			public int compare(State lhs, State rhs) {
				return (int) (lhs.fCost - rhs.fCost);
			}
		});
		return fringe;
	}
	
	private State getInitialState(Hashtable<String, Integer> state) {
		State s = new State();
		Enumeration<String> e = state.keys();
		while(e.hasMoreElements()){
			s._State.add(e.nextElement().toString());
		}
		return s;
	}

	private Goal getGoalConditions(ArrayList<String> goalState) {
		Goal g = new Goal();
		g.goalState = goalState;
		return g;
	}

	@SuppressWarnings("unused")
	private State lcdpTrial(State s){
		//HashSet<HashSet<String>> _VisitedNodes = new HashSet<HashSet<String>>();
		//Stack<State> _VisitedNodes = new Stack<State>();
		while(!s.solved){
			//_VisitedNodes.push(s);
			statesHash.add(s._State);
			matchApplicableActions(s);
			if(goalTest(s)){
				s._BestAction = "GOAL!";
				s.solved = true;
				break;
			}
			//Heuristic step here:
			//Expand!
			expandState(s);
			s = pickNextBestState(s);
			if(s == null){
				break;
			}			
			while(statesHash.contains(s._State)){
				//System.out.println("Repeated state: " + s.getLastAction().Name);
				s = pickNextBestState(s);
				if(s == null){
					return null;
				}
			}
		}
		return s;
	}
	
	private State checkSolved(State s){
		while(s.getParent() != null){
			//System.out.println("Action: " + s._BestAction);
			if(s.testChildSolved()){
				policy.put(s._State, s._BestAction);
				s.solved = true;
				s.getParent()._BestAction = s.getLastAction().Name;
				s = s.getParent();
			}
			else{
				//System.out.println("Solving another branch!");
				s = s.getUnsolvedChild();
				cleanFringe();
				return s;
			}
		}
		if(s.getParent() == null){
			s.solved = true;
			policy.put(s._State, s._BestAction);
			return s;
		}
		return null;
	}
	
	private State pickNextBestState(State s) {
		//State s_old = s;
		State s_new = _Fringe.poll();
		if(s_new == null){
			//System.out.println("Dead-end detected!: failure planning: " + s._State);
			lastVisited = s;
			return null;
		}
		//s_old._BestAction = s_new.getLastAction().Name;
		State s_old = s_new.getParent();
		s_old._BestAction = s_new.getLastAction().Name;
		return s_new;
	}
	
	private State markFailed(State s){
		/*State last visited is a dead-end, so:
		 * 1 mark state s as fail
		 * 2 backtrack to its parent (s = s.parent) and 
		 * see if it has another state children not marked as failed
		 * 3 if yes, expand it, if not go back to 1
		*/
		while(s.getParent() != null){
			//System.out.println("Action: " + s._BestAction + " FAIL!");
			s.fail = true;
			s = s.getParent();
			State s_new = s.getUnvisitedChilds();
			if(s_new == null){
				s.fail = true;
			}else{
				if(s_new.solved){
					s.solved = true;
					return s;
				}				
			}
		}
		return null;
	}

	public void expandState(State s) {
		//Else expand node
		for(Action action : s.applicableActions){
			expand(action, s);
		}
	}
	
	private void expand(Action a, State s) {
		if(a._Branches.isEmpty()){
			State node_sucessor = applyAction(a, s);
			addStateHeuristic(node_sucessor, s);
		}else{
			for(Branch b : a._Branches){
				State node_sucessor = s.applyBranch(b);
				node_sucessor.setLastAction(a);
				addStateHeuristic(node_sucessor, s);
			}
		}
	}
	
	private void cleanFringe(){
		_Fringe.clear();
	}
	
	private void addStateHeuristic(State node_sucessor, State s){
		if(node_sucessor != null){
			Graphplan gp = new Graphplan(node_sucessor._State, _Domain.list_actions, _Domain.goalState);
			node_sucessor.heuristicValue = gp.heuristicValue * w;
			node_sucessor.pathCost = s.pathCost + 1;
			node_sucessor.fCost = node_sucessor.heuristicValue + node_sucessor.pathCost;
			if(!gp.getPlan().isEmpty()){
				node_sucessor._BestHeuristicAction = gp.getPlan().get(gp.getPlan().size()-1);
			}
			node_sucessor.setParent(s);
			s._NextStates.add(node_sucessor);
			//s._SuccessorStates.add(node_sucessor);
			_Fringe.add(node_sucessor);
		}
	}
	
	public State applyAction(Action a, State s) {
		State node_sucessor = new State();
		node_sucessor.setLastAction(a);
		node_sucessor._State = (HashSet<String>) s._State.clone();
		//1-Apply conditional effects:
		for(Effect conditionalEffect : a._Effects){
			if(s.isEffectApplicable(conditionalEffect)){
				for(String effectC : conditionalEffect._Effects){
					if(effectC.startsWith("~")){
						node_sucessor._State.remove(effectC.substring(1));
					}else{
						node_sucessor._State.add(effectC);
					}
				}
			}
		}
		return node_sucessor;
	}
	
	private void matchApplicableActions(State s){
		ArrayList<Action> return_list = new ArrayList<Action>();
		if(s._BestHeuristicAction != null){
			return_list.add(_Domain.list_actions.get(s._BestHeuristicAction));
		}else{
			Enumeration<String> e = _Domain.list_actions.keys();
			while(e.hasMoreElements()){
				Action a = _Domain.list_actions.get(e.nextElement().toString());
				if(s.canApply(a)){
					return_list.add(a);
				}
			}
		}		
		s.applicableActions = return_list;
	}
	
	/**Test for goal*/
	private boolean goalTest(State node){
		//TODO: transform to a BDD test
		for(String predicate : goal.goalState){
			if(!node.contains(predicate)){
				return false;
			}
		}
		return true;
	}
}
