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
public class Simulator {

	private static Domain _Domain;
	private static ArrayList<String> _actionsApplied = new ArrayList<String>();
	//private static Hashtable<String, Integer> observations_Hash = new Hashtable<String, Integer>();
	private static Hashtable<String, ArrayList<String>> cellsObservations = new Hashtable<String, ArrayList<String>>();
	
	public static void Wumpus(Domain dom){
		Enumeration<String> e = dom.hidden_state.keys();
		//1-Add state 
		while(e.hasMoreElements()){
			String keyPosition = e.nextElement().toString();
			//Wumpus only next line!
			if(!keyPosition.startsWith("adj")){
				int positionIndex = keyPosition.lastIndexOf("p");
				if(positionIndex>0){
					String auxPosition = keyPosition.substring(positionIndex);
					if(cellsObservations.containsKey(auxPosition)){
						ArrayList<String> auxList = cellsObservations.get(auxPosition);
						auxList.add(keyPosition);
						cellsObservations.put(auxPosition, auxList);
					}else{
						ArrayList<String> auxList = new ArrayList<String>();
						auxList.add(keyPosition);
						cellsObservations.put(auxPosition, auxList);
					}
				}
			}
		}
	}
	
	public static void simulate(Domain dom, ArrayList<String> plan){
		boolean success = false;
		_Domain = dom;
		int size = plan.size();
		for(int i = 0;i<size;i++){
			execute(plan.get(i));
			try {
				System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static void execute(String Action){
		//System.out.println("Action: " + Action);
		if(_Domain.list_actions.containsKey(Action.toLowerCase())){
			Action a = _Domain.list_actions.get(Action.toLowerCase());
			if(checkPreconditions(a)){
				System.out.println("Action executed: " + a.Name);
				if(!a.IsObservation){
					executeAction(a);
					_actionsApplied.add(a.Name);
				}
				senseWorld();
			}else{
				System.out.println("Action " + a.Name + " cannot be executed.");
			}			
		}
	}
	
	private static void senseWorld() {
		// TODO Auto-generated method stub
		String keyPosition = Wumpus_checkPosition();		
		if(cellsObservations.containsKey(keyPosition)){
			System.out.println("Sensing:");
			for(String pred : cellsObservations.get(keyPosition)){
				System.out.println("*: " + pred);
				_Domain.hidden_state.put(pred, 1);
			}
		}
	}
	
	private static String Wumpus_checkPosition(){
		String position = "";
		String lastAction = _actionsApplied.get(_actionsApplied.size()-1);
		Action a = _Domain.list_actions.get(lastAction.toLowerCase());
		if(a != null){
			for(Effect effects : a._Effects){
				for(String e : effects._Effects){
					if(e.startsWith("at_")){
						int positionIndex = e.lastIndexOf("p");
						position = e.substring(positionIndex);
					}
				}
			}
		}
		System.out.println("Agent at position: " + position);
		return position;
	}

	private static boolean checkPreconditions(Action a){
		for(String precondition : a._precond){
			if(!_Domain.hidden_state.containsKey(precondition)){
				return false;
			}
		}
		return true;
	}
	
	private static void executeAction(Action a){
		for(Effect e : a._Effects){
			if(e._Condition.isEmpty() || checkConditionalEffect(e)){
				applyEffect(e);
			}
		}
	}

	private static void applyEffect(Effect e) {
		for(String effect : e._Effects){
			if(effect.startsWith("~")){
				_Domain.hidden_state.remove(effect.substring(1));
				//System.out.println("Removing: " + effect);
			}else{
				_Domain.hidden_state.put(effect, 1);
				//System.out.println("Adding: " + effect);
			}
		}
	}

	private static boolean checkConditionalEffect(Effect e) {
		for(String condition : e._Condition){
			if(!_Domain.hidden_state.containsKey(condition)){
				return false;
			}
		}
		return true;
	}
}
