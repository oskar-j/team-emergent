package EmergingTeams;

import java.util.ArrayList;
import java.lang.String;

public class DefaultAgent {
	
	protected String name = "DefaultAgent";
	
	public String firstname;
	public String lastname;

	/***
	 * Default Constructor
	 */
	public DefaultAgent() {
	}

	/***
	 * Get's this agent's name. Don't delete or change this function or weird
	 * behavior could ensue.
	 */
	public String getName() {
		return name;
	}

	/***
	 * decideActions is called when the agent needs to make a decision on
	 * whether it will join a task or not and if it does what task it will join
	 * 
	 * @param thisAgent
	 *            the stats of this agent
	 * @param neighbors
	 *            the stats of all the neighbors of this agent
	 * @param currentTime
	 *            the current clock tick count
	 * @param tasks
	 *            all the tasks that are currently inactive and legal to join.
	 * @return the index of the task this agent would like to join
	 */
	public int decideAction(NodeViewable thisAgent,
			ArrayList<NodeViewable> neighbors, double currentTime,
			ArrayList<TeamFormationModel.Task> tasks) {

		if (tasks.size() > 0)
			return 0;

		return -1;
	}

	/***
	 * This method is called when the agent is freed from any tasks. An
	 * excellent place to perform learning or similar functions
	 * 
	 * @param thisAgent
	 *            the stats of this agent
	 * @param neighbors
	 *            the stats of all the neighbors of this agent
	 * @param currentTime
	 *            the current clock tick count
	 * @param lastTaskSuccessful
	 *            whether or not the previous task was successful.
	 * @param previousTask
	 *            the task just completed
	 */
	public void free(NodeViewable thisAgent, ArrayList<NodeViewable> neighbors,
			double currentTime, boolean lastTaskSuccessful,
			TeamFormationModel.Task previousTask) {
		// TO DO: write somethin here
	}
}
