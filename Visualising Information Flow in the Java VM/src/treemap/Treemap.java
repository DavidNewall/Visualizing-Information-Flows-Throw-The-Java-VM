package treemap;

import java.util.ArrayList;

public class Treemap {

	// instance variable
	private Node root;
	
	
	//constructs a treemap with default values
	public Treemap(){
		
		root = new Node("Root",100.0,10.0,true,null,null,null);
		
		String[]  names = {"a","b","c","d","e","f","g","h","i","j"};
		
		ArrayList<Node> rootChildren = new ArrayList<Node>();
		
		for (int i = 0; i < 10; i++){
			
			Node currentNode = new Node(names[i], ((10 - i)*20.0), ((10 - i)*50.0), false, null, null,null);
			
			ArrayList<Node> children = new ArrayList<Node>();
			
			//creates children for the current node.
			for (int j = 0; j < 10; j++){
				Node n = new Node(names[j], ((10 - j)*20.0), ((10 - j)*50.0), false, null, null,null);
				n.setParent(currentNode);
				children.add(n);
			}
			currentNode.setChildren(children);
			
			currentNode.setParent(root);
			rootChildren.add(currentNode);
		}
		
		root.setChildren(rootChildren);
	}
	
	public Node getRoot(){
		return root;
	}
	
}
