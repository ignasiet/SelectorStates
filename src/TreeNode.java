import java.util.ArrayList;


public class TreeNode {
	
	public String name;
	public String parent;
	//public ArrayList<TreeNode> sucessor = new ArrayList<TreeNode>();
	public TreeNode right_sucessor;
	public TreeNode left_sucessor;
	
	
	public TreeNode(String n) {
		name = n;
	}
	
	public boolean hasChild(String childName){
		if(!(left_sucessor == null) && left_sucessor.name.equals(childName)){
			return true;
		}else if(!(right_sucessor == null) && right_sucessor.name.equals(childName)){
			return true;
		}
		return false;
	}
	
	public void addNode(TreeNode childNode){
		if(left_sucessor == null){
			left_sucessor = childNode;
		}else if(right_sucessor == null){
			right_sucessor = childNode;
		}
	}

	public TreeNode getChildren(String childName) {
		if(!(left_sucessor == null) && left_sucessor.name.equals(childName)){
			return left_sucessor;
		}
		else if(!(right_sucessor == null) && right_sucessor.name.equals(childName)){
			return right_sucessor;
		}
		return null;
	}
}
