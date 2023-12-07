public class TransportRequest {
    private int ID;
    private String boxID;
    private Storage pickupLocation;
    private Storage dropOffLocation;

    public TransportRequest(int ID, String boxId, Storage pickupLocation, Storage dropOffLocation) {
        this.ID = ID;
        this.boxID = boxId;
        this.pickupLocation = pickupLocation;
        this.dropOffLocation = dropOffLocation;
    }

    public String getBoxID() {
        return boxID;
    }

    public Storage getPickupLocation() {
        return pickupLocation;
    }

    public Storage getDropOffLocation() {
        return dropOffLocation;
    }

    
    public String toString(){
        return boxID + " from " + pickupLocation.getName() + " -> " + dropOffLocation.getName();
    }
}
