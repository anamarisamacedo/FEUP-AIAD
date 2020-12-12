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

	public Distributor(Location location, DistributorMethod method, int nrClients) {
		System.out.println("GFAFDGFHNKFZIRD"+ method);
		generateVehicles(nrClients);
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

	public List<Pair<Order, Double>> allocate(ArrayList<Order> orders, Location source) {
		if(this.method == DistributorMethod.even)
		{
			return allocateEven(orders,source);
		}
		else if(this.method == DistributorMethod.random)
		{
			return allocateRandom(orders,source);
		}
		else if(this.method == DistributorMethod.regular)
		{
			return allocateRegular(orders,source);
		}
		else if(this.method == DistributorMethod.reduceCost)
		{
			return allocateReduceCost(orders, source);
		}
		return null;
	}

	public List<Pair<Order, Double>> allocateRegular(ArrayList<Order> orders, Location source) {

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
			if (this.getFleet().get(i).getOrders().size() > 0) {
				time_per_order.addAll(this.path(this.fleet.get(i), source));
			}
		}
		
		return time_per_order;
	}

	public List<Pair<Order, Double>> allocateRandom(ArrayList<Order> orders, Location source) {

		List<Pair<Order, Double>> time_per_order = new ArrayList<Pair<Order, Double>>();
		Random random = new Random(System.currentTimeMillis());

		ListIterator<Order> iter = orders.listIterator();

		while (iter.hasNext()) {
			Order order = iter.next();
			while(true)
			{
				Vehicle vehicle = this.fleet.get(random.nextInt(this.fleet.size()));
				if(vehicle.canPlace(order))
				{
					vehicle.addOrder(order);
					break;
				}
			}
		}

		for (int i = 0; i < this.fleet.size(); i++) {
			if (this.getFleet().get(i).getOrders().size() > 0) {
				time_per_order.addAll(this.path(this.fleet.get(i), source));
			}
		}

		return time_per_order;
	}

	public List<Pair<Order, Double>> allocateEven(ArrayList<Order> orders, Location source) {
		List<Pair<Order, Double>> time_per_order = new ArrayList<Pair<Order, Double>>();
		ListIterator<Order> iter = orders.listIterator();
		//iterate through every order
		while (iter.hasNext()) {
			int leastOccupied = Integer.MAX_VALUE;
			Order order = iter.next();
			//get least full vehicle
			for (Vehicle candidateVehicle : this.fleet) {
				if ((candidateVehicle.getCapacityOccupied() < leastOccupied) && candidateVehicle.canPlace(order)) {
					candidateVehicle.addOrder(order);
					leastOccupied = candidateVehicle.getCapacityOccupied();
				}
			}
		}

		for (int i = 0; i < this.fleet.size(); i++) {
			if (this.getFleet().get(i).getOrders().size() > 0) {
				time_per_order.addAll(this.path(this.fleet.get(i), source));
			}
		}

		return time_per_order;
	}

	public List<Pair<Order, Double>> allocateReduceCost(ArrayList<Order> orders, Location source)
	{
		List<Pair<Order, Double>> time_per_order = new ArrayList<Pair<Order, Double>>();

		ListIterator<Order> iter = orders.listIterator();
		//iterate through every order
		while (iter.hasNext()) {
			double bestCost = Double.MAX_VALUE;
			Order order = iter.next();
			//get least costly vehicle
			for (Vehicle candidateVehicle : this.fleet) {
				if ((candidateVehicle.getCost() < bestCost) && candidateVehicle.canPlace(order)) {
					candidateVehicle.addOrder(order);
					bestCost = candidateVehicle.getCost();
				}
			}
		}

		for (int i = 0; i < this.fleet.size(); i++) {
			if (this.getFleet().get(i).getOrders().size() > 0) {
				time_per_order.addAll(this.path(this.fleet.get(i), source));
			}
		}

		return time_per_order;
	}

	public boolean finalized(boolean[] visited) {
		for (int i = 0; i < visited.length; i++) {
			if (!visited[i])
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
		//Second location of the vehicle is the pickupLocation
		v.addLocationToPath(source);

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
				if (currLoc.distanceTo(locations.get(i)) < distTo && !visited[i]
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
				System.out.println(this.method + "-                     " + time);
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

	public double getTotalTripsCost()
	{
		double total = 0.0;
		for(Vehicle v : this.fleet)
		{
			total+=v.getTotalTripCost();
		}
		return total;
	}
}