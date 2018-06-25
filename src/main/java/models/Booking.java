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
    private int packageID;
    private int packageQuantity;
    private List<Accommodation> accommodation;
    private List<Golf> golf;
    private List<Activity> activities;
    private List<Transport> transport;

    public Booking(String gsNumber, String clientName, String contactNumber, String email, String people, String arrival, String departure, String process, String bookingAmount, String consultant, String depositDate, int depositPaid, int fullPaid, String bookingMadeDate, int packageID, int packageQuantity, List<Accommodation> accommodation, List<Golf> golf, List<Activity> activities, List<Transport> transport) {
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
        this.packageID = packageID;
        this.packageQuantity = packageQuantity;
        this.accommodation = accommodation;
        this.golf = golf;
        this.activities = activities;
        this.transport = transport;
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

    public int getPackageID() {
        return packageID;
    }

    public int getPackageQuantity() {
        return packageQuantity;
    }

    public List<Accommodation> getAccommodation() {
        return accommodation;
    }

    public List<Golf> getGolf() {
        return golf;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public List<Transport> getTransport() {
        return transport;
    }
}

