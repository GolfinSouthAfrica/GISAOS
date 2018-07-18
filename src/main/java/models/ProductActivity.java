package models;

public class ProductActivity extends Product {

    private String supplierName;
    private String category;
    private String province;
    private String productName;
    private String expiryDate;
    private String price;
    private String commission;

    public ProductActivity(String supplierName, String category, String province, String productName, String expiryDate, String price, String commission) {
        this.supplierName = supplierName;
        this.category = category;
        this.province = province;
        this.productName = productName;
        this.expiryDate = expiryDate;
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

    public String getPrice() {
        return price;
    }

    public String getCommission() {
        return commission;
    }

    @Override
    public String toString(){
        return productName + " - R" + price + " - Com:" + commission + "% - Expire: " + expiryDate;
    }
}
