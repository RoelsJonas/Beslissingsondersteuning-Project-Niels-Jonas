import java.util.*;

public class Warehouse {
    // Constants:
    private final int STACK_CAPACITY;
    private final int VEHICLE_SPEED;
    private final int LOADING_TIME;

    private Buffer[] buffers;
    private Rack[] racks;
    private Vehicle[] vehicles;
    private HashSet<TransportRequest> requests;
    private HashMap<Box, Rack> inventory;

    public Warehouse(Vehicle[] vehicles, Rack[] racks, HashSet<TransportRequest> requests, Buffer[] buffers, int stackcapacity, int vehiclespeed, int loadingduration) {
        this.vehicles =vehicles;
        this.racks = racks;
        this.requests = requests;
        this.buffers = buffers;

        // Set constants
        STACK_CAPACITY = stackcapacity;
        VEHICLE_SPEED = vehiclespeed;
        LOADING_TIME = loadingduration;

        // Setup the buffers
        setupBuffers();

        // Setup inventory
        inventory = new HashMap<>();
        setupInventory();
    }

    public void addBox(String boxID, int rackID) throws RackException {
        Box b = new Box(boxID);
        if(rackID >= 0) {
            racks[rackID].addBoxes(b);
            inventory.put(b, racks[rackID]);
        }

        else buffers[0].addBox(b);
    }

    public void pickUpBox(int vehicleID,int rackID, String boxID) throws RackException {
        // get the position in the stack of the box (0 being top)
        int boxPos = racks[rackID].getBoxPosition(new Box(boxID));
        Stack<Box> boxes = racks[rackID].removeBoxes(boxPos);
        // check if we can fit this box and all the ones above it on the vehicle
//        if(vehicles[vehicleID].getFreeSpace() > boxPos) {

        //TODO
//
//        }

    }

    public void setupInventory(){
        for(Rack rack : racks){
            for(Box box : rack.getStack()){
                inventory.put(box, rack);
            }
        }
    }

    // Because the boxes in the vuffers are not given we need to search for those
    public void setupBuffers(){
        for(Buffer buffer : buffers){
            for(TransportRequest request : requests){
                if(buffer.getName().equals(request.getPickupLocation())){
                    Box b = new Box(request.getBoxID());
                    buffer.addBox(b);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n Buffers: \n");
        for(Buffer b : buffers)
            sb.append("\t" + b + "\n");

        sb.append("\n Racks: \n");
        for(Rack r : racks)
            sb.append("\t" + r + "\n");

        sb.append("\n Vehicles: \n");
        for(Vehicle v : vehicles)
            sb.append("\t" + v + "\n");

        return sb.toString();
    }
}
