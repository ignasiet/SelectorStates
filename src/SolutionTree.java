import java.util.Hashtable;


public class SolutionTree {
	
	Hashtable<String, TreeNode> solution = new Hashtable<String, TreeNode>();
	public TreeNode root;
	public TreeNode last_node;
	
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

}
