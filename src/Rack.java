import java.util.Stack;

public class Rack {
    private int ID;
    private String name;
    
    private int MAX_CAPACITY;
    
    private int x;
    private int y;
    private Stack<Box> stack;

    public Rack(int ID, String name, int x, int y, int MAX_CAPACITY) {
        this.ID = ID;
        this.x = x;
        this.y = y;
        this.name = name;
        this.MAX_CAPACITY = MAX_CAPACITY;
        stack = new Stack<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return stack.size();
    }

    public int getMAX_CAPACITY() {
        return MAX_CAPACITY;
    }
    public Stack<Box> getStack() {
        return stack;
    }

    public void addBoxes(Stack<Box> boxes) throws Exception {
        if(stack.size() + boxes.size() <= MAX_CAPACITY) {
            stack.addAll(boxes);
        }

        else throw new RackException("Rack " + name + " is too full to fit boxes");
    }

    public void addBoxes(Box box) throws RackException {
        if(stack.size() < MAX_CAPACITY) {
            stack.add(box);
        }

        else throw new RackException("Rack " + name + " is Full");
    }

    public Stack<Box> removeBoxes(int pos) throws RackException {
        if(pos > stack.size()) throw new RackException("Not enough elements in rack");
        Stack<Box> res = new Stack<>();
        Object[] temp = stack.toArray();
        for(int i = temp.length - 1 - pos; i < temp.length; i++) {
            res.add((Box) temp[i]);
            stack.pop();
        }

//        for (Box box : res) {
//            System.out.print(box + " | ");
//        }
//        System.out.println();

        return res;
    }

    public int getBoxPosition(Box b) {
        return stack.search(b) - 1;
    }

        public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("--name: "+ name +"-------------------------------------");
        sb.append("\n\t\t Stack:");
        for (Box box : stack) {
            sb.append(box + " | ");
        }
        return sb.toString();
    }
}

class RackException extends Exception {
    public RackException(String s) {
        super(s);
    }
}
