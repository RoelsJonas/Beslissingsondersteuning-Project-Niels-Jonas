import java.util.HashSet;
import java.util.Objects;
import java.util.Stack;

public class Storage {
    protected int ID;
    protected String name;
    public int requestCount = 0;
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

    
    public void addBox(Box b) throws Exception{}
    public void addBoxes(Stack<Box> boxes) throws Exception{}
    public Box removeBox(String boxID)throws Exception{return null;}
    public Stack<Box> removeBoxes(int pos)throws Exception{return null;}
    
    public int getFreeSpace() {return 0;}
    public int getBoxPosition(Box b) {return 0;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Storage storage = (Storage) o;
        return ID == storage.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    public boolean equals(Storage s){
        return name.equals(s.name);
    }

}

class RackException extends Exception {
    public RackException(String s) {
        super(s);
    }
}