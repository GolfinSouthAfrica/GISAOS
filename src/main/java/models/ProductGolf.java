package models;

public class ProductGolf extends Product {

    private String supplierName;
    private String category;
    private String province;
    private String productName;
    private String expiryDate;
    private String price;
    private String commission;
    private int carts;

    public ProductGolf(String supplierName, String category, String province, String productName, String expiryDate, String price, String commission, int carts) {
        this.supplierName = supplierName;
        this.category = category;
        this.province = province;
        this.productName = productName;
        this.expiryDate = expiryDate;
        this.price = price;
        this.commission = commission;
        this.carts = carts;
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

    public int getCarts() {
        return carts;
    }

    @Override
    public String toString(){
        if(carts == 1) {
            return productName + " incl carts - R" + price + " - Com:" + commission + "% - Expire: " + expiryDate;
        } else {
            return productName + " carts not included - R" + price + " - Com:" + commission + "% - Expire: " + expiryDate;
        }
    }
}
