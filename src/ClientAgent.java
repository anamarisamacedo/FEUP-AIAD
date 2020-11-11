import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/*
 * Simulate a costumer. Will ask orderfactory to generate orders,
 * and make a purchase (request to the supplier)
 * */
public class ClientAgent extends Agent {

	public void setup() {
		ArrayList<Order> orders = createRandomOrders(10);
		System.out.format("Created 10 orders, the first one has this location: %d, %d\n", orders.get(0).getLocation().getLon(), orders.get(0).getLocation().getLat());
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
