public class TransportRequest {
    private int ID;
    private String boxID;
    private String pickupLocation;
    private String dropOffLocation;

    public TransportRequest(int ID, String boxId, String pickupLocation, String dropOffLocation) {
        this.ID = ID;
        this.boxID = boxId;
        this.pickupLocation = pickupLocation;
        this.dropOffLocation = dropOffLocation;
    }

    public String getBoxID() {
        return boxID;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getDropOffLocation() {
        return dropOffLocation;
    }

    
    public String toString(){
        return boxID + " from " + pickupLocation + " -> " + dropOffLocation;
    }
}
