package searcher;
import java.util.ArrayList;


public class TreeNode {
	
	public String name;
	public String parent;
	//public ArrayList<TreeNode> sucessor = new ArrayList<TreeNode>();
	public TreeNode right_sucessor;
	public TreeNode left_sucessor;
	public String decissor_right;
	public String decissor_left;
	
	
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
	
	public void addNode(TreeNode childNode, String next_son){
		if(next_son.length() > 0){
			if(next_son.equals("left")){
				left_sucessor = childNode;
				childNode.parent = name;
			}else if(next_son.equals("right")){
				right_sucessor = childNode;
				childNode.parent = name;
			}
		}
		else{
			if(left_sucessor == null){
				left_sucessor = childNode;
				childNode.parent = name;
			}else if(right_sucessor == null){
				right_sucessor = childNode;
				childNode.parent = name;
			}
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
