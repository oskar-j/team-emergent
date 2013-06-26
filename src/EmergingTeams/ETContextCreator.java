package EmergingTeams;

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
 * Here the space for agents is created.
 * 
 * @author Oskar Jarczyk
 * 
 */
@SuppressWarnings("rawtypes")
public class ETContextCreator implements ContextBuilder {
	
	@SuppressWarnings("unused")
	private void say(String s) {
		System.out.println(s);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Context build(Context context) {
		int xdim = 50; // The x dimension of the physical space
		int ydim = 50; // The y dimension of the physical space

		// Create a new 2D continuous space to model the physical space
		ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null)
				.createContinuousSpace("ContinuousSpace2D", context,
						new RandomCartesianAdder(),
						new repast.simphony.space.continuous.StickyBorders(),
						xdim, ydim);

		NetworkFactoryFinder.createNetworkFactory(null).createNetwork(
				"DevelopersNetwork", context, false);

		// The environment parameters contain the user-editable values that
		// appear in the GUI.
		// Get the parameters p and then specifically the initial numbers of
		// wolves and sheep.
		Parameters p = RunEnvironment.getInstance().getParameters();
		int numNodes = (Integer) p.getValue("numNodes");
		int numTeams = (Integer) p.getValue("numTeams");
		int percStartMembership = (Integer) p.getValue("percStartMembership");
		boolean allowMultiMembership = (Boolean) p.getValue("allowMultiMembership");
		int numSteps = (Integer) p.getValue("numSteps");

		// Populate the root context with the initial agents
		GlobalMessenger theGlobalMessenger = new GlobalMessenger();
		context.add(theGlobalMessenger);
		theGlobalMessenger.initialize();
		say("init Global Messenger, adentID = "
				+ theGlobalMessenger.agentID + ", agentIDcounter = "
				+ GlobalMessenger.agentIDCounter);

		for (int i = 1; i <= numNodes; i++) {
			Node node = new Node();
			context.add(node);
			node.initialize(theGlobalMessenger);
			say("Creating node, adentID = " + node.agentID
					+ ", agentIDcounter = " + Node.agentIDCounter);
		}
		
		for (int i = 1; i <= numTeams; i++) {
			Team team = new Team();
			context.add(team);
			say("Creating team, team.id = " + team.id
					+ ", team.name = " + team.name);
		}

		return context;
	}
}