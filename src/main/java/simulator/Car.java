package simulator;

import roadNetwork.Path;
import roadNetwork.RoadNode;

public class Car {

    private RoadNode location;
    private RoadNode nextLocation;
    private Path path;
    private long residualWeight;  //the residual distance from next location
    private int carStatus;

    public Car(RoadNode location){
        this.location = location;
        this.nextLocation = location;
        this.path = null;
        this.residualWeight = 0;
        this.carStatus = 0;
    }

    public RoadNode getLocation(){
        return this.location;
    }

    public void setLocation(RoadNode location){
        this.location = location;
    }

    public RoadNode getNextLocation(){
        return nextLocation;
    }

    public void setNextLocation(RoadNode nextLocation){
        this.nextLocation = nextLocation;
    }

    public Path getPath(){
        return this.path;
    }

    public void setPath(Path path){
        this.path = path;
    }

    public long getResidualWeight(){
        return this.residualWeight;
    }

    public void setResidualWeight(long residualWeight){
        this.residualWeight = residualWeight;
    }

    public int getCarStatus(){
        return this.carStatus;
    }

    public void setCarStatus(int carStatus){
        this.carStatus = carStatus;
    }
}