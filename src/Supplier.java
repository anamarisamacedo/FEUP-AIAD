import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class Supplier {

	Location l1 = new Location(509, 103);
	Location l2 = new Location(101, 784);
	Location l3 = new Location(615, 846);

	private List<Location> pickups = new ArrayList<>();

	public Supplier() {
		pickups.add(l1);
		pickups.add(l2);
		pickups.add(l3);
	}

	public Location allocatePickUp(ArrayList<Order> orders) {
		int minDist = 999999999;
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

	public Location allocatePickUp(Location loc) {
		double minDist = 0.0;
		Location pickupPoint = pickups.get(0);
		for (Location pick : this.pickups) {
			double dist = pick.distanceTo(loc);

			if (dist < minDist) {
				minDist = dist;
				pickupPoint = pick;
			}
		}

		return pickupPoint;
	}
}
