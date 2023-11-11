import java.util.Stack;

public class Rack extends Storage{

    private Stack<Box> stack;

    public Rack(int ID, String name, int x, int y, int MAX_CAPACITY){
        super(ID, name, x, y, MAX_CAPACITY);
        stack = new Stack<>();
    }

    public void addBox(Box box) throws RackException {
        if(stack.size() < MAX_CAPACITY) {
            stack.add(box);
        }

        else throw new RackException("Rack " + name + " is Full");
    }
    public void addBoxes(Stack<Box> boxes) throws Exception {
        if(stack.size() + boxes.size() <= MAX_CAPACITY) {
            stack.addAll(boxes);
        }

        else throw new RackException("Rack " + name + " is too full to fit boxes");
    }


    public Stack<Box> removeBoxes(int pos) throws RackException {
        if(pos > stack.size()) throw new RackException("Not enough elements in rack");
        Stack<Box> res = new Stack<>();
        Object[] temp = stack.toArray();
        for(int i = temp.length - 1 - pos; i < temp.length; i++) {
            res.add((Box) temp[i]);
            stack.pop();
        }
        return res;
    }


    public Stack<Box> getStack() {
        return stack;
    }
    public int getFreeSpace() {
        return (MAX_CAPACITY - stack.size());
    }

    public int getBoxPosition(Box b){
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


