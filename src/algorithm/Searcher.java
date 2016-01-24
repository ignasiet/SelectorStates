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
	private HashSet<HashSet<String>> statesHash = new HashSet<HashSet<String>>();
	
	public void lcdp(Domain domain, float weight){
		_Domain = domain;
		w = weight;
		goal = getGoalConditions(domain.goalState);
		State initialState = getInitialState(domain.state);
		//matchApplicableActions(initialState);
		_Fringe = initFringe();
		while(!initialState.solved){
			lcdpTrial(initialState);
		}
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
	private void lcdpTrial(State s){
		//HashSet<HashSet<String>> _VisitedNodes = new HashSet<HashSet<String>>();
		Stack<State> _VisitedNodes = new Stack<State>();
		while(!s.solved){			
			_VisitedNodes.push(s);
			statesHash.add(s._State);
			matchApplicableActions(s);
			if(goalTest(s)){
				s.solved = true;
				break;
			}
			//TODO: heuristic step here:
			//Expand!
			//Action a = GreedyAction(s, _Domain);
			GreedyAction(s);
			
			//s = s.pickNextState();
			s = _Fringe.poll();
			while(statesHash.contains(s._State)){
				s = _Fringe.poll();
			}
			//System.out.println(s.getLastAction().Name + " FValue: " + s.fCost);
			//System.out.println();
			//System.out.println("========================STEP " + s.pathCost + "====================================");
		}
		/*while(!_VisitedNodes.isEmpty()){
			s = _VisitedNodes.pop();
			s.solved = true;
			System.out.println("Action: " + s.getLastAction().Name);
		}*/
		while(s.getParent() != null){
			System.out.println("Action: " + s.getLastAction().Name);
			s = s.getParent();
			s.solved = true;			
		}
	}
	
	public void GreedyAction(State s) {
		//Else expand node
		for(Action action : s.applicableActions){
			expand(action, s);
		}
		//return _Fringe.peek().getLastAction();
	}
	
	private void expand(Action a, State s) {
		if(a._Branches.isEmpty()){
			State node_sucessor = applyAction(a, s);
			node_sucessor.setLastAction(a);
			//System.out.println(a.Name);
			addStateHeuristic(node_sucessor, s);
		}else{
			for(Branch b : a._Branches){
				State node_sucessor = s.applyBranch(b);
				node_sucessor.setLastAction(a);
				//System.out.println(a.Name);
				addStateHeuristic(node_sucessor, s);
				//_SuccessorStates.add(node_sucessor);
			}
		}
	}
	
	private void addStateHeuristic(State node_sucessor, State s){
		if(node_sucessor != null){
			Graphplan gp = new Graphplan(node_sucessor._State, _Domain.list_actions, _Domain.goalState);
			node_sucessor.heuristicValue = gp.heuristicValue * w;
			node_sucessor.pathCost = s.pathCost + 1;
			node_sucessor.fCost = node_sucessor.heuristicValue + node_sucessor.pathCost;
			node_sucessor.setParent(s);
			//s._SuccessorStates.add(node_sucessor);
			_Fringe.add(node_sucessor);
		}
	}
	
	public State applyAction(Action a, State s) {
		State node_sucessor = new State();
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
		Enumeration<String> e = _Domain.list_actions.keys();
		while(e.hasMoreElements()){
			Action a = _Domain.list_actions.get(e.nextElement().toString());
			if(s.canApply(a)){
				return_list.add(a);
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
