package war;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Graph {

	static Map<String, Map<String, Double>> edgeMap = new HashMap<String, Map<String, Double>>();
	
public void addEdge(String vertex1, String vertex2){
		
		double distance = 0.0;
		Map<String, Double> adjacent = edgeMap.get(vertex1);
		Map<String, Double> adjacent2 = edgeMap.get(vertex2);
		
		if(adjacent == null){
			adjacent = new HashMap<String, Double>();
			edgeMap.put(vertex1, adjacent);		
		}
		adjacent.put(vertex2, distance);
		
		if(adjacent2 == null){
			adjacent2 = new HashMap<String, Double>();
			edgeMap.put(vertex2, adjacent2);		
		}
		adjacent2.put(vertex1, distance);
	}

	public boolean isWalkable(String vertex1, String vertex2){
		
		Map<String, Double> adjacent = edgeMap.get(vertex1);
		
		if(adjacent.get(vertex2) != null)
			return true;
		
		return false;
	}
	
public boolean isWalkable(Node vertex1, Node vertex2){
		
		Map<String, Double> adjacent = edgeMap.get(vertex1.name);
		
		if(adjacent.get(vertex2.name) != null)
			return true;
		
		return false;
	}
	
	public Double getDistance(Node vertex1, Node vertex2){
	
		Map<String, Double> adjacent = edgeMap.get(vertex1);
	
		if(adjacent == null){
			return (double) -1;
		}
		return adjacent.get(vertex2);
	}
}
