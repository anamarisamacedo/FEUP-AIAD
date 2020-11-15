import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class Supplier {

	//Pickups locations
	Location l1 = new Location(509, 103);
	Location l2 = new Location(101, 784);
	Location l3 = new Location(615, 846);

	private List<Location> pickups = new ArrayList<>();

	public Supplier() {
		pickups.add(l1);
		pickups.add(l2);
		pickups.add(l3);
	}
	
	//Get nearest pickup to the orders received by argument
	public Location allocatePickUp(ArrayList<Order> orders) {
		double minDist = Integer.MAX_VALUE;
		Location pickupPoint = pickups.get(0);

		for (Location pick : this.pickups) {
			int sumDist = 0;
			for (Order order : orders) {
				sumDist += pick.distanceTo(order.getLocation());
			}
			
			if (sumDist < minDist) {
				minDist = sumDist;
				pickupPoint = pick;
			}
		}
		
		return pickupPoint;
	}


	//Get nearest pickup to the order received by argument
	public Location allocatePickUp(Order order) {
		double minDist = Integer.MAX_VALUE;
		Location pickupPoint = this.pickups.get(0);
		for (Location pick : this.pickups) {
			double dist = pick.distanceTo(order.getLocation());

			if (dist < minDist) {
				minDist = dist;
				pickupPoint = pick;
			}
		}

		return pickupPoint;
	}
}
