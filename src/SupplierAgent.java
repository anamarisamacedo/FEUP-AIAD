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
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.*;

public class SupplierAgent extends Agent {

	ArrayList<Order> orders = null; 
	SupplierAgent supAgent = this;
	Supplier supplier = new Supplier();
	String clientID = null;
	
	public void setup() {
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
				//Get orders received from the client
				orders = (ArrayList<Order>)(request.getContentObject());
				
				clientID = request.getSender().getLocalName();

				//Call a new behaviour to initiate a communication with the distributor
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
			//Get nearest pickup to the client's orders location
			Location pickup = supplier.allocatePickUp(orders);
			
			//Create a pair with the pickup location and the orders array to send to distributor
			Pair<ArrayList<Order>, Location> ordersLocation = new Pair<>(orders, pickup);
			
			Vector<ACLMessage> v = new Vector<ACLMessage>();
			AID distrID = HelperClass.getAIDbyType(supAgent, "Distributor");
			msg.addReceiver(distrID);
			
			try {
				msg.setContentObject((Serializable)ordersLocation);
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
