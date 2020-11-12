import java.io.Serializable;

public class Location implements Serializable {
	private int lat; //x
	private int lon; //y
	private static final long serialVersionUID = 3L;
	
	public Location(int lat, int lon) {
		this.setLat(lat);
		this.setLon(lon);
	}

	public int getLat() {
		return lat;
	}

	public void setLat(int lat) {
		this.lat = lat;
	}

	public int getLon() {
		return lon;
	}

	public void setLon(int lon) {
		this.lon = lon;
	}
	
	public double distanceTo(Location loc) {
		return Math.pow((loc.getLat() - this.lat), 2) + Math.pow((loc.getLon() - this.lon), 2);
	}
	
	public boolean equals(Object obj) {
		if(obj != null && getClass() == obj.getClass()) {
			Location loc = (Location) obj;
			return this.lat == loc.getLat() && this.lon == loc.getLon(); 
		}
		
		return false;
	}
}
