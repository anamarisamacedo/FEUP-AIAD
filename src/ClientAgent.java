
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/*
 * Simulate a costumer (created by ClientFactory Agent)
 * and make a purchase (request to the supplier)
 * */
public class ClientAgent extends Agent {

	Order order = null;
	String clientID = null;
	Agent clientAgent = this;
	Location location = null;

	public void setup() {
		clientID = this.getAID().getLocalName();
		order = makeOrder();
		addBehaviour(new FIPARequestInitToSupplier(this, new ACLMessage(ACLMessage.REQUEST)));
		addBehaviour(new FIPAClientResp(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
		Random r = new Random();

		this.location = new Location(r.nextInt(1000), r.nextInt(1000));
		HelperClass.registerAgent(this, "Client");
	}

	class FIPARequestInitToSupplier extends AchieveREInitiator {

		public FIPARequestInitToSupplier(Agent a, ACLMessage msg) {
			super(a, msg);
		}

		protected Vector<ACLMessage> prepareRequests(ACLMessage msg) {
			Order order = makeOrder();
			System.out.format("%s: I'm gonna ask the supplier for %d items. My location is: %d, %d\n", clientID,
					order.getItems().size(), order.getLocation().getLat(), order.getLocation().getLon());
			Vector<ACLMessage> v = new Vector<ACLMessage>();

			AID supplierID = HelperClass.getAIDbyType(clientAgent, "Supplier");
			if (supplierID == null) {
				System.out.println("No supplier found, aborting");
				return v;
			}
			msg.addReceiver(supplierID);
			try {
				msg.setContentObject((Serializable) order);
			} catch (IOException e) {
				System.err.format("%s: Cannot make orders", clientID);
				e.printStackTrace();
			}
			v.add(msg);
			return v;
		}

		protected void handleAgree(ACLMessage agree) {
			System.out.println(clientID + ": " + agree);
		}

		protected void handleRefuse(ACLMessage refuse) {
			System.out.println(refuse);
		}

		protected void handleInform(ACLMessage inform) {
			System.out.println(inform);
		}

		protected void handleFailure(ACLMessage failure) {
			System.out.println(failure);
		}

	}

	// receives request from distributor
	class FIPAClientResp extends AchieveREResponder {

		public FIPAClientResp(Agent a, MessageTemplate mt) {
			super(a, mt);
		}

		protected ACLMessage handleRequest(ACLMessage request) {
			System.out.println(request);
			ACLMessage reply = request.createReply();
			reply.setPerformative(ACLMessage.AGREE);
			return reply;
		}

		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
			ACLMessage result = request.createReply();
			result.setPerformative(ACLMessage.INFORM);
			result.setContent("Client: Order received! Thanks for the expedience!");
			return result;
		}

	}

	private Order makeOrder() {
		Order order = new Order();
		order.setLocation(this.location);
		order.setClientID(this.getAID());

		Random r = new Random();
		// random nr of items for each order
		int probBuy = 20;

		ArrayList<Item> items = HelperClass.getItems("Products.txt");

		for (Item item : items) {
			if (r.nextInt(100) < probBuy) {
				order.addItem(item);
			}
		}
		return order;
	}

}
