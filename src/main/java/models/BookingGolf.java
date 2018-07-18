package models;

import java.util.List;

public class BookingGolf extends Product {

    private String supplierName;
    private String province;
    private String productName;
    private List<String> dates;
    private int quantity;
    private int rounds;
    private int carts;
    private Double costPricePerUnit;
    private Double sellPricePerUnit;
    private String addTo;
    private int supplierBooked;
    private Double amountPaidSup;

    public BookingGolf(String supplierName, String province, String productName, List<String> dates, int quantity, int rounds, int carts, Double costPricePerUnit, Double sellPricePerUnit, String addTo, int supplierBooked, Double amountPaidSup) {
        this.supplierName = supplierName;
        this.province = province;
        this.productName = productName;
        this.dates = dates;
        this.quantity = quantity;
        this.rounds = rounds;
        this.carts = carts;
        this.costPricePerUnit = costPricePerUnit;
        this.sellPricePerUnit = sellPricePerUnit;
        this.addTo = addTo;
        this.supplierBooked = supplierBooked;
        this.amountPaidSup = amountPaidSup;
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

    public int getCarts() {
        return carts;
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

    @Override
    public String toString(){
        if(carts == 1) {
            return supplierName + " - " + productName + " incl carts - Add To: " + addTo + " - Quantity:" + quantity + " - Rounds: " + rounds + " - Price per product: R" + sellPricePerUnit;
        } else {
            return supplierName + " - " + productName + " carts not included - Add To: " + addTo + " - Quantity:" + quantity + " - Rounds: " + rounds + " - Price per product: R" + sellPricePerUnit;
        }
    }
}
