import java.util.ArrayList;

/**
 * 
 */

/**
 * @author ignasi
 *
 */
public class SolutionNode {

	/**
	 * 
	 */
	public String Name;
	public ArrayList<String> Options = new ArrayList<String>();
	public ArrayList<SolutionNode> Sucessors = new ArrayList<SolutionNode>();
	
	public SolutionNode(String string) {
		Name = string;
	}
	
	public void addSucessor(SolutionNode sucessor){
		Sucessors.add(sucessor);
	}

}
