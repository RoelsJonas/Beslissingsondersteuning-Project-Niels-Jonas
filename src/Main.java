import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Stream;

import com.google.gson.*;

public class Main {

    static boolean DEBUG = false;

    public static void main(String[] args) throws Exception {
        // Read data from file
        String jsonFile = "I100_800_3_1_20b2";
//        String jsonFile = "I100_800_1_1_20b2";
//        String jsonFile = "I100_500_3_5_20";
//        String jsonFile = "I100_500_3_1_20b2";
//        String jsonFile = "I100_120_2_2_8b2";
//        String jsonFile = "I100_50_2_2_8b2";
//        String jsonFile = "I30_200_3_3_10";
//        String jsonFile = "I30_100_3_3_10";
//        String jsonFile = "I30_100_1_1_10";
//        String jsonFile = "I20_20_2_2_8b2";
//        String jsonFile = "I15_16_1_3";
//        String jsonFile = "I10_10_1";
//        String jsonFile = "I3_3_1_5";
//        String jsonFile = "I3_3_1";


        String filePath = "src/Data/" + jsonFile + ".json";
        String outputPath = "src/Solutions/logs_"+jsonFile+".txt";
        if(args.length > 0) filePath= args[0];
        if(args.length > 1) outputPath = args[1];
        if(args.length > 2) DEBUG = Boolean.parseBoolean(args[2]);


        JsonObject inputfile;

        try {
            inputfile = JsonParser.parseReader(new FileReader(filePath)).getAsJsonObject();
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

        Warehouse w = new Warehouse(vehicles.toArray(Vehicle[]::new), racks.toArray(Rack[]::new), transportRequests, buffers.toArray(Buffer[]::new), outputPath);

        if(DEBUG) System.out.println(w);

        long startTime= System.nanoTime();
        w.processAllRequests();
        if(DEBUG) System.out.println("Time: "  + (System.nanoTime() - startTime));
        if(DEBUG) w.validateWarehouse();
        if(DEBUG) w.validateWarehouse();
    }
}
