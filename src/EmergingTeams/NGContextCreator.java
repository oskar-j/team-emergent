package EmergingTeams;

import NetworkGenerator.ScaleFreeNetworkContext;
import NetworkGenerator.ScaleFreeNetworkGenerator;
import pool.NodeIds;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkFactoryFinder;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.valueLayer.GridValueLayer;

import static repast.simphony.essentials.RepastEssentials.*;

/**
 * Context creator for Team Emergence model 
 * Here the space for agents is
 * created.
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
public class NGContextCreator implements ContextBuilder {

	@SuppressWarnings("unused")
	private void say(String s) {
		System.out.println(s);
	}

	public NGContextCreator() {
		say("NGContextCreator object created");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Context build(Context context) {

		ScaleFreeNetworkContext scaleFreeNetworkContext = 
				new ScaleFreeNetworkContext();
		
		ScaleFreeNetworkGenerator sng = new ScaleFreeNetworkGenerator(snc);

		NetworkFactoryFinder.createNetworkFactory(null).createNetwork(
				"SensorNetwork", context, false);

		// The environment parameters contain the user-editable values that
		// appear in the GUI.
		// Get the parameters p and then specifically the initial numbers of
		// wolves and sheep.
		Parameters p = RunEnvironment.getInstance().getParameters();
		int numNodes = (Integer) p.getValue("numNodes");
		int numTeams = (Integer) p.getValue("numTeams");
		int percStartMembership = (Integer) p.getValue("percStartMembership");
		boolean allowMultiMembership = (Boolean) p
				.getValue("allowMultiMembership");
		int numSteps = (Integer) p.getValue("numSteps");

		for (int i = 1; i <= numTeams; i++) {
			Team team = new Team();
			context.add(team);
			team.initialize();
			say("Creating team, team.id = " + team.id + ", team.name = "
					+ team.name);
		}

		// Populate the root context with the initial agents
		/*
		 * GlobalMessenger theGlobalMessenger = new GlobalMessenger();
		 * context.add(theGlobalMessenger); theGlobalMessenger.initialize();
		 * say("init Global Messenger, adentID = " + theGlobalMessenger.agentID
		 * + ", agentIDcounter = " + GlobalMessenger.agentIDCounter);
		 * 
		 * for (int i = 1; i <= numNodes; i++) { Node node = new Node();
		 * context.add(node); node.initialize(theGlobalMessenger);
		 * say("Creating node, adentID = " + node.getId() +
		 * ", agentIDcounter = " + NodeIds.id); }
		 * 
		 * for (int i = 1; i <= numTeams; i++) { Team team = new Team();
		 * context.add(team); say("Creating team, team.id = " + team.id +
		 * ", team.name = " + team.name); }
		 * 
		 * return context;
		 */
		return context;
	}
}