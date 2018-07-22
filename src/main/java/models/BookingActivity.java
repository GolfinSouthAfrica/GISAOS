package models;

public class BookingActivity extends Product {

    private int ID;
    private String supplierName;
    private String province;
    private String productName;
    private String date;
    private int quantity;
    private Double costPricePerUnit;
    private Double sellPricePerUnit;
    private String addTo;
    private int supplierBooked;
    private Double amountPaidSup;

    public BookingActivity(int ID, String supplierName, String province, String productName, String date, int quantity, Double costPricePerUnit, Double sellPricePerUnit, String addTo, int supplierBooked, Double amountPaidSup) {
        this.ID = ID;
        this.supplierName = supplierName;
        this.province = province;
        this.productName = productName;
        this.date = date;
        this.quantity = quantity;
        this.costPricePerUnit = costPricePerUnit;
        this.sellPricePerUnit = sellPricePerUnit;
        this.addTo = addTo;
        this.supplierBooked = supplierBooked;
        this.amountPaidSup = amountPaidSup;
    }

    public int getID() {
        return ID;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getProvince() {
        return province;
    }

    public String getProductName() {
        return productName;
    }

    public String getDate() {
        return date;
    }

    public int getQuantity() {
        return quantity;
    }

    public Double getCostPricePerUnit() {
        return costPricePerUnit;
    }

    public Double getSellPricePerUnit() {
        return sellPricePerUnit;
    }

    public String getAddTo() {
        return addTo;
    }

    public int getSupplierBooked() {
        return supplierBooked;
    }

    public Double getAmountPaidSup() {
        return amountPaidSup;
    }

    public void setSupplierBooked(int supplierBooked) {
        this.supplierBooked = supplierBooked;
    }

    @Override
    public String toString(){
        return supplierName + " - " + productName + " Add To: " + addTo + " - Quantity: " + quantity + " - Price per person: " + sellPricePerUnit;
    }
}
