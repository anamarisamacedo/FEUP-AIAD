import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Class responsible for generating random orders
 * */
public class OrderFactoryAgent extends Agent {

	public void setup() {
		addBehaviour(new FIPARequestResp(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
	}
	
	class FIPARequestResp extends AchieveREResponder {

		public FIPARequestResp(Agent a, MessageTemplate mt) {
			super(a, mt);
		}
		
		protected ACLMessage handleRequest(ACLMessage request) {
			ACLMessage reply = request.createReply();
			ArrayList<Order> orders = createRandomOrders(10);
						
			try {
				reply.setContentObject((Serializable)orders);
	         } catch (IOException ex) {
	             System.err.println("Cannot add Order to message. Sending empty message.");
	             ex.printStackTrace(System.err);
	         }


			reply.setPerformative(ACLMessage.AGREE);
			return reply;
		}
		
		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
			ACLMessage result = request.createReply();
			result.setPerformative(ACLMessage.INFORM);
			result.setContent("here you go!");
			return result;
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
			
			//will be used to generate random nrs
			Random r = new Random();
			
			//list of all the orders generated
			ArrayList<Order> orders = new ArrayList<Order>();
			
			//Create nr of orders requested
			for(int i = 0; i < totalOrders; i++)
			{				
				Order newOrder = new Order();
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
}
