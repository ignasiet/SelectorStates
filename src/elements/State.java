/**
 * 
 */
package elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
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
	public float heuristicValue;
	public int pathCost;
	public float fCost;
	public boolean solved;
	public boolean fail;
	public ArrayList<Action> applicableActions;
	//public Queue<State> _SuccessorStates;
	public ArrayList<State> _NextStates = new ArrayList<State>();
	//public ArrayList<State> _ChildStates = new ArrayList<State>();
	private Action _LastAction;
	public String _BestAction;
	private State parent;
	public String _BestHeuristicAction;
	
	public State(){
		_State = new HashSet<String>();
		heuristicValue = Integer.MAX_VALUE;
		pathCost = 0;
		fCost = 0;
		solved = false;
		//_SuccessorStates = initFringe();
	}
	
	/*private Queue<State> initFringe(){
		//init Priority queue
		PriorityQueue<State> fringe = new PriorityQueue<State>(nMax, 
				new Comparator<State>(){
			public int compare(State lhs, State rhs) {
				return (int) (lhs.fCost - rhs.fCost);
			}
		});
		return fringe;
	}*/
	
	public boolean contains(String pred){
		if(_State.contains(pred)){
			return true;
		}else{
			return false;
		}
	}
	
	public State getUnvisitedChilds(){
		for(State s : _NextStates){
			if(!s.fail){
				return s;
			}
		}
		return null;
	}
	
	public State getUnsolvedChild(){
		for(State s : _NextStates){
			//Select states expanded
			if(s.getParent()._BestAction.equals(s._LastAction.Name)){
				if(!s.solved && !s.fail){
					return s;
				}
			}
		}
		return null;
	}
	
	public boolean testChildSolved(){
		if(solved){
			return true;
		}
		for(State s : _NextStates){
			//Select states expanded
			if(s.getParent()._BestAction.equals(s._LastAction.Name)){
				if(!s.solved){
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean canApply(Action a) {
		for(String precondition : a._precond){
			if(precondition.startsWith("~")){
				if(contains(precondition.substring(1))){
					return false;
				}
			}else{
				if(!contains(precondition)){
					return false;
				}
			}
		}
		return true;
	}

	public State applyBranch(Branch b) {
		State sucessorNode = new State();
		sucessorNode._State =  (HashSet<String>) _State.clone();
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
		node_sucessor._State = (HashSet<String>) _State.clone();
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
		return node_sucessor;
	}
	
	/**Verify if the conditional effect is applied*/
	public boolean isEffectApplicable(Effect e){
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
	
	/*public State pickNextState() {
		return _SuccessorStates.poll();
	}*/

	public Action getLastAction() {
		return _LastAction;
	}

	public void setLastAction(Action _LastAction) {
		this._LastAction = _LastAction;
	}

	public State getParent() {
		return parent;
	}

	public void setParent(State parent) {
		this.parent = parent;
	}
	
	
}
