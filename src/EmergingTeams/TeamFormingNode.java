package EmergingTeams;

/*
 TeamFormingNode.java
 Author: Adam Anthony
 demonstrative networked agent that strives to form a ring network with its neighbors
 */

import java.awt.Color;

import sun.text.UCompactIntArray;

//import uchicago.src.sim.util.Random;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;

public class TeamFormingNode {
	/*
	 * public final int UNCOMMITTED = 0; public final int COMMITTED = 1; public
	 * final int ACTIVE = 2;
	 */
	public enum state {
		UNCOMMITTED, COMMITTED, ACTIVE
	}

	public Object name;

	public int getSkill() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasEdgeToOrFrom(TeamFormingNode teamFormingNode) {
		// TODO Auto-generated method stub
		return false;
	}

	public void activate() {
		// TODO Auto-generated method stub
		
	}

	public void free() {
		// TODO Auto-generated method stub
		
	};

	
}
