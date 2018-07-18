package models;

public class ProductAccommodation extends Product {

    private String supplierName;
    private String category;
    private String province;
    private String productName;
    private String expiryDate;
    private int sleeps;
    private String price;
    private String commission;

    public ProductAccommodation(String supplierName, String category, String province, String productName, String expiryDate, int sleeps, String price, String commission) {
        this.supplierName = supplierName;
        this.category = category;
        this.province = province;
        this.productName = productName;
        this.expiryDate = expiryDate;
        this.sleeps = sleeps;
        this.price = price;
        this.commission = commission;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getCategory() {
        return category;
    }

    public String getProvince() {
        return province;
    }

    public String getProductName() {
        return productName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public int getSleeps() {
        return sleeps;
    }

    public String getPrice() {
        return price;
    }

    public String getCommission() {
        return commission;
    }

    @Override
    public String toString(){
        return productName + " - Sleeps: " + sleeps + " - R" + price + " - Com: " + commission + "% - Expire: " + expiryDate;
    }
}