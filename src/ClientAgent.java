import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;

import java.util.ArrayList;
import java.util.Vector;

/*
 * Simulate a costumer. Will ask orderfactory to generate orders,
 * and make a purchase (request to the supplier)
 * */
public class ClientAgent extends Agent {

	public void setup() {
		addBehaviour(new FIPARequestInit(this, new ACLMessage(ACLMessage.REQUEST)));
		System.out.println("AGENT IS ALIVE");
	}
	
	class FIPARequestInit extends AchieveREInitiator {

		public FIPARequestInit(Agent a, ACLMessage msg) {
			super(a, msg);
		}

		protected Vector<ACLMessage> prepareRequests(ACLMessage msg) {
			Vector<ACLMessage> v = new Vector<ACLMessage>();
			//get receiver by type, not name
			msg.addReceiver(new AID("OFAgent", false));
			msg.setContent("I need 10 random orders!");
			v.add(msg);
			return v;
		}
		
		protected void handleAgree(ACLMessage agree) {
			System.out.println("Received the orders!");
			ArrayList<Order> orders = null;
			try {
				orders = (ArrayList<Order>)(agree.getContentObject());
			} catch (UnreadableException e) {
				e.printStackTrace();
			}

			System.out.println("Gonna send them to the supplier");
			//send order array
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
