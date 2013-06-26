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

public class TeamFormingNode extends DefaultDrawableNode {
	/*
	 * public final int UNCOMMITTED = 0; public final int COMMITTED = 1; public
	 * final int ACTIVE = 2;
	 */
	public enum state {
		UNCOMMITTED, COMMITTED, ACTIVE
	};

	// so a state is one of the three constants above.
	protected state agent_state;

	protected Color activeColor;
	protected Color committedColor;
	protected Color uncommittedColor;
	protected Color borderColor;

	protected int skill;
	protected int total_accepted_tasks;
	protected int failed_tasks;
	protected int successful_tasks;
	private boolean cheated;

	public TeamFormationModel world;
	public TeamFormationModel.Task currentTask;
	protected ArrayList<NodeViewable> taskNeighbors;
	protected String packageName = "edu.umbc.cs.maple.autumn.basicTeamForm.DefaultAgent";

	protected String primary_name = "RANDOM BOT";

	protected DefaultAgent agent;

	public String name;
	public String nTag;

	public TeamFormingNode() {
	}

	// public TeamFormingNode(TeamFormationModel mdl, int nTag, int sk, Color
	// bColor) {
	// init(mdl,primary_name+nTag,sk,Color.BLUE,Color.RED,Color.GREEN,bColor);
	// }

	public TeamFormingNode(TeamFormationModel mdl, int nTag, int sk,
			Class agentClass, Color bColor) throws IllegalAccessException,
			InstantiationException {
		init(mdl, nTag, sk, agentClass, bColor);
	}

	public int getSkill() {
		return skill;
	}

	public String getPrimaryName() {
		return primary_name;
	}

	/***
	 * returns this agents score. Currently calculated as +1 for completed tasks
	 * and -1 for failed tasks
	 * 
	 * @return this agents score
	 */
	public double score() {
		if (cheated)
			return -500000;
		return (2 * (double) successful_tasks)
				- (total_accepted_tasks - successful_tasks);
		// return (double)total_accepted_tasks;
	}

	/***
	 * If agent's are uncommitted it gives them the oppurtunity to join a task.
	 * If they are committed or active it displays the interior of their node
	 * the apporpriate color and changes the edges that connect it to its team
	 * members to either the committed or active color (depending on waht state
	 * the team's in) to allow the team connection to be easily visible.
	 */
	public void work() {

		// randomize the list of inactive tasks
		world.shuffleTasks();

		if (agent_state == state.UNCOMMITTED) {

			// Turn the agents stats into a viewable format
			NodeViewable thisAgent = new NodeViewable(skill, agent_state,
					score(), total_accepted_tasks, successful_tasks);

			// Create List of Neighbors
			// Note: we don't send in the list of nodes to prevent the agent
			// from manipulating
			// the nodes for their own benefit. (i.e. removing edges, or forcing
			// them to
			// commit to tasks).
			ArrayList<TeamFormingNode> currentNeighbors = new ArrayList<TeamFormingNode>(
					getInNodes());
			taskNeighbors = new ArrayList<NodeViewable>();
			for (int i = 0; i < currentNeighbors.size(); i++) {
				int neighborSkill = currentNeighbors.get(i).getSkill();
				state neighborState = currentNeighbors.get(i).getAgentState();
				double neighborScore = currentNeighbors.get(i).score();
				;

				NodeViewable neighbor = new NodeViewable(neighborSkill,
						neighborState, neighborScore, total_accepted_tasks,
						successful_tasks);

				taskNeighbors.add(neighbor);
			}

			// Get list of all inactive tasks and then create a second list of
			// tasks this agent can
			// legally commit to. Also created one final list linking the second
			// lists entries to the
			// first's.
			ArrayList<TeamFormationModel.Task> tasks = world.getInactiveTasks();
			ArrayList<TeamFormationModel.Task> legalTasks = new ArrayList<TeamFormationModel.Task>();
			ArrayList<Integer> links = new ArrayList<Integer>();

			for (int i = 0; i < tasks.size(); i++) {
				if (canJoin(tasks.get(i))) {
					legalTasks.add(tasks.get(i));
					links.add(i);
				}
			}

			// Call Agent function to decide what to do
			int taskIndex = agent.decideAction(thisAgent, taskNeighbors, world
					.getSchedule().getCurrentTime(),
					new ArrayList<TeamFormationModel.Task>(legalTasks));

			// If decided to commit to a task, commit to it
			if (taskIndex > -1 && taskIndex < links.size()) {
				TeamFormationModel.Task t = tasks.get(links.get(taskIndex));
				if (canJoin(t)) {
					if (t.commit(this) || t.onTeam(this)) {
						agent_state = state.COMMITTED;
						currentTask = t;
						total_accepted_tasks++;
					}
				}
			}

			// Check to make sure the agent isn't cheating by commiting to a
			// another task
			// Note: There shouldn't be any way for them to cheat in this manner
			for (int j = 0; j < tasks.size(); j++) {
				// Make sure its not the agent's actual selection
				if (taskIndex > -1 && taskIndex < links.size()) {
					if (j == links.get(taskIndex)) {
						continue;
					}
				}
				TeamFormationModel.Task t = tasks.get(j);
				if (t.onTeam(this)) {
					cheated = true;
				}
			}

		}

		ArrayList edO = null;
		switch (agent_state) {
		case UNCOMMITTED:
			setColor(uncommittedColor);
			setBorderColor(borderColor);
			edO = getOutEdges();

			Iterator edgeIter = edO.iterator();
			while (edgeIter.hasNext()) {
				SimpleEdge temp_edge = (SimpleEdge) edgeIter.next();
				temp_edge.setColor(Color.LIGHT_GRAY);
				temp_edge.setWidth(1);
			}

			edO = getInEdges();
			edgeIter = edO.iterator();
			while (edgeIter.hasNext()) {
				SimpleEdge temp_edge = (SimpleEdge) edgeIter.next();
				temp_edge.setColor(Color.LIGHT_GRAY);
				temp_edge.setWidth(1);
			}

			break;
		case COMMITTED:
			setColor(committedColor);
			setBorderColor(borderColor);
			if (currentTask != null) {
				ArrayList<TeamFormingNode> neighbors = currentTask
						.getNeighborsOnTeam(this);

				for (int i = 0; i < neighbors.size(); i++) {
					HashSet edT = getEdgesTo(neighbors.get(i));
					if (edT != null) {
						edgeIter = edT.iterator();
						while (edgeIter.hasNext()) {
							SimpleEdge temp_edge = (SimpleEdge) edgeIter.next();
							temp_edge.setColor(committedColor);
							temp_edge.setWidth(7);
						}
					}

					HashSet edF = getEdgesFrom(neighbors.get(i));
					if (edF != null) {
						edgeIter = edF.iterator();
						while (edgeIter.hasNext()) {
							SimpleEdge temp_edge = (SimpleEdge) edgeIter.next();
							temp_edge.setColor(committedColor);
							temp_edge.setWidth(7);
						}
					}
				}
			}
			break;
		case ACTIVE:
			setColor(activeColor);
			setBorderColor(borderColor);
			if (currentTask != null) {
				ArrayList<TeamFormingNode> neighbors = currentTask
						.getNeighborsOnTeam(this);
				for (int i = 0; i < neighbors.size(); i++) {
					HashSet edT = getEdgesTo(neighbors.get(i));
					if (edT != null) {
						edgeIter = edT.iterator();
						while (edgeIter.hasNext()) {
							SimpleEdge temp_edge = (SimpleEdge) edgeIter.next();
							temp_edge.setColor(activeColor);
							temp_edge.setWidth(7);
						}
					}

					HashSet edF = getEdgesFrom(neighbors.get(i));
					if (edF != null) {
						edgeIter = edF.iterator();
						while (edgeIter.hasNext()) {
							SimpleEdge temp_edge = (SimpleEdge) edgeIter.next();
							temp_edge.setColor(activeColor);
							temp_edge.setWidth(7);
						}
					}
				}
			}
			break;
		default:
			break;
		}
	}

	/***
	 * Forces an agent to start a new task, if it can commit to a task
	 * 
	 * @param t
	 *            the task the agent is being forced to commit to
	 * @return true if the agent was successfully forced into commiting, falso
	 *         otherwise
	 * 
	 */
	public boolean forceCommit(TeamFormationModel.Task t) {
		if (agent_state == state.UNCOMMITTED && canJoin(t)) {
			if (t.commit(this)) {
				agent_state = state.COMMITTED;
				currentTask = t;
				total_accepted_tasks++;
				return true;
			}
		}
		return false;
	}

	/***
	 * returns true if the agent can join this task
	 * 
	 * @param t
	 *            the task in question
	 * @return true if the agent is allowed to join this task, falso otherwise
	 */
	protected boolean canJoin(TeamFormationModel.Task t) {

		return !t.hasTeam() || t.hasNeighborOnTeam(this);
	}

	protected boolean acceptTask(TeamFormationModel.Task t) {
		// default random move: coin toss
		double check = Math.random();
		if (check <= 0.5) {
			return true;
		}
		return false;
	}

	/***
	 * Prints the important statistics of this agent to standard output
	 */
	public void printStats() {

		System.out.println("******************************");
		System.out.println("Stats for agent " + name);
		System.out.println("Skill: " + skill);
		System.out.println("Total teams joined: " + total_accepted_tasks);
		System.out.println("Total tasks completed: " + successful_tasks);
		System.out.println("Total tasks failed: "
				+ (total_accepted_tasks - successful_tasks));
	}

	/***
	 * returns the current state
	 * 
	 * @return the node's state
	 */
	public state getAgentState() {
		return agent_state;
	}

	/***
	 * Makes the node go from committed to active when a team has been
	 * completely filled.
	 * 
	 */
	public void activate() {
		agent_state = state.ACTIVE;
		setColor(activeColor);
		setBorderColor(borderColor);
	}

	/***
	 * method called when the agent is freed from a task
	 * 
	 */
	public void free() {
		// if(agent_state == COMMITTED) failed_tasks++;
		boolean taskSuccessful = false;

		if (agent_state == state.ACTIVE) {
			successful_tasks++;
			if (successful_tasks > total_accepted_tasks) {
				cheated = true;
			}
			taskSuccessful = true;
		}

		// Turn the agents stats into a viewable format
		NodeViewable thisAgent = new NodeViewable(skill, agent_state, score(),
				total_accepted_tasks, successful_tasks);

		agent.free(thisAgent, taskNeighbors, world.getSchedule()
				.getCurrentTime(), taskSuccessful, currentTask);
		agent_state = state.UNCOMMITTED;
		currentTask = null;
		determineNewBorderWidth();
	}

	/***
	 * Changes the width of the nodes border depending on the nodes score.
	 * Higher scores receive larger widths.
	 * 
	 * @return the borderwidth that was determined
	 */
	public int determineNewBorderWidth() {
		if (score() < 0)
			this.setBorderWidth(1);
		else if (score() == 0)
			this.setBorderWidth(2);
		else if (score() < 75) {
			this.setBorderWidth((int) (score() / 5) + 2);
		} else {
			this.setBorderWidth(17);
		}

		return this.getBorderWidth();

	}

	/**
	 * Initialize this RingNode, this takes the place of the constructor when a
	 * RingNode is created from a file using its no-arg constructor. x : x
	 * position in the drawing window y : y position in the drawing window sk:
	 * the agent's skill note -- the initial state of an agent is
	 * TeamFormingNode.state.UNCOMMITTED; The colors specify how the agent will
	 * appear: uColor --> uncommitted color cColor --> committed color aColor
	 * --> active color bColor --> border color
	 */
	public void init(TeamFormationModel mdl, String nm, int sk,
			Class AgentClass, Color bColor) throws IllegalAccessException,
			InstantiationException {
		init(mdl, nm, sk, AgentClass, Color.BLUE, Color.RED, Color.GREEN,
				bColor);
	}

	public void init(TeamFormationModel mdl, String nm, int sk,
			Class AgentClass, Color uColor, Color cColor, Color aColor,
			Color bColor) throws IllegalAccessException, InstantiationException {
		activeColor = aColor;
		name = nm;
		committedColor = cColor;
		uncommittedColor = uColor;
		borderColor = bColor;
		total_accepted_tasks = 0;
		failed_tasks = 0;
		successful_tasks = 0;

		agent_state = state.UNCOMMITTED;

		this.setBorderWidth(3);
		skill = sk;
		world = mdl;

		agent = (DefaultAgent) AgentClass.newInstance();

	}

	public void init(TeamFormationModel mdl, int nTag, int sk,
			Class AgentClass, Color bColor) throws IllegalAccessException,
			InstantiationException {
		activeColor = Color.GREEN;
		committedColor = Color.RED;
		uncommittedColor = Color.BLUE;
		borderColor = bColor;
		total_accepted_tasks = 0;
		successful_tasks = 0;

		agent_state = state.UNCOMMITTED;

		this.setBorderWidth(3);
		skill = sk;
		world = mdl;

		agent = (DefaultAgent) AgentClass.newInstance();

		name = agent.getName() + nTag;
	}

	public String getAgentName() {
		return agent.getName();
	}

	public void setPosition(int x, int y) {
		RectNetworkItem rect = new RectNetworkItem(x, y);
		setDrawable(rect);
	}

	/**
	 * Makes an edge to the specified node and from the specifed node to
	 * thisRingNode if both nodes do not already have edges to each other and if
	 * adding the edge keeps their degrees less than maxDegree. The edges is
	 * displayed in the specified color.
	 */
	public void makeEdgeToFrom(TeamFormingNode node) {
		if ((!hasEdgeTo(node))) {

			Edge edge = new SimpleEdge(this, node, Color.LIGHT_GRAY);
			addOutEdge(edge);
			node.addInEdge(edge);
			Edge otherEdge = new SimpleEdge(node, this, Color.LIGHT_GRAY);
			node.addOutEdge(otherEdge);
			addInEdge(otherEdge);
		}
	}

	public TeamFormationModel.Task getCurrentTask() {
		return currentTask;
	}

	public String toString() {
		String outstr = name;
		outstr += ": Skill: ";
		outstr += skill;
		outstr += " Score: ";
		outstr += score();
		return outstr;
	}

}
