import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
        for(int i = 1; i <= AMOUNT_OF_RACKS; i++)
            racks[i] = new Rack(i, 2 * i, 2, RACK_SIZE);

        vehicles = new Vehicle[AMOUNT_OF_VEHICLES];
        for(int i = 0; i < AMOUNT_OF_VEHICLES; i++)
            vehicles[i] = new Vehicle(i, VEHICLE_SPEED, VEHICLE_SIZE, 0, i+1);
    }

    public void addBox(int boxID, int rackID) throws RackException {
        racks[rackID].addBoxes(new Box(boxID));
    }

    public void addRequest(int boxId, int pickUpLocation, int dropOffLocation) {
        requests.add(new TransportRequest(boxId, pickUpLocation, dropOffLocation));
    }
}
