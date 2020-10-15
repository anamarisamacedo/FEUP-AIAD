public class Order {
	// physical dimensions: w*h*l
	private int[] dimensions;
	private int weight;
	private String name;

	public Order(int[] dimensions, Integer weight, String name) {
		this.dimensions = dimensions;
		this.weight = weight;
		this.name = name;
	}

	public Order() {
		this.dimensions = new int[] { 1, 2, 3 };
		this.weight = 0;
		this.name = "No-name";
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDimensions(int[] dimensions) {
		this.dimensions = dimensions;
	}

	public int getWeight() {
		return this.weight;
	}

	public String getName() {
		return this.name;
	}

	public int[] getDimensions() {
		return this.dimensions;
	}
}
