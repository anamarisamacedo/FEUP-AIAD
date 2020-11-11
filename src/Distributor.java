import java.io.*;
import java.util.*;

class Distributor  
{
	public void allocate() {
		/*
		 * receives list of orders
		 * and fills necessary vehicles with the orders until full capacity
		 * then, calls path with the locations each vehicle is assigned
		 */
	}
	
	public boolean finalized(boolean[] visited) {
		for (int i = 0; i < visited.length; i++) {
    	    if(visited[i] != true)
    	    	return false;
    	}
		
		return true;
	}
	
	//TODO: add vehicle to calculate speed
	//TODO: guarantee vehicle has name/id
    public int path(List<Location> locations, Location source) { 
    	//traveled distance
    	int dist = 0;
    	//location currently visiting
    	int count = 0;
    	//current location
    	Location currLoc = source;
    	
    	//visited locations and initialization
    	boolean visited[] = new boolean[locations.size()];
    	for (int i = 0; i < visited.length; i++) {
    	    visited[i] = false;
    	}
    	
    	PrintWriter pw = null;
    	String fName = "hello.txt"; //"vehicle" String.format("vehicle%s.txt", vehicle.name);

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
				//TODO: float time = distTo / vehicle.speed;
				count++;
				//String print = 
				//		String.format("Vehicle %s delivered to (Lat: %d Loc: %d) in %d place and took %f time;", vehicle.name, currLoc.getLat(), currLoc.getLon(), count, time);
				//pw.println(print);
    		}
    	}
    	
    	if(pw != null)
    		pw.close();
    	
    	return dist;
    }
}
