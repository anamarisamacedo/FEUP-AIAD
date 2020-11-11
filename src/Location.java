
public class Location {
	private double lat; //x
	private double lon; //y
	
	public Location(double lat, double lon) {
		this.setLat(lat);
		this.setLon(lon);
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
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
