import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class Buffer extends Storage{

    private final HashSet<Box> boxes;

    public Buffer(int ID, String name, int x, int y) {
        super(ID, name, x, y, Integer.MAX_VALUE);
        boxes = new HashSet<>();
    }
    
    public void addBox(Box box){
        boxes.add(box);
    }
    public void addBoxes(Stack<Box> boxes){
       this.boxes.addAll(boxes);
    }

    public Box removeBox(String boxID){
        Iterator<Box> iterator = boxes.iterator();

        while (iterator.hasNext()) {
            Box box = iterator.next();
            if (box.getID().equals(boxID)) {
                iterator.remove(); 
                return box; 
            }
        }
    
        return null;
    }
 
    public int getFreeSpace() {
        return (MAX_CAPACITY - boxes.size());
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