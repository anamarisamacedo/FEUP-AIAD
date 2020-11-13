import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.*;
import org.javatuples.Pair;

public class SupplierAgent extends Agent {

	ArrayList<Order> orders = null; 
	SupplierAgent supAgent = this;
	Supplier supplier = new Supplier();
	String clientID = null;
	Pair<ArrayList<Order>, Location> sendingMessage;
	
	public void setup() {
		addBehaviour(new FIPARequestClientResp(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
		System.out.println("Supplier active!!");
	}
	

	class FIPARequestClientResp extends AchieveREResponder {

		public FIPARequestClientResp(Agent a, MessageTemplate mt) {
			super(a, mt);
		}
		
		protected ACLMessage handleRequest(ACLMessage request) {
			ACLMessage reply = request.createReply();
			try {
				orders = (ArrayList<Order>)(request.getContentObject());
				clientID = request.getSender().getLocalName(); //****************
				
				//Get nearest pickup
				Location pickup = supplier.allocatePickUp(orders);
				System.out.print("PICKUUPPP: " + pickup);
				
				//send pickup location and orders array to distributor
				sendingMessage = new Pair<>(orders, pickup);
				addBehaviour( new FIPARequestDistributorInit(supAgent, new ACLMessage(ACLMessage.REQUEST))); 
				
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
			msg.addReceiver(new AID("DistAgent", false));
			try {
				msg.setContentObject((Serializable)sendingMessage);
			} catch (IOException e) {
				System.err.format("Cannot send %s orders to distributor", clientID);
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
