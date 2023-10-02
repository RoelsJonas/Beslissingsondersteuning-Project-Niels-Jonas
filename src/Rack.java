import java.util.Stack;

public class Rack {
    private int ID;
    private int MAX_CAPACITY;

    private int x;
    private int y;
    private Stack<Box> stack;

    public Rack(int ID, int x, int y, int MAX_CAPACITY) {
        this.ID = ID;
        this.x = x;
        this.y = y;
        this.MAX_CAPACITY = MAX_CAPACITY;
        stack = new Stack<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCapacity() {
        return stack.size();
    }

    public int getMAX_CAPACITY() {
        return MAX_CAPACITY;
    }

    public void addBoxes(Stack<Box> boxes) throws Exception {
        if(stack.size() + boxes.size() <= MAX_CAPACITY) {
            stack.addAll(boxes);
        }

        else throw new RackException("Rack " + ID + " is too full to fit boxes");
    }

    public void addBoxes(Box box) throws RackException {
        if(stack.size()< MAX_CAPACITY) {
            stack.add(box);
        }

        else throw new RackException("Rack " + ID + " is Full");
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("--id: "+ ID +"-------------------------------------");
        sb.append("\n\t\t Stack:");
        for (Box box : stack) {
            sb.append(box.toString() + " | ");
        }
        
        return sb.toString();
    }
}

class RackException extends Exception {
    public RackException(String s) {
        super(s);
    }
}
