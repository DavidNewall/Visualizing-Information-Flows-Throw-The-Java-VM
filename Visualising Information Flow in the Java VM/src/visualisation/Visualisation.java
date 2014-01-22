package visualisation;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import com.macrofocus.treemap.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import treemap.Node;
import treemap.Treemap;


public class Visualisation {
	
	// this is the frame for the visualisation window
	private static JFrame treemapWindow = new JFrame();
	
	// it is recommended to use a hashmap between the object name its size and its entropy
	private static HashMap<String,Double[]> data1 = new HashMap<String,Double[]>();
	
	// a double array to represent the size of the current rectangle
	private static Double[] currentRectangle = {1.0,1.0};
	
	// a treemap
	private static Treemap objectMap = new Treemap();

	private static HashMap<String, HashMap<String,Integer>> data = new HashMap<String, HashMap<String,Integer>> ();
	
	private static GridBagConstraints c = new GridBagConstraints();
	
	//private static Double remainingArea = 0.0;
	
	public static void main(String args[]){
		c.gridx = 0;
		c.gridy = 0;
		
		JPanel initialPane = new JPanel();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		treemapWindow.setBounds(((int)(screenSize.getWidth()-600))/2,((int)(screenSize.getHeight()-600))/2,600,600);
		initialPane.setLayout(new GridBagLayout());
		initialPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	
		drawTreeMap(objectMap, initialPane);	
		treemapWindow.add(initialPane);
		treemapWindow.setDefaultCloseOperation(treemapWindow.EXIT_ON_CLOSE);
		treemapWindow.setVisible(true);
		
	}
	
	
	private static void drawTreeMap (Treemap tM, JPanel initialPane){
		Node root = tM.getRoot();
		
		
		System.out.println(root.getAreaArray()[0].intValue());
		Dimension d = new Dimension (root.getAreaArray()[0].intValue(), root.getAreaArray()[1].intValue());
		
		initialPane.setMinimumSize(d);
		
		root.setPane(initialPane);
		
		ArrayList<Node> children = root.getChildren();
		
		System.out.println("Here are the children: ");
		
		for (Node n : children){
			System.out.println(n.getName());
		}
		
		traverseTree(root);
		
		
	}
	
	public static void traverseTree(Node n){
		System.out.println("The current node is " + n.getName() + " it's entropy is " + n.getEntropy());
		System.out.println();
		ArrayList<Node> children = n.getChildren();
		if (children != null){
			System.out.println("It makes it in here!");
			drawChildren(n);
			System.out.println("It makes it past draw children");
			System.out.println(children.size());
			for (Node c : children){
				System.out.println("It is drawing the rest of the children");
				traverseTree(c);
			}
		}
	}
	
	public static void drawChildren (Node n){
		JPanel parentPane =  new JPanel();
		
		if (n.isRoot()){
			parentPane = n.getPane();
		}
		else {
			parentPane = n.getParent().getPane();
		}

		if (!n.isRoot()){
			System.out.println("The current node is: " + n.getName());
			System.out.println("The parent of this node is:  " + n.getParent().getName() + " it's pane size is " + parentPane.getSize());
		}
		
		
		currentRectangle[0] = (double) parentPane.getWidth();
		currentRectangle[1] = (double) parentPane.getHeight();
		
		ArrayList<Node> newRow = new ArrayList<Node>();
		ArrayList<Node> children = new ArrayList<Node>();
		children.addAll(n.getChildren());
		squarified(children, newRow, width());

	}
	
	//eventually this shall need to replace the code in the main method as this is what shall be called from other classes
	public Visualisation () {
		
	}
	
	private static Color getColour(int i){
		switch (i) {
		case 0 : return Color.blue;
		case 1 : return Color.orange;
		case 2 : return Color.green;
		case 3 : return Color.cyan;
		case 4 : return Color.darkGray;
		case 5 : return Color.red;
		default : return Color.white;
		}
		
	}
	
	private static void squarified (ArrayList<Node> children , ArrayList<Node> row, Double w){
		Node c = children.get(0);
		ArrayList<Node> rowAndChild = new ArrayList<Node>();
		rowAndChild.addAll(row);
		rowAndChild.add(c);
		if (row.isEmpty() || worstNode(row, w) >= worstNode(rowAndChild, w)){
			children.remove(0);
			ArrayList<Node> remainingChildren = children;
			if (remainingChildren.isEmpty()){
				layoutNode(rowAndChild, false);
			}
			else {
				squarified(remainingChildren, rowAndChild, w);
			}
		}
		else {
			layoutNode(row, false);
			ArrayList<Node> newRow = new ArrayList<Node>();
			squarified(children,newRow,width());
		}
	}
	
	// this gives the length of the shortest side of the remaining rectangle in which the current row is placed
	private static Double width(){
		if (currentRectangle[0] < currentRectangle[1]){ return currentRectangle[0];}
		return currentRectangle[1];
	}
	
	private static Double worstNode(ArrayList<Node> row, Double length){
		Double[] aspectRatio = {0.0,0.0};
		ArrayList<Double[]> areas = new ArrayList<Double[]>();
		for (Node r : row){
			areas.add(r.getAreaArray());
		}
		
		for(Double[] d : areas){
			Double gcd = gcd(d[0], length);
			if ((d[0]/gcd) > aspectRatio[0] && (length/gcd) > aspectRatio[1]){
				aspectRatio[0] = d[0]/gcd;
				aspectRatio[1] = length/gcd;
			}
		}
		
		Double worstArea = aspectRatio[0] * aspectRatio[1];
		
		//System.out.println("The aspect ratio is: " + aspectRatio[0] + ":" + aspectRatio[1]);
		//Dimension d = new Dimension (aspectRatio[0].intValue(), aspectRatio[1].intValue());
		return worstArea;
	}
	
	// calculate the gcd of the width and height in order to determine the aspect ratio
	private static Double gcd(Double a, Double b){
		if (b == 0){ return a; }
		return gcd (b, a%b);
		
	}
	
	private static void layoutNode(ArrayList<Node> row, boolean isNewRow){
		int counter = 0;
		for (Node n : row) {
			JPanel parentPane = n.getParent().getPane();
			System.out.println("The parent is: " + n.getParent() + " the size of it's pane is: " + n.getParent().getPane());
			JPanel newPane = new JPanel(new GridBagLayout());
			c.gridy++;
			if (isNewRow){ c.gridx++;}
			Dimension d = new Dimension(n.getSize().intValue(), n.getSize().intValue());
			newPane.setMinimumSize(d);
			Color colour = getColour(counter);
			newPane.setBackground(colour);
			n.setPane(newPane);
			counter++;
			parentPane.add(newPane,c);
			currentRectangle[0] = currentRectangle[0] -  n.getSize();
			currentRectangle[1] = currentRectangle[1] - n.getSize();
			
			System.out.println("The size of the current rectangle is: " + currentRectangle[0] + " " + currentRectangle[1]);
		}
	}
	

	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// here is some spare code that may potentially  be useful
	/*
	 * 	
	// this was never correctly imple
	private void slicenDice(){
		int n_Values = 10;
		
	}
	
	// this adds a new row of children to the rectangle
	// will need to determine how exactly this works as if it is adding rows and sizes and such to an array then there will 
	// need to be a global variable to support this
	private static void layoutrow(ArrayList<Double[]> row){
		drawPanels(basePane,row);
	}
	
		// in order to draw the panel it must take in the intial pane which all other panes shall be added too
	// i think this may perform a similar function to that which layout is supposed to
	// if this is to be done in sections that it shall need to be done in a row manner, with each row being added to the initial pane
	// as after they are created it will be hard to reference the individual panels
	// look up swing objects to determine if there is a better way to do this as there probably is
	
	
	private static void drawPanels(JPanel pane, ArrayList<Double[]> row){
		int counter = 0;
		for (Double[] item : row){
			JPanel newPane = new JPanel();
			Dimension d = new Dimension ( item[0].intValue(), item[1].intValue()); 
			newPane.setSize(d);
			newPane.setLayout(new FlowLayout());
			Color c = getColour(counter);
			newPane.setBackground(c);
			counter++;
			pane.add(newPane);
		}
	}
		private static Dimension splitter(JPanel pane, int numberOfObjects){
		
	int[][] sizes = new int[numberOfObjects][2];
	
	double width = pane.getWidth();
	double height  = pane.getHeight();
	
	//System.out.println("The initial width is: " + width + " the initial height is: " + height);
	
	for (int i = 0; i < numberOfObjects; i++){
		if (width >= height){
			width = width/2.0;
		}
		else {
			height = height/2.0;
		}
		//System.out.println("The width is: " + width + " the height is: " + height);
	}
	
	
		// need to somehow keep track of the dimension of the remaining rectangle of the current pane in which squarify takes place
	
	private static void squarify(ArrayList<Double[]> children, ArrayList<Double[]> row, Double w){
		Double[] c = children.get(0); // remove the head of the list
		ArrayList<Double[]> rowAndChild = new ArrayList<Double[]>();
		rowAndChild.addAll(row);
		rowAndChild.add(c);
		if (row.isEmpty() || worst(row, w) >= worst(rowAndChild, w)) { // if the current row is empty or the best dimension of the current row is greater than or equal to that of the new row
			children.remove(0);
			ArrayList<Double[]> remainingChildren = children; // remaining children
			if (remainingChildren.isEmpty()) {
				layoutrow(rowAndChild); // draw out the new row of the items in the old row and the remaining child
			}
			else {
				squarify(remainingChildren, rowAndChild, w);
			}
		}
		else {
			layoutrow(row); // draw out the current row
			ArrayList<Double[]> emptyList = new ArrayList<Double[]>(); 
			squarify(children,emptyList, width()); // squarify the rest
		}
			
		
	}
	
	
	Dimension d = new Dimension();
	d.setSize(width, height);
	return d;
	}
	
		private void traverseTreeMap(Node n){
		if (n == null || n.getChildren() == null){
			return;
		}
		
		if (n.getParent() == null){
			String output = "";
			for (Node c : n.getChildren()){
				output = output + " " + c.getName();
			}
			System.out.println(output);
			
			for (Node c : n.getChildren()){
				traverseTreeMap(c);
			}
		}
	}
	
		private static void sliceNDice(){
		
		Set<String> objectNames = data.keySet();
		
		for (String s: objectNames){
			HashMap<String, Integer> object = data.get(s);
			int numberOfObjects = object.size();
			
		}
		
	}
	
		// here is the code for the squarified treemap algorithm
	// here are some observations about this w is the width of the shortest side of the remaining available area
	// children is the list of children to be added to the current area
	// row is the list of sizes for the current row - i think that it may be possible for this to be both horizontal and vertical -
	
	private void squarify(ArrayList<Double[]> children, ArrayList<Double[]> row, Double w){
		Double[] c = children.get(0);
		
		ArrayList<Double[]> rowAndChild = new ArrayList<Double[]>();
		rowAndChild.add(c);
		
		if (worst(row,w) <= worst(rowAndChild, w)){
			ArrayList<Double[]> tail = new ArrayList<Double[]>();
			children.remove(0);
			tail.addAll(children);
			ArrayList<Double[]> emptyList = new ArrayList<Double[]>();
			squarify(tail,emptyList, width());
		}
		else {
			layoutrow(row);
			ArrayList<Double[]> emptyList = new ArrayList<Double[]>();
			squarify(children,emptyList,width());
		}
		
	}
	
	
	// method for drawing the panels on screen each time the treemap is update the panes are redrawn
	private static void drawPanels(JPanel pane, int numberOfObjects, Dimension d, String text){
		
		for (int i = 0; i < numberOfObjects; i++){
			JPanel p = new JPanel();
			p.setSize(d);
			p.setLayout(new FlowLayout());
			JLabel l = new JLabel();
			l.setText(text);
			int number = i;
			if (number >= 5) {number = number - 5;}
			l.setBackground(getColour(number));
			pane.add(p);
		}
		
		
	}
		// gives the highest aspect ratio of a list of rectangles, given the length
	// of the side along which they are to be laid out.
	// this is to determine what gives the worst aspect ratio or area
	private static Double worst(ArrayList<Double[]> row, Double length){
		
		Double[] aspectRatio = {0.0,0.0};
		for (Double[] d: row){
			Double gcd = gcd(d[0], length);
			if ((d[0]/gcd) > aspectRatio[0] && (length/gcd) > aspectRatio[1]){
				aspectRatio[0] = d[0]/gcd;
				aspectRatio[1] = length/gcd;
			}
		}
		
		System.out.println("The aspect ratio is: " + aspectRatio[0] + ":" + aspectRatio[1]);
		Dimension d = new Dimension (aspectRatio[0].intValue(), aspectRatio[1].intValue());
		return d;
	}
	
		// this is a test method to make up fake values in order to see if the squarified algorithm works correctly
	// this shall eventually be replaced by the actual data being piped in from the entropy section
	private static void fillData(){
		
		String[] letter = {"a","b","c","d","e","f","g","h","i","j"};
		
		HashMap<String,Integer> values = new HashMap<String,Integer>();
		
		for (int j = 0; j < 10; j++){
			for (int i = 0; i < 10; i++){
				values.put(letter[i],i);
			}
			data.put(letter[j], values);
			Double[] sizeAndEntropy = new Double[2];
			sizeAndEntropy[0] = j + 10.0;
			sizeAndEntropy[1] = j * 1.0;
			data1.put(letter[j], sizeAndEntropy);
		}
		
	}
	

	*/

}
