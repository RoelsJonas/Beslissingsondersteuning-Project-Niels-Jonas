import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;

import com.google.gson.*;

import javax.lang.model.type.ArrayType;

public class Main {

    static boolean debug = false;

    public static void main(String[] args) throws Exception {

        // Read data from file
        String jsonFile = "I100_120_2_2_8b2";
//        String jsonFile = "I100_50_2_2_8b2";
//        String jsonFile = "I30_200_3_3_10";
//        String jsonFile = "I30_100_3_3_10";
//        String jsonFile = "I30_100_1_1_10";
//        String jsonFile = "I20_20_2_2_8b2";
//        String jsonFile = "I3_3_1_5";
        JsonObject inputfile;
        try {
            inputfile = JsonParser.parseReader(new FileReader("src/Data/" + jsonFile + ".json")).getAsJsonObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Read const data 
        int loadingduration = inputfile.get("loadingduration").getAsInt();
        int vehiclespeed =inputfile.get("vehiclespeed").getAsInt();
        int stackcapacity = inputfile.get("stackcapacity").getAsInt();

        // Read racks/stacks data
        JsonArray racksObj = inputfile.getAsJsonArray("stacks");
        ArrayList<Rack> racks = new ArrayList<>();
        int rackIndex = 0;
        for(JsonElement jr: racksObj) {
            if(jr instanceof JsonNull) break;
            JsonObject obj = jr.getAsJsonObject();

            int id = obj.get("ID").getAsInt();
            String name = obj.get("name").getAsString();
            int x = obj.get("x").getAsInt();
            int y = obj.get("y").getAsInt();

            JsonArray boxesArray = obj.getAsJsonArray("boxes");
            Stack<Box> boxes = new Stack<>();
            for (JsonElement boxElement : boxesArray) {
                if(boxElement instanceof JsonNull) break;
                String boxId = boxElement.getAsString();
                Box newBox = new Box(boxId);
                boxes.add(newBox);
            }

            Rack rack = new Rack(id, name, x, y, stackcapacity);
            rack.addBoxes(boxes);
            racks.add(rack);
        }

        // Read bufferpoints data
        JsonArray buffersObj = inputfile.getAsJsonArray("bufferpoints");
        ArrayList<Buffer> buffers = new ArrayList<>();
        int bufferIndex = 0;
        for(JsonElement jr: buffersObj) {
            if(jr instanceof JsonNull) break;
            JsonObject obj = jr.getAsJsonObject();

            int id = obj.get("ID").getAsInt();
            String name = obj.get("name").getAsString();
            int x = obj.get("x").getAsInt();
            int y = obj.get("y").getAsInt();

            Buffer buffer = new Buffer(id, name, x, y);
            buffers.add(buffer);
        }

        // Read vehicles data
        JsonArray vehiclesObj = inputfile.getAsJsonArray("vehicles");
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        int vehicleIndex = 0;
        for(JsonElement jr: vehiclesObj) {
            if(jr instanceof JsonNull) break;
            JsonObject obj = jr.getAsJsonObject();


            int id = obj.get("ID").getAsInt();
            String name = obj.get("name").getAsString();
            int capacity = obj.get("capacity").getAsInt();
            int x = obj.get("x").getAsInt();
            int y = obj.get("y").getAsInt();
            Vehicle vehicle = new Vehicle(id, name, vehiclespeed, loadingduration, capacity, x, y);
            vehicles.add(vehicle);
        }

        // Get all storages in one array
        Storage[] storages = Stream.concat(Arrays.stream(buffers.toArray()), Arrays.stream(racks.toArray())).toArray(Storage[]::new);

        // Read transportrequests data
        JsonArray transportRequestsObj = inputfile.getAsJsonArray("requests");
        ArrayList<TransportRequest> transportRequests = new ArrayList<>();
        for(JsonElement jr: transportRequestsObj) {
            if(jr instanceof JsonNull) break;
            JsonObject obj = jr.getAsJsonObject();

            int id = obj.get("ID").getAsInt();
            String boxID = obj.get("boxID").getAsString();
            String pickupLocation = obj.get("pickupLocation").getAsString();
            String dropLocation =  obj.get("placeLocation").getAsString();

            Storage pickupStorage = null;
            Storage dropStorage = null;
            for(Storage s : storages){
                if(s.getName().equals(pickupLocation)) pickupStorage = s;
                if(s.getName().equals(dropLocation)) dropStorage = s;
            }

            if(pickupStorage == null || dropStorage == null) System.err.println("Pickup or drop storage not found!");

            TransportRequest transportRequest = new TransportRequest(id, boxID, pickupStorage, dropStorage);
            transportRequests.add(transportRequest);
        }

        Warehouse w = new Warehouse(vehicles.toArray(Vehicle[]::new), racks.toArray(Rack[]::new), transportRequests, buffers.toArray(Buffer[]::new), stackcapacity, vehiclespeed, loadingduration, storages, jsonFile);

        if(debug){
            System.out.println(w);
        }

        w.processAllRequests();
        w.validateWarehouse();
    }
}
