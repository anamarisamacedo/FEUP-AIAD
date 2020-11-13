import java.io.Serializable;

public class Item implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private int weight;
	private String name;
	private int volume;

	public Item(Integer weight, String name){
		this.weight = weight;
		this.name = name;
	}
	public Item(Integer weight, String name, int volume){
		this.weight = weight;
		this.name = name;
		this.volume = volume;
	}

	public Item() {
		this.weight = 0;
		this.name = "No-name";
		this.volume = 0;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWeight() {
		return this.weight;
	}

	public String getName() {
		return this.name;
	}

	public void setVolume(int volume)
	{
		this.volume = volume;
	}

	public int getVolume()
	{
		return this.volume;
	}

	public boolean equals(Item item) {
		return this.name.equals(item.getName()) && this.weight == item.getWeight()
				&& this.volume == item.getVolume();
	}
}
