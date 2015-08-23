package war;

import java.util.Comparator;

public class CostComparator implements Comparator<State>{

	@Override
	public int compare(State state1, State state2) {
		if(state1.cost > state2.cost)
			return -1;
		else if(state1.cost < state2.cost){
			return 1;
		}
		else if(state1.cost == state2.cost){
			//return state1.destination.name.compareTo(state2.destination.name);
			  if(state1.action < state2.action)
				return -1;
			else if(state1.action > state2.action){
				return 1;
			}
			else if(state1.action == state2.action){
				return state1.destination.name.compareTo(state2.destination.name);
			}
		}
		return 0;
	}

}
