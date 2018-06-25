package models;

import java.io.Serializable;

public class Supplier implements Serializable {

    private int supplierNumber;
    private String supplierName;
    private String category;
    private String province;
    private String address;
    private String coOrdinates;
    private String contactPerson;
    private String contactNumber;
    private String email;

    public Supplier(int supplierNumber, String supplierName, String category, String province, String address, String coOrdinates, String contactPerson, String contactNumber, String email) {
        this.supplierNumber = supplierNumber;
        this.supplierName = supplierName;
        this.category = category;
        this.province = province;
        this.address = address;
        this.coOrdinates = coOrdinates;
        this.contactPerson = contactPerson;
        this.contactNumber = contactNumber;
        this.email = email;
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

    public String getContactPerson() {
        return contactPerson;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString(){
        return supplierName;
    }
}
