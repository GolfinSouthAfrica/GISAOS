package models;

import java.io.Serializable;
import java.util.List;

public class TripPackage implements Serializable {

    private int packageID;
    private String packageName;
    private double totalPackageAmount;
    private String category;
    private String people;
    private String province;
    private String expiryDate;
    private String extra;
    private List<BookingAccommodation> bookingAccommodation;
    private List<BookingGolf> bookingGolf;
    private List<BookingTransport> bookingTransport;
    private List<BookingActivity> bookingActivities;

    public TripPackage(int packageID, String packageName, double totalPackageAmount, String category, String people, String province, String expiryDate, String extra, List<BookingAccommodation> bookingAccommodation, List<BookingGolf> bookingGolf, List<BookingTransport> bookingTransport, List<BookingActivity> bookingActivities) {
        this.packageID = packageID;
        this.packageName = packageName;
        this.totalPackageAmount = totalPackageAmount;
        this.category = category;
        this.people = people;
        this.province = province;
        this.expiryDate = expiryDate;
        this.extra = extra;
        this.bookingAccommodation = bookingAccommodation;
        this.bookingGolf = bookingGolf;
        this.bookingTransport = bookingTransport;
        this.bookingActivities = bookingActivities;
    }

    public int getPackageID() {
        return packageID;
    }

    public String getPackageName() {
        return packageName;
    }

    public double getTotalPackageAmount() {
        return totalPackageAmount;
    }

    public String getCategory() {
        return category;
    }

    public String getPeople() {
        return people;
    }

    public String getProvince() {
        return province;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getExtra() {
        return extra;
    }

    public List<BookingAccommodation> getBookingAccommodation() {
        return bookingAccommodation;
    }

    public List<BookingGolf> getBookingGolf() {
        return bookingGolf;
    }

    public List<BookingTransport> getBookingTransport() {
        return bookingTransport;
    }

    public List<BookingActivity> getBookingActivities() {
        return bookingActivities;
    }

    @Override
    public String toString(){
        if(!packageName.matches("Bespoke")) {
            return packageName + " - " + category + " - " + province + " - R" + totalPackageAmount + " - Expiring:" + expiryDate;
        } else {
            return packageName + " Package";
        }
    }
}
