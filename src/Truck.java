public class Truck extends Vehicle {

    public Truck(int id) {
        super(id);
        this.capacity = 0;
    }

    public int getCapacity() {
        return this.capacity;
    }
}