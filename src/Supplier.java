import java.util.List;
import java.util.ArrayList;

public class Supplier {
	private List<Location> pickups = new ArrayList<>();

	public Location allocatePickUp(Location loc) {
		double minDist = 0.0;
		Location pickupPoint = pickups.get(0);
		for (Location pick : this.pickups) {
			double dist = pick.distanceTo(loc);
			
			if(dist < minDist) {
				minDist = dist;
				pickupPoint = pick;
			}
		}
		
		return pickupPoint;
	}
}
