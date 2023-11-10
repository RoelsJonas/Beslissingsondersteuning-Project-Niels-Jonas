import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;

import com.google.gson.*;

public class Main {

    public static void main(String[] args) throws Exception {

        // Read data from file
        String jsonFile = "src/Data/I15_16_1_3.json";
        JsonObject inputfile;
        try {
            inputfile = JsonParser.parseReader(new FileReader(jsonFile)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Read const data 
        int loadingduration = inputfile.get("loadingduration").getAsInt();
        int vehiclespeed =inputfile.get("vehiclespeed").getAsInt();
        int stackcapacity = inputfile.get("stackcapacity").getAsInt();

        // Read racks/stacks data
        JsonArray racksObj = inputfile.getAsJsonArray("stacks");
        Rack[] racks = new Rack[racksObj.size()];
        int rackIndex = 0;
        for(JsonElement jr: racksObj) {
            JsonObject obj = jr.getAsJsonObject();

            int id = obj.get("ID").getAsInt();
            String name = obj.get("name").getAsString();
            int x = obj.get("x").getAsInt();
            int y = obj.get("y").getAsInt();

            JsonArray boxesArray = obj.getAsJsonArray("boxes");
            Stack<Box> boxes = new Stack<>();
            for (JsonElement boxElement : boxesArray) {
                String boxId = boxElement.getAsString();
                Box newBox = new Box(boxId);
                boxes.add(newBox);
            }

            Rack rack = new Rack(id, name, x, y, stackcapacity);
            rack.addBoxes(boxes);
            racks[rackIndex++] = rack;
        }

        // Read bufferpoints data
        JsonArray buffersObj = inputfile.getAsJsonArray("bufferpoints");
        Buffer[] buffers = new Buffer[buffersObj.size()];
        int bufferIndex = 0;
        for(JsonElement jr: buffersObj) {
            JsonObject obj = jr.getAsJsonObject();

            int id = obj.get("ID").getAsInt();
            String name = obj.get("name").getAsString();
            int x = obj.get("x").getAsInt();
            int y = obj.get("y").getAsInt();

            Buffer buffer = new Buffer(id, name, x, y);
            buffers[bufferIndex++] = buffer;
        }

        // Read vehicles data
        JsonArray vehiclesObj = inputfile.getAsJsonArray("vehicles");
        Vehicle[] vehicles = new Vehicle[vehiclesObj.size()];
        int vehicleIndex = 0;
        for(JsonElement jr: vehiclesObj) {
            JsonObject obj = jr.getAsJsonObject();


            int id = obj.get("ID").getAsInt();
            String name = obj.get("name").getAsString();
            int capacity = obj.get("capacity").getAsInt();
            int x = obj.get("xCoordinate").getAsInt();
            int y = obj.get("yCoordinate").getAsInt();

            Vehicle vehicle = new Vehicle(id, name, vehiclespeed, loadingduration, capacity, x, y);
            vehicles[vehicleIndex++] = vehicle;
        }

        // Get all storages in one array
        Storage[] storages = Stream.concat(Arrays.stream(buffers), Arrays.stream(racks)).toArray(Storage[]::new);

        // Read transportrequests data
        JsonArray transportRequestsObj = inputfile.getAsJsonArray("requests");
        HashSet<TransportRequest> transportRequests = new HashSet<>(); 
        for(JsonElement jr: transportRequestsObj) {
            JsonObject obj = jr.getAsJsonObject();

            int id = obj.get("ID").getAsInt();
            String boxID = obj.get("boxID").getAsString();
            String pickupLocation = obj.getAsJsonArray("pickupLocation").get(0).getAsString();
            String dropLocation =  obj.getAsJsonArray("placeLocation").get(0).getAsString();

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

        Warehouse w = new Warehouse(vehicles, racks, transportRequests, buffers, stackcapacity, vehiclespeed, loadingduration, storages);

        System.out.println(w);

        w.processAllRequests();
    }
}
