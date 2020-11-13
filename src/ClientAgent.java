
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;

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

	ArrayList<Order> orders = null;
	String clientID = null;
	Agent clientAgent = this;
	
	public void setup() {
		clientID = this.getAID().getLocalName();
		orders = createRandomOrders(2);
		addBehaviour(new FIPARequestInitToSupplier(this, new ACLMessage(ACLMessage.REQUEST)));

		HelperClass.registerAgent(this, "Client");
	}
	
	class FIPARequestInitToSupplier extends AchieveREInitiator {

		public FIPARequestInitToSupplier(Agent a, ACLMessage msg) {
			super(a, msg);
		}

		protected Vector<ACLMessage> prepareRequests(ACLMessage msg) {
			System.out.format("%s: I'm gonna ask the supplier for 10 orders. The first order has this location: %d, %d\n", clientID, orders.get(0).getLocation().getLon(), orders.get(0).getLocation().getLat());
			Vector<ACLMessage> v = new Vector<ACLMessage>();

			AID supplierID = HelperClass.getAIDbyType(clientAgent, "Supplier");
			if(supplierID == null)
			{
				System.out.println("No supplier found, aborting");
				return v;
			}
			msg.addReceiver(supplierID);
			try {
				msg.setContentObject((Serializable)orders);
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
	
	private ArrayList<Order> createRandomOrders(int totalOrders)
	{
		//This should probably be sent by the ClientAgent in the request,
		//and then passed here
		int maxWeight = 100;
		int minWeight = 1;
		int minHeight = 20;
		int maxHeight = 40;
		int minWidth = 20;
		int maxWidth = 40;
		int minLength = 20;
		int maxLength = 40;
		int maxItems = 5;
		int minLat = 0;
		int maxLat = 1000;
		int minLon = 0;
		int maxLon = 1000;

		//will be used to generate random nrs
		Random r = new Random();

		//list of all the orders generated
		ArrayList<Order> orders = new ArrayList<Order>();

		//Create nr of orders requested
		for(int i = 0; i < totalOrders; i++)
		{
			Order newOrder = new Order();
			int lat = r.nextInt((maxLat-minLat)+1)+minLat;
			int longit = r.nextInt((maxLon-minLon)+1)+minLon;
			newOrder.setLocation(new Location(lat, longit));
			newOrder.setClientID(this.getAID());

			//random nr of items for each order
			int itemNr = r.nextInt(maxItems);

			for(int j = 0; j < itemNr; j++)
			{
				int weight = r.nextInt((maxWeight-minWeight)+1)+minWeight;
				int height = r.nextInt((maxHeight-minHeight)+1)+minHeight;
				int width = r.nextInt((maxWidth-minWidth)+1)+minWidth;
				int length = r.nextInt((maxLength-minLength)+1)+minLength;
				String name = "Item" + Integer.toString(i);

				newOrder.addItem(new Item(new int[] { width, height, length }, weight, name));
			}
			orders.add(newOrder);
		}
		return orders;
	}
	
	
}
