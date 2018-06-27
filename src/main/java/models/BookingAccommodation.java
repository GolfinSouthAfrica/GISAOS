package models;

import java.io.Serializable;

public class BookingAccommodation implements Serializable {

    private int BAID;
    private int supplierNumber;
    private int accommodationId;
    private int nights;
    private int people; //TODO get and auto cal if house or per person
    private Double cost;

    public BookingAccommodation(int BAID, int supplierNumber, int accommodationId, int nights, int people, Double cost) {
        this.BAID = BAID;
        this.supplierNumber = supplierNumber;
        this.accommodationId = accommodationId;
        this.nights = nights;
        this.people = people;
        this.cost = cost;
    }

    public int getBAID() {
        return BAID;
    }

    public int getSupplierNumber() {
        return supplierNumber;
    }

    public int getAccommodationId() {
        return accommodationId;
    }

    public int getNights() {
        return nights;
    }

    public int getPeople() {
        return people;
    }

    public Double getCost() {
        return cost;
    }
}
