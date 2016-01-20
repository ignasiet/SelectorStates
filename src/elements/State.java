/**
 * 
 */
package elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;

import landmarker.Graphplan;
import pddlElements.Action;
import pddlElements.Branch;
import pddlElements.Domain;
import pddlElements.Effect;

/**
 * @author ignasi
 *
 */
public class State {
	private int nMax = 200;
	public HashSet<String> _State;
	public int heuristicValue;
	public int pathCost;
	public int fCost;
	public boolean solved;
	public ArrayList<Action> applicableActions;
	private Queue<State> _SuccessorStates;
	private Action _LastAction;
	
	public State(){
		_State = new HashSet<String>();
		heuristicValue = Integer.MAX_VALUE;
		pathCost = 0;
		fCost = 0;
		solved = false;
		_SuccessorStates = initFringe();
	}
	
	private Queue<State> initFringe(){
		//init Priority queue
		PriorityQueue<State> fringe = new PriorityQueue<State>(nMax, 
				new Comparator<State>(){
			public int compare(State lhs, State rhs) {
				return lhs.fCost - rhs.fCost;
			}
		});
		return fringe;
	}
	
	public boolean contains(String pred){
		if(_State.contains(pred)){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean canApply(Action a) {
		for(String precondition : a._precond){
			if(!contains(precondition)){
				return false;
			}
		}
		return true;
	}

	public Action GreedyAction(Domain domain) {
		//Else expand node
		for(Action action : applicableActions){
			expand(action, domain);
		}
		return _SuccessorStates.poll()._LastAction;
	}
	
	private void expand(Action a, Domain domain) {
		if(a._Branches.isEmpty()){
			State node_sucessor = applyAction(a);
			if(node_sucessor != null){
				Graphplan gp = new Graphplan(node_sucessor._State, domain.list_actions, domain.goalState);			
				node_sucessor.heuristicValue = gp.heuristicValue;
				node_sucessor.pathCost = pathCost + 1;
				node_sucessor.fCost = node_sucessor.heuristicValue + node_sucessor.pathCost;			
				_SuccessorStates.add(node_sucessor);
				//return node_sucessor;
			}
		}else{
			for(Branch b : a._Branches){
				State node_sucessor = applyBranch(b);
				_SuccessorStates.add(node_sucessor);
			}
		}
	}
	
	private State applyBranch(Branch b) {
		State sucessorNode = new State();
		sucessorNode._State = _State;
		for(String effectC : b._Branches){
			if(effectC.startsWith("~")){
				sucessorNode._State.remove(effectC.substring(1));
			}else{
				sucessorNode._State.add(effectC);
			}
		}
		return sucessorNode;
	}

	public State applyAction(Action a) {
		State node_sucessor = new State();
		node_sucessor._State = _State;
		//1-Apply conditional effects:
		for(Effect conditionalEffect : a._Effects){
			if(isEffectApplicable(conditionalEffect)){
				for(String effectC : conditionalEffect._Effects){
					if(effectC.startsWith("~")){
						node_sucessor._State.remove(effectC.substring(1));
					}else{
						node_sucessor._State.add(effectC);
					}
				}
			}
		}			
		_LastAction = a;
		return node_sucessor;
	}
	
	/**Verify if the conditional effect is applied*/
	private boolean isEffectApplicable(Effect e){
		if(e._Condition.isEmpty()){
			return true;
		}else{
			for(String precondition : e._Condition){
				if(!precondition.startsWith("~")){
					if(!_State.contains(precondition)){
						//System.out.println(a.Name);
						return false;
					}
				}else {
					if(_State.contains(precondition.substring(1))){
						return false;
					}
				}
			}
			return true;
		}		
	}

	
	public State pickNextState() {
		return _SuccessorStates.poll();
	}
	
	
}
