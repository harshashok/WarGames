package war;

public class Node {

	public static final int UNEXPLORED = 0;
	public static final int CONFEDERACY = -1;
	public static final int UNION = 1;
	
	String name;
	double value;
	int status;
	
	Node(){}
	
	Node(String name, double value, int status){
		this.name = name;
		this.value = value;
		this.status = status;		
	}
	
	Node(Node node){
		this.name = node.name;
		this.value = node.value;
		this.status = node.status;
	}
}
