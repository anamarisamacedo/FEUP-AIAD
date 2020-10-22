import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
/*
 * ORDER CLASS IS COMPOSED ONLY OF THE ITEMS BOUGHT
 * */
public class Order {
	private List<Item> items;
	private LocalDateTime date;
	private PriorityType priority = PriorityType.normal;
	
	public Order() {
		this.items = new ArrayList<Item>();
		this.date = LocalDateTime.now();
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

	public List<Item> getItems() {
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
}
