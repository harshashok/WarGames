package war;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.PriorityQueue;






public class war {
	

	public static final int UNEXPLORED = 0;
	public static final int CONFEDERACY = -1;
	public static final int UNION = 1;
	
	public static final int FORCEMARCH = 0;
	public static final int PARATROOP = 1;	
	
	static int task;
	static int cutoffDepth;
	static String mapFile;
	static String initFile;
	static String outputFile;
	static String outputLogFile;
	
	static List<Node> globalNodeList = new ArrayList();
	
	static Comparator<State> stateComparator = new StateComparator();
	static Comparator<State> costComparator = new CostComparator();
	
	static List<State> stateLog = new LinkedList();

	static Graph g = new Graph();
	
	static State currState = new State();
	
	static int expandedNodes;
	
	static //ALGO: MINMAX.
	State minMax(State state, int player, int depth){
		
		PriorityQueue<State> children = generateStates(state);
		
		State currScore = state;
		State bestScore = null;
		if(depth == 0 || children.isEmpty()){
			return bestScore;
		}
		
		if(player == UNION){
			//TODO: find max.
			while(!children.isEmpty()){
				currScore = minMax(currScore, CONFEDERACY, depth - 1);
				if(bestScore == null || currScore.compareTo(bestScore) >= 0){
					bestScore = currScore;
				}
			}
			return bestScore;
		}else if(player == CONFEDERACY){
			//find min
			while(!children.isEmpty()){
				currScore = minMax(currScore, CONFEDERACY, depth - 1);
				if(bestScore == null || currScore.compareTo(bestScore) <= 0){
					bestScore = currScore;
				}
			}
			return bestScore;
		}
		return null;
	}
	
	//ALGO-DEBUG: MINMAX STUFF.
	State minmaxDecision(State state){
		
		int depth = 0;
		State resultState = new State();		
		double resultValue = Double.NEGATIVE_INFINITY;
		
		System.out.println("N/A,N/A,N/A,0,"+resultValue);
		List<State> list =  new ArrayList(genList(state, UNION));
		for(State s : list){
			//System.out.println("INDec: "+s.player);
			s.printLog(depth+1, Double.POSITIVE_INFINITY);
			double value = minValue(s, CONFEDERACY, depth + 1);
			s.cost = value;
			
			 if(value >= resultValue){
				resultValue = value;
			}
			 System.out.println("N/A,N/A,N/A,0,"+resultValue);
		}
		System.out.println("N/A,N/A,N/A,0,"+resultValue);
		Collections.sort(list, costComparator);
		for(State l : list)
			System.out.println("NAM "+l.destination.name+" "+l.cost);
		resultState = list.get(0);
		return resultState;
	}
	
	double maxValue(State state, int player, int depth){
		
		//state.printLog(depth);
		state.player = player;
		double value = Double.NEGATIVE_INFINITY;
		state.cost = value;
		if((state.union.size() + state.confederacy.size() == globalNodeList.size()) || depth == cutoffDepth-1 ){
			//
			state.cost = state.eval();
			state.printLog(depth);
			return state.cost;
		}
		
		
		List<State> actionList = new ArrayList(genList(state, UNION));//generateStateList(generateStates(state));
		for(State s : actionList){
			s.printLog(depth);
			//System.out.println("InMax: "+s.player);
			value = Math.max(value, minValue(s, CONFEDERACY, depth+1));
			s.cost = value;
			//s.printLog(depth);
		}
		return value;
	}
	
	double minValue(State state, int player, int depth){
		
		//state.player = player;
		//	state.printLog(depth);
		
		double value = Double.POSITIVE_INFINITY;
		state.cost = value;
		
		if((state.union.size() + state.confederacy.size() == globalNodeList.size()) || depth == cutoffDepth-1 ){
			state.cost = state.eval();
			//state.printLog(depth);
			return state.cost;
		}
		List<State> actionList = new ArrayList(genList(state, CONFEDERACY));
		for(State s : actionList){
			s.printLog(depth);
			//System.out.println("MIN "+s.player);
			value = Math.min(value, maxValue(s, UNION, depth+1));
			s.cost = value;
			//s.printLog(depth);
		}
		return value;
	}
	
	//ALGO-UTILITY FUNC: Generate next level in list form.
	List<State> generateStateList(PriorityQueue<State> pq){
		List<State> ret = new ArrayList();
		while(!pq.isEmpty()){
			ret.add(pq.remove());
		}
		return ret;
	}
	//ALGO UTILITY FUNC: Generate possible moves.
	static List<Node> generatePossibleMoves(State state){
		List<Node> unexplored = new LinkedList(state.unexplored);
		return unexplored;
	}
	static //ALGO-GEN FUNC: 
	List<State> genList(State state, int player){
		
		//State state = new State(state1);
		
		/*if(state.player == UNEXPLORED || state.player == CONFEDERACY)
			state.player = UNION;
		else if(state.player == UNION)
			state.player = CONFEDERACY;
		*/
		state.player = player;
		
		//System.out.println("pl: "+state.player);
		List<Node> unexplored = generatePossibleMoves(state);
		List<State> list = new ArrayList();
		//System.out.println(unexplored.size());
		// CALC FOR UNION PLAYER.
		int counter = 0;
		if(state.player == UNION){
			for( Node ux : unexplored){ 		
				for( Node teamnode : state.union){
					if(g.isWalkable(ux, teamnode)){
						State forceState = new State(state, ux, UNION, FORCEMARCH, g);
						list.add(forceState);						
						//forceState.printLog();
						break;
					}
				}		
			}
			for( Node ux : unexplored){				
				
					State paraState = new State(state, ux, UNION, PARATROOP, g);
					list.add(paraState);
					//paraState.printLog();
					counter++;						
			}
		}
		// CALC FOR CONFEDERACY PLAYER.
		if(state.player == CONFEDERACY){
			for( Node ux : unexplored){				
				for( Node teamnode : state.confederacy){
					if(g.isWalkable(ux, teamnode)){
						State forceState = new State(state, ux, CONFEDERACY, FORCEMARCH, g);
						list.add(forceState);						
						break;
					}
				}		
			}
			for( Node ux : unexplored){				
					State paraState = new State(state, ux, CONFEDERACY, PARATROOP, g);
					list.add(paraState);		
			}
		}
		return list;
	}
	
	//ALGO- GENERATE FUNC: Generate the next level of states.
	static PriorityQueue<State> generateStates(State state){
		
		if(state.player == UNEXPLORED || state.player == CONFEDERACY)
			state.player = UNION;
		else if(state.player == UNION)
			state.player = CONFEDERACY;
		
		//System.out.println(state.player);
		List<Node> unexplored = generatePossibleMoves(state);
		//System.out.println("unex:"+unexplored.size());
		PriorityQueue<State> stateQueue = new PriorityQueue<State>(globalNodeList.size()*2, stateComparator);
		
		// CALC FOR UNION PLAYER.
		int counter = 0;
		if(state.player == UNION){
			for( Node ux : unexplored){
				
				for( Node teamnode : state.union){
					if(g.isWalkable(ux, teamnode)){
						State forceState = new State(state, ux, UNION, FORCEMARCH, g);
						stateQueue.add(forceState);
						//forceState.printLog();
						break;
					}
				}
				State paraState = new State(state, ux, UNION, PARATROOP, g);
				stateQueue.add(paraState);
				//paraState.printLog();
			}
		}
		
		
		// CALC FOR CONFEDERACY PLAYER.
		if(state.player == CONFEDERACY){
			for( Node ux : unexplored){
				//TODO: Check if Force March is possible.
				for( Node teamnode : state.confederacy){
					if(g.isWalkable(ux, teamnode)){
						State forceState = new State(state, ux, CONFEDERACY, FORCEMARCH, g);
						stateQueue.add(forceState);
						//forceState.printLog();
						break;
					}
				}
				//TODO: New state with paratroop drop.
				State paraState = new State(state, ux, CONFEDERACY, PARATROOP, g);
				stateQueue.add(paraState);
				//paraState.printLog();
			}
		}
		//System.out.println("genState "+stateQueue.size());
		return stateQueue;
		
	}
	
	//INPUT-READ FUNC: Read input text file into HashMap.
	void readInitFile(){
				
		BufferedReader reader = null;
		String[] inputs;
				
		try {
				reader = new BufferedReader(new FileReader(initFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					 inputs = line.split(",");					    
					 globalNodeList.add(new Node(inputs[0].toString(), Double.parseDouble(inputs[1])-0.0f, Integer.parseInt(inputs[2])));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			currState.makeState(globalNodeList);                  // adds the init state to state.
		}
	
	//INPUT-READ MAP FUNC: Read input map file into HashMap.
	void readMapFile(){
			
		BufferedReader reader = null;
		String[] inputs;
			
		try{
			reader = new BufferedReader(new FileReader(mapFile));
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line = null;
		try{
			while ((line = reader.readLine()) != null) {
				inputs = line.split(",");
				g.addEdge(inputs[0].toString(), inputs[1].toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
		
	//INPUT FUNC: Parse Command Line.
	public void parseCommandLine(String[] args){
				
		int i=0;
		boolean flag = false;
		String option;
		
		if(args.length < 2){
			System.out.println("ERROR! CommandLine arguments are incorrect!");
			return;
		}
		
		while(i < args.length){
					
			option = args[i++].toString();
			//System.out.println("option: "+ option);
				
			if(option.equals("-t") && !flag){
				task = Integer.parseInt(args[i]);
				System.out.println("Task: "+task);			
			}
			
			if(option.equals("-d")){
				cutoffDepth = Integer.parseInt(args[i]);
				System.out.println("cutoffDepth: "+ cutoffDepth);
			}
			
			if(option.equals("-m")){
				mapFile = args[i];
				System.out.println("mapFile: "+ mapFile);
			}
			
			if(option.equals("-i")){
				initFile = args[i];
				System.out.println("inputFile: "+ initFile);
			}				
				
			if(option.equals("-op")){
				outputFile = args[i];
				System.out.println("outputFile: "+ outputFile);
			}
			if(option.equals("-ol")){
				outputLogFile = args[i];
				System.out.println("outLogFile: "+ outputLogFile);
			}
			i++;
			flag = true;
			if(i >= args.length){
				//System.out.println("SUCCESS!");
				break;
			}
		}				
	}
	
	public static void printMap(String name) {
		Iterator<Entry<String, Double>> i = g.edgeMap.get(name).entrySet().iterator();	
		while (i.hasNext()) {
	        Map.Entry<String, Double> pairs = (Map.Entry<String, Double>)i.next();
	        System.out.println(pairs.getKey() + " = " + pairs.getValue());	       
	    }
	}
	public void generateOutputFile(List<State> stateLog){
		 try {
			       BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
			       		int turn = 0;
			           for(State st : stateLog){
			        	 //turn.
			       		out.write("TURN = "+turn);
			       		out.newLine();
			       		
			       		//Player.
			       		if(st.player == UNEXPLORED)
			       			System.out.println("PLAYER = N/A");
			       		if(st.player == CONFEDERACY)
			       			System.out.println("PLAYER = Confederacy");
			       		if(st.player == UNION)
			       			System.out.println("PLAYER = Union");
			       		
			       		//Action.
			       		if(turn == 0)
			       			System.out.println("ACTION = N/A");
			       		else if(st.action == 0)
			       			System.out.println("ACTION = FORCE MARCH");
			       		else
			       			System.out.println("ACTION = PARATROOP DROP");
			       		
			       		//Destination.
			       		if(turn == 0)
			       			System.out.println("DESTINATION = N/A");
			       		else 
			       			System.out.println("DESTINATION = "+st.destination.name);
			       				
			       		String str = st.getUnion();
			       		if (str.contentEquals("}"))
			       			System.out.println("Union,{},");
			       		else
			       		System.out.println("Union,{"+str+"},"+st.calcUnionCost());
			       		
			       		str = st.getConfederacy();
			       		if (str.contentEquals("}"))
			       			System.out.println("Confederacy,{},");
			       		else
			       		System.out.println("Confederacy,{"+str+"},"+st.calcConfederacyCost());
			       		System.out.println("----------------------------------------------");
			       		turn++;
			           }
			            out.close();
			 } catch (IOException e) {
			     e.printStackTrace();
			   }
		}
	
	void selectTask(int task){
		
		switch(task){
		
		case 1: 
			while((currState.union.size() + currState.confederacy.size() != globalNodeList.size())){
			PriorityQueue<State> tempqueue = new PriorityQueue(globalNodeList.size()*2, stateComparator);
			
			tempqueue = generateStates(currState);
			currState = tempqueue.remove();
			currState.turn += 1;
			stateLog.add(currState);
			currState.printState();
			}
			//generateOutputFile(stateLog);
			break;
			
		case 2:
			
				/*while((currState.union.size() + currState.confederacy.size() != globalNodeList.size())){
					currState.turn += 1;
					currState = minmaxDecision(currState);
					currState.printState();
					currState.turn +=1;
					PriorityQueue<State> tempqueue = new PriorityQueue(globalNodeList.size()*2, stateComparator);
					tempqueue = generateStates(currState);
					currState = tempqueue.remove();
					currState.printState();
				}*/
			
			currState.turn += 1;
			//currState.cost = currState.eval();
			currState = minmaxDecision(currState);
			currState.printState();
			
			PriorityQueue<State> tempqueue = new PriorityQueue(globalNodeList.size()*2, stateComparator);		
			tempqueue = generateStates(currState);
			currState = tempqueue.remove();
			currState.turn += 1;
			currState.printState();
			
			/*
			currState.turn += 1;
			currState = minmaxDecision(currState);
			currState.printState();
			*/	//State temp2 = new State(currState);
				//currState = minmaxDecision(temp2);
				//currState.printState();
				System.out.println("expandedModes: "+expandedNodes);
			
			break;
			
		case 3:
			break;
		
		default:
			System.out.println("ERROR! Wrong task or task not inputted!");
			break;
		}
	}
	public static void main(String[] args){
		
		war w = new war(); 
		
		w.parseCommandLine(args);
		w.readInitFile();
		w.readMapFile();
		
		currState.printState();
		stateLog.add(currState);
		w.selectTask(task);
		
		//List<State> sa = genList(currState);
		
		//currState = minMax(currState, UNION, 1);
		//currState.turn +=1;
		//currState.printState();
		
		/*currState = minMax(currState, UNION, 0);
		currState.turn += 1;
		currState.printState();
		*/
		/*while((currState.union.size() + currState.confederacy.size() != globalNodeList.size())){
			PriorityQueue<State> tempqueue = new PriorityQueue(globalNodeList.size()*2, stateComparator);
			tempqueue = generateStates(currState);
			currState = tempqueue.remove();
			currState.turn += 1;
			currState.printState();
		}*/
		
		
		/*PriorityQueue<State> tempqueue = new PriorityQueue(globalNodeList.size()*2, stateComparator);
		tempqueue = generateStates(currState);
		currState = tempqueue.remove();
		currState.turn += 1;
		currState.printState();
		tempqueue = generateStates(currState);
		currState = tempqueue.remove();
		currState.turn += 1;
		currState.printState();
		*/
		//DEBUG: globalList.
		/*for(Node i : globalNodeList){
			System.out.println(i.name+"  "+i.value+" "+i.status);
		}*/	
		
		
		
	}
	
}
