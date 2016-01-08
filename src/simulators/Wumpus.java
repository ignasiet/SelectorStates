package simulators;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pddlElements.Action;
import pddlElements.Axiom;
import pddlElements.Domain;
import pddlElements.Effect;

public class Wumpus extends Simulator{
	private int size = 0;
	private String[][] map;
	private String xPosition ="";
	private String yPosition ="";
	
	public Wumpus(Domain dom){
		_Domain = dom;
		initMap();
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

	private String checkPosition(){
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
		//System.out.println("Agent at position: " + position);
		plotMap(position);
		return position;
	}
	
	protected void plotMap(){
		//System.out.print("\n\n\n\n\n\n\n\n\n\n");
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
	    	if(event.startsWith("stench")){
	    		map[xPos][yPos]="~";
	    	}
	    	if(event.startsWith("wumpus")){
	    		map[xPos][yPos]="W";
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
	}
	
	private void checkAxioms(){
		for(Axiom axiom : _Domain._Axioms){
			if(testPreconditionsAxiom(axiom._Body)){
				for(String pred : axiom._Head){
					//System.out.println("Deducted: " + pred);
					//_Domain.state.put(pred, 1);
					if(pred.startsWith("~")){
						if(_Domain.state.containsKey(pred.substring(1))){
							_Domain.state.remove(pred.substring(1));
							updateEvents(pred.substring(pred.lastIndexOf("p")), pred);
							System.out.println("Deducting: " + pred);
						}
					}else{
						if(!_Domain.state.containsKey(pred)){
							_Domain.state.put(pred, 1);
							updateEvents(pred.substring(pred.lastIndexOf("p")), pred);
							System.out.println("Deducting: " + pred);
						}
					}
				}
			}
		}
	}
	
	private void checkInitialDisjunctions(){
		//TODO: check oneof exclusions!
	}
	
	private boolean testPreconditionsAxiom(ArrayList<String> cond){
		for(String condition : cond){
			if(condition.startsWith("~") || !_Domain.state.containsKey(condition)){
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
		if(checkPredicateState(_Domain.state, observation)){
			//System.out.println("Observation predicted: " + observation);
			return 1;
		}else{
			System.out.println("Error in plan, observation prediction was: " + observation + " failed! Replaning!");
			return 0;
		}
	}
}
