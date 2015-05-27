import java.util.ArrayList;
import java.util.Hashtable;

/**
 * 
 */

/**
 * @author ignasi
 *
 */
public class SolutionPlan {

	//private Hashtable<String, ArrayList<String>> solution = new Hashtable<String, ArrayList<String>>();
	private Hashtable<String, ArrayList<String>> solution = new Hashtable<String, ArrayList<String>>();
	private ArrayList<String> plan = new ArrayList<String>();
	
	public SolutionPlan() {
		
	}
	
	public void AddNode(String action, String parent_action){
		if(!solution.containsKey(parent_action)){
			ArrayList<String> listaIndividual = new ArrayList<String>();
			listaIndividual.add(action);
			solution.put(parent_action, listaIndividual);
		}else{
			ArrayList<String> listaIndividual = solution.get(parent_action);
			if(!listaIndividual.contains(action)){
				listaIndividual.add(action);
				solution.put(parent_action, listaIndividual);
			}
		}
	}

}
