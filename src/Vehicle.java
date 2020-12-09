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
        System.out.println("My lat is: " + this.getLocation().getLat());
        System.out.println("My lon is: " + this.getLocation().getLon());
        if(this.path.isEmpty())
        {
            System.out.println("It's empty");
            return;
        }

        System.out.println("GONNA UPDATE POS");

        Location nextLocation = this.getNextStop();
        if(location.equals(this.getNextStop()))
        {
            this.path.remove();

            if(this.path.isEmpty())
            {
                return;
            }
        }

        int xIncrement = 0;
        int yIncrement = 0;

        if(abs(nextLocation.getLat() - this.getLocation().getLat()) < this.baseSpeed)
        {
            xIncrement = nextLocation.getLat() - this.getLocation().getLat();
        }
        else
        {
            if(nextLocation.getLat() < this.location.getLat())
            {
                xIncrement = -1*this.baseSpeed;
            }
            else
            {
                xIncrement = this.baseSpeed;
            }
        }

        if(abs(nextLocation.getLon() - this.getLocation().getLon()) < this.baseSpeed)
        {
            yIncrement = nextLocation.getLon() - this.getLocation().getLon();
        }
        else
        {
            if(nextLocation.getLon() < this.location.getLon())
            {
                yIncrement = -1*this.baseSpeed;
            }
            else
            {
                yIncrement = this.baseSpeed;
            }
        }
        this.setLocation(new Location(this.location.getLat()+xIncrement, this.location.getLat()+yIncrement));
        System.out.println("My next lat is: " + this.getLocation().getLat());
        System.out.println("My next lon is: " + this.getLocation().getLon());
    }

}