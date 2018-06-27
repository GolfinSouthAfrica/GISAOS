package models;

import java.io.Serializable;
import java.util.List;

public class Booking implements Serializable {

    private String gsNumber;
    private String clientName;
    private String contactNumber;
    private String email;
    private String people;
    private String arrival;
    private String departure;
    private String process;
    private String bookingAmount;
    private String consultant;
    private String depositDate;
    private int depositPaid;
    private int fullPaid;
    private String bookingMadeDate;
    private String notes;
    private List<BookingAccommodation> bookingAccommodation;
    private List<BookingGolf> bookingGolf;
    private List<BookingActivity> activities;
    private List<BookingTransport> bookingTransport;

    public Booking(String gsNumber, String clientName, String contactNumber, String email, String people, String arrival, String departure, String process, String bookingAmount, String consultant, String depositDate, int depositPaid, int fullPaid, String bookingMadeDate, String notes, List<BookingAccommodation> bookingAccommodation, List<BookingGolf> bookingGolf, List<BookingActivity> activities, List<BookingTransport> bookingTransport) {
        this.gsNumber = gsNumber;
        this.clientName = clientName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.people = people;
        this.arrival = arrival;
        this.departure = departure;
        this.process = process;
        this.bookingAmount = bookingAmount;
        this.consultant = consultant;
        this.depositDate = depositDate;
        this.depositPaid = depositPaid;
        this.fullPaid = fullPaid;
        this.bookingMadeDate = bookingMadeDate;
        this.notes = notes;
        this.bookingAccommodation = bookingAccommodation;
        this.bookingGolf = bookingGolf;
        this.activities = activities;
        this.bookingTransport = bookingTransport;
    }

    public String getGsNumber() {
        return gsNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPeople() {
        return people;
    }

    public String getArrival() {
        return arrival;
    }

    public String getDeparture() {
        return departure;
    }

    public String getProcess() {
        return process;
    }

    public String getBookingAmount() {
        return bookingAmount;
    }

    public String getConsultant() {
        return consultant;
    }

    public String getDepositDate() {
        return depositDate;
    }

    public int getDepositPaid() {
        return depositPaid;
    }

    public int getFullPaid() {
        return fullPaid;
    }

    public String getBookingMadeDate() {
        return bookingMadeDate;
    }

    public String getNotes() {
        return notes;
    }

    public List<BookingAccommodation> getBookingAccommodation() {
        return bookingAccommodation;
    }

    public List<BookingGolf> getBookingGolf() {
        return bookingGolf;
    }

    public List<BookingActivity> getActivities() {
        return activities;
    }

    public List<BookingTransport> getBookingTransport() {
        return bookingTransport;
    }
}

