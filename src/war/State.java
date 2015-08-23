package war;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class State implements Comparable<State>, Cloneable{

	public static final int UNEXPLORED = 0;
	public static final int CONFEDERACY = -1;
	public static final int UNION = 1;
	
	public static final int FORCEMARCH = 0;
	public static final int PARATROOP = 1;	
	
	static int turn = 0;
	
	List<Node> union = new ArrayList();
	List<Node> confederacy = new ArrayList();
	List<Node> unexplored = new ArrayList();
	
	double costUnion;
	double costConfederacy;
	
	double cost;
	
	int action;
	Node destination;
	
	int player;
	
	State(){}
	
	State(State state){
		
		this.union = new ArrayList();
		for(Node n : state.union){
			this.union.add(n);
		}
		
		this.confederacy = new ArrayList();
		for(Node n : state.confederacy){
			this.confederacy.add(n);
		}
		
		this.unexplored = new ArrayList(state.unexplored);
		for(Node n : state.unexplored){
			this.unexplored.add(n);
		}
		
		this.player = state.player;
		this.action = state.action;
		this.cost = this.cost;
		
		this.destination = new Node(state.destination);
	}
	
	State(State state, Node ux, int player, int action, Graph g){
		
		this.union = new ArrayList(state.union);
		this.confederacy = new ArrayList(state.confederacy);
		this.unexplored = new ArrayList(state.unexplored);
		this.action = action;
		this.destination = ux;
		
		this.unexplored.remove(ux);
		
		this.player = player;
		//MODIFICATIUON 
		
		//For FORCEMARCH.
		List<Node> adjNodes = new ArrayList();
		if(action == FORCEMARCH && player == UNION){
			/*
			for(Node teamnode : this.union){
				if(g.isWalkable(ux.name, teamnode.name)){
					adjNodes.add(teamnode);
				}
			}*/
		
			if(player == UNION){
				ux.status = UNION;
				union.add(ux);
			}else if(player == CONFEDERACY){
				ux.status = CONFEDERACY;
				confederacy.add(ux);
			}
			
			List<Node> conquered = new ArrayList();
			
			//for(Node adj : adjNodes){
				for(Node oppnode : this.confederacy){
					if(g.isWalkable(ux.name, oppnode.name)){
						conquered.add(oppnode);
					}
				}
			//}
			
			if(!conquered.isEmpty()){				
				for(Node conq : conquered){
					Iterator<Node> it = this.confederacy.iterator();
					while(it.hasNext()){
						Node value = it.next();
						if(value.name.contentEquals(conq.name)){
							it.remove();
						}
					}
				}
				
				for(Node conq : conquered){
					if(!this.union.contains(conq)){
					conq.status = UNION;
					this.union.add(conq);
					}
				}
			}
		}
		
		//FORCE MARCH: CONFEDERACY.
		if(action == FORCEMARCH && player == CONFEDERACY){
			/*for(Node teamnode : this.confederacy){
				if(g.isWalkable(ux.name, teamnode.name)){
					adjNodes.add(teamnode);
					
				}
			}*/
		
				ux.status = CONFEDERACY;
				confederacy.add(ux);
			
			List<Node> conquered = new ArrayList();
			
			//for(Node adj : adjNodes){
				for(Node oppnode : this.union){
					if(g.isWalkable(ux.name, oppnode.name)){
						conquered.add(oppnode);
					}
				}
			//}
			
			if(!conquered.isEmpty()){
				
				/*for(Node conq : conquered){
					for(Node i : this.confederacy){
						if(conq.name.contentEquals(i.name)){
							this.confederacy.remove(i);
						}
					}
				}*/
				
				for(Node conq : conquered){
					Iterator<Node> it = this.union.iterator();
					while(it.hasNext()){
						Node value = it.next();
						if(value.name.contentEquals(conq.name)){
							it.remove();
						}
					}
				}
				
				for(Node conq : conquered){
					if(!this.confederacy.contains(conq)){
					conq.status = CONFEDERACY;
					this.confederacy.add(conq);
					}
				}
			}
		}
		if(action == PARATROOP && player == UNION){
			ux.status = UNION;
			union.add(ux);
		}
		
		if(action == PARATROOP && player == CONFEDERACY){
			ux.status = CONFEDERACY;
			confederacy.add(ux);
		}
		
		costUnion = calcUnionCost();
		costConfederacy = calcConfederacyCost();
		
	}
	
	void makeState(List<Node> initList){
		
		for(Node node : initList){
			if(node.status == UNEXPLORED)
				unexplored.add(new Node(node));
			
			if(node.status == UNION)
				union.add(new Node(node));
			
			if(node.status  == CONFEDERACY)
				confederacy.add(new Node(node));
		}
		costUnion = calcUnionCost();
		costConfederacy = calcConfederacyCost();
		player = UNEXPLORED;
	}
	
	double calcUnionCost(){
		double cost = 0.0;
		for(Node n : union)
			cost += n.value;	
	return cost;
	}
	
	double calcConfederacyCost(){
		double cost = 0.0;
		for(Node n : confederacy)
			cost += n.value;	
	return cost;
	}
	
	double eval(){
		if(player == UNION){
			return calcUnionCost() - calcConfederacyCost();
		}
		else{ 			 
			return calcConfederacyCost() - calcUnionCost();
		}
	}
	
	String getUnion(){
		String str = "";
		
		for(Node n : union){
			str += n.name+",";
		}
		str += "}";
		str = str.replace(",}", "");
		return str;		
	}
	
	String getConfederacy(){
		String str = "";
		
		for(Node n : confederacy){
			str += n.name+",";
		}
		str += "}";
		str = str.replace(",}", "");
		return str;		
	}
	
	void printState(){
		//turn.
		System.out.println("TURN = "+turn);
		
		//Player.
		if(player == UNEXPLORED)
			System.out.println("PLAYER = N/A");
		if(player == CONFEDERACY)
			System.out.println("PLAYER = Confederacy");
		if(player == UNION)
			System.out.println("PLAYER = Union");
		
		//Action.
		if(turn == 0)
			System.out.println("ACTION = N/A");
		else if(action == 0)
			System.out.println("ACTION = FORCE MARCH");
		else
			System.out.println("ACTION = PARATROOP DROP");
		
		//Destination.
		if(turn == 0)
			System.out.println("DESTINATION = N/A");
		else 
			System.out.println("DESTINATION = "+destination.name);
				
		String str = getUnion();
		System.out.println("Union,{"+str+"},"+calcUnionCost());
		
		str = getConfederacy();
		System.out.println("Confederacy,{"+str+"},"+calcConfederacyCost());
		System.out.println("----------------------------------------------");
	}
	
	void printLog(int depth){
		if(player == CONFEDERACY)
			System.out.print("Confederacy, ");
		if(player == UNION)
			System.out.print("Union, ");
		
		/*
		if(turn == 0)
			System.out.println("N/A");
		else if(turn%2 == 0 && turn != 0)
			System.out.print("Confederacy, ");
		else if(turn%2 == 1)
			System.out.print("Union, ");
		
		*/
		if(action == FORCEMARCH)
			System.out.print("Force March, ");
		else
			System.out.print("Paratroop Drop, ");
		
		System.out.print(this.destination.name+", ");
		
		System.out.print(depth+", ");
		
		System.out.println(this.cost);
	}
	
	void printLog(int depth, double cc){
		if(player == CONFEDERACY)
			System.out.print("Confederacy, ");
		else
			System.out.print("Union, ");
		
		/*
		if(turn == 0)
			System.out.println("N/A");
		else if(turn%2 == 0 && turn != 0)
			System.out.print("Confederacy, ");
		else if(turn%2 == 1)
			System.out.print("Union, ");
		
		*/
		if(action == FORCEMARCH)
			System.out.print("Force March, ");
		else
			System.out.print("Paratroop Drop, ");
		
		System.out.print(this.destination.name+", ");
		
		System.out.print(depth+", ");
		
		System.out.println(cc);
	}

	@Override
	public int compareTo(State other) {
		if(eval() > other.eval())
			return 1;
		else if(eval() < other.eval()){
			return -1;
		}
		else if(eval() == other.eval()){
			//return state1.destination.name.compareTo(state2.destination.name);
			  if(action < other.action)
				return 1;
			else if(action > other.action){
				return -1;
			}
			else if(action == other.action){
				return destination.name.compareTo(other.destination.name);
			}
		}
		return 0;
	}
}
