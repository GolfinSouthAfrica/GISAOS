package models;

import java.io.Serializable;

public class BookingTransport implements Serializable {

    private int BRID;
    private int supplierNumber;
    private int transportId;
    private int quantity;
    private Double cost;

    public BookingTransport(int BRID, int supplierNumber, int transportId, int quantity, Double cost) {
        this.BRID = BRID;
        this.supplierNumber = supplierNumber;
        this.transportId = transportId;
        this.quantity = quantity;
        this.cost = cost;
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

    public Double getCost() {
        return cost;
    }
}
