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
		State s = initialState;
		while(!initialState.solved){
			s = lcdpTrial(s);
			s = checkSolved(s);
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
	private State lcdpTrial(State s){
		//HashSet<HashSet<String>> _VisitedNodes = new HashSet<HashSet<String>>();
		//Stack<State> _VisitedNodes = new Stack<State>();
		while(!s.solved){
			//_VisitedNodes.push(s);
			statesHash.add(s._State);
			matchApplicableActions(s);
			if(goalTest(s)){
				s.solved = true;
				break;
			}
			//Heuristic step here:
			//Expand!
			expandState(s);			
			s = pickNextBestState();
			
			while(statesHash.contains(s._State)){
				//System.out.println("Repeated state: " + s.getLastAction().Name);
				s = pickNextBestState();
			}
		}
		return s;
	}
	
	private State checkSolved(State s){
		while(s.getParent() != null){
			System.out.println("Action: " + s._BestAction);
			if(s.testChildSolved()){
				s.solved = true;
				s.getParent()._BestAction = s.getLastAction().Name;
				s = s.getParent();
			}
			else{
				System.out.println("Solving another branch!");
				s = s.getUnsolvedChild();
				//lcdpTrial(s);
				cleanFringe();
				return s;
			}
		}
		return null;
	}
	
	private State pickNextBestState() {
		//State s_old = s;
		State s_new = _Fringe.poll();
		//s_old._BestAction = s_new.getLastAction().Name;
		State s_old = s_new.getParent();
		s_old._BestAction = s_new.getLastAction().Name;
		return s_new;
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
