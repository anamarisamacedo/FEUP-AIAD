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

    public Vehicle() {
        this.id = Vehicle.native_id++;
        this.baseSpeed = 0;
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

}