/**
 * 
 */
package simulators;

import java.util.ArrayList;
import java.util.Hashtable;

import pddlElements.Action;
import pddlElements.Domain;
import pddlElements.Effect;

/**
 * @author ignasi
 *
 */
public abstract class Simulator {

	protected Domain _Domain;
	protected ArrayList<String> _actionsApplied = new ArrayList<String>();
	//private static Hashtable<String, Integer> observations_Hash = new Hashtable<String, Integer>();
	protected Hashtable<String, ArrayList<String>> cellsObservations = new Hashtable<String, ArrayList<String>>();
	protected Hashtable<String, String> _PredictedObservations;
	
	public int simulate(Domain dom, ArrayList<String> plan){
		_Domain = dom;
		/*Status:
		 * 0: executing
		 * 1: success, exiting
		 * -1: failure, need to replan
		*/
		int size = plan.size();
		for(int i = 0;i<size;i++){
			if(execute(plan.get(i))==0){
				//System.out.println("Plan failed. Need to replan.");
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
			if(!a.IsObservation){
				closureAction();
				if(checkPreconditions(a)){
					executeAction(a);
					_actionsApplied.add(a.Name);
					senseWorld();
					closureAction();
					plotMap();
				}else{
					System.out.println("Action " + a.Name + " cannot be executed.");
					return 0;
				}
			}
			else{				
				//plotMap();
				String obsPredicted = _PredictedObservations.get(Action.toLowerCase());
				return checkObservations(obsPredicted);
			}
			return 1;
		}
		//System.out.println("Action " + Action + " not found. Possibly deductive translated action");
		return 1;
	}
	
	abstract int checkObservations(String obsPredicted);
	abstract void senseWorld();
	abstract void predictedObservations(Hashtable<String, String> obs);
	abstract void closureAction();
	abstract void plotMap();
	
	private boolean checkPreconditions(Action a){
		for(String precondition : a._precond){
			//if(!_Domain.hidden_state.containsKey(precondition)){
			//Since testing Kpredicates, the first letter must be eliminated:
			/*if(precondition.startsWith("Kn_")){
				precondition = "~" + precondition.substring(3);
			}
			else{
				precondition = precondition.substring(1);
			}*/
			if(!checkPredicateState(_Domain.state, precondition)){
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
				_Domain.hidden_state.remove(effect.substring(1));
				_Domain.state.remove(effect.substring(1));
				//System.out.println("Removing: " + effect);
			}else{
				_Domain.hidden_state.put(effect, 1);
				_Domain.state.put(effect, 1);
				//System.out.println("Adding: " + effect);
			}
		}
	}

	protected boolean checkConditionalEffect(Effect e) {
		for(String condition : e._Condition){
			//if(!_Domain.hidden_state.containsKey(condition)){
			if(!checkPredicateState(_Domain.state, condition)){
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
