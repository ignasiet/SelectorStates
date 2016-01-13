package simulators;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pddlElements.Action;
import pddlElements.Axiom;
import pddlElements.Disjunction;
import pddlElements.Domain;
import pddlElements.Effect;

public class Wumpus extends Simulator{
	private int size = 0;
	private String[][] map;
	//private String xPosition ="";
	//private String yPosition ="";
	
	public Wumpus(Domain dom){
		_Domain = dom;
		initMap();
		Enumeration<String> e = dom.hidden_state.keys();
		//1-Add state 
		while(e.hasMoreElements()){
			String keyPosition = e.nextElement().toString();
			//Wumpus only next line!
			if(!keyPosition.contains("adj")){
				int positionIndex = keyPosition.lastIndexOf("p");
				if(positionIndex>0){
					String auxPosition = keyPosition.substring(positionIndex);
					if(cellsObservations.containsKey(auxPosition)){
						ArrayList<String> auxList = cellsObservations.get(auxPosition);
						auxList.add("K"+keyPosition);
						cellsObservations.put(auxPosition, auxList);
					}else{
						ArrayList<String> auxList = new ArrayList<String>();
						auxList.add("K"+keyPosition);
						cellsObservations.put(auxPosition, auxList);
					}
				}
			}
		}
	}

	private String checkPosition(){
		String position = "";
		String lastAction = _actionsApplied.get(_actionsApplied.size()-1);
		Action a = _Domain.list_actions.get(lastAction.toLowerCase());
		if(a != null){
			for(Effect effects : a._Effects){
				for(String e : effects._Effects){
					if(e.startsWith("Kat_")){
						int positionIndex = e.lastIndexOf("p");
						position = e.substring(positionIndex);
					}
				}
			}
		}
		//System.out.println("Agent at position: " + position);
		plotMap(position);
		return position;
	}
	
	protected void plotMap(){
		//System.out.print("\b\b\b\b\b");
		System.out.println("\f");
		/*try {
			Runtime.getRuntime().exec("clear");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				System.out.print(map[i][j] + "\t");
			}
			System.out.println();
		}
	}
	
	private void plotMap(String position) {		
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				if(map[i][j].equals("A")){
					map[i][j] = "-";
				}
			}
		}
		Matcher m = Pattern.compile(".([0-9][0-9]*)-([0-9][0-9]*)").matcher(position);			
	    while(m.find()) {
	    	int xPos = Integer.parseInt(m.group(1).trim())-1;
	    	int yPos = Integer.parseInt(m.group(2).trim())-1;
	    	map[xPos][yPos]="A";
	    }
	}
	
	private void updateEvents(String position, String event){
		Matcher m = Pattern.compile(".([0-9][0-9]*)-([0-9][0-9]*)").matcher(position);			
	    while(m.find()) {
	    	int xPos = Integer.parseInt(m.group(1).trim())-1;
	    	int yPos = Integer.parseInt(m.group(2).trim())-1;
	    	if(event.startsWith("Kstench")){
	    		map[xPos][yPos]="~";
	    	}
	    	if(event.startsWith("Kwumpus")){
	    		map[xPos][yPos]="W";
	    	}
	    	if(event.startsWith("Kbreeze")){
	    		map[xPos][yPos]="S";
	    	}
	    }
	}

	protected void senseWorld() {
		String keyPosition = checkPosition();		
		if(cellsObservations.containsKey(keyPosition)){
			System.out.println("Sensing:");
			for(String pred : cellsObservations.get(keyPosition)){
				System.out.println("*: " + pred);
				_Domain.hidden_state.put(pred, 1);
				_Domain.state.put(pred, 1);
				updateEvents(keyPosition, pred);
			}
		}
	}
	
	private void initMap(){
		size = Integer.parseInt(_Domain.ProblemInstance.substring(_Domain.ProblemInstance.indexOf("-")+1));
		map = new String[size][size];
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				map[i][j]="*";
			}
		}
		map[0][0] = "I";
		map[size-1][size-1]="G";
	}
	
	protected void closureAction() {
		checkAxioms();
		checkInitialDisjunctions();
	}
	
	private void checkAxioms(){
		for(Axiom axiom : _Domain._Axioms){
			if(testPreconditionsAxiom(axiom._Body)){
				for(String pred : axiom._Head){
					if(!_Domain.state.containsKey(pred)){
						_Domain.state.put(pred, 1);
						updateEvents(pred.substring(pred.lastIndexOf("p")), pred);
						System.out.println("Deducting: " + pred);
					}					
				}
				
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void checkInitialDisjunctions(){
		//TODO: check oneof exclusions!
		for(Effect eff :_Domain.disjunctionAction._Effects){
			if(checkConditionalEffect(eff)){
				//System.out.println("Deducting: if " + eff._Condition.toString() + " is true, then " + eff._Effects.toString());
				for(String effect : eff._Effects){
					_Domain.state.put(effect, 1);
				}
			}
		}
	}
	
	private boolean testPreconditionsAxiom(ArrayList<String> cond){
		for(String condition : cond){
			if(!_Domain.state.containsKey(condition)){
				return false;
			}
			/*else{
				if(_Domain.state.containsKey(condition)){
					return true;
				}
			}*/
		}
		return true;
	}

	public void predictedObservations(Hashtable<String, String> obs) {
		_PredictedObservations = obs;		
	}

	
	protected int checkObservations(String observation) {
		String planedObservation = observation;
		if(observation.startsWith("k")){
			//TODO: change to lower case in translation?
			observation = observation.replace("k", "K");
			observation = observation.replace("n_", "~");
		}
		int error = 0;
		if(observation.startsWith("K~")){
			if(_Domain.state.containsKey(observation.replace("~", ""))){
				System.out.println("Error in plan, observation predicted was: " + planedObservation + " and turned to be "+ observation + " failed! Replaning!");
				error = 1;
			}
		}else{
			if(!_Domain.state.containsKey(observation)){
				observation = observation.replace("K", "K~");
				System.out.println("Error in plan, observation predicted was: " + planedObservation + " and turned to be "+ observation + " failed! Replaning!");
				_Domain.hidden_state.put(observation, 1);
				_Domain.state.put(observation, 1);
				error = 1;
			}
		}
		if(error > 0){
			closureAction();
			return 0;
		}else{
			_Domain.hidden_state.put(observation, 1);
			_Domain.state.put(observation, 1);
			closureAction();
		}
		return 1;
	}
}
