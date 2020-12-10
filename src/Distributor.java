import java.io.*;
import java.util.*;

class Distributor {

	private List<Vehicle> fleet = new ArrayList<Vehicle>();
	private Location location;
	private DistributorMethod method;

	public Distributor() {
		generateVehicles(100);
		this.method = DistributorMethod.regular;
	}

	public Distributor(Location location) {
		generateVehicles(100);
		this.location = location;
		this.method = DistributorMethod.regular;
	}

	public Distributor(Location location, DistributorMethod method) {
		generateVehicles(100);
		this.location = location;
		this.method = method;
	}

	public void moveVehicles()
	{
		for(Vehicle v : fleet)
		{
			if(!v.getPath().isEmpty())
			{
				v.moveVehicle();
			}
		}
	}

	public void generateVehicles(int vehicleNr) {
		List<Vehicle> vehicles = new ArrayList<Vehicle>();
		for(int i = 0; i < vehicleNr; i++)
		{
			Vehicle vehicle;
			Random r = new Random();
			int result = r.nextInt(99) + 1;

			if (0 < result && result <= 30) {
				vehicle = new MailMan();
			} else if (31 <= result && result <= 50) {
				vehicle = new Motorcycle();
			} else
				vehicle = new Truck();

			vehicles.add(vehicle);
		}
		this.fleet = vehicles;
	}

	//TODO: Crate different algorithms to study their performance
	public List<Pair<Order, Double>> allocate(ArrayList<Order> orders, Location source) {
		List<Pair<Order, Double>> time_per_order = new ArrayList<Pair<Order, Double>>();


		int size = orders.size();
		while (size > 0) {

			Vehicle vehicle = this.fleet.get(size);
			int occupancy = 0;

			while (occupancy <= vehicle.getCapacity() && size > 0) {
				ListIterator<Order> iter = orders.listIterator();
				while (iter.hasNext()) {
					Order currOrder = iter.next();
					if (occupancy + currOrder.getWeight() <= vehicle.getCapacity()) {
						vehicle.addOrder(currOrder);
						occupancy += currOrder.getWeight();
						iter.remove();
						size--;
					} else
						continue;
				}
				break;
			}
		}

		for (int i = 0; i < this.fleet.size(); i++) {
			if(this.getFleet().get(i).getOrders().size() > 0)
			{
				time_per_order.addAll(this.path(this.fleet.get(i), source));
			}
		}

		return time_per_order;
	}


	public boolean finalized(boolean[] visited) {
		for (int i = 0; i < visited.length; i++) {
			if (visited[i] != true)
				return false;
		}

		return true;
	}

	public List<Pair<Order, Double>> path(Vehicle v, Location source) {
		// traveled distance
		int dist = 0;
		// location currently visiting
		int count = 0;
		// current location
		Location currLoc = source;

		//First location of the vehicle is the distributor's location
		v.addLocationToPath(this.location);

		// get all delivery locations
		List<Location> locations = new ArrayList<Location>();
		for (int i = 0; i < v.getOrders().size(); i++) {
			locations.add(v.getOrders().get(i).getLocation());
		}

		// visited locations and initialization
		boolean visited[] = new boolean[locations.size()];
		for (int i = 0; i < visited.length; i++) {
			visited[i] = false;
		}

		// annexing a double time to each order
		List<Pair<Order, Double>> time_per_order = new ArrayList<Pair<Order, Double>>();

		PrintWriter pw = null;
		String fName = String.format("vehicle%s.txt", v.getId());

		try {
			File file = new File(fName);
			if (file.createNewFile()) {

				System.out.println("File has been created.");
			} else {

				System.out.println("File already exists.");
			}

			FileWriter fw = new FileWriter(file, true);
			pw = new PrintWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (!finalized(visited)) {
			int i = 0, min = 0;
			double distTo = Integer.MAX_VALUE;
			Location temp = null;
			for (; i < locations.size(); i++) {
				if (currLoc.distanceTo(locations.get(i)) < distTo && visited[i] == false
						&& !currLoc.equals(locations.get(i))) {
					distTo = currLoc.distanceTo(locations.get(i));
					temp = locations.get(i);
					min = i;
				}
			}

			if (temp != null) {
				visited[min] = true;
				dist += distTo;
				currLoc = temp;
				double time = dist / v.baseSpeed();
				count++;
				String print = String.format(
						"Vehicle %s (%s,capacity: %d) delivered from (Lat: %d Lon: %d) to (Lat: %d Lon: %d) in %d place and took %f time;",
						v.getId(), v.getType(), v.capacity, source.getLat(), source.getLon(), currLoc.getLat(), currLoc.getLon(), count, time);
				pw.println(print);
				time_per_order.add(new Pair<Order, Double>(v.getOrders().get(min), time));
				v.addLocationToPath(v.getOrders().get(min).getLocation());
			}
		}

		//Last location of the vehicle is the distributor's location
		v.addLocationToPath(this.location);

		if (pw != null)
			pw.close();

		return time_per_order;
	}

	public List<Vehicle> getFleet()
	{
		return this.fleet;
	}
}