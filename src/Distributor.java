import java.io.*;
import java.util.*;

class Distributor  
{
	private List<Order> orders = new ArrayList<Order>();
	
	public Vehicle generateVehicle() {
		Vehicle vehicle;
		Random r = new Random();
		int result = r.nextInt(99) + 1;
		
		if(0 < result && result <= 30) {
			vehicle = new MailMan();
		}
		else if (31 <= result && result <= 50) {
			vehicle = new Motorcycle();
		}
		else vehicle = new Truck();
		
		return vehicle;
	}
	
	//TODO: location sent by supplier
	public void allocate(List<Order> orders) {
		this.orders = orders;
		
		Vehicle vehicle;
		List<Vehicle> fleet = new ArrayList<Vehicle>();
		//TODO: vv temporary!!
		Location source = new Location(0, 0);
		
		while(this.orders.size() > 0) {
			vehicle = this.generateVehicle();
			int occupancy = 0;
			
			while(occupancy <= vehicle.getCapacity()) {
				ListIterator<Order> iter = this.orders.listIterator();
				while(iter.hasNext()){
	    			if(occupancy + iter.next().getWeight() <= vehicle.getCapacity()){
	    				vehicle.addOrder(iter.next());
	    				occupancy += iter.next().getWeight();
	        			iter.remove();
	    			}
				}
			}
			fleet.add(vehicle);
		}
		
		for(int i = 0; i < fleet.size(); i++) {
			this.path(fleet.get(i), source);
		}
	}
	
	public boolean finalized(boolean[] visited) {
		for (int i = 0; i < visited.length; i++) {
    	    if(visited[i] != true)
    	    	return false;
    	}
		
		return true;
	}
	
	
    public int path(Vehicle v, Location source) { 
    	//traveled distance
    	int dist = 0;
    	//location currently visiting
    	int count = 0;
    	//current location
    	Location currLoc = source;
    	
    	//get all delivery locations
    	List<Location> locations = new ArrayList<Location>();
    	for(int i = 0; i < v.getOrders().size(); i++) {
    		locations.add(v.getOrders().get(i).getLocation());
    	}
    	
    	//visited locations and initialization
    	boolean visited[] = new boolean[locations.size()];
    	for (int i = 0; i < visited.length; i++) {
    	    visited[i] = false;
    	}
    	
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
    	
    	while(!finalized(visited)) {
    		int i = 0;
    		double distTo = Integer.MAX_VALUE;
    		Location temp = null;
    		for(; i < locations.size(); i++) {
        		if(currLoc.distanceTo(locations.get(i)) < distTo && visited[i] == false && !currLoc.equals(locations.get(i))) {
        			distTo = currLoc.distanceTo(locations.get(i));
        			temp = locations.get(i);
        		}
        	}
    		
    		if(temp != null) {
				visited[i] = true;
				dist += distTo;
				currLoc = temp;
				double time = distTo / v.baseSpeed();
				count++;
				String print = 
						String.format("Vehicle %s delivered to (Lat: %d Loc: %d) in %d place and took %f time;", v.getId(), currLoc.getLat(), currLoc.getLon(), count, time);
				pw.println(print);
    		}
    	}
    	
    	if(pw != null)
    		pw.close();
    	
    	return dist;
    }
}
