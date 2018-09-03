package models;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ProductAccommodation extends Product {

    private String supplierName;
    private String category;
    private String province;
    private String productName;
    private int sleeps;
    private String commission;
    private List<String[]>prices = new ArrayList<>();
    private String dateSelected = "";

    public ProductAccommodation(String supplierName, String category, String province, String productName, int sleeps, String commission, List<String[]> prices) {
        this.supplierName = supplierName;
        this.category = category;
        this.province = province;
        this.productName = productName;
        this.sleeps = sleeps;
        this.commission = commission;
        this.prices = prices;
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

    public int getSleeps() {
        return sleeps;
    }

    public String getCommission() {
        return commission;
    }

    public List<String[]> getPrices() {
        return prices;
    }

    public String getPrice(){
        if (prices!=null){
            try {
                for (String[] p : prices) {
                    if ((LocalDate.parse(p[0]).isBefore(LocalDate.parse(dateSelected)) || LocalDate.parse(p[0]).isEqual(LocalDate.parse(dateSelected))) && (LocalDate.parse(p[1]).isAfter(LocalDate.parse(dateSelected)) || LocalDate.parse(p[1]).isEqual(LocalDate.parse(dateSelected)))) {
                        return p[2];
                    }
                }
            } catch (DateTimeParseException ex) {

            }
        }
        return productName + " - This product is not valid in the selected time frame";
    }

    public void setDateSelected(String dateSelected) {
        this.dateSelected = dateSelected;
    }

    @Override
    public String toString(){
        if(dateSelected.matches("All")){
            return productName + " - Sleeps: " + sleeps;
        } else if (prices!=null){
            try {
                for (String[] p : prices) {
                    if ((LocalDate.parse(p[0]).isBefore(LocalDate.parse(dateSelected)) || LocalDate.parse(p[0]).isEqual(LocalDate.parse(dateSelected))) && (LocalDate.parse(p[1]).isAfter(LocalDate.parse(dateSelected)) || LocalDate.parse(p[1]).isEqual(LocalDate.parse(dateSelected)))) {
                        return productName + " - Sleeps: " + sleeps + " - STO R" + p[2] + " - Com: " + commission + "%";
                    }
                }
            } catch (DateTimeParseException ex) {

            }
        }
        return productName + " - This product is not valid in the selected time frame";
    }
}
