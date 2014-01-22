package treemap;

import java.util.ArrayList;

import javax.swing.JPanel;

public class Node {

	// instance variables of the class
	private String objectName;
	private Double objectSize;
	private Double objectEntropy;
	private boolean isRoot;
	private ArrayList<Node> children;
	private Node parent;
	private JPanel pane;
	private int positionX;
	private int positionY;
	
	//constructor
	public Node(String obN, Double size, Double entropy, boolean root, ArrayList<Node> c, Node p, JPanel pane){
		objectName = obN;
		objectSize = size;
		objectEntropy = entropy;
		isRoot = root;
		children = c;
		parent = p;
		this.pane = pane;
	}
	
	// getters and setters
	
	public void setName(String name){
		objectName = name;
	}
	
	public String getName(){
		return objectName;
	}
	
	public void setSize(Double size){
		objectSize = size;
	}
	
	public Double getSize(){
		return objectSize;
	}
	
	public void setEntropy(Double entropy){
		objectEntropy= entropy;
	}
	
	public Double getEntropy(){
		return objectEntropy;
	}
	
	public boolean isRoot(){
		return isRoot;
	}
	
	public ArrayList<Node> getChildren(){
		return children;
	}
	
	public void setChildren(ArrayList<Node> c){
		children = c;
	}
	
	public void addChildren(ArrayList<Node> c){
		children.addAll(c);
	}
	
	public void removeChildren(ArrayList<Node> c){
		children.removeAll(c);
	}
	
	public void setParent(Node p){
		parent = p;
	}
	
	public Node getParent(){
		return parent;
	}
	
	public void setPane(JPanel p){
		pane = p;
	}
	
	public JPanel getPane(){
		return pane;
	}

	public Double[] getAreaArray(){
		if (objectEntropy == null) return null;
		Double side = Math.sqrt(objectEntropy);
		Double[] area = {side,side};
		return area;
	}
	
	public void setPositionX(int x){
		positionX = x;
	}
	
	public int getPositionX(){
		return positionX;
	}
	
	public void setPositionY(int y){
		positionY = y;
	}
	
	public int getPositionY(){
		return positionY;
	}
		
	
}
