package models;

import java.io.Serializable;

public class Activity implements Serializable {

    private int BTID;
    private int supplierNumber;
    private int activityId;
    private int people;

    public Activity(int BTID, int supplierNumber, int activityId, int people) {
        this.BTID = BTID;
        this.supplierNumber = supplierNumber;
        this.activityId = activityId;
        this.people = people;
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
}
