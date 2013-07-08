package NetworkGenerator;

import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.space.graph.Network;
import repast.simphony.space.projection.Projection;

public class ScaleFreeNetworkContext extends DefaultContext<NodeAgent> {

	private Network<NodeAgent> network;

	// Constructor
	public ScaleFreeNetworkContext() {

		// Call constructor of paret class
		super("ScaleFreeNetworkContext");
		addAgents(500);
		buildNetwork();

	}

	private void addAgents(int numeroAgents) {

		for (int i = 0; i < numeroAgents; i++) {
			this.add(new NodeAgent("Agente-" + i));
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