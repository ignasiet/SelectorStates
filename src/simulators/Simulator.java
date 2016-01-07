/**
 * 
 */
package simulators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import pddlElements.Action;
import pddlElements.Domain;
import pddlElements.Effect;
import planner.Planner;

/**
 * @author ignasi
 *
 */
public abstract class Simulator {

	protected Domain _Domain;
	protected ArrayList<String> _actionsApplied = new ArrayList<String>();
	//private static Hashtable<String, Integer> observations_Hash = new Hashtable<String, Integer>();
	protected Hashtable<String, ArrayList<String>> cellsObservations = new Hashtable<String, ArrayList<String>>();
	
	
	public int simulate(Domain dom, ArrayList<String> plan){
		boolean success = false;
		_Domain = dom;
		/*Status:
		 * 0: executing
		 * 1: success, exiting
		 * -1: failure, need to replan
		*/
		int status = 0;
		int size = plan.size();
		for(int i = 0;i<size;i++){
			if(execute(plan.get(i))==0){
				//System.out.println("Plan failed. Need to replan.");
				status = -1;
				return -1;
			}
			/*try {
				System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		return 1;
	}
	
	private int execute(String Action){
		//System.out.println("Action: " + Action);
		if(_Domain.list_actions.containsKey(Action.toLowerCase())){
			System.out.println("Executing: " + Action);
			Action a = _Domain.list_actions.get(Action.toLowerCase());
			if(checkPreconditions(a)){				
				if(!a.IsObservation){
					executeAction(a);
					_actionsApplied.add(a.Name);
					senseWorld();
					closureAction();
					plotMap();
				}
				return 1;
			}else{
				System.out.println("Action " + a.Name + " cannot be executed.");
				return 0;
			}
		}
		//System.out.println("Action " + Action + " not found. Possibly deductive translated action");
		return 1;
	}
	
	abstract void senseWorld();
	abstract void closureAction();
	abstract void plotMap();
	
	private boolean checkPreconditions(Action a){
		for(String precondition : a._precond){
			//if(!_Domain.hidden_state.containsKey(precondition)){
			//Since testing Kpredicates, the first letter must be eliminated
			if(!checkPredicateState(_Domain.hidden_state, precondition)){
				return false;
			}
		}
		return true;
	}
	
	private void executeAction(Action a){
		for(Effect e : a._Effects){
			if(e._Condition.isEmpty() || checkConditionalEffect(e)){
				applyEffect(e);
			}
		}
	}

	private void applyEffect(Effect e) {
		for(String effect : e._Effects){
			if(effect.startsWith("~")){
				_Domain.hidden_state.put(effect.substring(1), 0);
				_Domain.state.put(effect.substring(1), 0);
				//System.out.println("Removing: " + effect);
			}else{
				_Domain.hidden_state.put(effect, 1);
				_Domain.state.put(effect, 1);
				//System.out.println("Adding: " + effect);
			}
		}
	}

	private boolean checkConditionalEffect(Effect e) {
		for(String condition : e._Condition){
			//if(!_Domain.hidden_state.containsKey(condition)){
			if(!checkPredicateState(_Domain.hidden_state, condition)){
				return false;
			}
		}
		return true;
	}
	
	protected boolean checkPredicateState(Hashtable<String, Integer> state, String predicate){
		if(predicate.startsWith("~")){
			if(state.containsKey(predicate.substring(1))){
				return false;
			}
		}else{
			if(!state.containsKey(predicate)){
				return false;
			}
		}
		return true;
	}
}
