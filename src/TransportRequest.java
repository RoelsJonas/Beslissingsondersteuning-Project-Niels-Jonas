public class TransportRequest {
    private int boxId;
    private int pickupLocation;
    private int dropOffLocation;

    public TransportRequest(int boxId, int pickupLocation, int dropOffLocation) {
        this.boxId = boxId;
        this.pickupLocation = pickupLocation;
        this.dropOffLocation = dropOffLocation;
    }

    public int getBoxId() {
        return boxId;
    }

    public int getPickupLocation() {
        return pickupLocation;
    }

    public int getDropOffLocation() {
        return dropOffLocation;
    }

    
    public String toString(){
        return boxId + " from " + pickupLocation + " -> " + dropOffLocation;
    }
}
