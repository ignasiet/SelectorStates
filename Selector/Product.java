import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;


public class Product {
	
	private static Hashtable<String, ArrayList> constantes = new Hashtable<String, ArrayList>();
	private static Hashtable<String, Action> list_actions = new Hashtable<String, Action>();
	
	
	public static void main(String[] args){
		/*extract("(:objects o1 o2 - obj p1-1 p1-2 p2-1 p2-2 - pos red blue purple green - col t1 t2 t3 t4 - gar )");
		
		System.out.println("Hashtable: " + constantes.get("col").toString());
		Action action = new Action();
		action.Name = "observe-ball";
		action._parameters.add("?pos");
		action._parameters.add("?o");
		action.action_parameters.put("?pos", "pos");
		action.action_parameters.put("?o", "obj");		
		action._precond.add("at_?pos");
		action._effect.add("obj-at_?o_?pos");
		ground_actions(action);*/
		Planner.startPlanner();
	}
	
	public static void extract(String objects){
		String[] splited_objects = objects.split(" ");
		String last_object = "";
		ArrayList<String> lista_objetos = new ArrayList<String>(Arrays.asList(splited_objects));
		ArrayList<String> lista_predicados = new ArrayList<String>();
		lista_objetos.remove(0);
		for(String predicate : lista_objetos){
			if(last_object.equals("-")){
				lista_predicados.remove(lista_predicados.size()-1);
				ArrayList<String> lista_b = new ArrayList<String>(lista_predicados);
				constantes.put(predicate, lista_b);
				lista_predicados.clear();
			}
			else{
				lista_predicados.add(predicate);				
			}
			last_object = predicate;
		}
	}
		
	public static ArrayList<String> product(ArrayList<String> list1, ArrayList<String> list2){
		if(list2.isEmpty()){
			return list1;
		}
		else{
			ArrayList<String> result = new ArrayList<String>();
			for(String element1 : list1){
				for(String element2: list2){
					if(!element1.equals(element2)){
						result.add(element1 + ";" + element2);
					}
				}
			}
			return result;
		}
	}

	public static void ground_actions(Action action){
		ArrayList<String> result = new ArrayList<String>();
		//Hashtable<String, String> substitution = new Hashtable<String, String>();
		Enumeration e = action.action_parameters.keys();
		while(e.hasMoreElements()){
			String parameter = e.nextElement().toString();
			result = product(constantes.get(action.action_parameters.get(parameter)), result);
		}
		for(String combination : result){
			System.out.println(combination);
			Action act_grounded = new Action();
			act_grounded.Name = action.Name + "_" + combination.replace(";", "_");
			ArrayList<String> lista_objetos = new ArrayList<String>(Arrays.asList(combination.split(";")));
			int i = 0;
			String eff = action._effect.toString().replace("[", "").replace("]", "");
			String precond = action._precond.toString().replace("[", "").replace("]", "");
			for(String parameter : action._parameters){
				//String parameter = e.nextElement().toString();
				eff = eff.replace(parameter, lista_objetos.get(i));
				precond = precond.replace(parameter, lista_objetos.get(i));
				i++;
			}
			ArrayList<String> lista_efeitos = new ArrayList<String>(Arrays.asList(eff.split(",")));
			ArrayList<String> lista_precond = new ArrayList<String>(Arrays.asList(precond.split(",")));
			act_grounded._effect = lista_efeitos;				
			act_grounded._precond = lista_precond;
			list_actions.put(act_grounded.Name, act_grounded);
		}
	}
	
}
