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
    private ArrayList<TransportRequest> requests;
    private HashMap<Box, Storage> inventory;

    private StringBuilder logs;
    private HashMap<Rack, Vehicle> vehicleRackMapping = new HashMap<>();

    public Warehouse(Vehicle[] vehicles, Rack[] racks, ArrayList<TransportRequest> requests, Buffer[] buffers, int stackcapacity, int vehiclespeed, int loadingduration, Storage[] storages) {
        this.vehicles = vehicles;
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

        double average = (double) requests.size() / (double) vehicles.length;
        int i = 0;
        for(Vehicle v : vehicles) {
            int sum = 0;
            while(sum < average && i < racks.length) {
                vehicleRackMapping.put(racks[i], v);
                sum += racks[i].requestCount;
                i++;
            }
        }

        for(Rack r : racks) {
            System.out.println(r.getID() +  ": " + vehicleRackMapping.get(r).getID());
        }

        requests.sort((o1, o2) -> {
            int res = 0;
            if(o1.getDropOffLocation().getClass() != o2.getDropOffLocation().getClass()) {
                if(o1.getDropOffLocation().getClass() == Buffer.class) return Integer.MIN_VALUE;
                else return Integer.MAX_VALUE;
            }
            String id1 = o1.getBoxID();
            String id2 = o2.getBoxID();
            Box b1 = new Box(id1);
            Box b2 = new Box(id2);
            Storage s1 = inventory.get(b1);
            Storage s2 = inventory.get(b2);
            return s1.getBoxPosition(b1) - s2.getBoxPosition(b2);
        });

        System.out.println(this);

        Iterator<TransportRequest> iterator = requests.iterator();

        while (iterator.hasNext()) {
            TransportRequest request = iterator.next();

            if(request.getPickupLocation().getClass() == Buffer.class) vehicle = vehicleRackMapping.get(request.getDropOffLocation());
            else vehicle = vehicleRackMapping.get(request.getPickupLocation());
            String boxID = request.getBoxID();
            Storage pickup = request.getPickupLocation();
            Storage drop = request.getDropOffLocation();

            vehicle.addTransportRequest(request);

            // Load the box
            vehicle.drive(pickup);
            pickUpBox(vehicle.getID(), pickup, boxID);

            // Unload the box
            vehicle.drive(drop);
            dropOffBox(vehicle.getID(), drop, boxID);

            vehicle.removeTransportRequest(0);
        }

        System.out.println(this);

        logs.append(vehicle.getLogs());
        
        // Write logs
        String logContent = logs.toString();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/Solutions/logs2.txt"))) {
            writer.write(logContent);
        } catch (IOException e) {
            System.err.println("Error writing logs to file: " + e.getMessage());
        }
    }

    // Pickup a box from a Buffer or Rack and load it onto a vehicle
    public void pickUpBox(int vehicleID, Storage storage, String boxID) throws Exception {
        Vehicle vehicle = vehicles[vehicleID];
        Box box = new Box(boxID);
        // Add the box from a Buffer to a vehicle
        if (storage instanceof Buffer){
            if(vehicle.getFreeSpace() > 0) {
                box = storage.removeBox(boxID);
                vehicle.addBox(box);
                removeBoxesInventory(box);
            }
            else System.out.printf("nee"); // TODO WAT ALS STORAGE VAN VEHICLE VOL
        }
        // Add box(es) from a Rack to a vehicle
        else{
            int boxPos = storage.getBoxPosition(box);
            if(vehicle.getFreeSpace() > boxPos) {
                Stack<Box> boxes = storage.removeBoxes(boxPos);
                vehicle.addBoxes(boxes);
                removeBoxesInventory(boxes);
                for(Box b : boxes) {
                    if(!b.getID().equals(boxID)) {
                        storage.addBox(b);
                        addBoxesInventory(b, storage);
                    }
                }
            }
            else {
                System.out.println("aah"); // TODO WAT ALS STORAGE VAN VEHICLE VOL
                int amountToRelocate = boxPos - vehicle.getFreeSpace() + 1;
                String[] boxIds = new String[amountToRelocate];
                int[] rackIds = new int[amountToRelocate];
                // We need to relocate boxes
                int initialSpace = vehicle.getFreeSpace();
                int j = 0;
                while(vehicle.getFreeSpace() < storage.getBoxPosition(box)) {
                    Stack<Box> boxes = storage.removeBoxes(vehicle.getFreeSpace());
                    vehicle.addBoxes(boxes);
                    int i = 0;
                    while(vehicle.getFreeSpace() != initialSpace && i < racks.length) {
                        if(racks[i].getFreeSpace() > 0 && vehicleRackMapping.get(racks[i]) == vehicle) {
                            vehicle.drive(racks[i]);
                            for(Box b : boxes) {
                                vehicle.removeBox(b.getID());
                                racks[i].addBox(b);
                                boxIds[j] = b.getID();
                                rackIds[j] = i;
                                j++;
                                if(racks[i].getFreeSpace() == 0) break;
                            }
                        }
                        i++;
                    }
                    vehicle.drive(storage);
                }
                Stack<Box> boxes2 = storage.removeBoxes(boxPos);
                vehicle.addBoxes(boxes2);
                removeBoxesInventory(boxes2);
                for(Box b : boxes2) {
                    if(!b.getID().equals(boxID)) {
                        storage.addBox(b);
                        addBoxesInventory(b, storage);
                    }
                }
                for(int i = amountToRelocate - 1; i >= 0; i--) {
                    vehicle.drive(racks[rackIds[i]]);
                    vehicle.addBox(racks[rackIds[i]].removeBox(boxIds[i]));
                    vehicle.drive(storage);
                    storage.addBox(vehicle.removeBox(boxIds[i]));
                }
            }
        }
    }

    // Dropoff a box to a Buffer or Rack and load it onto a vehicle
    public void dropOffBox(int vehicleID, Storage storage, String boxID) throws Exception {
        Vehicle vehicle = vehicles[vehicleID];
        if(storage.getFreeSpace() > 0){
            Box box = vehicle.removeBox(boxID);
            storage.addBox(box);
            addBoxesInventory(box, storage);
        }
        else System.out.println("else"); // TODO WAT ALS STORAGE VOL IS
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
        for(TransportRequest r : requests) {
            r.getDropOffLocation().requestCount++;
            r.getPickupLocation().requestCount++;
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

    public void validateWarehouse(){
        int wrongs = 0;
        for(TransportRequest request : requests){
            String boxID = request.getBoxID();
            Storage dropOffStorage = request.getDropOffLocation();

            Storage currentStorage = inventory.get(new Box(boxID));
            if(currentStorage == null){
                System.out.println("FAIL: Box with id " + boxID + " wasn't found in the inventory, probably still in a vehicle or lost");
                wrongs++;
                continue;
            }

            if(!currentStorage.equals(dropOffStorage)){
                System.out.println("FAIL: Box with id " + boxID + " wasn't located on storage with name " + dropOffStorage.getName() + " but on " + currentStorage.getName());
                wrongs++;
            }
            else{
                System.out.println("SUCCESS: Box with id " + boxID + " was located in the right storage");
            }
        }

        if(wrongs == 0){
            System.out.println("No errors found during processing, all boxes located in the right storages");
        }
        else{
            System.out.println("Found " + wrongs + "/"+ requests.size() + " boxes that are not located in the right storage");
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
