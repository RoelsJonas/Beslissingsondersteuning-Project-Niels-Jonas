public class Main {

    public static void main(String[] args) throws RackException {
        Warehouse w = new Warehouse();
        w.addBox("0", -1);
        w.addBox("1",0);
        w.addBox("2", 4);
        w.addBox("3",5);
        w.addBox("4", 4);
        w.addBox("5",5);

        w.addRequest(0, 0, 1);
        w.addRequest(1, 0, 5);
        w.addRequest(3, 5, 4);

        System.out.println(w);

//        w.pickUpBox(0, 4, 4);
        w.pickUpBox(0, 4, "2");
    }
}
