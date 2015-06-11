import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;


public class SolutionTree {
	
	Hashtable<String, TreeNode> solution = new Hashtable<String, TreeNode>();
	public TreeNode root;
	public TreeNode last_node;
	private Stack<TreeNode> stack_nodes = new Stack<TreeNode>();
	private Hashtable<String, String> observations = new Hashtable<String, String>();
	private Hashtable<String, String> observations_reversed = new Hashtable<String, String>();
	//private ArrayList<TreeNode> stack_nodes = new ArrayList<TreeNode>();
	
	public SolutionTree() {
		
	}
	
	public TreeNode getLastNode(){
		return last_node;
	}
	
	public boolean hasRoot(){
		if(root == null){
			return false;
		}else{
			return true;
		}
	}
	
	public void printTree(){
		TreeNode used_node = root;
		stack_nodes.push(used_node);
		while(!stack_nodes.isEmpty()){
			used_node = stack_nodes.pop();
			if(observations.containsKey(used_node.name)){
				System.out.println(observations.get(used_node.name) + " {");
			}
			System.out.println(used_node.name);
			if(used_node.right_sucessor != null){
				System.out.println("If observation = ");
				stack_nodes.push(used_node.right_sucessor);
				stack_nodes.push(used_node.left_sucessor);
			}else if(used_node.left_sucessor != null){
				stack_nodes.push(used_node.left_sucessor);
			}else{
				System.out.println("}");
				if(!stack_nodes.isEmpty()){
					System.out.println("Else if");
				}
			}
			//used_node = stack_nodes.pop();
		}
	}

	public void put_observation(String _applyAction, String observation_divisor) {
		observations.put(_applyAction, observation_divisor);
		observations_reversed.put(observation_divisor, _applyAction);
	}
	

	
	public TreeNode getObservationNode(TreeNode tNode, String observation){
		String next = observations_reversed.get("K" + observation);
		return tNode.getChildren(next);
	}

}
