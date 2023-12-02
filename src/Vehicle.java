import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class Vehicle {
    private int ID;
    private String name;
    
    private float SPEED;
    private float LOADING_TIME;
    private int CAPACITY;
    
    private int x,y;
    
    private int time = 0;
    private HashMap<String, Box> stack = new HashMap<>();
    private ArrayList<TransportRequest> requests = new ArrayList<>();
    private static StringBuilder logs = new StringBuilder();

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

    public String getLogs(){
        return logs.toString();
    }

    public int getCapacity(){
        return stack.size();
    }

    public void addBox(Box b) throws VehicleException{
        if(stack.size() == CAPACITY){
            throw new VehicleException("Vehicle with name " + name + " can't fit any more boxes but wants to add more");
        }

        int nextTime = (int)(time + LOADING_TIME);
        logs.append(name+";"+x+";"+y+";"+time+";"+x+";"+y+";"+nextTime+";"+b.getID()+";Load\n");
        time = nextTime;

        stack.put(b.getID(), b);
    }

    public void addBoxes(Stack<Box> boxes) throws Exception {
        if(stack.size() + boxes.size() <= CAPACITY) {
            int nextTime = (int)(time + LOADING_TIME);
            logs.append(name+";"+x+";"+y+";"+time+";"+x+";"+y+";"+nextTime+";"+boxes.toString()+";Load\n");
            time = nextTime;

            for(Box b : boxes) stack.put(b.getID(), b);
        }
        else throw new RackException("Vehicle with name " + name + " is too full to fit boxes");
    }

    // Remove the boxes one by one (timewise)

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
        int nextTime = (int)(time + distance / SPEED);
        logs.append(name+";"+x+";"+y+";"+time+";"+storage.x+";"+storage.y+";"+nextTime+";None;Drive\n");

        time = nextTime;
        x = storage.x;
        y = storage.y;
    }

    public Box removeBox(String boxId) {
        Box res = stack.getOrDefault(boxId, null);
        if(res != null) stack.remove(boxId);
        else System.out.println(boxId);
        return res;
    }

    public Stack<Box> removeBoxes(int number) {
        if(number > stack.size()) throw new IllegalArgumentException("Can't remove more boxes than there are in vehicle " + name);

        Stack<Box> res = new Stack<>();
        for(int i = 0; i < number; i++) {
            res.push(stack.remove(stack.keySet().iterator().next()));
        }
        return res; 
    }

    public void removeBoxes(Stack<Box> boxes) {
        if(boxes.size() > stack.size()) throw new IllegalArgumentException("Can't remove more boxes than there are in vehicle " + name);

        for(Box b : boxes) {
            stack.remove(b);
        }
    }
    
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("--id: "+ name +"-------------------------------------");
        sb.append("\n\t\t Stack:");
        for (Box box : stack.values()) {
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