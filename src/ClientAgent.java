import jade.core.Agent;

public class ClientAgent extends Agent {
	public void setup() {
		Order newOrder = new Order(new int[] { 4, 5, 6 }, 25, "NewOrder");
		System.out.println(newOrder.getWeight());
	}
}
