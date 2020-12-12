import java.awt.*;

public class Truck extends Vehicle {

    public Truck() {
        super();
        this.capacity = 2000;
        this.baseSpeed = 50;
        this.cost = 40;
        this.type = "Truck";
        this.color = Color.GRAY;
    }
}