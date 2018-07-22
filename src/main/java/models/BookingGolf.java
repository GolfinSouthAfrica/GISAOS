package models;

import java.util.List;

public class BookingGolf extends Product {

    private int ID;
    private String supplierName;
    private String province;
    private String productName;
    private List<String> dates;
    private int quantity;
    private int rounds;
    private Double costPricePerUnit;
    private Double sellPricePerUnit;
    private String addTo;
    private int supplierBooked;
    private Double amountPaidSup;

    public BookingGolf(int ID, String supplierName, String province, String productName, List<String> dates, int quantity, int rounds, Double costPricePerUnit, Double sellPricePerUnit, String addTo, int supplierBooked, Double amountPaidSup) {
        this.ID = ID;
        this.supplierName = supplierName;
        this.province = province;
        this.productName = productName;
        this.dates = dates;
        this.quantity = quantity;
        this.rounds = rounds;
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

    public List<String> getDates() {
        return dates;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getRounds() {
        return rounds;
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
        return supplierName + " - " + productName + " - Add To: " + addTo + " - Quantity:" + quantity + " - Rounds: " + rounds + " - Price per product: R" + sellPricePerUnit;
    }
}
