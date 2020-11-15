import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.QueryAgentsOnLocation;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.*;

public class SupplierAgent extends Agent {

	
	public int orderCount = 0;
	public int clientCount = 10;
	public ArrayList<Order> orders = new ArrayList<Order>();
	public ArrayList<Order> finalOrders = new ArrayList<Order>();
	private SupplierAgent supAgent;
	private Supplier supplier = new Supplier();
	public LocalDateTime dayStart;

	public void setup() {
		supAgent = this;
		// Add behaviour to receive requests from clients
		addBehaviour(new FIPARequestClientResp(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
		System.out.println("Supplier active!!");

		HelperClass.registerAgent(this, "Supplier");
	}

	class FIPARequestClientResp extends AchieveREResponder {

		public FIPARequestClientResp(Agent a, MessageTemplate mt) {
			super(a, mt);
		}

		protected ACLMessage handleRequest(ACLMessage request) {
			ACLMessage reply = request.createReply();
			

			try {
				// Get the orders received from the client
				Order order = (Order) (request.getContentObject());
				if(orderCount==0) {
					dayStart = LocalDateTime.now();
				}
				orderCount++;
				ArrayList<Pair<Item, Integer>> itemsStock = HelperClass.getItemsAndStock("Products.txt");
				// Check if the ordered items have stock
				for (Item clientItem : order.getItems()) {
					for (Pair<Item, Integer> stockItem : itemsStock) {
						if (clientItem.equals(stockItem.getFirst())) {
							if(stockItem.getSecond()==0) {								
								LocalDateTime dayEnd = LocalDateTime.now();
								Long duration = Duration.between(dayStart, dayEnd).getSeconds();
								if(duration >= 10) {
									orderCount = 0;
									finalOrders = orders;
									orders = new ArrayList<Order>();
									addBehaviour(new FIPARequestDistributorInit(supAgent, new ACLMessage(ACLMessage.REQUEST)));
								}
								//If an item doesn't have stock, a REFUSE message is sent to the client
								//and the process finishes 
								reply.setPerformative(ACLMessage.REFUSE);
								reply.setContent("We do not have stock for " + clientItem.getName());
								return reply;
							}
						}
					}
				}
				
				orders.add(order);

				LocalDateTime dayEnd = LocalDateTime.now();
				Long duration = Duration.between(dayStart, dayEnd).getSeconds();
				
				// At the end of a day (10 second) the supplier calls the distributor to send the orders
				if(duration >= 10) {	
					orderCount = 0;
					finalOrders = orders;
					orders = new ArrayList<Order>();
					//a new behaviour is created to initiate a communication with the distributor
					addBehaviour(new FIPARequestDistributorInit(supAgent, new ACLMessage(ACLMessage.REQUEST)));
				}

			} catch (UnreadableException e) {
				e.printStackTrace();
			}

			reply.setPerformative(ACLMessage.AGREE);
			return reply;
		}

		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
			ACLMessage result = request.createReply();
			result.setPerformative(ACLMessage.INFORM);
			result.setContent("Supplier: Request received! I'm gonna call the distributor!");
			return result;
		}
	}

	class FIPARequestDistributorInit extends AchieveREInitiator {

		public FIPARequestDistributorInit(Agent a, ACLMessage msg) {
			super(a, msg);
		}

		protected Vector<ACLMessage> prepareRequests(ACLMessage msg) {
			// Get nearest pickup to the client's orders location
			Location pickup = supplier.allocatePickUp(finalOrders);

			// Create a pair with the pickup location and the orders array to send to
			// distributor
			Pair<ArrayList<Order>, Location> ordersLocation = new Pair<>(finalOrders, pickup);

			Vector<ACLMessage> v = new Vector<ACLMessage>();
			AID distrID = HelperClass.getAIDbyType(supAgent, "Distributor");
			if (distrID == null) {
				System.out.println("No ditributor found, aborting");
				return v;
			}

			msg.addReceiver(distrID);
			try {
				msg.setContentObject((Serializable) ordersLocation);
			} catch (IOException e) {
				System.err.format("Cannot send orders to distributor");
				e.printStackTrace();
			}
			v.add(msg);
			return v;
		}

		protected void handleAgree(ACLMessage agree) {
			System.out.println(agree);
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

}
