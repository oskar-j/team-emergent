package NetworkGenerator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.graph.Network;
import repast.simphony.space.projection.Projection;
import EmergingTeams.Team;

public class ScaleFreeNetworkContext extends DefaultContext {

	private Network<NodeAgent> network;

	int agentCount = 0;
	int teamCount = 0;
	double pctFilled = 0.0;
	int groups = 0;
	private static double dampingFactor = 0.85;
	int numSteps;
	private static int agentsToAdd = 4;
	boolean allowMultiMembership;
	int teamBrk = 0;
	int agntBrk = 0;

	Vector totCommunities = new Vector();
	Hashtable allAgents = new Hashtable();
	
	private void say(String s) {
		System.out.println(s);
	}

	public ScaleFreeNetworkContext() {
		// Call constructor of paret class
		super("ScaleFreeNetworkContext");
		Parameters param = RunEnvironment.getInstance().getParameters();

		agentCount = (Integer) param.getValue("numNodes");
		teamCount = (Integer) param.getValue("numTeams");
		pctFilled = (Integer) param.getValue("percStartMembership");
		allowMultiMembership = (Boolean) param.getValue("allowMultiMembership");
		numSteps = (Integer) param.getValue("numSteps");
		groups = (Integer) param.getValue("cultGroups");

		NetworkBuilder<Object> netBuilderSN = new NetworkBuilder<Object>(
				"linkedin", (Context<Object>) this, true);
		netBuilderSN.buildNetwork();

		if (groups > 1) {
			agntBrk = agentCount / groups;
			teamBrk = teamCount / groups;
			say(teamBrk+" "+agntBrk);
		}
		
		for (int grp=0; grp<groups; grp++) {
			ArrayList community = new ArrayList();
			totCommunities.add(community);
		}
		
		Network artifct = (Network) this.getProjection("linkedin");

		addAgents(500, true);
		buildNetwork();

		String algo = (String) param.getValue("algo");

		System.out.println("Number of agents created "
				+ this.getObjects(NodeAgent.class).size());
		System.out.println("Number of teams created "
				+ this.getObjects(Team.class).size());
		System.out.println("Algorithm tested: " + algo);
	}

	private void addAgents(int numeroAgents, boolean concentrate) {
		for (int i = 0; i < numeroAgents; i++) {
			this.add(new NodeAgent("Agent-" + i));
		}
	}

	public void buildNetwork() {
		NetworkBuilder<NodeAgent> builder = new NetworkBuilder<NodeAgent>(
				"NetworkTest", this, false);
		builder.setGenerator(new ScaleFreeNetworkGenerator(this));
		Network<NodeAgent> network = builder.buildNetwork();
		// this.addProjection(network);
		this.network = network;
	}

	public Projection<NodeAgent> getProjection(String name) {
		return network;
	}
}