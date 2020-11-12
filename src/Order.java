import jade.core.AID;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.io.Serializable;
/*
 * ORDER CLASS IS COMPOSED ONLY OF THE ITEMS BOUGHT
 * */
public class Order implements Serializable{

	private static final long serialVersionUID = 1L;
	private ArrayList<Item> items;
	private LocalDateTime date;
	private PriorityType priority = PriorityType.normal;
	private Location location;
	private AID clientID;
	
	public Order() {
		this.items = new ArrayList<Item>();
		this.date = LocalDateTime.now();
		this.location = new Location(0, 0);
	}

	public Order(PriorityType priority) {
		this();
		this.priority = priority;
	}

	public void addItem(Item item) {
		this.items.add(item);
	}

	public void removeItem(Item item) {
		this.items.remove(item);
	}

	public ArrayList<Item> getItems() {
		return this.items;
	}
	
	public String getDate()
	{
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		return this.date.format(dateFormatter);
	}

	public int getWeight() {
		int totalWeight = 0;
		for (Item item : items) {
			totalWeight += item.getWeight();
		}
		return totalWeight;
	}

	public PriorityType getPriority() {
		return this.priority;
	}

	public void setPriority(PriorityType priority) {
		this.priority = priority;
	}

	public Location getLocation() {return this.location;}

	public void setLocation(Location location) {this.location = location;}

	public AID getClientID(){return this.clientID;}

	public void setClientID(AID clientID) {this.clientID = clientID;}
}
