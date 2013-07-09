package NetworkGenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkGenerator;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class ScaleFreeNetworkGenerator implements NetworkGenerator<NodeAgent> {

	private Context<NodeAgent> agentContext;

	public ScaleFreeNetworkGenerator(Context<NodeAgent> agentContext) {
		this.agentContext = agentContext;
	}

	public Network<NodeAgent> createNetwork(Network<NodeAgent> network) {

		ArrayList<NodeAgent> nosNaRede = new ArrayList<NodeAgent>();
		Iterator<NodeAgent> nodesIterator = agentContext.iterator();

		Random randGenerator = new Random();

		while (nodesIterator.hasNext()) {

			NodeAgent nodeAgent = nodesIterator.next();

			// Special case when the network is empty
			if (network.getDegree() == 0) {
				NodeAgent secondAgent = nodesIterator.next();
				network.addEdge(nodeAgent, secondAgent, 1);
				nosNaRede.add(nodeAgent);
				nosNaRede.add(secondAgent);
				System.out.println("Agent have degree 0");
				continue;
			} else {
				System.out.println("Agent have degree <> 0");
			}

			// Number of edges to create
			// between 0% and 1% of the number of nodes are linked.
			// Creating at least one link
			int nEdgesCriar = (int) (Math.random() * nosNaRede.size() + 1 * 0.01);

			nEdgesCriar = nEdgesCriar == 0 ? 1 : nEdgesCriar;

			int edgesCriados = 0;

			int numeroTotalEdges = network.getDegree();
			boolean added = false;
			// Enquanto nao criar a
			while (nEdgesCriar > edgesCriados) {

				// Get random number
				NodeAgent nodeAleatorio = nosNaRede.get(randGenerator
						.nextInt(nosNaRede.size()));

				// Probability of linking
				double prob = 1.0 * network.getDegree(nodeAleatorio)
						/ numeroTotalEdges;
				double r = randGenerator.nextDouble();

				if (r < prob) {
					network.addEdge(nodeAleatorio, nodeAgent);
					edgesCriados++;

					if (!added) {
						added = true;
						nosNaRede.add(nodeAgent);
						System.out.println("Novo No " + nosNaRede.size());
					}
				}
			}

		}

		return network;
	}

}
