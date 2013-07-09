package EmergingTeams;

import java.util.ArrayList;

import EmergingTeams.tasks.Task;

import repast.simphony.parameter.Parameter;

public class TeamWatcher {

	public ArrayList<Task> tasks[]; // Each element contains an ArrayList of
	// Messages for receiver i

	public int tasksCreated = 0;
	public int tasksClosed = 0;
	public int tasksResolved = 0;

	@Parameter(displayName = "Tasks Queue", usageName = "tasks")
	public synchronized ArrayList<Task>[] getTasks() {
		return tasks;
	}

	public synchronized void setTasks(ArrayList<Task>[] newValue) {
		tasks = newValue;
	}

	@Parameter(displayName = "tasksCreated Counter", usageName = "tasksCreated")
	public synchronized int gettasksCreated() {
		return tasksCreated;
	}

	public synchronized void settasksCreated(int newValue) {
		tasksCreated = newValue;
	}

	

}
