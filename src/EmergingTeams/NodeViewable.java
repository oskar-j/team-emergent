package EmergingTeams;

import EmergingTeams.TeamFormingNode.state;

public class NodeViewable {
	private int mySkill;
	private state myState;
	private double myScore;
	private int myAcceptedTasks;
	private int mySuccessfulTasks;

	public NodeViewable(int skill, state initialState, double score,
			int acceptedTasks, int successfulTasks) {
		mySkill = skill;
		myState = initialState;
		myScore = score;
		myAcceptedTasks = acceptedTasks;
		mySuccessfulTasks = successfulTasks;
	}

	public NodeViewable(NodeViewable otherNodeViewable) {
		if (this != otherNodeViewable) {
			mySkill = otherNodeViewable.getSkill();
			myState = otherNodeViewable.getState();
			myScore = otherNodeViewable.getScore();
			myAcceptedTasks = otherNodeViewable.getAcceptedTasks();
			mySuccessfulTasks = otherNodeViewable.getSuccessfulTasks();
		}
	}

	/***
	 * @return this node's skill
	 */
	public int getSkill() {
		return mySkill;
	}

	/***
	 * Gets the node's current state. The state can be one of the following:
	 * UNCOMMITTED, COMMITTED, or ACTIVE.
	 * 
	 * If a node is UNCOMMITTED that means it currently is not on any team. If a
	 * node is COMMITTED that means the node is on a team but the requirements
	 * for the team haven't been met yet. If a node is ACTIVE that means the
	 * node is on a team and the requirements for the team have been met.
	 * 
	 * @return the node's state
	 */
	public state getState() {
		return myState;
	}

	/***
	 * @return the score of the node
	 */
	public double getScore() {
		return myScore;
	}

	/***
	 * @return the number of tasks this node has accepted
	 */
	public int getAcceptedTasks() {
		return myAcceptedTasks;
	}

	/***
	 * @return the number of tasks this node has completed successfully.
	 */
	public int getSuccessfulTasks() {
		return mySuccessfulTasks;
	}
}
