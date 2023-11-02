import java.util.ArrayList;
import java.util.Stack;

public class Vehicle {
    private float SPEED = 5;
    private float CAPACITY = 5;
    private int ID;
    private String name;

    private int x,y = 0;
    private Stack<Box> stack = new Stack<>();
    private ArrayList<TransportRequest> requests = new ArrayList<>();

    public Vehicle(int ID, String name, float SPEED, float CAPACITY, int x, int y){
        this.ID = ID;
        this.name = name;
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

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
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

    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("--id: "+ ID +"-------------------------------------");
        sb.append("\n\t\t Stack:");
        for (Box box : stack) {
            sb.append(box.toString() + " | ");
        }
        
        sb.append("\n\t\t Requests:");
        for (TransportRequest request : requests) {
            sb.append("\t\t\t" + request.toString());
        }

        return sb.toString();
    }
}

class VehicleException extends Exception{
    public VehicleException(String s){
        super(s);
    }
}