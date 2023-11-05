import java.util.HashSet;
import java.util.Stack;

public class Buffer {
    private int ID;
    private String name;

    private int x;
    private int y;

    HashSet<Box> boxes;

    public Buffer(int ID, String name, int x, int y) {
        this.ID = ID;
        this.name = name;        
        this.x = x;        
        this.y = y;
        this.boxes = new HashSet<>();
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
    public HashSet<Box> getBoxes(){
        return boxes;
    }

    
    public void addBox(Box b){
        boxes.add(b);
    }
    public void addBoxes(Stack<Box> boxes){
       this.boxes.addAll(boxes);
    }

    public void pickUpBox(String boxID){
        boxes.removeIf(box -> box.getID().equals(boxID));
    }


    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("--name: "+ name +"-------------------------------------");
        sb.append("\n\t\t Boxes:");
        for (Box box : boxes) {
            sb.append(box + " | ");
        }
        return sb.toString();
    }
}