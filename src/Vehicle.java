import java.util.ArrayList;
import java.util.Stack;

public class Vehicle {
    private float SPEED = 5;
    private float CAPACITY = 5;
    private int ID;

    private int x,y = 0;
    private Stack<Box> stack = new Stack<>();
    private ArrayList<TransportRequest> requests = new ArrayList<>();

    public Vehicle(int ID, float SPEED, float CAPACITY, int x, int y){
        this.ID = ID;
        this.SPEED = SPEED;
        this.CAPACITY = CAPACITY;
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }

    public int getCapacity(){
        return stack.size();
    }

    public void addBox(Box b) throws VehicleException{
        if(stack.size() == CAPACITY){
            throw new VehicleException("Vehicle with id " + ID + " can't fit any more boxes but wants to add more");
        }

        stack.push(b);
    }

    public Box removeBox() throws VehicleException{
        if(stack.size() > 0){
            throw new VehicleException("Vehicle with id " + ID + " wants to remove a box but has none");
        }

        Box b = stack.pop();

        return b;
    }


    public void addTransportRequest(TransportRequest t){
        requests.add(t);
    }

    public void removeTransportRequest(int i){
        requests.remove(i);    
    }
    
}

class VehicleException extends Exception{
    public VehicleException(String s){
        super(s);
    }
}