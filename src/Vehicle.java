import uchicago.src.sim.network.DefaultDrawableNode;

import java.util.ArrayList;
import java.util.List;

abstract class Vehicle {
	private static int native_id = 0;
	
	//Type (Mailman, Motorcycle or Truck)
	protected String type;
    //total capacity
    protected int capacity;
    // vehicle speed when empty
    protected int baseSpeed;
    // array of currently loaded items
    protected List<Order> orders = new ArrayList<Order>();
    // vehicle id
    private int id;
    //current location
    protected Location location;
    //path the vehicle will follow
    protected List<Location> path = new ArrayList<Location>();
    private int lastVisited = 0;

    public DefaultDrawableNode getNode() {
        return myNode;
    }

    public void setNode(DefaultDrawableNode myNode) {
        this.myNode = myNode;
    }

    DefaultDrawableNode myNode;


    public Vehicle() {
        this.id = Vehicle.native_id++;
        this.baseSpeed = 0;
        this.location = new Location(0, 0);
        this.myNode = new DefaultDrawableNode();
    }

    public int baseSpeed() {
        return this.baseSpeed;
    }

    public List<Order> getOrders() {
        return this.orders;
    }
    
    public String getType() {
        return this.type;
    }

	public int getId() {
		return id;
	}
	
	public int getCapacity() {
        return this.capacity;
    }
	
	public void addOrder(Order order) {
		orders.add(order);
	}

	public Location getLocation()
    {
        return this.location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
        this.myNode.setX(location.getLat());
        this.myNode.setY(location.getLon());
        System.out.println("My new location is: " + myNode.getX() + ", " + myNode.getY());
    }

    //vehicle will follow the exact order of path
    public void addLocationToPath(Location location)
    {
        this.path.add(location);
    }

    public Location getNextStop()
    {
        return this.path.get(lastVisited);
    }

    public void iterateStop()
    {
        this.lastVisited++;
    }
}