public class Main {

    public static void main(String[] args) throws RackException {
        Warehouse w = new Warehouse();
        w.addBox(0, 0);
        w.addBox(1,0);
        w.addBox(2, 4);
        w.addBox(3,5);

        System.out.println(w);
    }
}
