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

import java.util.Iterator;

public class TeamFormationModel extends SimModelImpl {

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
	private PrintWriter out;
	private OpenSequenceGraph graph;
	private OpenSequenceGraph scoreGraph;
	private Legend legend;
	private TextDisplay scoreBox;

	// stats!
	private int completed_tasks;
	private int expired_tasks;
	private int total_tasks;

	private String packageName = "edu.umbc.cs.maple.autumn.basicTeamForm";
	private String userDirectory = System.getProperty("user.dir");
	private String filePath = userDirectory
			+ "/src/edu/umbc/cs/maple/autumn/basicTeamForm/";
	private String agentTypesFile = "agentTypes.txt";
	private String performanceFile = "teamStats.txt";
	private String seperator = ",";
	private ArrayList<String> agentTypes;
	// default agent type is the agent type used if the number of nodes in the
	// graph does
	// not divide evenly with the number of teams. When this happens, the extra
	// nodes create
	// a new team of the defualt agent type.
	private String defaultAgentType = "DefaultAgent";
	private ArrayList<Color> clanColors;

	// competition variables
	private ArrayList<TeamFormingNodeClan> agentClans;
	private int cutSize = 5;

	// implementation variables
	private String layoutType = "CircleLayout";
	private DisplaySurface surface;
	private Schedule schedule;
	private AbstractGraphLayout graphLayout;
	private BasicAction initialAction;

	private ArrayList<Task> inactiveTasks;
	private ArrayList<Task> activeTasks;

	private java.util.Random randSkill;

	private int textBoxUpdateCount = 1;
	private int graphUpdateCount = 250;

	public int getRows() {
		return rows;
	}

	public void setRows(int newRows) {
		rows = newRows;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int newColumns) {
		columns = newColumns;
	}

	public int getTextBoxUpdateCount() {
		return textBoxUpdateCount;
	}

	public void setTextBoxUpdateCount(int i) {
		textBoxUpdateCount = i;
	}

	public int getGraphUpdateCount() {
		return graphUpdateCount;
	}

	public void setGraphUpdateCount(int i) {
		graphUpdateCount = i;
	}

	public int getNumCompletedTasks() {
		return completed_tasks;
	}

	public int getNumTotalTasks() {
		return total_tasks;
	}

	public int getNumExpiredTasks() {
		return expired_tasks;
	}

	public String getLayoutType() {
		return layoutType;
	}

	public void setLayoutType(String type) {
		layoutType = type;
	}

	public void setUpdateEveryN(int updates) {
		updateEveryN = updates;
	}

	public int getUpdateEveryN() {
		return updateEveryN;
	}

	public int getWorldXSize() {
		return worldXSize;
	}

	public void setWorldXSize(int size) {
		worldXSize = size;
	}

	public int getWorldYSize() {
		return worldYSize;
	}

	public void setWorldYSize(int size) {
		worldYSize = size;
	}

	public void setNumSkills(int num) {
		numSkills = num;
	}

	public int getNumSkills() {
		return numSkills;
	}

	public void setTaskSize(int t) {
		taskSize = t;
	}

	public int getTaskSize() {
		return taskSize;
	}

	public void setIntroInterval(int i) {
		introInterval = i;
	}

	public int getIntroInterval() {
		return introInterval;
	}

	public void setAdvertInterval(double a) {
		advertInterval = a;
	}

	public double getAdvertInterval() {
		return advertInterval;
	}

	public void setDurationInterval(double d) {
		durationInterval = d;
	}

	public double getDurationInterval() {
		return durationInterval;
	}

	public int getCutSize() {
		return cutSize;
	}

	public void setCutSize(int cutSize) {
		this.cutSize = cutSize;
	}

	public int getSim_end() {
		return sim_end;
	}

	public void setSim_end(int sim_end) {
		this.sim_end = sim_end;
	}

	public String[] getInitParam() {
		String[] params = { "rows", "columns", "updateEveryN", "LayoutType",
				"numSkills", "taskSize", "introInterval", "advertInterval",
				"durationInterval", "sim_end", "cutSize", "graphUpdateCount" };
		return params;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public String getName() {
		return "Team FormationModel";
	}

	public int getNumNodes() {
		return rows * columns;
	}

	/***
	 * Gets the agent class names for the different clans, and sets up their
	 * colors from the specified configuration file
	 * 
	 * Configuration file should be formatted as follows: AgentClassName1
	 * r_value_for_color g_value_for_color b_value_for_color AgentClassName2
	 * r_value_for_color g_value_for_color b_value_for_color ...etc
	 * 
	 * For Example: Agent1 255 255 255 Agent2 192 150 0 Agent3 0 0 255
	 * 
	 * Use spaces between the different values. It is important that there is
	 * only one agent per line and that the format is followed correctly.
	 * 
	 * @param filename
	 *            the filename of the agent types parameter file
	 * @param filePath
	 *            the path to that file
	 */
	public void getAgentTypes(String filename) {
		getAgentTypes(filename, filePath);
	}

	/***
	 * Gets the agent class names for the different clans, and sets up their
	 * colors from the specified configuration file
	 * 
	 * Configuration file should be formatted as follows: AgentClassName1
	 * r_value_for_color g_value_for_color b_value_for_color AgentClassName2
	 * r_value_for_color g_value_for_color b_value_for_color ...etc
	 * 
	 * For Example: Agent1 255 255 255 Agent2 192 150 0 Agent3 0 0 255
	 * 
	 * Use spaces between the different values. It is important that there is
	 * only one agent per line and that the format is followed correctly.
	 * 
	 * @param filename
	 *            the filename of the agent types parameter file
	 * @param filePath
	 *            the path to that file
	 */
	public void getAgentTypes(String filename, String filePath) {
		BufferedReader input = null;
		agentTypes = new ArrayList<String>();
		clanColors = new ArrayList<Color>();

		try {
			input = new BufferedReader(new FileReader(filePath + filename));

			String line = null;

			while ((line = input.readLine()) != null) {
				String[] tokens = line.split(" ");
				agentTypes.add(tokens[0]);
				if (tokens.length == 4) {
					int r = Integer.valueOf(tokens[1]);
					int g = Integer.valueOf(tokens[2]);
					int b = Integer.valueOf(tokens[3]);
					clanColors.add(new Color(r, g, b));
				} else {
					clanColors.add(Color.BLACK);
				}
			}
		} catch (FileNotFoundException ex) {
			SimUtilities.showError("Agent Types File Not Found", ex);
		} catch (IOException ex) {
			SimUtilities.showError("IO Exception", ex);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException ex) {
				SimUtilities.showError("IO Exception", ex);
			}
		}
	}

	/***
	 * Writes the performance of this run to a file. If the file given already
	 * exists, and contains the exact same teams in the same order as this run,
	 * then it will add this runs performance onto the previous runs
	 * performance. Otherwise it will overwrite the file with the new
	 * information. Uses the default file path.
	 * 
	 * The values in the file are seperated by whatever the seperator class
	 * variable is assigned to
	 * 
	 * @param filename
	 *            the filename of the performance file
	 */
	private void WritePerformanceToFile(String filename) {
		WritePerformanceToFile(filename, filePath);
	}

	/***
	 * Writes the performance of this run to a file. If the file given already
	 * exists, and contains the exact same teams in the same order as this run,
	 * then it will add this runs performance onto the previous runs
	 * performance. Otherwise it will overwrite the file with the new
	 * information.
	 * 
	 * The values in the file are seperated by whatever the seperator class
	 * variable is assigned to
	 * 
	 * @param filename
	 *            the filename for the performance file
	 * @param filePath
	 *            the filepath to the performance file
	 */
	private void WritePerformanceToFile(String filename, String filePath) {
		BufferedReader input = null;
		BufferedWriter out = null;
		ArrayList<String> lineTokens = new ArrayList<String>();
		ArrayList<ArrayList<String>> info = new ArrayList<ArrayList<String>>();
		ArrayList<Double> totalScores = new ArrayList<Double>();
		ArrayList<Double> squaredTotalScores = new ArrayList<Double>();
		ArrayList<Integer> degree = new ArrayList<Integer>();
		boolean previousFile = true; // ture if there is a previous file
										// containing the
										// same teams as this run
		int previousRuns = 0; // the number of runs done before this one

		// read previous performance file if one exists: to add on the info of
		// this run
		try {
			input = new BufferedReader(new FileReader(filePath + filename));

			String line = null;

			while ((line = input.readLine()) != null) {
				// file should be in csv format, so we need to break the lines
				// up
				// by the commas
				lineTokens = seperateLine(line.trim(), seperator);
				info.add(lineTokens);
			}
		} catch (FileNotFoundException ex) {
			previousFile = false;
		} catch (IOException ex) {
			SimUtilities.showError("IO Exception", ex);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException ex) {
				SimUtilities.showError("IO Exception", ex);
			}
		}

		// Make sure the previous file had the same agents as this run
		if (previousFile) {
			if (info.size() > 0) {
				if (info.get(0).size() == agentClans.size() + 2) {
					for (int i = 2; i < info.get(0).size(); i++) {
						if (info.get(0).get(i)
								.compareTo(agentClans.get(i - 2).getClanName()) != 0) {
							previousFile = false;
							break;
						}
					}
				} else {
					previousFile = false;
				}
			}
		}

		// figure out how many runs have already been done
		if (previousFile) {
			for (int i = 1; i < info.size(); i++) {
				if (info.get(i).get(0).startsWith("Run"))
					previousRuns++;
			}
		}

		// Write the information (back) to the file
		try {
			out = new BufferedWriter(new FileWriter(filePath + filename));

			// write the first row of the csv file
			// first column is blank
			// second column always says clock ticks
			// third to the remaining columns are the names of the agent teams
			out.write(seperator + "Clock Ticks");
			for (int i = 0; i < agentClans.size(); i++) {
				out.write(seperator + agentClans.get(i).getClanName());
			}
			out.write("\n");

			// rewrite the previous runs, if some exist
			for (int j = 1; j < previousRuns + 1; j++) {
				out.write("Run " + j);
				out.write("");
				for (int k = 1; k < info.get(j).size(); k++) {
					out.write(seperator + info.get(j).get(k));
				}
				out.write("\n");
			}

			// write the new run
			out.write("Run " + (previousRuns + 1));
			out.write(seperator + getSchedule().getCurrentTime());
			for (int k = 0; k < agentClans.size(); k++) {
				out.write(seperator + agentClans.get(k).getClanScore());
			}
			out.write("\n");
			out.write("\n");

			// Lastly, some summary statistics on the data

			// Calculate and output the total scores of the teams from all runs
			out.write("Totals" + seperator);
			totalScores = calculateTotalScores(info, previousRuns);
			for (int i = 0; i < agentClans.size(); i++) {
				out.write(seperator + totalScores.get(i));
			}
			out.write("\n");

			// Calculate and output the average score of the teams from all runs
			out.write("Average" + seperator);
			for (int i = 0; i < agentClans.size(); i++) {
				out.write(seperator + totalScores.get(i) / (previousRuns + 1));
			}
			out.write("\n");

			// Calculate and output the standard deviation of the scores of teh
			// teams from all runs
			out.write("Std. Dev." + seperator);
			squaredTotalScores = calculateSquaredTotalScores(info, previousRuns);
			double expectedSquaredValue = 0;
			double expectedValue = 0;
			for (int i = 0; i < agentClans.size(); i++) {
				expectedValue = totalScores.get(i) / (previousRuns + 1);
				expectedSquaredValue = squaredTotalScores.get(i)
						/ (previousRuns + 1);
				out.write(seperator
						+ java.lang.Math.sqrt(expectedSquaredValue
								- expectedValue * expectedValue));
			}
			out.write("\n");

			// Calculate and output the relative ranking of the team. 1 being
			// the team with
			// the best total score, and with teams having equivalent rankings
			// sharing the
			// same rank, so forinstance if their were 4 teams with scores 11,
			// 12, 12, and 14
			// then the rankings would be 4, 2, 2, 1 respectively.
			out.write("Degree" + seperator);
			for (int i = 0; i < totalScores.size(); i++) {
				degree.add(new Integer(1));
				for (int j = 0; j < totalScores.size(); j++) {
					if (i != j) {
						if (totalScores.get(i) < totalScores.get(j)) {
							degree.set(i, degree.get(i) + 1);
						}
					}
				}
			}
			for (int i = 0; i < degree.size(); i++) {
				out.write(seperator + (degree.get(i)));
			}
			out.write("\n");

		} catch (IOException ex) {
			SimUtilities.showError("IO Exception", ex);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				SimUtilities.showError("IO Exception", ex);
			}
		}
	}

	/***
	 * This function takes a string and turns it into an array list of strings
	 * by splitting the string into seperate strings whenever the seperator
	 * symbol passed in is seen.
	 * 
	 * For example the string "Hello,My,Name,Is,X" would be returned as an array
	 * list containing the strings "Hello","My","Name","Is" and "X" if the
	 * seperator symbol was ","
	 * 
	 * Example 2: if the seperator symbol is "and" then the string
	 * "AreandYouandBad" would be returned as an array list containing the
	 * strings "Are","You", and "Bad"
	 * 
	 * @param line
	 *            the string to seperate
	 * @param seperatorSymbol
	 *            the symbol to seperator on
	 * @return An ArrayList containing the strings seperated into different
	 *         values.
	 */
	private ArrayList<String> seperateLine(String line, String seperatorSymbol) {
		ArrayList<String> tokens = new ArrayList<String>();
		int prevIndex = 0;
		int nextIndex = line.indexOf(seperatorSymbol, prevIndex);
		while (nextIndex != -1) {
			tokens.add(line.substring(prevIndex, nextIndex));
			prevIndex = nextIndex + seperatorSymbol.length();
			nextIndex = line.indexOf(seperatorSymbol, prevIndex);
		}
		tokens.add(line.substring(prevIndex, line.length()));

		return tokens;
	}

	/***
	 * A Utility method for write performance to file. Calculates all of the
	 * teams total scores from all runs and returns it as an ArrayList. This
	 * function is highly coupled with WritePerformanceToFile, as it assumes the
	 * structure of the info variable passed in is correct It also assumes that
	 * the number of previous runs is correct.If these two assumptions are
	 * violated, strange behavior may result.
	 * 
	 * @param info
	 *            the matrix of tokens generated by reading the previous
	 *            performance file
	 * @param previousRuns
	 *            the number of runs in the previous performance file
	 * @return an arraylist containing the total scores from all runs of each
	 *         team
	 */
	private ArrayList<Double> calculateTotalScores(
			ArrayList<ArrayList<String>> info, int previousRuns) {
		ArrayList<Double> totalScores = new ArrayList<Double>();

		for (int i = 0; i < agentClans.size(); i++) {
			// Calculate and add to the array list each individual team's score
			totalScores.add(calculateTotalScores(info, previousRuns, i + 2));
		}

		return totalScores;
	}

	/***
	 * A helper function for calculateTotalScores. This function calculates an
	 * individual teams score.
	 * 
	 * @param info
	 *            the matrix of tokens generated by reading the previous
	 *            performance file
	 * @param previousRuns
	 *            the number of runs in the previous performance file
	 * @param index
	 *            the index of this team
	 * @return this team's total score over all runs
	 */
	private double calculateTotalScores(ArrayList<ArrayList<String>> info,
			int previousRuns, int index) {
		double totalScore = 0;

		if (info.size() > previousRuns) {
			for (int i = 0; i < previousRuns; i++) {
				if (info.get(i + 1).size() > index) {
					try {
						totalScore += Double
								.valueOf(info.get(i + 1).get(index));
					} catch (NumberFormatException nfe) {
						SimUtilities.showError("NumberFormatException: ", nfe);
					}
				} else {
					SimUtilities.showMessage("Error: No score at index "
							+ index + " for run " + (i + 1));
				}
			}
		} else {
			SimUtilities.showMessage("Error: Previous Runs insufficient");
		}

		totalScore += agentClans.get(index - 2).getClanScore();

		return totalScore;
	}

	/***
	 * A Utility method for write performance to file. Calculates all of the
	 * teams total squared scores from all runs and returns it as an ArrayList.
	 * This function is highly coupled with WritePerformanceToFile, as it
	 * assumes the structure of the info variable passed in is correct It also
	 * assumes that the number of previous runs is correct.If these two
	 * assumptions are violated, strange behavior may result.
	 * 
	 * @param info
	 *            the matrix of tokens generated by reading the previous
	 *            performance file
	 * @param previousRuns
	 *            the number of runs in the previous performance file
	 * @return an arraylist containing the total squared scores from all runs of
	 *         each team
	 */
	private ArrayList<Double> calculateSquaredTotalScores(
			ArrayList<ArrayList<String>> info, int previousRuns) {
		ArrayList<Double> totalSquaredScores = new ArrayList<Double>();

		for (int i = 0; i < agentClans.size(); i++) {
			totalSquaredScores.add(calculateSquaredTotalScores(info,
					previousRuns, i + 2));
		}

		return totalSquaredScores;
	}

	/***
	 * A helper function for calculateSquaredTotalScores. This function
	 * calculates an individual teams's squared score.
	 * 
	 * @param info
	 *            the matrix of tokens generated by reading the previous
	 *            performance file
	 * @param previousRuns
	 *            the number of runs in the previous performance file
	 * @param index
	 *            the index of this team
	 * @return this team's squared total score over all runs
	 */
	private double calculateSquaredTotalScores(
			ArrayList<ArrayList<String>> info, int previousRuns, int index) {
		double totalSquaredScore = 0;

		if (info.size() > previousRuns) {
			for (int i = 0; i < previousRuns; i++) {
				if (info.get(i + 1).size() > index) {
					try {
						totalSquaredScore += (Double.valueOf(info.get(i + 1)
								.get(index)))
								* (Double.valueOf(info.get(i + 1).get(index)));
					} catch (NumberFormatException nfe) {
						SimUtilities.showError("NumberFormatException: ", nfe);
					}
				} else {
					SimUtilities.showMessage("Error: No score at index "
							+ index + " for run " + (i + 1));
				}
			}
		} else {
			SimUtilities.showMessage("Error: Previous Runs insufficient");
		}

		totalSquaredScore += agentClans.get(index - 2).getClanScore()
				* agentClans.get(index - 2).getClanScore();

		return totalSquaredScore;
	}

	// this is included because we must read the tasks in a random order for
	// each agent
	public void shuffleTasks() {
		SimUtilities.shuffle(inactiveTasks);
	}

	// here is access to an iterator over the inactive tasks
	Iterator<Task> getTaskIterator() {
		return inactiveTasks.iterator();
	}

	ArrayList<TeamFormationModel.Task> getInactiveTasks() {
		return inactiveTasks;
	}

	public TeamFormationModel() {
		Vector<String> vect = new Vector<String>();
		vect.add("Fruch");
		vect.add("KK");
		vect.add("CircleLayout");
		vect.add("GridLayout");
		getAgentTypes(agentTypesFile);
		ListPropertyDescriptor pd = new ListPropertyDescriptor("LayoutType",
				vect);
		descriptors.put("LayoutType", pd);
		randSkill = new java.util.Random(System.currentTimeMillis());
		agentClans = new ArrayList<TeamFormingNodeClan>();
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(
					"C:\\teamFormContest\\taskData"
							+ System.currentTimeMillis() + ".txt")));

		} catch (Exception ioe) {
			System.out.println("trouble opening the datafile");
			ioe.printStackTrace();
		}
	}

	// builds the model
	public void buildModel() {

		try {

			agentList = new ArrayList<TeamFormingNode>();
			agentClans = new ArrayList<TeamFormingNodeClan>();
			stopGenerating = false;
			int numAgentsPerTeam = getNumNodes() / agentTypes.size();
			int numExtraNodes = getNumNodes() % agentTypes.size();
			if (numAgentsPerTeam == 0) {
				SimUtilities
						.showMessage("The world must be large enough for at least one agent per team.");
				System.exit(-5);
			}

			ArrayList<TeamFormingNode> thisTeam = new ArrayList<TeamFormingNode>();
			TeamFormingNode tempAgent;

			try {
				for (int i = 0; i < agentTypes.size(); i++) {
					agentClans.add(new TeamFormingNodeClan());
					thisTeam = agentClans.get(i)
							.initializeTeam(
									this,
									numAgentsPerTeam,
									numSkills,
									Class.forName(packageName + "."
											+ agentTypes.get(i)),
									clanColors.get(i));
					for (int j = 0; j < thisTeam.size(); j++)
						agentList.add(thisTeam.get(j));
				}

				if (numExtraNodes > 0) {
					SimUtilities
							.showMessage("Filling extra nodes with a team of the default agent type.");
					agentClans.add(new TeamFormingNodeClan());
					thisTeam = agentClans.get(agentClans.size() - 1)
							.initializeTeam(
									this,
									numExtraNodes,
									numSkills,
									Class.forName(packageName + "."
											+ defaultAgentType), Color.GRAY);
					for (int j = 0; j < thisTeam.size(); j++)
						agentList.add(thisTeam.get(j));
				}

			} catch (IllegalAccessException iae) {
				SimUtilities.showError("Error Forming Coalition: ", iae);
				System.exit(-1);
			} catch (InstantiationException ie) {
				SimUtilities.showError("Error Forming Coalition: ", ie);
				System.exit(-2);
			} catch (IndexOutOfBoundsException iobe) {
				SimUtilities.showError("Error Forming Coalition: ", iobe);
				System.exit(-3);
			} catch (ClassNotFoundException e) {
				SimUtilities.showError("Agent Type not found: ", e);
				System.exit(-4);
			}

			agentList = new ArrayList<TeamFormingNode>(
					NetworkFactory.createWattsStrogatzNetwork(
							getNumNodes(),
							2,
							0.05,
							Class.forName("edu.umbc.cs.maple.autumn.basicTeamForm.TeamFormingNode"),
							Class.forName("edu.umbc.cs.maple.autumn.basicTeamForm.SimpleEdge"),
							agentList));

			int spacing = worldXSize / (rows > columns ? rows : columns);
			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < columns; col++) {
					TeamFormingNode tn = (TeamFormingNode) agentList.get(row
							* columns + col);
					tn.setPosition((col) * spacing + 5, (row) * spacing + 5);
				}
			}

			inactiveTasks = new ArrayList<Task>();
			activeTasks = new ArrayList<Task>();

			total_tasks = 0;
			completed_tasks = 0;
			expired_tasks = 0;
		} catch (java.lang.ClassNotFoundException cne) {
			SimUtilities.showError(
					"did not find one of the provided node or edge classes",
					cne);
			System.exit(-4);
		}
	}

	// building the schedule means that we assign methods and functions to be
	// run at certain
	// time steps during the simulation. This code is pretty easy to understand
	// if you take
	// two minutes to read the documentation on how these functions work
	// for the team formation competition, you shouldn't have to modify this.
	private void buildSchedule() {
		initialAction = schedule.scheduleActionAt(initialSteps, this,
				"initialAction");
		schedule.scheduleActionAt(initialSteps, this, "removeInitialAction",
				Schedule.LAST);
		schedule.scheduleActionBeginning(initialSteps + 1, this, "mainAction");
		schedule.scheduleActionAtInterval(introInterval, this, "introduceTask",
				Schedule.LAST);

		try {
			schedule.scheduleActionBeginningRnd(
					initialSteps + 1,
					agentList,
					Class.forName("edu.umbc.cs.maple.autumn.basicTeamForm.TeamFormingNode"),
					"work");
		} catch (ClassNotFoundException cnf) {
			cnf.printStackTrace();
		}

		schedule.scheduleActionBeginning(sim_end, this, "cleanup");
	}

	/***
	 * Performs clean up on the simulation. Stops the simulation when all active
	 * and inactive tasks are complete, and then prints the statistics of this
	 * run, finds the best and worst agents in this run and writes the
	 * performance of this run to the performance file.
	 */
	public void cleanup() {
		stopGenerating = true;
		if (!(inactiveTasks.isEmpty() && activeTasks.isEmpty()))
			return;
		getController().stopSim();
		printStats();
		findBestAndWorstAgents();
		WritePerformanceToFile(performanceFile);
	}

	/***
	 * Performs the initial action to setup the various displays and start the
	 * simulation.
	 * 
	 */
	public void initialAction() {
		graphLayout.updateLayout();
		surface.updateDisplay();
		introduceTask();

		scoreBox.clearLines();
		for (int i = 0; i < agentClans.size(); i++) {
			String tempString = agentClans.get(i).getClanName() + ": "
					+ agentClans.get(i).getClanScore() + "\n";
			scoreBox.addLine(tempString);
		}
	}

	/***
	 * Performs the necessary updates and actions for each step of the
	 * simulation. -Updates the displays, if necessary -Activates tasks where
	 * the team has filled up -Removes tasks that have expired -Removes tasks
	 * that have been completed -Changes the edges randomly
	 */
	public void mainAction() {
		graphLayout.updateLayout();
		surface.updateDisplay();
		scoreBox.clearLines();
		for (int i = 0; i < agentClans.size(); i++) {
			String tempString = agentClans.get(i).getClanName() + ": "
					+ agentClans.get(i).getClanScore() + "\n";
			scoreBox.addLine(tempString);
		}
		if (getTickCount() % graphUpdateCount == 0) {
			graph.step();
			scoreGraph.step();
		}
		activateTasks();
		removeExpiredTasks();
		removeCompletedTasks();
		moveRandomEdge();
	}

	public void removeInitialAction() {
		schedule.removeAction(initialAction);
	}

	// this function randomly moves one edge...specific to the team formation
	// contest
	public void moveRandomEdge() {
		boolean edgeRemoved;
		int safeGuard = 0;
		TeamFormingNode iNode;
		do {
			int index = Random.uniform.nextIntFromTo(0, getNumNodes() - 1);
			iNode = (TeamFormingNode) agentList.get(index);
			edgeRemoved = removeNeighbor(iNode, null);
			safeGuard++;
		} while (!edgeRemoved || safeGuard > 150000);

		int i = -1;
		int j = -1;
		TeamFormingNode jNode = null;
		do {
			i = Random.uniform.nextIntFromTo(0, getNumNodes() - 1);
			do {
				j = Random.uniform.nextIntFromTo(0, getNumNodes() - 1);
			} while (i == j);
			iNode = (TeamFormingNode) agentList.get(i);
			jNode = (TeamFormingNode) agentList.get(j);
		} while (iNode.hasEdgeToOrFrom(jNode));
		iNode.makeEdgeToFrom(jNode);
	}

	/**
	 * Removes a link between this RingNode and one chosen at random from those
	 * linked to this RingNode.
	 * 
	 * @return true if an edge was successfully removed, false otherwise
	 */
	public boolean removeNeighbor(TeamFormingNode iNode, TeamFormingNode jNode) {
		if (jNode == null)
			jNode = (TeamFormingNode) iNode.getRandomNodeOut();
		if (iNode.hasEdgeTo(jNode)) {

			// will be null if no outEdges.
			if (jNode != null) {
				Task temp = iNode.getCurrentTask();
				if (temp == null || !temp.onTeam(jNode)) {
					iNode.removeEdgesTo(jNode);
					jNode.removeEdgesFrom(iNode);
					iNode.removeEdgesFrom(jNode);
					jNode.removeEdgesTo(iNode);
					return true;
				}
			}
		}

		return false;
	}

	// this function removes all the tasks that have been completed by their
	// team
	// it is called at each time step
	public void removeCompletedTasks() {
		for (Iterator<Task> i = activeTasks.iterator(); i.hasNext();) {
			Task temp = i.next();
			if (temp.completed()) {
				i.remove();
				temp.freeAgents();
				completed_tasks++;
			}
		}
	}

	// this function removes all the tasks that have expired without a team
	// successfully completing it
	// it is called at each time step
	public void removeExpiredTasks() {
		for (Iterator<Task> i = inactiveTasks.iterator(); i.hasNext();) {
			Task temp = i.next();
			if (temp.expired()) {
				i.remove();
				temp.freeAgents();
				expired_tasks++;
				// System.out.println("expired a task");
			}
		}
	}

	// this function introduces a task...it occurs at a fixed interval because I
	// scheduled it above
	// to occur at a parameterized interval
	public void introduceTask() {
		if (stopGenerating)
			return;
		Task t = new Task();
		int index = -1;
		// TeamFormingNode iNode = null;
		// SimUtilities.shuffle(agentList);
		// for(TeamFormingNode agent : agentList){
		// if(agent.forceCommit(t)){
		inactiveTasks.add(t);
		total_tasks++;
		return;
		// }
		// }

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

	/***
	 * finds the best and worst performing agents, and prints the statistics of
	 * all the competing clans.
	 * 
	 */
	public void findBestAndWorstAgents() {
		TeamFormingNode[] cut = new TeamFormingNode[cutSize];
		TeamFormingNode[] champs = new TeamFormingNode[cutSize];
		Iterator i = agentList.iterator();
		while (i.hasNext()) {
			TeamFormingNode temp = ((TeamFormingNode) i.next());
			for (int j = 0; j < cutSize; j++) {
				if (cut[j] == null || temp.score() < cut[j].score()
						|| cut[j].score() == Double.NaN) {
					cut[j] = temp;
					break;
				}
				// tie-breakersintroduced
				if (cut[j].score() == temp.score()) {
					if (temp.successful_tasks < cut[j].successful_tasks) {
						cut[j] = temp;
						break;
					}
					if (temp.successful_tasks == cut[j].successful_tasks
							&& temp.failed_tasks > cut[j].failed_tasks) {
						cut[j] = temp;
						break;
					}
				}
			}
			for (int j = 0; j < cutSize; j++) {
				if (champs[j] == null || temp.score() > champs[j].score()
						|| champs[j].score() == Double.NaN) {
					champs[j] = temp;
					break;
				}
				// tie-breakers
				if (champs[j].score() == temp.score()) {
					if (temp.successful_tasks > champs[j].successful_tasks) {
						champs[j] = temp;
						break;
					}
					if (temp.successful_tasks == champs[j].successful_tasks
							&& temp.failed_tasks < champs[j].failed_tasks) {
						champs[j] = temp;
						break;
					}
				}

			}
		}

		System.out.println("Best agents for this round, with cut-size of "
				+ cutSize);
		for (int j = 0; j < cutSize; j++) {
			// System.out.println(champs[j]);
			champs[j].printStats();
		}

		System.out.println("Worst agents for this round, with cut-size of "
				+ cutSize);
		for (int j = 0; j < cutSize; j++) {
			// System.out.println(cut[j]);
			cut[j].printStats();
		}

		System.out.println("===========Aggregate Clan Scores===========");
		String clanOwner = "";
		double clanScore = 0;
		for (int k = 0; k < agentClans.size(); k++) {
			clanOwner = agentClans.get(k).getClanName();
			clanScore = agentClans.get(k).getClanScore();

			System.out.println(clanOwner + ": " + clanScore);

		}
	}

	// I schedule this to print when the simulation stops.
	public void printStats() {

		System.out.println("-----------Global Results-----------");
		System.out.println("Total Number of Tasks Introduced" + total_tasks);
		System.out.println("Number of Completed Tasks: " + completed_tasks);
		System.out.println("Number of Expired Tasks: " + expired_tasks);

		System.out.println("Ratio of Completed Tasks: "
				+ (float) completed_tasks / total_tasks);

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

	/*
	 * Below are some functions that are necessary components of the program but
	 * are barely changed from the example code that served as a starting point
	 * for this project
	 */

	public void buildDisplay() {
		if (layoutType.equals("KK")) {
			graphLayout = new KamadaGraphLayout(agentList, worldXSize,
					worldYSize, surface, updateEveryN);
		} else if (layoutType.equals("Fruch")) {
			graphLayout = new FruchGraphLayout(agentList, worldXSize,
					worldYSize, surface, updateEveryN);
		} else if (layoutType.equals("CircleLayout")) {
			graphLayout = new CircularGraphLayout(agentList, worldXSize,
					worldYSize);
		} else if (layoutType.equals("GridLayout")) {
			graphLayout = new DefaultGraphLayout(agentList, worldXSize,
					worldYSize);
		}
		// these four lines hook up the graph layouts to the stop, pause, and
		// exit buttons on the toolbar. When stop, pause, or exit is clicked
		// the graph layouts will interrupt their layout as soon as possible.
		Controller c = (Controller) getController();
		c.addStopListener(graphLayout);
		c.addPauseListener(graphLayout);
		c.addExitListener(graphLayout);

		Network2DDisplay display = new Network2DDisplay(graphLayout);
		surface.addDisplayableProbeable(display, "FindRing Display");

		legend = new Legend();

		scoreBox = new TextDisplay(400, 150, Color.BLACK);
		surface.addDisplayableProbeable(scoreBox, "Scores");

		// add the display as a Zoomable. This means we can "zoom" in on
		// various parts of the network.
		surface.addZoomable(display);
		surface.setBackground(java.awt.Color.white);
		addSimEventListener(surface);

		graph.createSequence("Completed Tasks", this, "getNumCompletedTasks");
		graph.createSequence("Introduced Tasks", this, "getNumTotalTasks");
		graph.createSequence("Expired Tasks", this, "getNumExpiredTasks");

		for (Iterator iter = agentClans.iterator(); iter.hasNext();) {
			TeamFormingNodeClan tm = (TeamFormingNodeClan) iter.next();

			scoreGraph.createSequence(tm.getClanName(), tm, "getClanScore");
		}

	}

	public void begin() {
		buildModel();
		buildDisplay();
		buildSchedule();
		graphLayout.updateLayout();
		surface.display();
		graph.display();
		scoreGraph.display();
		legend.clearLines();
		for (int i = 0; i < agentClans.size(); i++) {
			String tempString = agentClans.get(i).getClanName() + "\n";
			legend.addLine(tempString, agentClans.get(i).getClanColor());
		}
		legend.display();
	}

	public void setup() {
		Random.createUniform();
		if (surface != null)
			surface.dispose();

		surface = null;
		schedule = null;

		System.gc();

		surface = new DisplaySurface(this, "Team Formation Display");
		registerDisplaySurface("Main Display", surface);
		graph = new OpenSequenceGraph("Completed Tasks vs Time", this);
		this.registerMediaProducer("Task Graph", graph);
		scoreGraph = new OpenSequenceGraph("Scores vs Time", this);
		this.registerMediaProducer("Score Graph", scoreGraph);
		schedule = new Schedule();
		agentList = new ArrayList<TeamFormingNode>();
		worldXSize = 500;
		worldYSize = 500;

	}

	public static void main(String[] args) {
		uchicago.src.sim.engine.SimInit init = new uchicago.src.sim.engine.SimInit();
		TeamFormationModel model = new TeamFormationModel();
		init.loadModel(model, "", false);
	}

}
