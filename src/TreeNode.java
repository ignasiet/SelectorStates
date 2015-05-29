import java.util.ArrayList;


public class TreeNode {
	
	public String name;
	public String parent;
	public ArrayList<TreeNode> sucessor = new ArrayList<TreeNode>();
	
	public TreeNode(String n) {
		name = n;
	}
	
	public boolean hasChild(String childName){
		if(sucessor.isEmpty()){
			return false;
		}
		for(TreeNode tNode : sucessor){
			if(tNode.name.equals(childName)){
				return true;
			}
		}
		return false;
	}

}
