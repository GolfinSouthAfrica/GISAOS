package models;

import java.io.Serializable;

public class ContactDetails implements Serializable {

    private int contactDetailsID;
    private String personName;
    private String position;
    private String number;
    private String email;
    private String dateAdded;

    public ContactDetails(int contactDetailsID, String personName, String position, String number, String email, String dateAdded) {
        this.contactDetailsID = contactDetailsID;
        this.personName = personName;
        this.position = position;
        this.number = number;
        this.email = email;
        this.dateAdded = dateAdded;
    }

    public int getContactDetailsID() {
        return contactDetailsID;
    }

    public String getPersonName() {
        return personName;
    }

    public String getPosition() {
        return position;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public String getDateAdded() {
        return dateAdded;
    }
}
