package models;

import java.io.Serializable;
import java.util.List;

public class Booking implements Serializable {

    private String gsNumber;
    private String clientName;
    private String contactNumber;
    private String email;
    private int golfersSharing;
    private double golferSharingPrice;
    private int nongolfersSharing;
    private double nonGolferSharingPrice;
    private int golfersSingle;
    private double golferSinglePrice;
    private int nongolfersSingle;
    private double nonGolferSinglePrice;
    private String arrival;
    private String departure;
    private String process;
    private double bookingAmount;
    private String consultant;
    private String depositDate;
    private int depositPaid;
    private int fullPaid;
    private String packageName;
    private String bookingMadeDate;
    private String notes;
    private List<BookingAccommodation> bookingAccommodation;
    private List<BookingGolf> bookingGolf;
    private List<BookingActivity> bookingActivities;
    private List<BookingTransport> bookingTransport;
    private List<Transaction> transactions;

    public Booking(String gsNumber, String clientName, String contactNumber, String email, int golfersSharing, double golferSharingPrice, int nongolfersSharing, double nonGolferSharingPrice, int golfersSingle, double golferSinglePrice, int nongolfersSingle, double nonGolferSinglePrice, String arrival, String departure, String process, double bookingAmount, String consultant, String depositDate, int depositPaid, int fullPaid, String packageName, String bookingMadeDate, String notes, List<BookingAccommodation> bookingAccommodation, List<BookingGolf> bookingGolf, List<BookingActivity> bookingActivities, List<BookingTransport> bookingTransport, List<Transaction> transactions) {
        this.gsNumber = gsNumber;
        this.clientName = clientName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.golfersSharing = golfersSharing;
        this.golferSharingPrice = golferSharingPrice;
        this.nongolfersSharing = nongolfersSharing;
        this.nonGolferSharingPrice = nonGolferSharingPrice;
        this.golfersSingle = golfersSingle;
        this.golferSinglePrice = golferSinglePrice;
        this.nongolfersSingle = nongolfersSingle;
        this.nonGolferSinglePrice = nonGolferSinglePrice;
        this.arrival = arrival;
        this.departure = departure;
        this.process = process;
        this.bookingAmount = bookingAmount;
        this.consultant = consultant;
        this.depositDate = depositDate;
        this.depositPaid = depositPaid;
        this.fullPaid = fullPaid;
        this.packageName = packageName;
        this.bookingMadeDate = bookingMadeDate;
        this.notes = notes;
        this.bookingAccommodation = bookingAccommodation;
        this.bookingGolf = bookingGolf;
        this.bookingActivities = bookingActivities;
        this.bookingTransport = bookingTransport;
        this.transactions = transactions;
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

    public int getGolfersSharing() {
        return golfersSharing;
    }

    public double getGolferSharingPrice() {
        return golferSharingPrice;
    }

    public int getNongolfersSharing() {
        return nongolfersSharing;
    }

    public double getNonGolferSharingPrice() {
        return nonGolferSharingPrice;
    }

    public int getGolfersSingle() {
        return golfersSingle;
    }

    public double getGolferSinglePrice() {
        return golferSinglePrice;
    }

    public int getNongolfersSingle() {
        return nongolfersSingle;
    }

    public double getNonGolferSinglePrice() {
        return nonGolferSinglePrice;
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

    public double getBookingAmount() {
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

    public String getPackageName() {
        return packageName;
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

    public List<BookingActivity> getBookingActivities() {
        return bookingActivities;
    }

    public List<BookingTransport> getBookingTransport() {
        return bookingTransport;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setGsNumber(String gsNumber) {
        this.gsNumber = gsNumber;
    }

    public void setProcess(String process) {
        this.process = process;
    }
}

