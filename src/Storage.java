import java.util.HashSet;
import java.util.Stack;

public class Storage {
    protected int ID;
    protected String name;

    protected int MAX_CAPACITY;

    protected int x;
    protected int y;

    public Storage(int ID, String name, int x, int y, int MAX_CAPACITY) {
        this.ID = ID;
        this.name = name;        
        this.x = x;        
        this.y = y;
        this.MAX_CAPACITY = MAX_CAPACITY;
    }

    public int getID() {
        return ID;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return x;
    }
    public String getName() {
        return name;
    }
    public int getMAX_CAPACITY() {
        return MAX_CAPACITY;
    }

    
    public void addBox(Box b) throws Exception{}
    public void addBoxes(Stack<Box> boxes) throws Exception{}
    public Box removeBox(String boxID)throws Exception{return null;}
    public Stack<Box> removeBoxes(int pos)throws Exception{return null;}
    
    public int getCapacity() {return 0;}
    public int getBoxPosition(Box b) {return 0;}
}

class RackException extends Exception {
    public RackException(String s) {
        super(s);
    }
}