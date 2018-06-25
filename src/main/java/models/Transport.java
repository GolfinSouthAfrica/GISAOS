package models;

import java.io.Serializable;

public class Transport implements Serializable {

    private int BRID;
    private int supplierNumber;
    private int transportId;
    private int quantity;

    public Transport(int BRID, int supplierNumber, int transportId, int quantity) {
        this.BRID = BRID;
        this.supplierNumber = supplierNumber;
        this.transportId = transportId;
        this.quantity = quantity;
    }

    public int getBRID() {
        return BRID;
    }

    public int getSupplierNumber() {
        return supplierNumber;
    }

    public int getTransportId() {
        return transportId;
    }

    public int getQuantity() {
        return quantity;
    }
}
