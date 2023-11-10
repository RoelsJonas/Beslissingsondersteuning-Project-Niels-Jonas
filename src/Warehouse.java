import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Warehouse {
    // Constants:
    private final int STACK_CAPACITY;
    private final int VEHICLE_SPEED;
    private final int LOADING_TIME;

    private Storage[] storages;
    private Buffer[] buffers;
    private Rack[] racks;
    private Vehicle[] vehicles;
    private HashSet<TransportRequest> requests;
    private HashMap<Box, Storage> inventory;

    private StringBuilder logs;

    public Warehouse(Vehicle[] vehicles, Rack[] racks, HashSet<TransportRequest> requests, Buffer[] buffers, int stackcapacity, int vehiclespeed, int loadingduration, Storage[] storages) {
        this.vehicles =vehicles;
        this.racks = racks;
        this.requests = requests;
        this.buffers = buffers;
        this.storages = storages;
        logs = new StringBuilder();
        logs.append("%vehicle;startx;starty;starttime;endx;endy;endtime;box;operation\n");

        // Set constants
        STACK_CAPACITY = stackcapacity;
        VEHICLE_SPEED = vehiclespeed;
        LOADING_TIME = loadingduration;

        // Setup inventory and buffers
        inventory = new HashMap<>();
        setupInventory();
        setupBuffers();
    }

    public void processAllRequests() throws Exception{
        Vehicle vehicle = vehicles[0];
        Iterator<TransportRequest> iterator = requests.iterator();

        while (iterator.hasNext()) {
            TransportRequest request = iterator.next();
            String boxID = request.getBoxID();
            Storage pickup = request.getPickupLocation();
            Storage drop = request.getDropOffLocation();

            vehicle.addTransportRequest(request);

            // Load the box
            logs.append(vehicle.getName()+";"+vehicle.getX()+";"+vehicle.getY()+";"+vehicle.getTime()+";");
            vehicle.drive(pickup);
            pickUpBox(vehicle.getID(), pickup, boxID);
            logs.append(vehicle.getX()+";"+vehicle.getY()+";"+vehicle.getTime()+";"+boxID+";"+"PL\n");

            // Unload the box
            logs.append(vehicle.getName()+";"+vehicle.getX()+";"+vehicle.getY()+";"+vehicle.getTime()+";");
            vehicle.drive(drop);
            dropOffBox(vehicle.getID(), drop, boxID);
            logs.append(vehicle.getX()+";"+vehicle.getY()+";"+vehicle.getTime()+";"+boxID+";"+"PU\n");

            // Write logs
            String logContent = logs.toString();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/Solutions/logs.txt"))) {
                writer.write(logContent);
                System.out.println("Logs written to file successfully.");
            } catch (IOException e) {
                System.err.println("Error writing logs to file: " + e.getMessage());
            }
        }
        
    }

    // Pickup a box from a Buffer or Rack and load it onto a vehicle
    public void pickUpBox(int vehicleID, Storage storage, String boxID) throws Exception {
        Vehicle vehicle = vehicles[vehicleID];

        // Add the box from a Buffer to a vehicle
        if (storage instanceof Buffer){
            if(vehicle.getFreeSpace() > 0) {
                Box box = storage.removeBox(boxID);
                vehicle.addBox(box);
            }
        }
        // Add box(es) from a Rack to a vehicle
        else{
            int boxPos = storage.getBoxPosition(new Box(boxID));
            if(vehicle.getFreeSpace() > boxPos) {
                Stack<Box> boxes = storage.removeBoxes(boxPos);
                vehicle.addBoxes(boxes);
            }
        }
    }

    // Dropoff a box to a Buffer or Rack and load it onto a vehicle
    public void dropOffBox(int vehicleID, Storage storage, String boxID) throws Exception {
        Vehicle vehicle = vehicles[vehicleID];
        Box box = vehicle.removeBox();
        if(storage.getMAX_CAPACITY() - storage.getCapacity() > 1){
            storage.addBox(box);
        }
    }


    // Remove one or multiple boxes from the inventory
    public void removeBoxesInventory(Box box){
        inventory.remove(box);
    }
    public void removeBoxesInventory(Stack<Box> boxes){
        for(Box box : boxes) inventory.remove(box);
    }

    // Add one or multiple boxes from the inventory
    public void addBoxesInventory(Box box, Storage storage){
        inventory.put(box, storage);
    }
    public void addBoxesInventory(Stack<Box> boxes, Storage storage){
        for(Box box : boxes) inventory.put(box, storage);
    }

    // Setup the inventory of the warehouse after initialisation
    public void setupInventory(){
        for(Rack rack : racks){
            for(Box box : rack.getStack()){
                inventory.put(box, rack);
            }
        }
    }

    // Because the boxes in the buffers are not given we need to search for those
    public void setupBuffers(){
        for(Buffer buffer : buffers){
            for(TransportRequest request : requests){
                if(buffer == request.getPickupLocation()){
                    Box b = new Box(request.getBoxID());
                    buffer.addBox(b);
                    inventory.put(b, buffer);
                }
            }
        }
    }

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

        sb.append("\n Requests: \n");
        for(TransportRequest request : requests)
            sb.append("\t" + request + "\n");

        return sb.toString();
    }
}
