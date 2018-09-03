package models;

import java.io.Serializable;
import java.util.List;

public class TripPackage implements Serializable {

    private int packageID;
    private String packageName;
    private double totalPackageAmount;
    private String category;
    private int golfersSharing;
    private int nongolfersSharing;
    private int golfersSingle;
    private int nongolfersSingle;
    private String province;
    private String expiryDate;
    private List<BookingAccommodation> bookingAccommodation;
    private List<BookingGolf> bookingGolf;
    private List<BookingTransport> bookingTransport;
    private List<BookingActivity> bookingActivities;

    public TripPackage() {

    }

    public TripPackage(int packageID, String packageName, double totalPackageAmount, String category, int golfersSharing, int nongolfersSharing, int golfersSingle, int nongolfersSingle, String province, String expiryDate, List<BookingAccommodation> bookingAccommodation, List<BookingGolf> bookingGolf, List<BookingTransport> bookingTransport, List<BookingActivity> bookingActivities) {
        this.packageID = packageID;
        this.packageName = packageName;
        this.totalPackageAmount = totalPackageAmount;
        this.category = category;
        this.golfersSharing = golfersSharing;
        this.nongolfersSharing = nongolfersSharing;
        this.golfersSingle = golfersSingle;
        this.nongolfersSingle = nongolfersSingle;
        this.province = province;
        this.expiryDate = expiryDate;
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

    public int getGolfersSharing() {
        return golfersSharing;
    }

    public int getNongolfersSharing() {
        return nongolfersSharing;
    }

    public int getGolfersSingle() {
        return golfersSingle;
    }

    public int getNongolfersSingle() {
        return nongolfersSingle;
    }

    public String getProvince() {
        return province;
    }

    public String getExpiryDate() {
        return expiryDate;
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

    public void setBookingAccommodation(List<BookingAccommodation> bookingAccommodation) {
        this.bookingAccommodation = bookingAccommodation;
    }

    public void setBookingGolf(List<BookingGolf> bookingGolf) {
        this.bookingGolf = bookingGolf;
    }

    public void setBookingTransport(List<BookingTransport> bookingTransport) {
        this.bookingTransport = bookingTransport;
    }

    public void setBookingActivities(List<BookingActivity> bookingActivities) {
        this.bookingActivities = bookingActivities;
    }

    public void setTotalPackageAmount(double totalPackageAmount) {
        this.totalPackageAmount = totalPackageAmount;
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
