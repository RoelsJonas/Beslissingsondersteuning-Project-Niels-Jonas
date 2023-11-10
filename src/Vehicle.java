import java.util.ArrayList;
import java.util.Stack;

public class Vehicle {
    private int ID;
    private String name;
    
    private float SPEED;
    private float LOADING_TIME;
    private int CAPACITY;
    
    private int x,y;
    
    private int time = 0;
    private Stack<Box> stack = new Stack<>();
    private ArrayList<TransportRequest> requests = new ArrayList<>();

    public Vehicle(int ID, String name, float SPEED, float LOADING_TIME, int CAPACITY, int x, int y){
        this.ID = ID;
        this.name = name;
        this.SPEED = SPEED;
        this.LOADING_TIME = LOADING_TIME;
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

    public int getTime(){
        return time;
    }

    public int getCapacity(){
        return stack.size();
    }

    public void addBox(Box b) throws VehicleException{
        if(stack.size() == CAPACITY){
            throw new VehicleException("Vehicle with name " + name + " can't fit any more boxes but wants to add more");
        }

        time += LOADING_TIME;
        stack.push(b);
    }

    public void addBoxes(Stack<Box> boxes) throws Exception {
        if(stack.size() + boxes.size() <= CAPACITY) {
            time += LOADING_TIME;
            stack.addAll(boxes);
        }
        else throw new RackException("Vehicle with name " + name + " is too full to fit boxes");
    }

    public Box removeBox() throws VehicleException{
        if(stack.size() <= 0){
            throw new VehicleException("Vehicle with name " + name + " wants to remove a box but has none");
        }
        time += LOADING_TIME;
        Box b = stack.pop();
        return b;
    }


    public void addTransportRequest(TransportRequest t){
        requests.add(t);
    }

    public void removeTransportRequest(int i){
        requests.remove(i);    
    }

    public int getFreeSpace() {
        return (CAPACITY - stack.size());
    }


    public void drive(Storage storage){
        float distance = Math.abs(storage.x - x) + Math.abs(storage.y - y);
        time += distance / SPEED;
        x = storage.x;
        y = storage.y;
    }

    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("--id: "+ name +"-------------------------------------");
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