import jade.core.Agent;

/*
 * DUMMY AGENT, USED ONLY FOR DEBUGGING CLASSES IN DEVELOPMENT
 * */
public class ClassTesterAgent extends Agent {
	public void setup() {
		Order newOrder = new Order();
		Item newItem1 = new Item(new int[] { 1, 2, 3 }, 4, "First");
		Item newItem2 = new Item(new int[] { 5, 6, 7 }, 8, "Second");
		newOrder.addItem(newItem1);
		newOrder.addItem(newItem2);
		System.out.println("Order weight: " + newOrder.getWeight());
		Vehicle newDrone = new Drone(1);
		System.out.println("Drone capacity: " + newDrone.getCapacity());
	}
}
