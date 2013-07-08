package EmergingTeams;

import java.io.*;
import java.math.*;
import java.util.*;
import javax.measure.unit.*;
import org.jscience.mathematics.number.*;
import org.jscience.mathematics.vector.*;
import org.jscience.physics.amount.*;

import pool.NodeIds;
import repast.simphony.adaptation.neural.*;
import repast.simphony.adaptation.regression.*;
import repast.simphony.context.*;
import repast.simphony.context.space.continuous.*;
import repast.simphony.context.space.gis.*;
import repast.simphony.context.space.graph.*;
import repast.simphony.context.space.grid.*;
import repast.simphony.engine.environment.*;
import repast.simphony.engine.schedule.*;
import repast.simphony.engine.watcher.*;
import repast.simphony.groovy.math.*;
import repast.simphony.integration.*;
import repast.simphony.matlab.link.*;
import repast.simphony.query.*;
import repast.simphony.query.space.continuous.*;
import repast.simphony.query.space.gis.*;
import repast.simphony.query.space.graph.*;
import repast.simphony.query.space.grid.*;
import repast.simphony.query.space.projection.*;
import repast.simphony.parameter.*;
import repast.simphony.random.*;
import repast.simphony.space.continuous.*;
import repast.simphony.space.gis.*;
import repast.simphony.space.graph.*;
import repast.simphony.space.grid.*;
import repast.simphony.space.projection.*;
import repast.simphony.ui.probe.*;
import repast.simphony.util.*;
import simphony.util.messages.*;
import static java.lang.Math.*;
import static repast.simphony.essentials.RepastEssentials.*;

/**
 * 
 * This is an agent. A person, a contributor to the COIN Network
 * 
 * @author Oskar Jarczyk
 * 
 */
public class Node extends DefaultAgent {

	public int msgSent = 0;
	public ArrayList<Message> msgReceived;
	public GlobalMessenger globalMessenger;
	public double communicationRange = 60;
	public double energy = 0;
	public ArrayList<Message> msgToSendList;

	private long id;
	private String label;

	protected String agentID = "Node: " + this.id + " " + this.getName();
	
	public Node(){
		this.id = NodeIds.id++;
	}

	/**
	 * The serialization runtime associates with each serializable class a
	 * version number, called a serialVersionUID, which is used during
	 * deserialization to verify that the sender and receiver of a serialized
	 * object have loaded classes for that object that are compatible with
	 * respect to serialization.
	 * 
	 * @field serialVersionUID
	 * 
	 */
	private static final long serialVersionUID = (long) (Long.MAX_VALUE / (42L * (Math
			.random())));

	// 42 is answer to the life, universe and everything..

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Parameter(displayName = "Msg Sent", usageName = "msgSent")
	public int getMsgSent() {
		return msgSent;
	}

	public void setMsgSent(int newValue) {
		msgSent = newValue;
	}

	@Parameter(displayName = "Msg Received list", usageName = "msgReceived")
	public ArrayList<Message> getMsgReceived() {
		return msgReceived;
	}

	public void setMsgReceived(ArrayList<Message> newValue) {
		msgReceived = newValue;
	}

	@Parameter(displayName = "Global Messenger", usageName = "globalMessenger")
	public GlobalMessenger getGlobalMessenger() {
		return globalMessenger;
	}

	public void setGlobalMessenger(GlobalMessenger newValue) {
		globalMessenger = newValue;
	}

	@Parameter(displayName = "Comm Range", usageName = "communicationRange")
	public double getCommunicationRange() {
		return communicationRange;
	}

	public void setCommunicationRange(double newValue) {
		communicationRange = newValue;
	}

	@Parameter(displayName = "Energy", usageName = "energy")
	public double getEnergy() {
		return energy;
	}

	public void setEnergy(double newValue) {
		energy = newValue;
	}

	@Parameter(displayName = "Msg To Send List", usageName = "msgToSendList")
	public ArrayList<Message> getMsgToSendList() {
		return msgToSendList;
	}

	public void setMsgToSendList(ArrayList<Message> newValue) {
		msgToSendList = newValue;
	}

	/**
	 * This is the step behavior.
	 * 
	 * @method initialize
	 */
	public void initialize(GlobalMessenger aGlobalMessenger) {
		
		say("initialize(GlobalMessenger aGlobalMessenger) " + 
		"method was lunched on " + this.toString());

		setMsgReceived(new ArrayList<Message>());
		setMsgSent(0);
		setGlobalMessenger(aGlobalMessenger);
		setEnergy(RandomDraw(1, 100));
		setMsgToSendList(new ArrayList<Message>());

	}

	/**
	 * 
	 * @method updateNeighbors This method updates the SensorNetwork neighbors
	 *         and it runs at the start of each timestep (Priority = FIRST).
	 *         This is useful in case the nodes are moving.
	 * 
	 */
	@ScheduledMethod(start = 1d, priority = 1.7976931348623157E308d, shuffle = false)
	public void updateNeighbors() {

		say("updateNeighbors() method was lunched on " + this.toString());

		Network network = (Network) FindProjection("EmergingTeams/SensorNetwork");
		Iterator netNeighbors = new NetworkAdjacent(network, this).query()
				.iterator();

		while (netNeighbors.hasNext()) {
			Object oldNeighbor = netNeighbors.next();
			RemoveEdge("EmergingTeams/SensorNetwork", this, oldNeighbor);
		}

		ContinuousSpace mySpace = (ContinuousSpace) FindProjection("EmergingTeams/ContinuousSpace2D");
		Iterator list = new ContinuousWithin(mySpace, this, communicationRange)
				.query().iterator();
		double myX = mySpace.getLocation(this).getX();
		double myY = mySpace.getLocation(this).getY();

		while (list.hasNext()) {

			Object o = list.next();

			if (o instanceof Node) {
				CreateEdge("EmergingTeams/SensorNetwork", this, o, 1.0);
			}
		}

	}

	/**
	 * 
	 * This is the step behavior.
	 * 
	 * @method step Note that each node HAS TO execute the operations in this
	 *         order compulsory: 1) RECEIVE msgs 2) PROCESS msgs 3) SEND msgs
	 * 
	 *         (Otherwise you screw up the simulation given the design of the
	 *         GlobalMessenger)
	 * 
	 */
	@ScheduledMethod(start = 1d, interval = 1d, priority = 0, // NORMAL priority
	shuffle = false)
	public void step() {

		say("step() method was lunched on " + this.toString());

		// RECEIVE msgs
		ArrayList<Message> msgReceivedTmp = globalMessenger.receive(this);

		// PROCESS msgs
		if (msgReceivedTmp.size() > 0) {
			msgReceived.addAll(msgReceivedTmp);
		}
		setMsgToSendList(hello());

		for (int i = 0; i < msgToSendList.size(); i++) {
			globalMessenger.send(msgToSendList.get(i));

		}

		// SEND msgs
		setMsgToSendList(new ArrayList<Message>());
	}

	/**
	 * 
	 * This is the step behavior.
	 * 
	 * @method hello
	 * 
	 *         Process received messages and returns an ArrayList of Message to
	 *         send.
	 * 
	 */
	public ArrayList<Message> hello() {

		say("hello() method was lunched on " + this.toString());

		ArrayList<Message> returnValue = new ArrayList<Message>();
		double timeDouble = GetTickCount();

		if (msgReceived.size() > 0) {

			for (int i = 0; i < msgReceived.size(); i++) {

				Message aMsgReceived = (Message) msgReceived.get(i);
				returnValue.add(new Message(this, aMsgReceived.getSender(),
						new String("Hello back from " + this + " to "
								+ aMsgReceived.getSender()), timeDouble));
				System.out.println(GetTickCount() + " - " + this
						+ " RECEIVED from " + aMsgReceived.getSender() + ": "
						+ (String) aMsgReceived.getContent() + " (timestamp: "
						+ aMsgReceived.getTimestamp() + ")");

			}

			setMsgReceived(new ArrayList<Message>());

		} else {

			Network network = (Network) FindProjection("EmergingTeams/SensorNetwork");
			Iterator netNeighbors = new NetworkAdjacent(network, this).query()
					.iterator();

			while (netNeighbors.hasNext()) {
				Node aNetNeighbor = (Node) netNeighbors.next();
				returnValue.add(new Message(this, aNetNeighbor, new String(
						"Hello from " + this + " to " + aNetNeighbor),
						timeDouble));
			}

		}

		return returnValue;

	}

	/**
	 * This method provides a human-readable name for the agent. A ProbeID
	 * overrides default toString()
	 * 
	 * @method toString
	 * 
	 */
	@ProbeID()
	public String toString() {
		// Set the default agent identifier.
		String returnValue = this.agentID;
		// Return the results.
		return returnValue;
	}

	private void say(String s) {
		System.out.println(s);
	}

}
