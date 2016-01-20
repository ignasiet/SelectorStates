package landmarker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import pddlElements.*;
/**
 * 
 * @author ignasi
 *
 */

public class Graphplan {
	
	private ArrayList<StepLandmark> _Steps = new ArrayList<StepLandmark>();
	private ArrayList<String> _Actions_list = new ArrayList<String>();
	private Hashtable<String, Action> _Actions = new Hashtable<String, Action>();
	private HashSet<String> _State = new HashSet<String>();
	//private Hashtable<String, Integer> _Invariants = new Hashtable<String, Integer>();
	private Hashtable<String, Integer> _GoalsAchieved = new Hashtable<String, Integer>();
	private ArrayList<String> _Goal = new ArrayList<String>();
	protected ArrayList<String> _Plan = new ArrayList<String>();
	protected Hashtable<Integer, ArrayList<String>> _ActionPlan = new Hashtable<Integer, ArrayList<String>>();
	protected Integer last_layer = 0;
	protected boolean fail = false;
	public int heuristicValue = 100000000;

	public Graphplan(HashSet<String> state, Hashtable<String, Action> Actions, ArrayList<String> goal) {
		_State = state;
		_Actions = Actions;
		_Goal = goal;
		//_Invariants = invariants;
		initActionList();
		expand();
		//cleanProblem();
		//heuristicValue = heuristicGraphPlan();
	}
	
	public void initActionList(){
		Enumeration enumerator_actions = _Actions.keys();
		while(enumerator_actions.hasMoreElements()){
			String aName = enumerator_actions.nextElement().toString();
			//System.out.println(aName);
			_Actions_list.add(aName);
		}
	}
	
	public boolean isGoal(StepLandmark predicates){
		for(String pred : _Goal){
			if(!predicates.Contains(pred)){
				return false;
			}
		}
		return true;
	}
	
	private void expand(){
		Hashtable<String,Integer> layerMembershipActions = new Hashtable<String,Integer>();
		Hashtable<String,Integer> layerMembershipFacts = new Hashtable<String,Integer>();
		//Init state
		Enumeration<String> e = Collections.enumeration(_State);
	    while(e.hasMoreElements()){
	    	String p = e.nextElement().toString();
	    	//NodeLandmark n = new NodeLandmark(p);
	    	//n.level = 0;
	    	layerMembershipFacts.put(p, 0);
	    }
	    int i = 0;
		while(!containsGoal(layerMembershipFacts) && i < _Actions.size()){
			ArrayList<String> _actions = new ArrayList<>();
			for(String action_name : _Actions_list){
				Action a = _Actions.get(action_name);
				if(isActionApplicable(a, layerMembershipFacts, i)){
					_actions.add(action_name);
				}
			}
		}
	}
	
	private boolean isActionApplicable(Action a, Hashtable<String, Integer> layerMembershipFacts, int i) {
		for(String precondition : a._precond){
			if(!precondition.startsWith("~")){
				//Verificar que contem a precondição
				if(!layerMembershipFacts.containsKey(precondition)){
					return false;
				}else{
					//e que a precondição foi adicionada anteriormente (nao nesta rodada)
					if(layerMembershipFacts.get(precondition) > i){
						return false;
					}
				}
			}else{
				return false;
			}
		}
		return true;
	}

	private boolean containsGoal(Hashtable<String, Integer> layerMembershipFacts) {
		for(String pred : _Goal){
			if(!layerMembershipFacts.containsKey(pred)){
				return false;
			}
		}
		return true;
	}

	public void expandStep(StepLandmark predicates_list) {
		// 1 expand actions if possible (applicable)
		StepLandmark ActionStep = new StepLandmark();
		StepLandmark PredicateStep = new StepLandmark();
		PredicateStep.step = predicates_list.step + 2;
		ActionStep.step = predicates_list.step + 1;
		for(String action_name : _Actions_list) {
			Action a = _Actions.get(action_name);
			if(isActionApplicable(a, predicates_list)){
				NodeLandmark no = new NodeLandmark(action_name);
				if(no.level > ActionStep.step) {
					no.level = ActionStep.step;
				}
				ActionStep.actionsHash.put(action_name, 1);
				ActionStep.addNode(no);
				for(String precondition : a._precond) {
					predicates_list.updateSuccessorNode(precondition, no);
					ActionStep.updateParentNode(action_name, predicates_list.getNode(precondition));
				}
				/*for(String effect : a._Positive_effects){
					NodeLandmark node_effect = new NodeLandmark(effect);
					if(node_effect.level > PredicateStep.step){
						node_effect.level = PredicateStep.step;
					}
					PredicateStep.addNode(node_effect);
					ActionStep.updateSuccessorNode(action_name, node_effect);
					PredicateStep.updateParentNode(effect, no);
				}*/
			}
		}
		// 2 Add no-ops actions and effects
		for(NodeLandmark predicate : predicates_list.getIterator()){
			if(!PredicateStep.Contains(predicate.predicate)){
				NodeLandmark no = new NodeLandmark("No-op-" + predicate.toString());	
				NodeLandmark node_effect_no = new NodeLandmark(predicate.toString());
				node_effect_no.level = predicate.level;
				ActionStep.addNode(no);
				ActionStep.updateParentNode(no.toString(), predicate);
				PredicateStep.addNode(node_effect_no);
				ActionStep.updateSuccessorNode(no.predicate, node_effect_no);
				PredicateStep.updateParentNode(node_effect_no.predicate, no);
			}		
		}
		ActionStep.father = predicates_list;
		PredicateStep.father = ActionStep;
		_Steps.add(ActionStep);
		_Steps.add(PredicateStep);
		last_layer = ActionStep.step;
	}
	
	private ArrayList<Hashtable<String, Integer>> initGoalLayer(int m){
		ArrayList<Hashtable<String, Integer>> returnList = new ArrayList<Hashtable<String, Integer>>();
		for(int iter = 0; iter<=m;iter++){
			Hashtable<String, Integer> tableList = new Hashtable<String, Integer>();
			returnList.add(iter, tableList);
		}
		return returnList;
	}

	/**Find action with g in add(o), difficulty minimal
	 * @param layerMembershipFacts 
	 * @param layerMembershipActions 
	 * @param iter 
	 * @param kGoal */
	private String chooseActionFF(String kGoal, int iter, StepLandmark layerMembershipActions, StepLandmark layerMembershipFacts){
		ArrayList<String> candidates = new ArrayList<>();
		for(NodeLandmark n : layerMembershipActions.getIterator()){
			Action a = _Actions.get(n.predicate);
			/*if((n.level == iter-1) && (a._Positive_effects.contains(kGoal))){
				candidates.add(n.predicate);
			}*/
			for(Effect eff : a._Effects){
				if(eff._Effects.contains(kGoal)){
					candidates.add(n.predicate);
				}
			}
		}
		int difficulty = Integer.MAX_VALUE;
		String best = "";
		if(!candidates.isEmpty()){
			best = candidates.get(0);
			for(String act : candidates){
				Action a = _Actions.get(act);
				int new_difficulty = 0;
				for(String pre : a._precond){
					new_difficulty += layerMembershipFacts.getNode(pre).level;
				}
				if(new_difficulty < difficulty){
					best = act;
					difficulty = new_difficulty;
				}
			}
		}		
		return best;
	}
	
	/**FF improved version*/
	private int heuristicGraphPlan(){
		//Scheduled actions variable
	    ArrayList<String> scheduledActions = new ArrayList<String>();
	    //Current Layer
	    int i=0;
	    StepLandmark layerMembershipFacts = new StepLandmark();
	    StepLandmark layerMembershipActions = new StepLandmark();
	    Enumeration<String> e = Collections.enumeration(_State);
	    while(e.hasMoreElements()){
	    	String p = e.nextElement().toString();
	    	NodeLandmark n = new NodeLandmark(p);
	    	n.level = 0;
	    	layerMembershipFacts.addNode(n);
	    }
	    
	    while(!isGoal(layerMembershipFacts) && i < _Actions.size()){
	    	ArrayList<String> stateApplicableActions = getApplicableActions(layerMembershipFacts);
	    	stateApplicableActions.removeAll(scheduledActions);
	    	ArrayList<String> scheduledNextActions = new ArrayList<String>();
	    	//Finds Actions that have not been selected and with all preconditions fulfilled
	    	for(String action : stateApplicableActions){
	    		NodeLandmark NodeAction = new NodeLandmark(action);
	    		NodeAction.level= i;
	    		scheduledNextActions.add(action);
		    	layerMembershipActions.addNode(NodeAction);
		    	//System.out.println(action);
	    	}
	    	i++;
	    	for(String action : scheduledNextActions){
	    		//Updating current state
	    		Action a = _Actions.get(action);
	    		//for(String eff : a._Positive_effects){
	    		for(Effect effect: a._Effects){
	    			if(effect._Condition.isEmpty() || isEffectApplicable(effect, layerMembershipFacts)){
	    				for(String eff: effect._Effects){
	    					NodeLandmark effNode = new NodeLandmark(eff);
			    			//Updating LayerMembership for facts
				            if(!layerMembershipFacts.Contains(eff)){
				            	effNode.level = i;
				            	//System.out.println(effNode);
				            	layerMembershipFacts.addNode(effNode);
				            }
	    				}	    				
	    			}	    			
	    		}
	    		//Updating LayerMembership for conditional effect-facts
	    		for(Effect effect : a._Effects){
	    			if(isEffectApplicable(effect, layerMembershipFacts)){
	    				for(String eff : effect._Effects){
		    				if(!layerMembershipFacts.Contains(eff)){
		    					NodeLandmark effNode = new NodeLandmark(eff);
				            	effNode.level = i;
				            	//System.out.println(effNode);
				            	layerMembershipFacts.addNode(effNode);
				            }
	    				}
	    			}	    			
	    		}
	    	}
	    	scheduledActions.addAll(scheduledNextActions);
	    }
	    //End of Graph extraction... beginning of plan extraction
	    int last = i;
	    //Build list of goals achieved in each layer
	    ArrayList<Hashtable<String, Integer>> goalLayer = initGoalLayer(i);
	    int num_selected_actions = 0;
	    Hashtable<String, Integer> markedTrue = new Hashtable<String, Integer>();
	    int layer = 0;
	    for(String goalPred : _Goal){
	    	NodeLandmark nodeGoal = layerMembershipFacts.getNode(goalPred);
	    	layer = nodeGoal.level;
	    	goalLayer.get(layer).put(goalPred, 1);
	    }
	    for(int iter = last; iter>=0;iter--){
	    	Hashtable<String, Integer> gIter = goalLayer.get(iter);
	    	Enumeration en = gIter.keys();
	    	while(en.hasMoreElements()){
	    		String kGoal = en.nextElement().toString();
	    		if((!markedTrue.containsKey(kGoal)) || (markedTrue.get(kGoal) > iter)){
	    			Action a = _Actions.get(chooseActionFF(kGoal, iter, layerMembershipActions, layerMembershipFacts));
	    			if(a != null){
		    			num_selected_actions++;
		    			//TODO: add effect conditions?
		    			for(String pr : a._precond){
		    				if(layerMembershipFacts.getNode(pr).level!=0){
		    					layer = layerMembershipFacts.getNode(pr).level;
		    					goalLayer.get(layer).put(pr, 1);
		    				}
		    			}
		    			//Here?
		    			for(Effect ef : a._Effects){
		    				for(String cond : ef._Condition){
			    				if(layerMembershipFacts.getNode(cond).level!=0){
			    					layer = layerMembershipFacts.getNode(cond).level;
			    					goalLayer.get(layer).put(cond, 1);
			    				}
		    				}
		    			}
		    			/*for(String eff : a._Positive_effects){
		    				markedTrue.put(eff, iter);
		    				markedTrue.put(eff, iter-1);
		    			}*/
	    			}
		    	}
	    	}	    	
	    }
	    //after all computations, The heuristic is the number of selected actions
	    return num_selected_actions;
	}
	
	private ArrayList<String> getApplicableActions(StepLandmark predicates_list){
		ArrayList<String> _actions = new ArrayList<>();
		for(String action_name : _Actions_list){
			Action a = _Actions.get(action_name);
			/*if(action_name.contains("move_p5-3_")){
				System.out.println("Checked");
			}*/
			if(isActionApplicable(a, predicates_list)){
				_actions.add(action_name);
			}
		}
		return _actions;
	}
	
	/**Verify if the conditional effect is applied*/
	private boolean isEffectApplicable(Effect e, StepLandmark s){
		for(String precondition : e._Condition){
			if(!precondition.startsWith("~")){
				if(!s.Contains(precondition)){
					//System.out.println(a.Name);
					return false;
				}
			}else {
				if(s.Contains(precondition.substring(1))){
					return false;
				}
			}
		}
		return true;
	}
	
	/**Verify if the action is applicable*/
	private boolean isActionApplicable(Action a, StepLandmark s){
		for(String precondition : a._precond){
			if(precondition.startsWith("~")){
				continue;
			}
			if(!s.Contains(precondition)){
				return false;
			}
		}
		return true;
	}
}
