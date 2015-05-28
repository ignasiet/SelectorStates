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

	private Hashtable<String, ArrayList<String>> solution = new Hashtable<String, ArrayList<String>>();
	private Hashtable<String, String> observations = new Hashtable<String, String>();
	
	public Hashtable<String, SolutionNode> nodes_list = new Hashtable<String, SolutionNode>();
	public SolutionNode node_solution = new SolutionNode("root");
	
	public SolutionPlan() {
		//list_nodes.put("root", root);
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
	
	public void put_action(String action, String sucessor){
		if(action.equals(sucessor)){
			return;
		}
		ArrayList<String> action_list = new ArrayList<String>();
		if(solution.containsKey(action)){
			action_list = solution.get(action);
			if(!action_list.contains(sucessor)){
				action_list.add(sucessor);
			}
		}else{
			action_list.add(sucessor);
		}
		solution.put(action, action_list);
	}
	
	public void put_observation(String action, String parent){
		observations.put(action, parent);
	}
	
	public boolean has_key(String key){
		return solution.containsKey(key);
	}
	
	public ArrayList<String> NextAction(String key){
		return solution.get(key);
	}
	
	public String getObservation(String action){
		return observations.get(action);
	}

	public void updateParents(String name, SolutionNode node_action) {
		if(!nodes_list.containsKey(name)){
			nodes_list.put(name, node_action);
		}
		else{
			SolutionNode sNode = nodes_list.get(name);
			sNode.addSucessor(node_action);
		}
	}
}
