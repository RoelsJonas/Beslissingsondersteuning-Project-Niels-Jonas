import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Warehouse {
    // Constants:
    private final int STACK_CAPACITY;
    private final int VEHICLE_SPEED;
    private final int LOADING_TIME;

    private final Storage[] storages;
    private final Buffer[] buffers;
    private final Rack[] racks;
    private final Vehicle[] vehicles;
    private final ArrayList<TransportRequest> requests;
    private final HashMap<Box, Storage> inventory;

    private final StringBuilder logs;
    private final HashMap<Rack, Vehicle> vehicleRackMapping = new HashMap<>();
    private final String fileName;

    protected static int totalDriveTime;
    protected static int totalLoadTime;
    protected static int relocationTime;

    public Warehouse(Vehicle[] vehicles, Rack[] racks, ArrayList<TransportRequest> requests, Buffer[] buffers, int stackcapacity, int vehiclespeed, int loadingduration, Storage[] storages, String fileName) {
        this.vehicles = vehicles;
        this.racks = racks;
        this.requests = requests;
        this.buffers = buffers;
        this.storages = storages;
        this.fileName = fileName;
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
        // Calculate the cost for all transport requests to and from a rack (without any relocations)
        int totalCost = 0;
        HashMap<Storage, Integer> rackCosts =  new HashMap<>();
        for(TransportRequest r : requests) {
            Storage pickup = r.getPickupLocation();
            Storage drop = r.getDropOffLocation();
            int cost = Math.abs(pickup.x - drop.x) + Math.abs(pickup.y - drop.y);
            totalCost += cost;
            if (pickup.getClass() == Buffer.class) {
                rackCosts.put(drop, cost + rackCosts.getOrDefault(drop, 0));
            }
            else {
                rackCosts.put(pickup, cost + rackCosts.getOrDefault(pickup, 0));
            }
        }

        // Allocate racks to each vehicle so that each vehicle has an equal cost for all transport requests to and from it's assigned racks
        double average = (double) totalCost / (double) vehicles.length;
        int i = 0;
        for(Vehicle v : vehicles) {
            int sum = 0;
            while(sum < average && i < racks.length) {
                vehicleRackMapping.put(racks[i], v);
                sum += rackCosts.getOrDefault(racks[i], 0);
                i++;
            }
        }
        int temp = 0;
        for(Rack r : racks) {
            if(vehicleRackMapping.get(r) == null) vehicleRackMapping.put(r, vehicles[temp++%vehicles.length]);
            if(Main.DEBUG) System.out.println(r.getID() +  ": " + vehicleRackMapping.get(r).getID());
        }

        // Sort requests such that we first move boxes to the buffer points, grouped by stacks and with the boxes at the top of stacks coming first
        requests.sort((o1, o2) -> {
            if(o1.getDropOffLocation().getClass() != o2.getDropOffLocation().getClass()) {
                if(o1.getDropOffLocation().getClass() == Buffer.class) return -1;
                else return 1;
            }

            if(o1.getPickupLocation().getClass() == Rack.class) {
                if(o1.getPickupLocation().getID() != o2.getPickupLocation().getID())
                    return o1.getPickupLocation().getID() - o2.getPickupLocation().getID();
            }

            String id1 = o1.getBoxID();
            String id2 = o2.getBoxID();
            Box b1 = new Box(id1);
            Box b2 = new Box(id2);
            Storage s1 = inventory.get(b1);
            Storage s2 = inventory.get(b2);
            return s1.getBoxPosition(b1) - s2.getBoxPosition(b2);
        });

        // We iteratively execute every transport request
        Iterator<TransportRequest> iterator = requests.iterator();
        while(iterator.hasNext()) {
            executeRequest(iterator);
        }

        // Write logs
        logs.append(Vehicle.logs);
        String logContent = logs.toString();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/Solutions/logs_"+fileName+".txt"))) {
            writer.write(logContent);
        } catch (IOException e) {
            System.err.println("Error writing logs to file: " + e.getMessage());
        }
    }

    private void executeRequest(Iterator<TransportRequest> iterator) throws Exception {
        TransportRequest request = iterator.next();
        
        Vehicle vehicle;
        Storage pickup = request.getPickupLocation();
        Storage drop = request.getDropOffLocation();
        
        if (pickup.getClass() == Buffer.class) {
            vehicle = vehicleRackMapping.get(drop);
        } else {
            vehicle = vehicleRackMapping.get(inventory.get(new Box(request.getBoxID())));
            pickup = inventory.get(new Box(request.getBoxID()));
        }
        
        String boxID = request.getBoxID();

        // Load the box
        vehicle.drive(pickup);
        pickUpBox(vehicle.getID(), pickup, boxID);

        // If we are picking up a box from a buffer we might be able to take another so we check for capacity
        if(pickup.getClass() == Buffer.class) {
            if (vehicle.getFreeSpace() > 0 && iterator.hasNext()) {
                executeRequest(iterator);
            }
        }

        // Unload the box
        vehicle.drive(drop);
        dropOffBox(vehicle.getID(), drop, boxID);
    }

    // Pickup a box from a Buffer or Rack and load it onto a vehicle
    public void pickUpBox(int vehicleID, Storage storage, String boxID) throws Exception {
        Vehicle vehicle = vehicles[vehicleID];
        Box box = new Box(boxID);
        // Add the box from a Buffer to a vehicle
        if (storage instanceof Buffer){
            pickUpFromBuffer(storage, boxID, vehicle);
        }

        // Add box(es) from a Rack to a vehicle
        else{
            int boxPos = storage.getBoxPosition(box);
            // Check if a relocation is necessary
            if(vehicle.getFreeSpace() > boxPos) {
                pickUpFromRack(storage, boxID, boxPos, vehicle);
            }
            else {
                relocateAndPickupFromRack(storage, boxID, vehicle, box);
            }
        }
    }

    private void relocateAndPickupFromRack(Storage storage, String boxID, Vehicle vehicle, Box box) throws Exception {
        int boxPos;
        Box[] rackIds = new Box[racks.length];
        int initialSpace = vehicle.getFreeSpace();

        // Split all the boxes over existing racks
        int startTime = vehicle.getTime();
        while(vehicle.getFreeSpace() < storage.getBoxPosition(box) + 1) {
            Stack<Box> boxes = storage.removeBoxes(vehicle.getFreeSpace() - 1);
            vehicle.addBoxes(boxes);

            while(vehicle.getFreeSpace() != initialSpace) {
                int max = Integer.MIN_VALUE;
                int i = -1;
                for(int j = 0; j < racks.length; j++) {
                    if(racks[j].getFreeSpace() > max && vehicleRackMapping.get(racks[j]) == vehicle && racks[j] != storage) {
                        max = racks[j].getFreeSpace();
                        i = j;
                    }
                }
                if(racks[i].getFreeSpace() > 0 && vehicleRackMapping.get(racks[i]) == vehicle && racks[i] != storage) {
                    vehicle.drive(racks[i]);
                    int boxesToRemove = Math.min(racks[i].getFreeSpace(), initialSpace - vehicle.getFreeSpace());
                    Stack<Box> toAdd = vehicle.removeBoxes(boxesToRemove);
                    racks[i].addBoxes(toAdd);
                    for(Box b : toAdd) {
                        inventory.put(b, racks[i]);
                    }
                    if(rackIds[i] == null){
                        rackIds[i] = toAdd.get(toAdd.size() - 1);
                    }
                }
            }

            vehicle.drive(storage);
        }
        int endTime = vehicle.getTime() + vehicle.getDriveTime();
        relocationTime += endTime - startTime;

        // Finnaly add the box to the vehicle
        boxPos = storage.getBoxPosition(box);
        Stack<Box> boxes = storage.removeBoxes(boxPos);
        vehicle.addBoxes(boxes);
        for(Box b : boxes) {
            if(!b.getID().equals(boxID)) {
                vehicle.removeBox(b.getID());
                storage.addBox(b);
            }
        }
        removeBoxesInventory(box);
    }

    private void pickUpFromRack(Storage storage, String boxID, int boxPos, Vehicle vehicle) throws Exception {
        Stack<Box> boxes = storage.removeBoxes(boxPos);
        vehicle.addBoxes(boxes);
        removeBoxesInventory(boxes);
        for(Box b : boxes) {
            if(!b.getID().equals(boxID)) {
                vehicle.removeBox(b.getID());
                storage.addBox(b);
                addBoxesInventory(b, storage);
            }
        }
    }

    private void pickUpFromBuffer(Storage storage, String boxID, Vehicle vehicle) throws Exception {
        Box box;
        if(vehicle.getFreeSpace() > 0) {
            box = storage.removeBox(boxID);
            vehicle.addBox(box);
            removeBoxesInventory(box);
        }
        else System.out.println("Unable to get box from bufferpoint as vehicle is full!");
    }

    // Dropoff a box to a Buffer or Rack and load it onto a vehicle
    public void dropOffBox(int vehicleID, Storage storage, String boxID) throws Exception {
        Vehicle vehicle = vehicles[vehicleID];
        if(storage.getFreeSpace() > 0){
            Box box = vehicle.removeBox(boxID);
            storage.addBox(box);
            addBoxesInventory(box, storage);
        }
        else {
            System.out.println("Can't drop box off as storage is full");
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

    // Set up the inventory of the warehouse after initialisation
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

        System.out.println("TOTAL DRIVE TIME: " + totalDriveTime);
        System.out.println("TOTAL LOAD TIME: " + totalLoadTime);
        System.out.println("TOTAL RELOCATION TIME: " + relocationTime);
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
