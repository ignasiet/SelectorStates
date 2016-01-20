/**
 * 
 */
package algorithm;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

import elements.Goal;
import elements.State;
import pddlElements.Action;
import pddlElements.Domain;


/**
 * @author ignasi
 *
 */
public class Searcher {
	
	private Domain _Domain;
	private Goal goal;
	
	public void lcdp(Domain domain){
		_Domain = domain;
		goal = getGoalConditions(domain.goalState);
		State initialState = getInitialState(domain.state);
		matchApplicableActions(initialState);
		while(!initialState.solved){
			lcdpTrial(initialState);
		}
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
			if(goalTest(s)){
				break;
			}
			//TODO: heuristic step here
			Action a = s.GreedyAction(_Domain);
			//s.update();
			s = s.pickNextState();
		}
		while(!_VisitedNodes.isEmpty()){
			s = _VisitedNodes.pop();
			/*if(!checksolved(s)){
				break;
			}*/
		}
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
