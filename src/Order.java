import java.util.Vector;

/*
 * ORDER CLASS IS COMPOSED ONLY OF THE ITEMS BOUGHT
 * Maybe add priority? High-priority, regular mail, etc 	
 * */
public class Order {
	private Vector<Item> items;

	private PriorityType priority;

	public Order() {
		this.items = new Vector<Item>();
		this.priority = PriorityType.normal;
	}

	public Order(PriorityType priority) {
		this.items = new Vector<Item>();
		this.priority = priority;
	}

	public void addItem(Item item) {
		this.items.add(item);
	}

	public void removeItem(Item item) {
		this.items.remove(item);
	}

	public Vector<Item> getItems() {
		return this.items;
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
}
