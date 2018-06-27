package models;

public class ProductTransport extends Product {

    private String supplierName;
    private String category;
    private String province;
    private String productName;
    private String expiryDate;
    private int totalAmountPeople;
    private String price;

    public ProductTransport(String supplierName, String category, String province, String productName, String expiryDate, int totalAmountPeople, String price) {
        this.supplierName = supplierName;
        this.category = category;
        this.province = province;
        this.productName = productName;
        this.expiryDate = expiryDate;
        this.totalAmountPeople = totalAmountPeople;
        this.price = price;
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

    public int getTotalAmountPeople() {
        return totalAmountPeople;
    }

    public String getPrice() {
        return price;
    }
}
