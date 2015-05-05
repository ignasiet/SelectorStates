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
	public ArrayList<String> _parameters = new ArrayList<String>();
	public ArrayList<String> _precond = new ArrayList<String>();
	public ArrayList<String> _effect = new ArrayList<String>();
	public String Name = ""; 
	
	public Action(){
		
	}
	
	public void parseParameters(String parametersList){
		String last_object = "";
		ArrayList<String> lista_param = new ArrayList<String>();
		for(String predicate : parametersList.split(" ")){
			if(last_object.equals("-")){
				lista_param.remove(lista_param.size()-1);
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
		boolean isFirst = true;
		Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(preconditions_List);
	    while(m.find()) {	    	
    		_precond.add(cleanString(m.group(1)));
	    }
	}
	
	public void parseEffects(String effect_List){
		boolean isFirst = true;
		Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(effect_List);
	    while(m.find()) {	    	
	    	_effect.add(cleanString(m.group(1)));
	    }
	}
	
	private String cleanString(String a){
		a = a.replace("and", "").replaceAll("\\n", "").replaceAll("[()]", "").replace("not ", "~").replace(" ", "_").trim();
		return a;
	}
}
