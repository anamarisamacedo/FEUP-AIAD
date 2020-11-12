import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class SupplierAgent extends Agent {

	public void setup() {
		addBehaviour(new FIPARequestResp(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
		System.out.println("Supplier active!!");
		//addBehaviour(new FIPARequestInit(this, new ACLMessage(ACLMessage.REQUEST)));
	}
	

	class FIPARequestResp extends AchieveREResponder {

		public FIPARequestResp(Agent a, MessageTemplate mt) {
			super(a, mt);
		}
		
		protected ACLMessage handleRequest(ACLMessage request) {
			try {
				ArrayList<Order> orders = (ArrayList<Order>)(request.getContentObject());
				Arrays.toString(orders.toArray());
				//send order array
				//addBehaviour( new FIPARequestSupplierInit(this, new ACLMessage(ACLMessage.REQUEST), orders )); 
				
			} catch (UnreadableException e) {
				e.printStackTrace();
			}	
			ACLMessage reply = request.createReply();
			reply.setPerformative(ACLMessage.AGREE);
			return reply;
		}
		
		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
			ACLMessage result = request.createReply();
			result.setPerformative(ACLMessage.INFORM);
			result.setContent("Supplier: Request received! We will start sending the orders!");
			return result;
		}

	}
	/*
	class FIPARequestInit extends AchieveREInitiator {

		public FIPARequestInit(Agent a, ACLMessage msg) {
			super(a, msg);
		}

		protected Vector<ACLMessage> prepareRequests(ACLMessage msg) {
			Vector<ACLMessage> v = new Vector<ACLMessage>();
			msg.addReceiver(new AID("DistributorAgent", false));
			msg.setContent("Distribute this 10 orders!");
			v.add(msg);
			return v;
		}
		
		protected void handleAgree(ACLMessage agree) {
			ArrayList<Order> orders = null;
			try {
				orders = (ArrayList<Order>)(agree.getContentObject());
				System.out.println("Received the orders! Gonna send them to the supplier");
				//send order array
				//addBehaviour( new FIPARequestSupplierInit(this, new ACLMessage(ACLMessage.REQUEST), orders )); 
				
			} catch (UnreadableException e) {
				e.printStackTrace();
			}	
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

	}*/
	

}
