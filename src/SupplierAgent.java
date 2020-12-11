import jade.core.AID;
import sajas.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import sajas.core.Agent;
import sajas.core.behaviours.Behaviour;
import sajas.core.behaviours.WakerBehaviour;
import sajas.proto.AchieveREInitiator;
import sajas.proto.AchieveREResponder;
import uchicago.src.sim.network.DefaultDrawableNode;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SupplierAgent extends Agent {
	public ArrayList<Order> orders = new ArrayList<Order>();
	private SupplierAgent supAgent;
	private Supplier supplier = new Supplier();
	private DefaultDrawableNode myNode;

	Behaviour loop;

	public void setup() {
		supAgent = this;
		HelperClass.registerAgent(this, "Supplier");
		// Add behaviour to receive requests/orders from clients
		addBehaviour(new FIPARequestClientResp(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
		System.out.println("Supplier active!!");
	}

	public SupplierAgent(List<Location> pickupList) {
		for (Location l : pickupList) {
			this.supplier.addPickupLocations(l);
		}
	}

	public void setNode(DefaultDrawableNode node) {
		this.myNode = node;
	}

	class FIPARequestClientResp extends AchieveREResponder {
		SequentialBehaviour seq;
		public FIPARequestClientResp(Agent a, MessageTemplate mt) {
			super(a, mt);
			seq = new SequentialBehaviour();
			seq.addSubBehaviour(new WakerBehaviour(supAgent, 5000) {
				protected void handleElapsedTimeout() {
					// a new behaviour is created to initiate a communication with the distributor
					addBehaviour(new FIPARequestDistributorInit(supAgent, new ACLMessage(ACLMessage.REQUEST)));

				}
			});
			addBehaviour(seq);
		}

		protected ACLMessage handleRequest(ACLMessage request) {
			ACLMessage reply = request.createReply();

			try {

				// Get the order received from the client
				Order order = (Order) (request.getContentObject());

				ArrayList<Pair<Item, Integer>> itemsStock = HelperClass.getItemsAndStock("Products.txt");
				// Check if the ordered items have stock
				for (Item clientItem : order.getItems()) {
					for (Pair<Item, Integer> stockItem : itemsStock) {
						if (clientItem.equals(stockItem.getFirst())) {
							if (stockItem.getSecond() == 0) {
								// If an item doesn't have stock, a REFUSE message is sent to the client
								// and the process finishes
								reply.setPerformative(ACLMessage.REFUSE);
								reply.setContent("We do not have stock for " + clientItem.getName());
								return reply;
							}
						}
					}
				}
				orders.add(order);

				// At the end of a day the supplier calls the distributor to send the orders

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
			Vector<ACLMessage> v = new Vector<ACLMessage>();
			// Get nearest pickup to the clients' orders locations at the end of the day
			Location pickup = supplier.allocatePickUp(orders);

			// Create a pair with the pickup location and the orders array to send to
			// distributor
			Pair<ArrayList<Order>, Location> ordersLocation = new Pair<>(orders, pickup);

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
