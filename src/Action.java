import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Action {
	public Hashtable<String, ArrayList<String>> action_parameters = new Hashtable<String, ArrayList<String>>();
	public Hashtable<String, String> parameters_type = new Hashtable<String, String>();
	public ArrayList<String> _parameters = new ArrayList<String>();
	public ArrayList<String> _precond = new ArrayList<String>();
	public ArrayList<String> _Positive_effects = new ArrayList<String>();
	public ArrayList<String> _Negative_effects = new ArrayList<String>();
	public String Name = ""; 
	
	public Action(){
		
	}
	
	public void parseParameters(String parametersList){
		String last_object = "";
		ArrayList<String> lista_param = new ArrayList<String>();
		for(String predicate : parametersList.split(" ")){
			if(last_object.equals("-")){
				lista_param.remove(lista_param.size()-1);
				for(String s : lista_param){
					parameters_type.put(s, predicate);
					_parameters.add(s);
				}
				ArrayList<String> lista_b = new ArrayList<String>(lista_param);
				if(!action_parameters.containsKey(predicate)){
					action_parameters.put(predicate, lista_b);
				}
				else{
					lista_b.addAll(action_parameters.get(predicate));
					action_parameters.put(predicate, lista_b);
				}
				lista_param.clear();
			}
			else{
				lista_param.add(predicate);				
			}
			last_object = predicate;
		}
	}
	
	public void parsePreconditions(String preconditions_List){
		Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(preconditions_List);
	    while(m.find()) {	    	
    		_precond.add(Planner.cleanString(m.group(1)));
	    }
	}
	
	public void parseEffects(String effect_List){
		Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(effect_List);
	    while(m.find()) {
	    	String effect = Planner.cleanString(m.group(1));
	    	if(effect.startsWith("~")){
	    		_Negative_effects.add(effect);
	    	}else{
	    		_Positive_effects.add(effect);
	    	}	    	
	    }
	}
}
