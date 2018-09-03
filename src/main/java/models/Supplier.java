package models;

import java.io.Serializable;
import java.util.List;

public class Supplier implements Serializable {

    private int supplierNumber;
    private String supplierName;
    private String category;
    private String province;
    private String address;
    private List<ContactDetails> contactDetails;

    public Supplier(int supplierNumber, String supplierName, String category, String province, String address, List<ContactDetails> contactDetails) {
        this.supplierNumber = supplierNumber;
        this.supplierName = supplierName;
        this.category = category;
        this.province = province;
        this.address = address;
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

    public List<ContactDetails> getContactDetails() {
        return contactDetails;
    }

    public void addContactDetails (ContactDetails cd){
        contactDetails.add(cd);
    }

    public void setSupplierNumber(int supplierNumber) {
        this.supplierNumber = supplierNumber;
    }

    @Override
    public String toString(){
        return supplierName;
    }
}
