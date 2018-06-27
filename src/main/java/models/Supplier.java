package models;

import java.io.Serializable;
import java.util.List;

public class Supplier implements Serializable {

    private int supplierNumber;
    private String supplierName;
    private String category;
    private String province;
    private String address;
    private String coOrdinates;
    private List<Product> products;
    private List<ContactDetails> contactDetails;

    public Supplier(int supplierNumber, String supplierName, String category, String province, String address, String coOrdinates, List<Product> products, List<ContactDetails> contactDetails) {
        this.supplierNumber = supplierNumber;
        this.supplierName = supplierName;
        this.category = category;
        this.province = province;
        this.address = address;
        this.coOrdinates = coOrdinates;
        this.products = products;
        this.contactDetails = contactDetails;
    }

    public int getSupplierNumber() {
        return supplierNumber;
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

    public String getAddress() {
        return address;
    }

    public String getCoOrdinates() {
        return coOrdinates;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<ContactDetails> getContactDetails() {
        return contactDetails;
    }

    @Override
    public String toString(){
        return supplierName;
    }
}
