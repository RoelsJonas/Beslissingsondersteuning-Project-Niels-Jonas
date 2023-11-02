import java.util.*;

public class Warehouse {
    // Constants:
    private static final int AMOUNT_OF_RACKS = 10;
    private static final int AMOUNT_OF_VEHICLES = 2;
    private static final int RACK_SIZE = 5;
    private static final int VEHICLE_SIZE = 4;
    private static final int BUFFER_X = 0;
    private static final int BUFFER_Y = 0;
    private static final int VEHICLE_SPEED = 5;
    private static final int LOADING_TIME = 1;

    private HashSet<Box> buffer;
    private Rack[] racks;
    private Vehicle[] vehicles;
    private HashSet<TransportRequest> requests;
    private HashMap<Box, Rack> inventory;

    public Warehouse() {
        buffer = new HashSet<>();
        requests = new HashSet<>();
        inventory = new HashMap<>();

        racks = new Rack[AMOUNT_OF_RACKS];
        for(int i = 0; i < AMOUNT_OF_RACKS; i++)
            racks[i] = new Rack(i, 2 * (i+1), 2, RACK_SIZE);

        vehicles = new Vehicle[AMOUNT_OF_VEHICLES];
        for(int i = 0; i < AMOUNT_OF_VEHICLES; i++)
            vehicles[i] = new Vehicle(i, VEHICLE_SPEED, VEHICLE_SIZE, 0, i+1);
    }

    public void addBox(int boxID, int rackID) throws RackException {
        Box b = new Box(boxID);
        if(rackID >= 0) {
            racks[rackID].addBoxes(b);
            inventory.put(b, racks[rackID]);
        }

        else buffer.add(b);
    }

    public void addRequest(int boxId, int pickUpLocation, int dropOffLocation) {
        requests.add(new TransportRequest(boxId, pickUpLocation, dropOffLocation));
    }

    public void pickUpBox(int vehicleID,int rackID, int boxID) throws RackException {
        // get the position in the stack of the box (0 being top)
        int boxPos = racks[rackID].getBoxPosition(new Box(boxID));
        Stack<Box> boxes = racks[rackID].removeBoxes(boxPos);
        // check if we can fit this box and all the ones above it on the vehicle
//        if(vehicles[vehicleID].getFreeSpace() > boxPos) {

        //TODO
//
//        }

    }

















    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        Object[] boxes = buffer.toArray();
        sb.append("Buffer: \n");
        for(Object b : boxes)
            sb.append("\t - " + b + "\n");

        sb.append("\n Racks: \n");
        for(Rack r : racks)
            sb.append("\t" + r + "\n");

        sb.append("\n Vehicles: \n");
        for(Vehicle v : vehicles)
            sb.append("\t" + v + "\n");

        return sb.toString();
    }
}
