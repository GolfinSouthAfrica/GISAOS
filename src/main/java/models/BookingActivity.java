package models;

import java.io.Serializable;

public class BookingActivity implements Serializable {

    private int BTID;
    private int supplierNumber;
    private int activityId;
    private int people;
    private Double cost;

    public BookingActivity(int BTID, int supplierNumber, int activityId, int people, Double cost) {
        this.BTID = BTID;
        this.supplierNumber = supplierNumber;
        this.activityId = activityId;
        this.people = people;
        this.cost = cost;
    }

    public int getBTID() {
        return BTID;
    }

    public int getSupplierNumber() {
        return supplierNumber;
    }

    public int getActivityId() {
        return activityId;
    }

    public int getPeople() {
        return people;
    }

    public Double getCost() {
        return cost;
    }
}
