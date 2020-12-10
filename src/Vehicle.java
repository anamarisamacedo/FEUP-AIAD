import uchicago.src.sim.network.DefaultDrawableEdge;
import uchicago.src.sim.network.DefaultDrawableNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.lang.Math.abs;

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
    DefaultDrawableNode myNode;

    public Vehicle() {
        this.id = Vehicle.native_id++;
        this.baseSpeed = 0;
        this.location = new Location(300, 300);
        this.myNode = new DefaultDrawableNode();
    }

    public int getRemainingSpace()
    {
        return this.capacity - this.getCapacityOccupied();
    }

    public int getCapacityOccupied()
    {
        int totalFilled = 0;
        for(Order order : orders)
        {
            totalFilled += order.getWeight();
        }
        return totalFilled;
    }
    public Queue<Location> getPath() {
        return path;
    }

    //path the vehicle will follow
    protected Queue<Location> path = new LinkedList<Location>();

    public DefaultDrawableNode getNode() {
        return myNode;
    }

    public void setNode(DefaultDrawableNode myNode) {
        this.myNode = myNode;
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
    }

    //vehicle will follow the exact order of path
    public void addLocationToPath(Location location)
    {
        this.path.add(location);
    }

    public Location getNextStop()
    {
        return this.path.peek();
    }

    public void moveVehicle()
    {
        if(this.path.isEmpty())
        {
            return;
        }

        Location nextLocation = this.getNextStop();
        if(location.equals(this.getNextStop()))
        {
            this.path.remove();

            if(this.path.isEmpty())
            {
                return;
            }

            DefaultDrawableEdge edge = (DefaultDrawableEdge) this.myNode.getOutEdges().get(0);
            edge.setTo(Repast3ServiceLauncher.getNodeAt(this.getNextStop()));

        }

        double xIncrement = 0;
        double yIncrement = 0;
        double velocity = this.baseSpeed*0.2;

        if(abs(nextLocation.getLat() - this.getLocation().getLat()) < velocity)
        {
            xIncrement = nextLocation.getLat() - this.getLocation().getLat();
        }
        else
        {
            if(nextLocation.getLat() < this.location.getLat())
            {
                xIncrement = -1*velocity;
            }
            else
            {
                xIncrement = velocity;
            }
        }

        if(abs(nextLocation.getLon() - this.getLocation().getLon()) < velocity)
        {
            yIncrement = nextLocation.getLon() - this.getLocation().getLon();
        }
        else
        {
            if(nextLocation.getLon() < this.location.getLon())
            {
                yIncrement = -1*velocity;
            }
            else
            {
                yIncrement = velocity;
            }
        }
        this.setLocation(new Location((int)(this.location.getLat()+xIncrement), (int)(this.location.getLon()+yIncrement)));
    }

}