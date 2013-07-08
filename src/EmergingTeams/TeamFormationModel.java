package EmergingTeams;

/*
 TemplateModel.java 
 Author: Adam Anthony
 TeamFormationModel.java is a simple java agent model for repast3
 which is intended to demonstrate the capabilities of 
 connected agents in the repast 3 simulator

 */

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Collections;
import java.util.Comparator;
import java.util.Arrays;
import static java.lang.System.out;

import java.util.Iterator;

import schedule.Schedule;


public class TeamFormationModel {

	// model variables
	private int rows = 15;
	private int columns = 10;
	private ArrayList<TeamFormingNode> agentList;
	private int worldXSize = 475;
	private int worldYSize = 400;
	private int updateEveryN = 5;
	private int initialSteps = 1;

	private int numSkills = 12;
	private int taskSize = 8;
	private int introInterval = 5;
	private double advertInterval = 10.0;
	private double durationInterval = 5.0;
	private int sim_end = 5000;
	private boolean stopGenerating = false;

	private ArrayList<Task> inactiveTasks;
	private ArrayList<Task> activeTasks;
	
	private Schedule schedule;

	private java.util.Random randSkill;

	public void setTaskSize(int t) {
		taskSize = t;
	}

	public int getTaskSize() {
		return taskSize;
	}

	// this function executes at every time step, and activates teams that have
	// fulfilled the requirements
	// for a task
	public void activateTasks() {
		for (Iterator<Task> i = inactiveTasks.iterator(); i.hasNext();) {
			Task temp = i.next();
			if (temp.active()) {
				activeTasks.add(temp);
				temp.activateTeam();
				i.remove();
			}
		}
	}

	public class Task {
		/*
		 * Class task is an important class for the team formation contest. You
		 * do not need to worry about creating or deleting tasks, but you will
		 * need to access some of the methods contained in this class in order
		 * to decide whether or not joining this task is a good idea. The
		 * comments below explicitly tell you which tasks are important to you,
		 * the teamForming agent creator.
		 */

		private int[] skill_set;
		private TeamFormingNode[] team;
		private boolean active;
		private boolean has_team;
		private int numCommit;
		private double introduce_time, expire_time;
		private double start_time;
		private double end_time;

		// we'll base the constructor off of the scheduler's clock.
		// this is possible becaue the scheduler is discrete, so the
		// time the scheduler has when the constructor is called should
		// be the proper creation time
		public Task() {
			skill_set = generateTask();
			introduce_time = schedule.getCurrentTime();
			expire_time = introduce_time + (taskSize * advertInterval);
			start_time = -1;
			end_time = -1;
			team = new TeamFormingNode[taskSize];
			active = false;
			numCommit = 0;
		}

		public Task(Task t) {
			if (t != this) {
				skill_set = t.getSkillSet();
				introduce_time = t.getIntroTime();
				expire_time = t.getExpireTime();
				start_time = t.start_time;
				end_time = t.end_time;
				team = new TeamFormingNode[taskSize];
				for (int i = 0; i < team.length; i++) {
					team[i] = t.team[i];
				}
				active = t.active();
				numCommit = t.numMembers();
			}
		}

		/***
		 * @return the clock tick that this task was first introduced
		 */
		public double getIntroTime() {
			return introduce_time;
		}

		/***
		 * @return the clock tick that this task will expire at
		 */
		public double getExpireTime() {
			return expire_time;
		}

		/***
		 * @return the amount of clock ticks left until this task expires
		 */
		public double getTimeLeft() {
			return getExpireTime() - schedule.getCurrentTime();
		}

		/***
		 * @return the total time the task is avaliable for
		 */
		public double getDuraction() {
			return getExpireTime() - getIntroTime();
		}

		/***
		 * commit: a function that accepts a node into the task's team
		 * 
		 * The node must both be useful to the team and be able to join the
		 * team. If both of these conditions are satisfied, then the node is
		 * added to the team, and the method returns true to let the node know
		 * that the commitment is mutual. If the node isn't useful or if the
		 * node can't join the team, the method returns false and the node isn't
		 * added to the team.
		 * 
		 * @param joiner
		 *            the node that wishes to join the team
		 * @return if the node successfully joined the task
		 */
		public boolean commit(TeamFormingNode joiner) {
			// check to make sure joiner isn't already on the team
			if (onTeam(joiner))
				return false;

			// check to make sure joiner is a neighbor to someone already on the
			// team
			if (hasTeam() && !hasNeighborOnTeam(joiner))
				return false;

			// Check to see if the joiner has a skill that is useful for the
			// team
			int skill = joiner.getSkill();
			int index = findPlace(skill);
			// if the index is greater than 0, then the agent must be useful for
			// the team
			if (index >= 0) {
				// add the agent to the team, and update the statistics of the
				// task.
				team[index] = joiner;
				numCommit++;
				// check to see if the task is now ready to be activated,
				// and if it is activate it
				if (numCommit == skill_set.length) {
					active = true;
					start_time = schedule.getCurrentTime() + 1;
					end_time = start_time + (taskSize * durationInterval);
				}
				has_team = true;
				return true;
			}
			return false;
		}

		/***
		 * checks to see if this node has a skill that is useful for this task
		 * 
		 * @param joiner
		 *            the node to check
		 * @return true if joiner's skill is needed, false otherwise
		 */
		public boolean needSkill(TeamFormingNode joiner) {
			int skill = joiner.getSkill();

			return (findPlace(skill) >= 0);
		}

		/***
		 * checks to see if this skill is useful for this task
		 * 
		 * @param skill
		 *            the skill to check
		 * @return true if this skill is useful, false otherwise
		 */
		public boolean needSkill(int skill) {
			return (findPlace(skill) >= 0);
		}

		/*
		 * Checks to see if this node has a neighbor already on the team
		 * 
		 * @param iNode the node to check
		 * 
		 * @return true if this node has a neighbor on the team, false otherwise
		 */
		public boolean hasNeighborOnTeam(TeamFormingNode iNode) {
			for (int i = 0; i < team.length; i++) {
				if (iNode.hasEdgeToOrFrom(team[i]))
					return true;
			}
			return false;
		}

		/***
		 * Gives the neighbors of this node
		 * 
		 * @param iNode
		 *            the node
		 * @return an arraylist of iNodes neighbors that are on this Task.
		 */
		public ArrayList<TeamFormingNode> getNeighborsOnTeam(
				TeamFormingNode iNode) {
			ArrayList<TeamFormingNode> neighbors = new ArrayList<TeamFormingNode>();

			for (int i = 0; i < team.length; i++) {
				if (iNode.hasEdgeToOrFrom(team[i])) {
					neighbors.add(team[i]);
				}
			}

			return neighbors;
		}

		/*
		 * Checks to see if this node is on the team
		 * 
		 * @param the node to check
		 * 
		 * @return true if this node is on the team, false otherwise
		 */
		public boolean onTeam(TeamFormingNode iNode) {
			for (int i = 0; i < taskSize(); i++) {
				if (this.team[i] != null) {
					if (iNode.name == team[i].name) {
						// SimUtilities.showMessage("On Team True " + iNode.name
						// + " " + i + " " + team[i].name);
						return true;
					}
				}
			}

			return false;
		}

		/***
		 * checks if their is a team already starting to form on this task.
		 * 
		 * @return true if their is at least one node on the team, false
		 *         otherwise
		 */
		public boolean hasTeam() {
			return has_team;
		}

		/***
		 * @return the number of agents that have joined this task
		 */
		public int numMembers() {
			return numCommit;
		}

		/***
		 * @return the total number of agents needed (regardless of the skills
		 *         needed) to complete this task
		 */
		public int taskSize() {
			return skill_set.length;
		}

		/***
		 * @return returns an array of all the skills required for this array.
		 *         Skills needed more than once will be repeated the number of
		 *         times they are needed in the array
		 * 
		 * @return the skill set for this task
		 */
		public int[] getSkillSet() {
			return skill_set;
		}

		/***
		 * Gives an ArrayList of the skills of all the nodes that have commited
		 * to the team.
		 * 
		 * @return ArrayList of the node's skills that are committed to the
		 *         task.
		 */
		public ArrayList<Integer> getSkillsAlreadyOnTeam() {
			ArrayList<Integer> skillsUnsatisfied = new ArrayList<Integer>();

			for (int i = 0; i < team.length; i++) {
				if (team[i] != null) {
					skillsUnsatisfied.add(skill_set[i]);
				}
			}

			return new ArrayList<Integer>(skillsUnsatisfied);
		}

		/***
		 * Gives an ArrayList of all the skills that still need to be filled for
		 * this task. Skills that are needed more than once are shown as
		 * redundant entries. (i.e. a skill that is needed 3 times will appaer 3
		 * times in the array). The array is not necessarily sorted.
		 * 
		 * @return the skills that have not been satisfied for this task
		 */
		public ArrayList<Integer> getSkillsUnsatisfied() {
			ArrayList<Integer> skillsUnsatisfied = new ArrayList<Integer>();

			for (int i = 0; i < team.length; i++) {
				if (team[i] == null) {
					skillsUnsatisfied.add(skill_set[i]);
				}
			}

			return new ArrayList<Integer>(skillsUnsatisfied);
		}

		/***
		 * Does a linear search to find if this skill is needed, and if so
		 * where.
		 * 
		 * @param skill
		 *            the skill to search for
		 * @return the index on the team where this skill is needed, or -1 if
		 *         this skill isn't needed
		 */
		private int findPlace(int skill) {
			int index = -1;
			for (int i = 0; i < skill_set.length; i++) {
				if (skill_set[i] == skill && team[i] == null) {
					index = i;
					break;
				}
			}
			return index;
		}

		/***
		 * @return true if this task has expired, false otherwise
		 */
		public boolean expired() {
			return expire_time <= schedule.getCurrentTime();
		}

		/***
		 * @return true if this task is active, false otherwise
		 */
		public boolean active() {
			return active;
		}

		/***
		 * @return true if this task has completed, false otherwise
		 */
		public boolean completed() {
			return end_time > 0 && end_time <= schedule.getCurrentTime();
		}

		/***
		 * randomly generates the skill set for a task
		 * 
		 * @return the new skill set
		 */
		private int[] generateTask() {
			int[] newTask = new int[taskSize];
			for (int i = 0; i < newTask.length; i++) {
				newTask[i] = randSkill.nextInt(numSkills) + 1;
			}
			/*
			 * System.out.println("new task: "); for(int i = 0;
			 * i<newTask.length; i++){ System.out.print(newTask[i]); }
			 */

			Arrays.sort(newTask);
			// System.out.println();

			return newTask;
		}

		/***
		 * tells all the nodes on the team that the task has been activated
		 */
		private void activateTeam() {
			int length = team.length;
			for (int i = 0; i < length; i++) {
				team[i].activate();
			}
			printTask();
		}

		/***
		 * tells all the nodes on the team that the task is now over
		 */
		private void freeAgents() {
			for (int i = 0; i < team.length; i++) {
				if (team[i] != null)
					team[i].free();
			}
			skill_set = null;
			team = null;
			active = false;
			numCommit = 0;

		}

		/***
		 * prints all the statistics of the task.
		 */
		public void printTask() {
			try {

				out.println("Task Introduced at: " + introduce_time);
				out.print("skills: ");
				int i;
				for (i = 0; i < taskSize - 1; i++) {
					out.print(skill_set[i] + ", ");
				}
				out.println(skill_set[i]);
				for (i = 0; i < taskSize; i++) {
					out.println("Agent " + i + ": " + team[i]);
				}
				out.println("start time: " + start_time);
				out.println("end time: " + end_time);
				out.flush();
			} catch (Exception ioe) {
				System.out.println("i/o exception");
				ioe.printStackTrace();
			}

		}

	}

	// !!!!!!!!!!!!!!!!!!!!!!!!!!end of class Task
	// declaration!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

}
