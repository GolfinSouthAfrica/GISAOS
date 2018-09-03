package models;

import java.io.Serializable;

public class Transaction implements Serializable {

    private int ID;
    private String transactionType;
    private String gsNumber;
    private String other;
    private String reference;
    private Double amount;
    private String transactionDate;

    public Transaction(int ID, String transactionType, String gsNumber, String other, String reference, Double amount, String transactionDate) {
        this.ID = ID;
        this.transactionType = transactionType;
        this.gsNumber = gsNumber;
        this.other = other;
        this.reference = reference;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public int getID() {
        return ID;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getGsNumber() {
        return gsNumber;
    }

    public String getOther() {
        return other;
    }

    public String getReference() {
        return reference;
    }

    public Double getAmount() {
        return amount;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    @Override
    public String toString(){
        return transactionType + " (R" + amount + ") on " + transactionDate + " Reference: " + reference;
    }
}
