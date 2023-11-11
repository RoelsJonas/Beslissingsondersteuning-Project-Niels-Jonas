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
    private StringBuilder logs;

    public Vehicle(int ID, String name, float SPEED, float LOADING_TIME, int CAPACITY, int x, int y){
        this.ID = ID;
        this.name = name;
        this.SPEED = SPEED;
        this.LOADING_TIME = LOADING_TIME;
        this.CAPACITY = CAPACITY;
        this.x = x;
        this.y = y;
        logs = new StringBuilder();
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

        stack.push(b);
    }

    public void addBoxes(Stack<Box> boxes) throws Exception {
        if(stack.size() + boxes.size() <= CAPACITY) {
            int nextTime = (int)(time + LOADING_TIME);
            logs.append(name+";"+x+";"+y+";"+time+";"+x+";"+y+";"+nextTime+";"+boxes.toString()+";Load\n");
            time = nextTime;

            stack.addAll(boxes);
        }
        else throw new RackException("Vehicle with name " + name + " is too full to fit boxes");
    }

    // Remove to top box from the vehicle
    public Box removeFirstBox() throws VehicleException{
        if(stack.size() <= 0){
            throw new VehicleException("Vehicle with name " + name + " wants to remove a box but has none");
        }
        int nextTime = (int)(time + LOADING_TIME);
        Box b = stack.pop();

        logs.append(name+";"+x+";"+y+";"+time+";"+x+";"+y+";"+nextTime+";"+b.getID()+";Unload\n");
        time = nextTime;
        return b;
    }
    // Remove the boxes one by one (timewise)
    public Stack<Box> removeBoxesOneByOne(int pos) throws RackException {
        if(pos > stack.size()) throw new RackException("Not enough elements in rack");
        
        Stack<Box> res = new Stack<>();
        Object[] temp = stack.toArray();
        for(int i = temp.length - 1 - pos; i < temp.length; i++){
            res.add((Box) temp[i]);
            stack.pop();
            int nextTime = (int)(time + LOADING_TIME);
            logs.append(name+";"+x+";"+y+";"+time+";"+x+";"+y+";"+nextTime+";"+((Box) temp[i]).getID()+";Unload\n");
            time = nextTime;
        }

        return res;
    }
    // Remove a full stack of boxes at once
    public Stack<Box> removeBoxesStacked(int pos) throws RackException {
        if(pos > stack.size()) throw new RackException("Not enough elements in rack");
        
        Stack<Box> res = new Stack<>();
        Object[] temp = stack.toArray();
        for(int i = temp.length - 1 - pos; i < temp.length; i++){
            res.add((Box) temp[i]);
            stack.pop();
        }

        int nextTime = (int)(time + LOADING_TIME);
        logs.append(name+";"+x+";"+y+";"+time+";"+x+";"+y+";"+nextTime+";"+res.toString()+";Unload\n");
        time = nextTime;

        return res;
    }

    public int getBoxPosition(Box b){
        return stack.search(b) - 1;
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
        int nextTime = (int)(time + distance / SPEED);
        logs.append(name+";"+x+";"+y+";"+time+";"+storage.x+";"+storage.y+";"+nextTime+";None;Drive\n");

        time = nextTime;
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