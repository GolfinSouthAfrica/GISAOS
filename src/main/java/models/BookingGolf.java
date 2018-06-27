package models;

import java.io.Serializable;

public class BookingGolf implements Serializable {

    private int BGID;
    private int supplierNumber;
    private int golfID;
    private int people;
    private int rounds;
    private int carts;
    private Double cost;

    public BookingGolf(int BGID, int supplierNumber, int golfID, int people, int rounds, int carts, Double cost) {
        this.BGID = BGID;
        this.supplierNumber = supplierNumber;
        this.golfID = golfID;
        this.people = people;
        this.rounds = rounds;
        this.carts = carts;
        this.cost = cost;
    }

    public int getBGID() {
        return BGID;
    }

    public int getSupplierNumber() {
        return supplierNumber;
    }

    public int getGolfID() {
        return golfID;
    }

    public int getPeople() {
        return people;
    }

    public int getRounds() {
        return rounds;
    }

    public int getCarts() {
        return carts;
    }

    public Double getCost() {
        return cost;
    }
}
