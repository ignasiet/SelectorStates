import java.util.ArrayList;
import java.util.Hashtable;


public class SolutionTree {
	private SearchNode root;	
	private Hashtable<SearchNode, Integer> allNodes = new Hashtable<SearchNode, Integer>();

	public SolutionTree(SearchNode node) {
		root = node;
	}
	
	public ArrayList<SearchNode> getChildren(SearchNode node){
		return node.sucessor_node;
	}
	
	public boolean containsNode(SearchNode node){
		return allNodes.containsKey(node);
	}
	
	public void insertNode(SearchNode node){
		allNodes.put(node, 1);
	}

}
