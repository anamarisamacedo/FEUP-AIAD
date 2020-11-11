
import java.util.ArrayList;
import java.util.List;

abstract class Vehicle {
    //total capacity
    protected int capacity;
    // vehicle speed when empty
    protected int baseSpeed;
    // how much the truck speed decreases when an item is added
    protected int speedDecrement;
    // array of currently loaded items
    protected List<Integer> items=new ArrayList<Integer>();
    // vehicle id
    private int id;

    public Vehicle(int id) {
        this.id = id;
        this.baseSpeed = 0;
    }

    public int baseSpeed() {
        return this.baseSpeed;
    }

    public List<Integer> getItems() {
        return this.items;
    }

}