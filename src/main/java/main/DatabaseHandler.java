package main;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import models.*;
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.lang.Boolean;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class DatabaseHandler {

    private Connection con;

    DatabaseHandler() {
        connectDB();
    }

    //<editor-fold desc="Database Connection">
    private void connectDB() {
        try {
            Boolean createDatabase = false;
            if (!Server.DATABASE_FILE.exists()) {
                createDatabase = true;
            }
            con = DriverManager.getConnection("jdbc:sqlite:" + Server.DATABASE_FILE.getAbsolutePath());
            if (createDatabase) {
                Statement stmt = con.createStatement();
                stmt.execute("CREATE TABLE USER (" +
                        "Username TEXT PRIMARY KEY, " +
                        "FirstName TEXT, " +
                        "LastName TEXT, " +
                        "Password TEXT, " +
                        "Email TEXT, " +
                        "ContactNumber TEXT);");
                stmt.execute("CREATE TABLE BOOKINGS (" +
                        "GSNumber INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "ClientName TEXT, " +
                        "ContactNumber TEXT, " +
                        "Email TEXT, " +
                        "GolfersSharing INTEGER , " +
                        "GolfersSharingPrice REAL , " +
                        "NonGolfersSharing INTEGER , " +
                        "NonGolfersSharingPrice REAL , " +
                        "GolfersSingle INTEGER , " +
                        "GolfersSinglePrice REAL , " +
                        "NonGolfersSingle INTEGER , " +
                        "NonGolfersSinglePrice REAL , " +
                        "Arrival TEXT, " +
                        "Departure TEXT, " +
                        "Process TEXT, " +
                        "BookingAmount REAL, " +
                        "Consultant TEXT, " +
                        "DepositDate TEXT, " +
                        "DepositPaid INTEGER, " +
                        "FullPaid INTEGER, " +
                        "PackageName TEXT, " +
                        "BookingMadeDate INTEGER," +
                        "Notes TEXT);");
                stmt.execute("CREATE TABLE BOOKINGSACCOMMODATION (" +
                        "BAID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "GSNumber INTEGER, " +
                        "SupplierName TEXT, " +
                        "Province TEXT," +
                        "ProductName TEXT, " +
                        "MaxSleep INTEGER, " +
                        "Arrival TEXT, " +
                        "Nights INTEGER, " +
                        "Quantity INTEGER, " +
                        "CostPricePerUnit REAL," +
                        "SellPricePerUnit REAL," +
                        "SupplierBooked INTEGER," +
                        "AmountPaid REAL," +
                        "AddTo TEXT);");
                stmt.execute("CREATE TABLE BOOKINGSGOLF (" +
                        "BGID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "GSNumber INTEGER, " +
                        "SupplierName TEXT, " +
                        "Province TEXT," +
                        "ProductName TEXT, " +
                        "Dates TEXT, " +
                        "Quantity INTEGER, " +
                        "Rounds INTEGER, " +
                        "CostPricePerUnit REAL, " +
                        "SellPricePerUnit REAL, " +
                        "SupplierBooked INTEGER, " +
                        "AmountPaid REAL, " +
                        "AddTo TEXT);");
                stmt.execute("CREATE TABLE BOOKINGSACTIVITIES (" +
                        "BTID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "GSNumber INTEGER, " +
                        "SupplierName TEXT, " +
                        "Province TEXT," +
                        "ProductName TEXT, " +
                        "ActivityDate TEXT, " +
                        "Quantity INTEGER, " +
                        "CostPricePerUnit REAL," +
                        "SellPricePerUnit REAL," +
                        "SupplierBooked INTEGER," +
                        "AmountPaid REAL," +
                        "AddTo TEXT);");
                stmt.execute("CREATE TABLE BOOKINGSTRANSPORT (" +
                        "BRID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "GSNumber INTEGER, " +
                        "SupplierName TEXT, " +
                        "Province TEXT," +
                        "ProductName TEXT, " +
                        "TravelDate TEXT, " +
                        "TravelFrom TEXT, " +
                        "TravelTo TEXT, " +
                        "Quantity INTEGER," +
                        "CostPricePerUnit REAL," +
                        "SellPricePerUnit REAL," +
                        "SupplierBooked INTEGER," +
                        "AmountPaid REAL," +
                        "AddTo TEXT);");
                stmt.execute("CREATE TABLE SUPPLIERS (" +
                        "SupplierNumber INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "SupplierName TEXT, " +
                        "Category TEXT, " +
                        "Province TEXT, " +
                        "Address TEXT);");
                stmt.execute("CREATE TABLE CONTACTDETAILS (" +
                        "ContactDetailsID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "SupplierNumber INTEGER," +
                        "PersonName TEXT," +
                        "PersonPosition TEXT," +
                        "ContactNumber TEXT," +
                        "Email TEXT," +
                        "DateAdded TEXT);");
                stmt.execute("CREATE TABLE PACKAGES (" +
                        "PackageID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "PackageName TEXT, " +
                        "Category TEXT, " +//Ex short break exc.
                        "GolfersSharing INTEGER , " +
                        "NonGolfersSharing INTEGER , " +
                        "GolfersSingle INTEGER , " +
                        "NonGolfersSingle INTEGER , " +
                        "TotalPackageAmount REAL , " +
                        "ExpiryDate TEXT, " +
                        "Province TEXT);");
                stmt.execute("CREATE TABLE PACKAGESINCLUDE (" +
                        "PackageIncludeID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "PackageID INTEGER," +
                        "SupplierName TEXT," +
                        "Province TEXT," +
                        "ProductName TEXT," +
                        "MaxCapacity TEXT," +
                        "Category TEXT," +
                        "Quantity INTEGER," +
                        "NightsRounds INTEGER," +
                        "AddTo TEXT," +
                        "CostPricePerUnit REAL ," +
                        "SellPricePerUnit REAL ," +
                        "Extra INTEGER);");//ex carts yes = 1
                stmt.execute("CREATE TABLE LOGINS (" +
                        "LoginID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "LoginName TEXT," +
                        "Username TEXT, " +
                        "Password TEXT);");
                stmt.execute("CREATE TABLE TRANSACTIONS (" +
                        "TransactionID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "TransactionType TEXT," +
                        "GSNumber TEXT, " +
                        "Other TEXT," +
                        "Reference TEXT," +
                        "Amount REAL," +
                        "TransactionDate TEXT);");
                log("Server> Created Database");
            }
            log("Server> Connected to database");
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> connectDB> " + ex);
            System.exit(0);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Authorisation">
    Boolean authoriseUser(String username, String password) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM USER WHERE Username = ? AND Password = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            return preparedStatement.executeQuery().next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> authoriseUser> " + username + "> " + ex);
            return false;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Getters">
    User getUser(String username) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM USER WHERE Username = ?;");
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            User user = new User(rs.getString("Username"), rs.getString("FirstName"), rs.getString("LastName"), rs.getString("Email"), rs.getString("ContactNumber"));
            log("Server> Successfully Created User: " + username);
            return user;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getUser> " + username + "> " + ex);
            return null;
        }
    }

    String getUserPassword(String username) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT Password FROM USER WHERE Username = ?;");
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                log("Server> Successfully Gotten Password For User: " + username);
                return rs.getString("Password");
            } else {
                log("Server> Failed To Get Password For User: " + username);
                return null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getUserPassword> " + ex);
            return null;
        }
    }

    List<Supplier> getSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM SUPPLIERS;");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                suppliers.add(new Supplier(rs.getInt("SupplierNumber"), rs.getString("SupplierName"), rs.getString("Category"), rs.getString("Province"), rs.getString("Address"), getSuppliersContactDetails(rs.getInt("SupplierNumber"))));
            }
            log("Server> Successfully Created Suppliers");
            return suppliers;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getSuppliers> " + ex);
            return null;
        }
    }

    List<ContactDetails> getSuppliersContactDetails(int supplierNumber) {
        List<ContactDetails> contactDetails = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM CONTACTDETAILS WHERE SupplierNumber = ?;");
            preparedStatement.setInt(1, supplierNumber);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                contactDetails.add(new ContactDetails(rs.getInt("ContactDetailsID"), rs.getString("PersonName"), rs.getString("PersonPosition"), rs.getString("ContactNumber"), rs.getString("Email"), rs.getString("DateAdded")));
            }
            log("Server> Successfully Gotten Suppliers Contact Details: " + contactDetails.size());
            return contactDetails;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getSuppliersContactDetails> " + ex);
            return null;
        }
    }

    Booking getBooking(String gsNumber) {
        if(gsNumber.startsWith("GS")){
            gsNumber = gsNumber.substring(2);
        }
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGS WHERE GSNumber = ?;");
            preparedStatement.setString(1, gsNumber);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return new Booking(Integer.toString(rs.getInt("GSNumber")), rs.getString("ClientName"), rs.getString("ContactNumber"), rs.getString("Email"), rs.getInt("GolfersSharing"), rs.getDouble("GolfersSharingPrice"), rs.getInt("NonGolfersSharing"), rs.getDouble("NonGolfersSharingPrice"), rs.getInt("GolfersSingle"), rs.getDouble("GolfersSinglePrice"), rs.getInt("NonGolfersSingle"), rs.getDouble("NonGolfersSinglePrice"), rs.getString("Arrival"), rs.getString("Departure"), rs.getString("Process"), rs.getDouble("BookingAmount"), rs.getString("Consultant"), rs.getString("DepositDate"), rs.getInt("DepositPaid"), rs.getInt("FullPaid"), rs.getString("PackageName"), rs.getString("BookingMadeDate"), null, getBookingAccommodation(rs.getString("GSNumber")), getBookingGolf(rs.getString("GSNumber")), getBookingActivities(rs.getString("GSNumber")), getBookingTransport(rs.getString("GSNumber")), getBookingTransactions("GS" + rs.getString("GSNumber")));
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getBooking> " + gsNumber + "> " + ex);
            return null;
        }
    }

    Boolean getBookingsMade(String gsNumber){
        try {
            boolean x = true;
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGSACCOMMODATION WHERE GSNumber = ?;");
            preparedStatement.setString(1, gsNumber);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                if(rs.getInt("SupplierBooked")!=1){
                    x = false;
                }
            }
            preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGSGOLF WHERE GSNumber = ?;");
            preparedStatement.setString(1, gsNumber);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                if(rs.getInt("SupplierBooked")!=1){
                    x = false;
                }
            }
            preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGSTRANSPORT WHERE GSNumber = ?;");
            preparedStatement.setString(1, gsNumber);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                if(rs.getInt("SupplierBooked")!=1){
                    x = false;
                }
            }
            preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGSACTIVITIES WHERE GSNumber = ?;");
            preparedStatement.setString(1, gsNumber);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                if(rs.getInt("SupplierBooked")!=1){
                    x = false;
                }
            }

            if(x) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getBooking> " + gsNumber + "> " + ex);
            return false;
        }
    }

    List<Booking> getBookings() {
        List<Booking> bookings = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGS;");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                bookings.add(new Booking(Integer.toString(rs.getInt("GSNumber")), rs.getString("ClientName"), rs.getString("ContactNumber"), rs.getString("Email"), rs.getInt("GolfersSharing"), rs.getDouble("GolfersSharingPrice"), rs.getInt("NonGolfersSharing"), rs.getDouble("NonGolfersSharingPrice"), rs.getInt("GolfersSingle"), rs.getDouble("GolfersSinglePrice"), rs.getInt("NonGolfersSingle"), rs.getDouble("NonGolfersSinglePrice"), rs.getString("Arrival"), rs.getString("Departure"), rs.getString("Process"), rs.getDouble("BookingAmount"), rs.getString("Consultant"), rs.getString("DepositDate"), rs.getInt("DepositPaid"), rs.getInt("FullPaid"), rs.getString("PackageName"), rs.getString("BookingMadeDate"), rs.getString("Notes"), getBookingAccommodation(rs.getString("GSNumber")), getBookingGolf(rs.getString("GSNumber")), getBookingActivities(rs.getString("GSNumber")), getBookingTransport(rs.getString("GSNumber")), getBookingTransactions("GS" + rs.getString("GSNumber"))));
            }
            log("Server> Successfully Got all Bookings");
            return bookings;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getBookings> " + ex);
            return null;
        }
    }

    private List<Transaction> getBookingTransactions(String gsNumber){
        List<Transaction> transactions = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM TRANSACTIONS WHERE GSNumber = ?;");
            preparedStatement.setString(1, gsNumber);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(rs.getInt("TransactionID"), rs.getString("TransactionType"), rs.getString("GSNumber"), rs.getString("Other"), rs.getString("Reference"), rs.getDouble("Amount"), rs.getString("TransactionDate")));
            }
            log("Server> Successfully Got all Transactions for Booking: " + gsNumber);
            return transactions;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getBookingTransactions> " + ex);
            return null;
        }
    }

    private List<BookingAccommodation> getBookingAccommodation (String gsNumber) {
        List<BookingAccommodation> bookingAccommodation = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGSACCOMMODATION WHERE GSNumber = ?;");
            preparedStatement.setString(1, gsNumber);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                bookingAccommodation.add(new BookingAccommodation(rs.getInt("BAID"), rs.getString("SupplierName"), rs.getString("Province"), rs.getString("ProductName"), rs.getString("MaxSleep"), rs.getString("Arrival"), rs.getInt("Nights"), rs.getInt("Quantity"), rs.getDouble("CostPricePerUnit"), rs.getDouble("SellPricePerUnit"), rs.getString("AddTo"),0, rs.getDouble("AmountPaid")));
            }
            log("Server> Successfully Got all BookingAccommodation for Booking: " + gsNumber);
            return bookingAccommodation;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getBookingAccommodation> " + ex);
            return null;
        }
    }

    private List<BookingGolf> getBookingGolf (String gsNumber) {
        List<BookingGolf> bookingGolf = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGSGOLF WHERE GSNumber = ?;");
            preparedStatement.setString(1, gsNumber);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                bookingGolf.add(new BookingGolf(rs.getInt("BGID"), rs.getString("SupplierName"), rs.getString("Province"), rs.getString("ProductName"), Arrays.asList(rs.getString("Dates").substring(1, rs.getString("Dates").length() - 1).split(", ")), rs.getInt("Quantity"), rs.getInt("Rounds"), rs.getDouble("CostPricePerUnit"), rs.getDouble("SellPricePerUnit"), rs.getString("AddTo"),0, rs.getDouble("AmountPaid")));
            }
            log("Server> Successfully Got all BookingGolf for Booking: " + gsNumber);
            return bookingGolf;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getBookingGolf> " + ex);
            return null;
        }
    }

    private List<BookingActivity> getBookingActivities (String gsNumber) {
        List<BookingActivity> activities = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGSACTIVITIES WHERE GSNumber = ?;");
            preparedStatement.setString(1, gsNumber);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                activities.add(new BookingActivity(rs.getInt("BTID"), rs.getString("SupplierName"), rs.getString("Province"), rs.getString("ProductNumber"), rs.getString("ActivityDate"), rs.getInt("Quantity"), rs.getDouble("CostPricePerUnit"), rs.getDouble("SellPricePerUnit"), rs.getString("AddTo"), 0, rs.getDouble("AmountPaid")));
            }
            log("Server> Successfully Got all Activities for Booking: " + gsNumber);
            return activities;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getBookingActivities> " + ex);
            return null;
        }
    }

    private List<BookingTransport> getBookingTransport (String gsNumber) {
        List<BookingTransport> bookingTransport = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGSTRANSPORT WHERE GSNumber = ?;");
            preparedStatement.setString(1, gsNumber);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                bookingTransport.add(new BookingTransport(rs.getInt("BRID"), rs.getString("SupplierName"), rs.getString("Province"), rs.getString("ProductName"), rs.getString("TravelDate"), rs.getInt("Quantity"), rs.getString("TravelFrom"), rs.getString("TravelTo"), rs.getDouble("CostPricePerUnit"), rs.getDouble("SellPricePerUnit"), rs.getString("AddTo"),0, rs.getDouble("AmountPaid")));
            }
            log("Server> Successfully Got all Activities for Booking: " + gsNumber);
            return bookingTransport;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getBookingTransport> " + ex);
            return null;
        }
    }

    private File[] getRateFiles(String category){
        File[] rates = new File[0];
        File[] files = Server.SUPPLIER_FOLDER.listFiles();
        for (File file: files){
            File nFile = new File(file.getAbsolutePath() + "/" + category);
            if(nFile.exists()) {
                File[] fif = nFile.listFiles();
                File[] temp = new File[rates.length + fif.length];
                for (int i = 0; i < rates.length; i++) {
                    temp[i] = rates[i];
                }
                for (int i = 0; i < fif.length; i++) {
                    temp[rates.length + i] = fif[i];
                }
                rates = temp;
            }
        }
        return rates;
    }

    List<ProductAccommodation> getProductAccommodation () {
        List<ProductAccommodation> accommodation = new ArrayList<>();
        File[] accommodationRates = getRateFiles("Accommodation");
        for(File file: accommodationRates) {
            String province = file.getAbsolutePath().split("\\\\")[3];
            String category = file.getAbsolutePath().split("\\\\")[4];
            String supplierName = file.getAbsolutePath().split("\\\\")[5].substring(0, file.getAbsolutePath().split("\\\\")[5].lastIndexOf("."));
            System.out.println(file.getAbsolutePath());
            Workbook wb;
            if(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1).matches("xls")) {
                try {
                    wb = Workbook.getWorkbook(file);
                    Sheet sheet = wb.getSheet(0);
                    Cell cell;//Start 0,0 column, row
                    int rows = sheet.getRows();
                    int columns = sheet.getColumns();
                    for (int i = 1; i < rows-1; i++) {//rows
                        try {
                            List<String[]>prices = new ArrayList<>();
                            for (int j = 0; j < columns-1; j++) {//columns

                                cell = sheet.getCell(j, i);
                                if (cell.getContents().contains("#")) {
                                    String[] datedateprice = new String[3];
                                    datedateprice[0] = cell.getContents().split("#")[0];
                                    datedateprice[1] = cell.getContents().split("#")[1];
                                    datedateprice[2] = sheet.getCell(j+1, i).getContents();
                                    prices.add(datedateprice);
                                }
                                if (cell.getContents().matches("")) {
                                    if (j == 0) {
                                        i = rows;
                                    } else {
                                        j = columns;
                                    }
                                }
                            }
                            accommodation.add(new ProductAccommodation(supplierName, category, province, sheet.getCell(0, i).getContents(), Integer.parseInt(sheet.getCell(2, i).getContents()), sheet.getCell(1, i).getContents(), prices));
                        } catch (ArrayIndexOutOfBoundsException ex) {

                        } catch (NumberFormatException ex) {

                        }
                    }
                    wb.close();
                } catch (Exception ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return accommodation;
    }

    List<ProductGolf> getProductGolf () {
        List<ProductGolf> golf = new ArrayList<>();
        File[] golfRates = getRateFiles("Golf");
        for(File file: golfRates) {
            String province = file.getAbsolutePath().split("\\\\")[3];
            String category = file.getAbsolutePath().split("\\\\")[4];
            String supplierName = file.getAbsolutePath().split("\\\\")[5].substring(0, file.getAbsolutePath().split("\\\\")[5].lastIndexOf("."));
            System.out.println(file.getAbsolutePath());
            Workbook wb;
            if(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1).matches("xls")) {
                try {
                    wb = Workbook.getWorkbook(file);
                    Sheet sheet = wb.getSheet(0);
                    Cell cell;//Start 0,0 column, row
                    int rows = sheet.getRows();
                    int columns = sheet.getColumns();
                    for (int i = 1; i < rows; i++) {//rows
                        try {
                            List<String[]>prices = new ArrayList<>();
                            for (int j = 0; j < columns-1; j++) {//columns

                                cell = sheet.getCell(j, i);
                                if (cell.getContents().contains("#")) {
                                    String[] datedateprice = new String[3];
                                    datedateprice[0] = cell.getContents().split("#")[0];
                                    datedateprice[1] = cell.getContents().split("#")[1];
                                    datedateprice[2] = sheet.getCell(j+1, i).getContents();
                                    prices.add(datedateprice);
                                }
                                if (cell.getContents().matches("")) {
                                    if (j == 0) {
                                        i = rows;
                                    } else {
                                        j = columns;
                                    }
                                }
                            }
                            golf.add(new ProductGolf(supplierName, category, province, sheet.getCell(0, i).getContents(), sheet.getCell(1, i).getContents(), prices));
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                    }
                    wb.close();
                } catch (Exception ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return golf;
    }

    List<ProductActivity> getProductActivities () {
        List<ProductActivity> activities = new ArrayList<>();
        File[] activityRates = getRateFiles("Activities");
        for(File file: activityRates) {
            String province = file.getAbsolutePath().split("\\\\")[3];
            String category = file.getAbsolutePath().split("\\\\")[4];
            String supplierName = file.getAbsolutePath().split("\\\\")[5].substring(0, file.getAbsolutePath().split("\\\\")[5].lastIndexOf("."));
            Workbook wb;
            if(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1).matches("xls")) {
                try {
                    wb = Workbook.getWorkbook(file);
                    Sheet sheet = wb.getSheet(0);
                    Cell cell;//Start 0,0 column, row
                    int rows = sheet.getRows();
                    int columns = sheet.getColumns();
                    for (int i = 1; i < rows; i++) {//rows
                        try {
                            List<String[]>prices = new ArrayList<>();
                            for (int j = 0; j < columns-1; j++) {//columns

                                cell = sheet.getCell(j, i);
                                if (cell.getContents().contains("#")) {
                                    String[] datedateprice = new String[3];
                                    datedateprice[0] = cell.getContents().split("#")[0];
                                    datedateprice[1] = cell.getContents().split("#")[1];
                                    datedateprice[2] = sheet.getCell(j+1, i).getContents();
                                    prices.add(datedateprice);
                                }
                                if (cell.getContents().matches("")) {
                                    if (j == 0) {
                                        i = rows;
                                    } else {
                                        j = columns;
                                    }
                                }
                            }
                            activities.add(new ProductActivity(supplierName, category, province, sheet.getCell(0, i).getContents(), sheet.getCell(1, i).getContents(), prices));
                        } catch (ArrayIndexOutOfBoundsException ex) {

                        } catch (NumberFormatException ex) {

                        }
                    }
                    wb.close();
                } catch (Exception ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return activities;
    }

    List<ProductTransport> getProductTransport () {
        List<ProductTransport> transport = new ArrayList<>();
        File[] transportRates = getRateFiles("Transport");
        for(File file: transportRates) {
            String province = file.getAbsolutePath().split("\\\\")[3];
            String category = file.getAbsolutePath().split("\\\\")[4];
            String supplierName = file.getAbsolutePath().split("\\\\")[5].substring(0, file.getAbsolutePath().split("\\\\")[5].lastIndexOf("."));
            Workbook wb;
            if(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1).matches("xls")) {
                try {
                    wb = Workbook.getWorkbook(file);
                    Sheet sheet = wb.getSheet(0);
                    Cell cell;//Start 0,0 column, row
                    int rows = sheet.getRows();
                    int columns = sheet.getColumns();
                    for (int i = 1; i < rows; i++) {//rows
                        try {
                            List<String[]>prices = new ArrayList<>();
                            for (int j = 0; j < columns-1; j++) {//columns

                                cell = sheet.getCell(j, i);
                                if (cell.getContents().contains("#")) {
                                    String[] datedateprice = new String[3];
                                    datedateprice[0] = cell.getContents().split("#")[0];
                                    datedateprice[1] = cell.getContents().split("#")[1];
                                    datedateprice[2] = sheet.getCell(j+1, i).getContents();
                                    prices.add(datedateprice);
                                }
                                if (cell.getContents().matches("")) {
                                    if (j == 0) {
                                        i = rows;
                                    } else {
                                        j = columns;
                                    }
                                }
                            }
                            transport.add(new ProductTransport(supplierName, category, province, sheet.getCell(0, i).getContents(), sheet.getCell(1, i).getContents(), prices));
                        } catch (ArrayIndexOutOfBoundsException ex) {

                        } catch (NumberFormatException ex) {

                        }
                    }
                    wb.close();
                } catch (Exception ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return transport;
    }

    List<Login> getLogins(){
        List<Login> logins = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM LOGINS;");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                logins.add(new Login(rs.getInt("LoginID"), rs.getString("LoginName"), rs.getString("UserName"), rs.getString("Password")));
            }
            log("Server> Successfully Got all Logins");
            return logins;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getLogins> " + ex);
            return null;
        }
    }

    List<Transaction> getTransactions(){
        List<Transaction> transactions = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM TRANSACTIONS;");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(rs.getInt("TransactionID"), rs.getString("TransactionType"), rs.getString("GSNumber"), rs.getString("Other"), rs.getString("Reference"), rs.getDouble("Amount"), rs.getString("TransactionDate")));
            }
            log("Server> Successfully Got all Transactions");
            return transactions;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getTransactions> " + ex);
            return null;
        }
    }

    List<TripPackage> getPackages(){
        List<TripPackage> packages = new ArrayList<>();
        packages.add(new TripPackage(0, "Bespoke", 0, "Bespoke", 0, 0, 0, 0, "Bespoke", "Bespoke",  null, null, null, null));
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM PACKAGES;");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                packages.add(new TripPackage(rs.getInt("PackageID"), rs.getString("PackageName"), rs.getDouble("TotalPackageAmount"), rs.getString("Category"), rs.getInt("GolfersSharing"), rs.getInt("NonGolfersSharing"), rs.getInt("GolfersSingle"), rs.getInt("NonGolfersSingle"),  rs.getString("Province"), rs.getString("ExpiryDate"), getTripAccommodation(rs.getInt("PackageID")), getTripGolf(rs.getInt("PackageID")), getTripTransport(rs.getInt("PackageID")), getTripActivities(rs.getInt("PackageID"))));
            }
            log("Server> Successfully Got all Packages");
            return packages;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getLogins> " + ex);
            return null;
        }
    }

    private List<BookingAccommodation> getTripAccommodation (int packageID) {
        List<BookingAccommodation> bookingAccommodation = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM PACKAGESINCLUDE WHERE PackageID = ? AND Category = ?;");
            preparedStatement.setInt(1, packageID);
            preparedStatement.setString(2, "Accommodation");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {//-2 if package
                bookingAccommodation.add(new BookingAccommodation(0, rs.getString("SupplierName"), rs.getString("Province"), rs.getString("ProductName"), rs.getString("MaxCapacity"), "", rs.getInt("NightsRounds"), rs.getInt("Quantity"), rs.getDouble("CostPricePerUnit"), rs.getDouble("SellPricePerUnit"), rs.getString("AddTo"),0, 0.0));
            }
            log("Server> Successfully Got all Accommodation for Package: " + packageID);
            return bookingAccommodation;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getTripAccommodation> " + ex);
            return null;
        }
    }

    private List<BookingGolf> getTripGolf (int packageID) {
        List<BookingGolf> bookingGolf = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM PACKAGESINCLUDE WHERE PackageID = ? AND Category = ?;");
            preparedStatement.setInt(1, packageID);
            preparedStatement.setString(2, "Golf");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                bookingGolf.add(new BookingGolf(0, rs.getString("SupplierName"), rs.getString("Province"), rs.getString("ProductName"), null, rs.getInt("Quantity"), rs.getInt("Rounds"), rs.getDouble("CostPricePerUnit"), rs.getDouble("SellPricePerUnit"), rs.getString("AddTo"),0, 0.0));
            }
            log("Server> Successfully Got all Golf for Package: " + packageID);
            return bookingGolf;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getTripGolf> " + ex);
            return null;
        }
    }

    private List<BookingActivity> getTripActivities (int packageID) {
        List<BookingActivity> activities = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM PACKAGESINCLUDE WHERE PackageID = ? AND Category = ?;");
            preparedStatement.setInt(1, packageID);
            preparedStatement.setString(2, "Activity");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                activities.add(new BookingActivity(0, rs.getString("SupplierName"), rs.getString("Province"), rs.getString("ProductNumber"), rs.getString("ActivityDate"), rs.getInt("Quantity"), rs.getDouble("CostPricePerUnit"), rs.getDouble("SellPricePerUnit"), rs.getString("AddTo"), 0, 0.0));
            }
            log("Server> Successfully Got all Activities for Package: " + packageID);
            return activities;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getTripActivities> " + ex);
            return null;
        }
    }

    private List<BookingTransport> getTripTransport (int packageID) {
        List<BookingTransport> bookingTransport = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM PACKAGESINCLUDE WHERE PackageID = ? AND Category = ?;");
            preparedStatement.setInt(1, packageID);
            preparedStatement.setString(2, "Transport");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                bookingTransport.add(new BookingTransport(0, rs.getString("SupplierName"), rs.getString("Province"), rs.getString("ProductName"), rs.getString("TravelDate"), rs.getInt("Quantity"), rs.getString("From"), rs.getString("To"), rs.getDouble("CostPricePerUnit"), rs.getDouble("SellPricePerUnit"), rs.getString("AddTo"),0, 0.0));
            }
            log("Server> Successfully Got all Transport for Package: " + packageID);
            return bookingTransport;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getTripTransport> " + ex);
            return null;
        }
    }

    List<DataFile> getDocuments(){
        List<DataFile> documents = new ArrayList<>();
        File classFilesDirectory = new File(Server.DOCUMENTS_FOLDER.getAbsolutePath());
        if (classFilesDirectory.exists()) {
            for (File file : classFilesDirectory.listFiles()) {
                documents.add(new DataFile("Documents", file.getName().substring(0, file.getName().lastIndexOf(".")), file.getName().substring(file.getName().lastIndexOf("."), file.getName().length()), (int) file.length()));
            }
        }
        log("Server> Successfully Gotten Documents");
        return documents;
    }

    /*private byte[] getContactImage(String contactID) {
        try {
            return Files.readAllBytes(new File(Server.CONTACT_IMAGES + "/" + contactID + "/profile.jpg").toPath());
        } catch (Exception ex) {
            System.out.println("Server> Can't find picture for contact, " + contactID);
        }
        return null;
    }*/

    /*private List<ClassFile> getFiles(int classID) {
        List<ClassFile> files = new ArrayList<>();
        File classFilesDirectory = new File(Server.FILES_FOLDER.getAbsolutePath() + "/" + classID);
        if (classFilesDirectory.exists()) {
            for (File file : classFilesDirectory.listFiles()) {
                files.add(new ClassFile(classID, file.getName(), (int) file.length()));
            }
        }
        log("Server> Successfully Gotten Files: ");
        return files;
    }*/
    //</editor-fold>

    //<editor-fold desc="Change Password">
    Boolean changeUserPassword(String studentNumber, String newPassword) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE USER SET Password = ? WHERE USERNAME = ?;");
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, studentNumber);
            log("Server> Successfully Changed Password For User: " + studentNumber);
            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> changeUserPassword> " + ex);
            return false;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Email Passwords">
    void emailUserPassword(String studentNumber) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT Password, Email FROM USER WHERE Username = ?;");
            preparedStatement.setString(1, studentNumber);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                log("Server> Successfully Emailed Password For User: " + studentNumber);
                String email = rs.getString("Email");
                String password = rs.getString("Password");
                new Thread(() -> Email.emailPassword(studentNumber, email, password)).start();
            } else {
                log("Server> Failed To Email Password For User: " + studentNumber);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> emailUserPassword> " + ex);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Adders">
    public int addBooking(Booking booking) {//TODO Create File
        int gsNumber = -1;
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO BOOKINGS (ClientName, ContactNumber, Email, GolfersSharing, GolfersSharingPrice, NonGolfersSharing, NonGolfersSharingPrice, GolfersSingle, GolfersSinglePrice, NonGolfersSingle, NonGolfersSinglePrice, Arrival, Departure, Process, BookingAmount, Consultant, DepositDate, DepositPaid, FullPaid, PackageName, BookingMadeDate, Notes) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
            preparedStatement.setString(1, booking.getClientName());
            preparedStatement.setString(2, booking.getContactNumber());
            preparedStatement.setString(3, booking.getEmail());
            preparedStatement.setInt(4, booking.getGolfersSharing());
            preparedStatement.setDouble(5, booking.getGolferSharingPrice());
            preparedStatement.setInt(6, booking.getNongolfersSharing());
            preparedStatement.setDouble(7, booking.getNonGolferSharingPrice());
            preparedStatement.setInt(8, booking.getGolfersSingle());
            preparedStatement.setDouble(9, booking.getGolferSinglePrice());
            preparedStatement.setInt(10, booking.getNongolfersSingle());
            preparedStatement.setDouble(11, booking.getNonGolferSinglePrice());
            preparedStatement.setString(12, booking.getArrival());
            preparedStatement.setString(13, booking.getDeparture());
            preparedStatement.setString(14, booking.getProcess());
            preparedStatement.setDouble(15, booking.getBookingAmount());
            preparedStatement.setString(16, booking.getConsultant());
            preparedStatement.setString(17, booking.getDepositDate());
            preparedStatement.setInt(18, booking.getDepositPaid());
            preparedStatement.setInt(19, booking.getFullPaid());
            preparedStatement.setString(20, booking.getPackageName());
            preparedStatement.setString(21, booking.getBookingMadeDate());
            preparedStatement.setString(22, booking.getNotes());
            preparedStatement.execute();
            preparedStatement = con.prepareStatement("SELECT GSNumber FROM BOOKINGS WHERE ClientName = ? AND ContactNumber = ? AND Email = ? AND GolfersSharing = ? AND GolfersSharingPrice = ? AND NonGolfersSharing = ? AND NonGolfersSharingPrice = ? AND GolfersSingle = ? AND GolfersSinglePrice = ? AND NonGolfersSingle = ? AND NonGolfersSinglePrice = ? AND Arrival = ? AND Departure = ? AND Process = ? AND BookingAmount = ? AND Consultant = ? AND DepositDate = ? AND DepositPaid = ? AND FullPaid = ? AND PackageName = ? AND BookingMadeDate = ? AND Notes = ?;");
            preparedStatement.setString(1, booking.getClientName());
            preparedStatement.setString(2, booking.getContactNumber());
            preparedStatement.setString(3, booking.getEmail());
            preparedStatement.setInt(4, booking.getGolfersSharing());
            preparedStatement.setDouble(5, booking.getGolferSharingPrice());
            preparedStatement.setInt(6, booking.getNongolfersSharing());
            preparedStatement.setDouble(7, booking.getNonGolferSharingPrice());
            preparedStatement.setInt(8, booking.getGolfersSingle());
            preparedStatement.setDouble(9, booking.getGolferSinglePrice());
            preparedStatement.setInt(10, booking.getNongolfersSingle());
            preparedStatement.setDouble(11, booking.getNonGolferSinglePrice());
            preparedStatement.setString(12, booking.getArrival());
            preparedStatement.setString(13, booking.getDeparture());
            preparedStatement.setString(14, booking.getProcess());
            preparedStatement.setDouble(15, booking.getBookingAmount());
            preparedStatement.setString(16, booking.getConsultant());
            preparedStatement.setString(17, booking.getDepositDate());
            preparedStatement.setInt(18, booking.getDepositPaid());
            preparedStatement.setInt(19, booking.getFullPaid());
            preparedStatement.setString(20, booking.getPackageName());
            preparedStatement.setString(21, booking.getBookingMadeDate());
            preparedStatement.setString(22, booking.getNotes());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                gsNumber = rs.getInt("GSNumber");
            }
            for (BookingAccommodation ac: booking.getBookingAccommodation()) {
                preparedStatement = con.prepareStatement("INSERT INTO BOOKINGSACCOMMODATION (GSNumber, SupplierName, Province, ProductName, MaxSleep, Nights, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, SupplierBooked) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setString(1, Integer.toString(gsNumber));
                preparedStatement.setString(2, ac.getSupplierName());
                preparedStatement.setString(3, ac.getProvince());
                preparedStatement.setString(4, ac.getProductName());
                preparedStatement.setString(5, ac.getMaxSleep());
                preparedStatement.setInt(6, ac.getNights());
                preparedStatement.setInt(7, ac.getQuantity());
                preparedStatement.setString(8, ac.getAddTo());
                preparedStatement.setDouble(9, ac.getCostPricePerUnit());
                preparedStatement.setDouble(10, ac.getSellPricePerUnit());
                preparedStatement.setInt(11, ac.getSupplierBooked());
                preparedStatement.execute();
            }
            for (BookingGolf gf: booking.getBookingGolf()) {
                preparedStatement = con.prepareStatement("INSERT INTO BOOKINGSGOLF (GSNumber, SupplierName, Province, ProductName, Quantity, Rounds, AddTo, CostPricePerUnit, SellPricePerUnit, SupplierBooked) VALUES (?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setString(1, Integer.toString(gsNumber));
                preparedStatement.setString(2, gf.getSupplierName());
                preparedStatement.setString(3, gf.getProvince());
                preparedStatement.setString(4, gf.getProductName());
                preparedStatement.setInt(5, gf.getQuantity());
                preparedStatement.setInt(6, gf.getRounds());
                preparedStatement.setString(7, gf.getAddTo());
                preparedStatement.setDouble(8, gf.getCostPricePerUnit());
                preparedStatement.setDouble(9, gf.getSellPricePerUnit());
                preparedStatement.setInt(10, gf.getSupplierBooked());
                preparedStatement.execute();
            }
            for (BookingActivity at: booking.getBookingActivities()) {
                preparedStatement = con.prepareStatement("INSERT INTO BOOKINGSACTIVITIES (GSNumber, SupplierName, Province, ProductName, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, SupplierBooked) VALUES (?,?,?,?,?,?,?,?,?);");
                preparedStatement.setString(1, Integer.toString(gsNumber));
                preparedStatement.setString(2, at.getSupplierName());
                preparedStatement.setString(3, at.getProvince());
                preparedStatement.setString(4, at.getProductName());
                preparedStatement.setInt(5, at.getQuantity());
                preparedStatement.setString(6, at.getAddTo());
                preparedStatement.setDouble(7, at.getCostPricePerUnit());
                preparedStatement.setDouble(8, at.getSellPricePerUnit());
                preparedStatement.setInt(9, at.getSupplierBooked());
                preparedStatement.execute();
            }
            for (BookingTransport at: booking.getBookingTransport()) {
                preparedStatement = con.prepareStatement("INSERT INTO BOOKINGSTRANSPORT (GSNumber, SupplierName, Province, ProductName, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, SupplierBooked) VALUES (?,?,?,?,?,?,?,?,?);");
                preparedStatement.setString(1, Integer.toString(gsNumber));
                preparedStatement.setString(2, at.getSupplierName());
                preparedStatement.setString(3, at.getProvince());
                preparedStatement.setString(4, at.getProductName());
                preparedStatement.setInt(5, at.getQuantity());
                preparedStatement.setString(6, at.getAddTo());
                preparedStatement.setDouble(7, at.getCostPricePerUnit());
                preparedStatement.setDouble(8, at.getSellPricePerUnit());
                preparedStatement.setInt(9, at.getSupplierBooked());
                preparedStatement.execute();
            }

        File template = new File("G:/My Drive/b. Templates/Costing/Costing Template.xls");
        File costing = new File("G:/My Drive/a. Bookings/a. Quotes/" + booking.getArrival().substring(2) + " " + booking.getClientName() + " GS" + gsNumber);
        if(!costing.exists()){
            costing.mkdirs();
        }
        costing = new File(costing.getAbsolutePath() + "/Costing.xls");
            WritableWorkbook wb;
            wb = Workbook.createWorkbook(costing, Workbook.getWorkbook(template));
            WritableSheet sheet = wb.getSheet(0);

            Label packageName = new Label(12, 55, booking.getPackageName());
            sheet.addCell(packageName);
            Number golfersSharing = new Number(12, 56, booking.getGolfersSharing());
            sheet.addCell(golfersSharing);
            Number nonGolfersSharing = new Number(12, 57, booking.getNongolfersSharing());
            sheet.addCell(nonGolfersSharing);
            Number golfersSingle = new Number(12, 58, booking.getGolfersSingle());
            sheet.addCell(golfersSingle);
            Number nonGolfersSingle = new Number(12, 59, booking.getNongolfersSingle());
            sheet.addCell(nonGolfersSingle);
            int row = 1;
            for (BookingAccommodation ac: booking.getBookingAccommodation()) {
                Label category = new Label(1, row, "Accommodation");
                sheet.addCell(category);
                Label sName = new Label(2, row, ac.getSupplierName());
                sheet.addCell(sName);
                Label pName = new Label(3, row, ac.getProductName());
                sheet.addCell(pName);
                /*Label dates = new Label(4, row, ac.getDate() + " - From: " + ac.getFrom() + " - To: " + ac.getTo());
                sheet.addCell(dates);*/
                if (ac.getAddTo().matches("Golfer Sharing")) {
                    Label addTo = new Label(5, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Non-Golfer Sharing")) {
                    Label addTo = new Label(6, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Golfer and Non-Golfer Sharing")) {
                    Label addTo = new Label(7, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Golfer Single")) {
                    Label addTo = new Label(8, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Non-Golfer Single")){
                    Label addTo = new Label(9, row, "x");
                    sheet.addCell(addTo);
                }
                Number quantity = new Number(10, row, ac.getQuantity());
                sheet.addCell(quantity);
                Number nights = new Number(11, row, ac.getNights());
                sheet.addCell(nights);
                Number sto = new Number(12, row, ac.getCostPricePerUnit());
                sheet.addCell(sto);
                Number commission = new Number(17, row, (ac.getCostPricePerUnit() - ac.getSellPricePerUnit()) / (- ac.getSellPricePerUnit()));
                sheet.addCell(commission);
                row++;
            }
            for (BookingGolf ac: booking.getBookingGolf()) {
                Label category = new Label(1, row, "Golf");
                sheet.addCell(category);
                Label sName = new Label(2, row, ac.getSupplierName());
                sheet.addCell(sName);
                Label pName = new Label(3, row, ac.getProductName());
                sheet.addCell(pName);
                /*Label dates = new Label(4, row, ac.getDate() + " - From: " + ac.getFrom() + " - To: " + ac.getTo());
                sheet.addCell(dates);*/
                if (ac.getAddTo().matches("Golfer Sharing")) {
                    Label addTo = new Label(5, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Non-Golfer Sharing")) {
                    Label addTo = new Label(6, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Golfer and Non-Golfer Sharing")) {
                    Label addTo = new Label(7, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Golfer Single")) {
                    Label addTo = new Label(8, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Non-Golfer Single")){
                    Label addTo = new Label(9, row, "x");
                    sheet.addCell(addTo);
                }
                Number quantity = new Number(10, row, ac.getQuantity());
                sheet.addCell(quantity);
                Number nights = new Number(11, row, ac.getRounds());
                sheet.addCell(nights);
                Number sto = new Number(12, row, ac.getCostPricePerUnit());
                sheet.addCell(sto);
                Number commission = new Number(17, row, (ac.getCostPricePerUnit() - ac.getSellPricePerUnit()) / (- ac.getSellPricePerUnit()));
                sheet.addCell(commission);
                row++;
            }
            for (BookingActivity ac: booking.getBookingActivities()) {
                Label category = new Label(1, row, "Activity");
                sheet.addCell(category);
                Label sName = new Label(2, row, ac.getSupplierName());
                sheet.addCell(sName);
                Label pName = new Label(3, row, ac.getProductName());
                sheet.addCell(pName);
                /*Label dates = new Label(4, row, ac.getDate() + " - From: " + ac.getFrom() + " - To: " + ac.getTo());
                sheet.addCell(dates);*/
                if (ac.getAddTo().matches("Golfer Sharing")) {
                    Label addTo = new Label(5, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Non-Golfer Sharing")) {
                    Label addTo = new Label(6, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Golfer and Non-Golfer Sharing")) {
                    Label addTo = new Label(7, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Golfer Single")) {
                    Label addTo = new Label(8, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Non-Golfer Single")){
                    Label addTo = new Label(9, row, "x");
                    sheet.addCell(addTo);
                }
                Number quantity = new Number(10, row, ac.getQuantity());
                sheet.addCell(quantity);
                Number nights = new Number(11, row, 1);
                sheet.addCell(nights);
                Number sto = new Number(12, row, ac.getCostPricePerUnit());
                sheet.addCell(sto);
                Number commission = new Number(17, row, (ac.getCostPricePerUnit() - ac.getSellPricePerUnit()) / (- ac.getSellPricePerUnit()));
                sheet.addCell(commission);
                row++;
            }
            for (BookingTransport ac: booking.getBookingTransport()) {
                Label category = new Label(1, row, "Transport");
                sheet.addCell(category);
                Label sName = new Label(2, row, ac.getSupplierName());
                sheet.addCell(sName);
                Label pName = new Label(3, row, ac.getProductName());
                sheet.addCell(pName);
                Label dates = new Label(4, row, ac.getDate() + " - From: " + ac.getFrom() + " - To: " + ac.getTo());
                sheet.addCell(dates);
                if (ac.getAddTo().matches("Golfer Sharing")) {
                    Label addTo = new Label(5, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Non-Golfer Sharing")) {
                    Label addTo = new Label(6, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Golfer and Non-Golfer Sharing")) {
                    Label addTo = new Label(7, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Golfer Single")) {
                    Label addTo = new Label(8, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Non-Golfer Single")){
                    Label addTo = new Label(9, row, "x");
                    sheet.addCell(addTo);
                }
                Number quantity = new Number(10, row, ac.getQuantity());
                sheet.addCell(quantity);
                Number nights = new Number(11, row, 1);
                sheet.addCell(nights);
                Number sto = new Number(12, row, ac.getCostPricePerUnit());
                sheet.addCell(sto);
                Number commission = new Number(17, row, (ac.getCostPricePerUnit() - ac.getSellPricePerUnit()) / (- ac.getSellPricePerUnit()));
                sheet.addCell(commission);
                row++;
            }
            Number golfersSharingPrice = new Number(20, 56, booking.getGolferSharingPrice());
            sheet.addCell(golfersSharingPrice);
            Number nonGolfersSharingPrice = new Number(20, 57, booking.getNonGolferSharingPrice());
            sheet.addCell(nonGolfersSharingPrice);
            Number golfersSinglePrice = new Number(20, 58, booking.getGolferSinglePrice());
            sheet.addCell(golfersSinglePrice);
            Number nonGolfersSinglePrice = new Number(20, 59, booking.getNonGolferSinglePrice());
            sheet.addCell(nonGolfersSinglePrice);
            wb.write();
            wb.close();
        } catch (Exception ioe) {
            ioe.printStackTrace();
            log("Server> addBooking> " + ioe);
        }
        log("Server> Successfully Added Booking: " + booking.getClientName());
        //notifyUpdatedStudent(s[0]);
        return gsNumber;
    }

    void addPackage(TripPackage tripPackage) {
        int packageID = 0;
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO PACKAGES (PackageName, Category, GolfersSharing, NonGolfersSharing, GolfersSingle, NonGolfersSingle, TotalPackageAmount, ExpiryDate, Province) VALUES (?,?,?,?,?,?,?,?,?);");
            preparedStatement.setString(1, tripPackage.getPackageName());
            preparedStatement.setString(2, tripPackage.getCategory());
            preparedStatement.setInt(3, tripPackage.getGolfersSharing());
            preparedStatement.setInt(4, tripPackage.getNongolfersSharing());
            preparedStatement.setInt(5, tripPackage.getGolfersSingle());
            preparedStatement.setInt(6, tripPackage.getNongolfersSingle());
            preparedStatement.setDouble(7, tripPackage.getTotalPackageAmount());
            preparedStatement.setString(8, tripPackage.getExpiryDate());
            preparedStatement.setString(9, tripPackage.getProvince());
            preparedStatement.execute();
            preparedStatement = con.prepareStatement("SELECT PackageID FROM PACKAGES WHERE PackageName = ? AND Category = ? AND GolfersSharing = ? AND NonGolfersSharing = ? AND GolfersSingle = ? AND NonGolfersSingle = ? AND TotalPackageAmount = ? AND ExpiryDate = ? AND Province = ?;");
            preparedStatement.setString(1, tripPackage.getPackageName());
            preparedStatement.setString(2, tripPackage.getCategory());
            preparedStatement.setInt(3, tripPackage.getGolfersSharing());
            preparedStatement.setInt(4, tripPackage.getNongolfersSharing());
            preparedStatement.setInt(5, tripPackage.getGolfersSingle());
            preparedStatement.setInt(6, tripPackage.getNongolfersSingle());
            preparedStatement.setDouble(7, tripPackage.getTotalPackageAmount());
            preparedStatement.setString(8, tripPackage.getExpiryDate());
            preparedStatement.setString(9, tripPackage.getProvince());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                packageID = rs.getInt("PackageID");
            }
            for (BookingAccommodation ac : tripPackage.getBookingAccommodation()) {
                preparedStatement = con.prepareStatement("INSERT INTO PACKAGESINCLUDE (PackageID, Province, SupplierName, ProductName, MaxCapacity, Category, NightsRounds, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setInt(1, packageID);
                preparedStatement.setString(2, ac.getProvince());
                preparedStatement.setString(3, ac.getSupplierName());
                preparedStatement.setString(4, ac.getProductName());
                preparedStatement.setString(5, ac.getMaxSleep());
                preparedStatement.setString(6, "Accommodation");
                preparedStatement.setInt(7, ac.getNights());
                preparedStatement.setInt(8, ac.getQuantity());
                preparedStatement.setString(9, ac.getAddTo());
                preparedStatement.setDouble(10, ac.getCostPricePerUnit());
                preparedStatement.setDouble(11, ac.getSellPricePerUnit());
                preparedStatement.setInt(12, 0);
                preparedStatement.execute();
            }
            for (BookingGolf gf : tripPackage.getBookingGolf()) {
                preparedStatement = con.prepareStatement("INSERT INTO PACKAGESINCLUDE (PackageID, Province, SupplierName, ProductName, MaxCapacity, Category, NightsRounds, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setInt(1, packageID);
                preparedStatement.setString(2, gf.getProvince());
                preparedStatement.setString(3, gf.getSupplierName());
                preparedStatement.setString(4, gf.getProductName());
                preparedStatement.setString(5, "1");
                preparedStatement.setString(6, "Golf");
                preparedStatement.setInt(7, gf.getRounds());
                preparedStatement.setInt(8, gf.getQuantity());
                preparedStatement.setString(9, gf.getAddTo());
                preparedStatement.setDouble(10, gf.getCostPricePerUnit());
                preparedStatement.setDouble(11, gf.getSellPricePerUnit());
                preparedStatement.setInt(12, 0);
                preparedStatement.execute();
            }
            for (BookingActivity at : tripPackage.getBookingActivities()) {
                preparedStatement = con.prepareStatement("INSERT INTO PACKAGESINCLUDE (PackageID, Province, SupplierName, ProductName, MaxCapacity, Category, NightsRounds, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setInt(1, packageID);
                preparedStatement.setString(2, at.getProvince());
                preparedStatement.setString(3, at.getSupplierName());
                preparedStatement.setString(4, at.getProductName());
                preparedStatement.setString(5, "1");
                preparedStatement.setString(6, "Activity");
                preparedStatement.setInt(7, 1);
                preparedStatement.setInt(8, at.getQuantity());
                preparedStatement.setString(9, at.getAddTo());
                preparedStatement.setDouble(10, at.getCostPricePerUnit());
                preparedStatement.setDouble(11, at.getSellPricePerUnit());
                preparedStatement.setInt(12, 0);
                preparedStatement.execute();
            }
            for (BookingTransport at : tripPackage.getBookingTransport()) {
                preparedStatement = con.prepareStatement("INSERT INTO PACKAGESINCLUDE (PackageID, Province, SupplierName, ProductName, MaxCapacity, Category, NightsRounds, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setInt(1, packageID);
                preparedStatement.setString(2, at.getProvince());
                preparedStatement.setString(3, at.getSupplierName());
                preparedStatement.setString(4, at.getProductName());
                preparedStatement.setString(5, "1");
                preparedStatement.setString(6, "Transport");
                preparedStatement.setInt(7, 1);
                preparedStatement.setInt(8, at.getQuantity());
                preparedStatement.setString(9, at.getAddTo());
                preparedStatement.setDouble(10, at.getCostPricePerUnit());
                preparedStatement.setDouble(11, at.getSellPricePerUnit());
                preparedStatement.setInt(12, 0);
                preparedStatement.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addSupplier(Supplier supplier) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO SUPPLIERS (SupplierName, Category, Province, Address) VALUES (?,?,?,?);");
            preparedStatement.setString(1, supplier.getSupplierName());
            preparedStatement.setString(2, supplier.getCategory());
            preparedStatement.setString(3, supplier.getProvince());
            preparedStatement.setString(4, supplier.getAddress());
            preparedStatement.execute();
            log("Server> Successfully Added Supplier: " + supplier.getSupplierName());
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> addSupplier> " + ex);
        }
    }

    void addContactDetails(ContactDetails cd) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO CONTACTDETAILS (SupplierNumber, PersonName, PersonPosition, ContactNumber, Email, DateAdded) VALUES (?,?,?,?,?,?);");
            preparedStatement.setInt(1, cd.getContactDetailsID() - 100000);
            preparedStatement.setString(2, cd.getPersonName());
            preparedStatement.setString(3, cd.getPosition());
            preparedStatement.setString(4, cd.getNumber());
            preparedStatement.setString(5, cd.getEmail());
            preparedStatement.setString(6, cd.getDateAdded());
            preparedStatement.execute();
            log("Server> Successfully Added Contact Details for Supplier: " + (cd.getContactDetailsID() - 100000));
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> addContactDetails> " + ex);
        }
    }

    void addLogin(Login login){
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO LOGINS (LoginName, Username, Password) VALUES (?,?,?);");
            preparedStatement.setString(1, login.getLoginName());
            preparedStatement.setString(2, login.getUsername());
            preparedStatement.setString(3, login.getPassword());
            preparedStatement.execute();
            log("Server> Successfully Added Login: " + login.getLoginName());
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> addLogin> " + ex);
        }
    }

    void addTransaction(Transaction transaction) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO TRANSACTIONS (TransactionType, GSNumber, Other, Reference, Amount, TransactionDate) VALUES (?,?,?,?,?,?);");
            preparedStatement.setString(1, transaction.getTransactionType());
            preparedStatement.setString(2, transaction.getGsNumber());
            preparedStatement.setString(3, transaction.getOther());
            preparedStatement.setString(4, transaction.getReference());
            preparedStatement.setDouble(5, transaction.getAmount());
            preparedStatement.setString(6, transaction.getTransactionDate());
            preparedStatement.execute();

            List<Transaction>transactions = new ArrayList<>();
            preparedStatement = con.prepareStatement("SELECT * FROM TRANSACTIONS WHERE GSNumber = ?;");
            preparedStatement.setString(1, transaction.getGsNumber());
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                transactions.add(new Transaction(rs.getInt("TransactionID"), rs.getString("TransactionType"), rs.getString("GSNumber"), rs.getString("Other"), rs.getString("Reference"), rs.getDouble("Amount"), rs.getString("TransactionDate")));
            }

            double totalRecieved = 0.0;
            double totalPaid = 0.0;
            for (Transaction t:transactions){
                if(t.getTransactionType().matches("Money Came In")){
                    totalRecieved += t.getAmount();
                } else if(t.getTransactionType().matches("Supplier Paid")){
                    totalPaid += t.getAmount();
                }
            }

            double totalClientInvoice = 0.0;
            preparedStatement = con.prepareStatement("SELECT BookingAmount FROM BOOKINGS WHERE GSNumber = ?;");
            preparedStatement.setString(1, transaction.getGsNumber().substring(2));
            rs = preparedStatement.executeQuery();
            rs.next();
            totalClientInvoice = rs.getDouble("BookingAmount");
            double totalSupplierInvoice = 0.0;
            Booking booking = getBooking(transaction.getGsNumber().substring(2));
            totalSupplierInvoice = getTotalSupplierInvoice(booking);
            String process = "";
            if(totalRecieved >= (totalClientInvoice * 0.4)) {
                process = "PendingDepositRecieved";
                preparedStatement = con.prepareStatement("UPDATE Bookings SET DepositPaid = ? WHERE GSNUMBER = ?");
                preparedStatement.setInt(1, 1);
                preparedStatement.setString(2, transaction.getGsNumber().substring(2));
                preparedStatement.execute();
            }
            if (totalPaid >= (totalSupplierInvoice * 0.1)) {
                process = "PendingDepositPaid";
            }
            if (totalRecieved >= (totalClientInvoice * 0.95)) {
                process = "PendingFullRecieved";
                preparedStatement = con.prepareStatement("UPDATE Bookings SET FullPaid = ? WHERE GSNUMBER = ?");
                preparedStatement.setInt(1, 1);
                preparedStatement.setString(2, transaction.getGsNumber().substring(2));
                preparedStatement.execute();
            }
            if (totalPaid >= (totalSupplierInvoice * 0.95)) {
                process = "ConfirmedFullPaid";
            }
            if(!process.matches("")){
                updateBookingProcess(transaction.getGsNumber(), process, booking.getArrival(), booking.getClientName());
            }
            log("Server> Successfully Added Transaction: " + transaction.getTransactionType());
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> addTransaction> " + ex);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Notify">

    //</editor-fold>

    //<editor-fold desc="Updaters">
    int updateBooking(Booking booking) {//TODO Update File
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT ClientName, Arrival FROM BOOKINGS WHERE GSNumber = ?");
            preparedStatement.setString(1, booking.getGsNumber());
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            String clientCurrentName = rs.getString("ClientName");
            String clientCurrentArrival = rs.getString("Arrival");
            File oldName = new File("G:/My Drive/a. Quotes/" + clientCurrentArrival + " " + clientCurrentName + " GS" + booking.getGsNumber());
            File newName = new File("G:/My Drive/a. Quotes/" + booking.getArrival() + " " + booking.getClientName() + " GS" + booking.getClientName());
            oldName.renameTo(newName);

            preparedStatement = con.prepareStatement("UPDATE BOOKINGS SET ClientName = ?, ContactNumber = ?, Email = ?, GolfersSharing = ?, GolfersSharingPrice = ?, NonGolfersSharing = ?, NonGolfersSharingPrice = ?, GolfersSingle = ?, GolfersSinglePrice = ?, NonGolfersSingle = ?, NonGolfersSinglePrice = ?, Arrival = ?, Departure = ?, Process = ?, BookingAmount = ?, Consultant = ?, DepositDate = ?, DepositPaid = ?, FullPaid = ?, PackageName = ?, BookingMadeDate = ?, Notes = ? WHERE GSNumber = ?");
            preparedStatement.setString(1, booking.getClientName());
            preparedStatement.setString(2, booking.getContactNumber());
            preparedStatement.setString(3, booking.getEmail());
            preparedStatement.setInt(4, booking.getGolfersSharing());
            preparedStatement.setDouble(5, booking.getGolferSharingPrice());
            preparedStatement.setInt(6, booking.getNongolfersSharing());
            preparedStatement.setDouble(7, booking.getNonGolferSharingPrice());
            preparedStatement.setInt(8, booking.getGolfersSingle());
            preparedStatement.setDouble(9, booking.getGolferSinglePrice());
            preparedStatement.setInt(10, booking.getNongolfersSingle());
            preparedStatement.setDouble(11, booking.getNonGolferSinglePrice());
            preparedStatement.setString(12, booking.getArrival());
            preparedStatement.setString(13, booking.getDeparture());
            preparedStatement.setString(14, booking.getProcess());
            preparedStatement.setDouble(15, booking.getBookingAmount());
            preparedStatement.setString(16, booking.getConsultant());
            preparedStatement.setString(17, booking.getDepositDate());
            preparedStatement.setInt(18, booking.getDepositPaid());
            preparedStatement.setInt(19, booking.getFullPaid());
            preparedStatement.setString(20, booking.getPackageName());
            preparedStatement.setString(21, booking.getBookingMadeDate());
            preparedStatement.setString(22, booking.getNotes());
            preparedStatement.setString(23, booking.getGsNumber());
            preparedStatement.executeUpdate();
            preparedStatement = con.prepareStatement("DELETE FROM BOOKINGSACCOMMODATION WHERE GSNumber = ?");
            preparedStatement.setString(1, booking.getGsNumber());
            preparedStatement.execute();
            preparedStatement = con.prepareStatement("DELETE FROM BOOKINGSGOLF WHERE GSNumber = ?");
            preparedStatement.setString(1, booking.getGsNumber());
            preparedStatement.execute();
            preparedStatement = con.prepareStatement("DELETE FROM BOOKINGSACTIVITIES WHERE GSNumber = ?");
            preparedStatement.setString(1, booking.getGsNumber());
            preparedStatement.execute();
            preparedStatement = con.prepareStatement("DELETE FROM BOOKINGSTRANSPORT WHERE GSNumber = ?");
            preparedStatement.setString(1, booking.getGsNumber());
            preparedStatement.execute();
            for (BookingAccommodation ac: booking.getBookingAccommodation()) {
                preparedStatement = con.prepareStatement("INSERT INTO BOOKINGSACCOMMODATION (GSNumber, SupplierName, Province, ProductName, MaxSleep, Nights, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, SupplierBooked) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setString(1, booking.getGsNumber());
                preparedStatement.setString(2, ac.getSupplierName());
                preparedStatement.setString(3, ac.getProvince());
                preparedStatement.setString(4, ac.getProductName());
                preparedStatement.setString(5, ac.getMaxSleep());
                preparedStatement.setInt(6, ac.getNights());
                preparedStatement.setInt(7, ac.getQuantity());
                preparedStatement.setString(8, ac.getAddTo());
                preparedStatement.setDouble(9, ac.getCostPricePerUnit());
                preparedStatement.setDouble(10, ac.getSellPricePerUnit());
                preparedStatement.setInt(11, ac.getSupplierBooked());
                preparedStatement.execute();
            }
            for (BookingGolf gf: booking.getBookingGolf()) {
                preparedStatement = con.prepareStatement("INSERT INTO BOOKINGSGOLF (GSNumber, SupplierName, Province, ProductName, Quantity, Rounds, AddTo, CostPricePerUnit, SellPricePerUnit, SupplierBooked) VALUES (?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setString(1, booking.getGsNumber());
                preparedStatement.setString(2, gf.getSupplierName());
                preparedStatement.setString(3, gf.getProvince());
                preparedStatement.setString(4, gf.getProductName());
                preparedStatement.setInt(5, gf.getQuantity());
                preparedStatement.setInt(6, gf.getRounds());
                preparedStatement.setString(7, gf.getAddTo());
                preparedStatement.setDouble(8, gf.getCostPricePerUnit());
                preparedStatement.setDouble(9, gf.getSellPricePerUnit());
                preparedStatement.setInt(10, gf.getSupplierBooked());
                preparedStatement.execute();
            }
            for (BookingActivity at: booking.getBookingActivities()) {
                preparedStatement = con.prepareStatement("INSERT INTO BOOKINGSACTIVITIES (GSNumber, SupplierName, Province, ProductName, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, SupplierBooked) VALUES (?,?,?,?,?,?,?,?,?);");
                preparedStatement.setString(1, booking.getGsNumber());
                preparedStatement.setString(2, at.getSupplierName());
                preparedStatement.setString(3, at.getProvince());
                preparedStatement.setString(4, at.getProductName());
                preparedStatement.setInt(5, at.getQuantity());
                preparedStatement.setString(6, at.getAddTo());
                preparedStatement.setDouble(7, at.getCostPricePerUnit());
                preparedStatement.setDouble(8, at.getSellPricePerUnit());
                preparedStatement.setInt(9, at.getSupplierBooked());
                preparedStatement.execute();
            }
            for (BookingTransport at: booking.getBookingTransport()) {
                preparedStatement = con.prepareStatement("INSERT INTO BOOKINGSTRANSPORT (GSNumber, SupplierName, Province, ProductName, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, SupplierBooked) VALUES (?,?,?,?,?,?,?,?,?);");
                preparedStatement.setString(1, booking.getGsNumber());
                preparedStatement.setString(2, at.getSupplierName());
                preparedStatement.setString(3, at.getProvince());
                preparedStatement.setString(4, at.getProductName());
                preparedStatement.setInt(5, at.getQuantity());
                preparedStatement.setString(6, at.getAddTo());
                preparedStatement.setDouble(7, at.getCostPricePerUnit());
                preparedStatement.setDouble(8, at.getSellPricePerUnit());
                preparedStatement.setInt(9, at.getSupplierBooked());
                preparedStatement.execute();
            }
            File template = new File("G:/My Drive/b. Templates/Costing/Costing Template.xls");
            File costing = new File("G:/My Drive/a. Bookings/a. Quotes/" + booking.getArrival() + " " + booking.getClientName() + " GS" + booking.getGsNumber());
            if(!costing.exists()){
                costing.mkdirs();
            }
            costing = new File(costing.getAbsolutePath() + "/Costing.xls");
            WritableWorkbook wb;
            wb = Workbook.createWorkbook(costing, Workbook.getWorkbook(template));
            WritableSheet sheet = wb.getSheet(0);

            Label packageName = new Label(12, 55, booking.getPackageName());
            sheet.addCell(packageName);
            Number golfersSharing = new Number(12, 56, booking.getGolfersSharing());
            sheet.addCell(golfersSharing);
            Number nonGolfersSharing = new Number(12, 57, booking.getNongolfersSharing());
            sheet.addCell(nonGolfersSharing);
            Number golfersSingle = new Number(12, 58, booking.getGolfersSingle());
            sheet.addCell(golfersSingle);
            Number nonGolfersSingle = new Number(12, 59, booking.getNongolfersSingle());
            sheet.addCell(nonGolfersSingle);
            int row = 1;
            for (BookingAccommodation ac: booking.getBookingAccommodation()) {
                Label category = new Label(1, row, "Accommodation");
                sheet.addCell(category);
                Label sName = new Label(2, row, ac.getSupplierName());
                sheet.addCell(sName);
                Label pName = new Label(3, row, ac.getProductName());
                sheet.addCell(pName);
                /*Label dates = new Label(4, row, ac.getDate() + " - From: " + ac.getFrom() + " - To: " + ac.getTo());
                sheet.addCell(dates);*/
                if (ac.getAddTo().matches("Golfer Sharing")) {
                    Label addTo = new Label(5, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Non-Golfer Sharing")) {
                    Label addTo = new Label(6, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Golfer and Non-Golfer Sharing")) {
                    Label addTo = new Label(7, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Golfer Single")) {
                    Label addTo = new Label(8, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Non-Golfer Single")){
                    Label addTo = new Label(9, row, "x");
                    sheet.addCell(addTo);
                }
                Number quantity = new Number(10, row, ac.getQuantity());
                sheet.addCell(quantity);
                Number nights = new Number(11, row, ac.getNights());
                sheet.addCell(nights);
                Number sto = new Number(12, row, ac.getCostPricePerUnit());
                sheet.addCell(sto);
                Number commission = new Number(17, row, (ac.getCostPricePerUnit() - ac.getSellPricePerUnit()) / (- ac.getSellPricePerUnit()));
                sheet.addCell(commission);
                row++;
            }
            for (BookingGolf ac: booking.getBookingGolf()) {
                Label category = new Label(1, row, "Golf");
                sheet.addCell(category);
                Label sName = new Label(2, row, ac.getSupplierName());
                sheet.addCell(sName);
                Label pName = new Label(3, row, ac.getProductName());
                sheet.addCell(pName);
                /*Label dates = new Label(4, row, ac.getDate() + " - From: " + ac.getFrom() + " - To: " + ac.getTo());
                sheet.addCell(dates);*/
                if (ac.getAddTo().matches("Golfer Sharing")) {
                    Label addTo = new Label(5, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Non-Golfer Sharing")) {
                    Label addTo = new Label(6, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Golfer and Non-Golfer Sharing")) {
                    Label addTo = new Label(7, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Golfer Single")) {
                    Label addTo = new Label(8, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Non-Golfer Single")){
                    Label addTo = new Label(9, row, "x");
                    sheet.addCell(addTo);
                }
                Number quantity = new Number(10, row, ac.getQuantity());
                sheet.addCell(quantity);
                Number nights = new Number(11, row, ac.getRounds());
                sheet.addCell(nights);
                Number sto = new Number(12, row, ac.getCostPricePerUnit());
                sheet.addCell(sto);
                Number commission = new Number(17, row, (ac.getCostPricePerUnit() - ac.getSellPricePerUnit()) / (- ac.getSellPricePerUnit()));
                sheet.addCell(commission);
                row++;
            }
            for (BookingActivity ac: booking.getBookingActivities()) {
                Label category = new Label(1, row, "Activity");
                sheet.addCell(category);
                Label sName = new Label(2, row, ac.getSupplierName());
                sheet.addCell(sName);
                Label pName = new Label(3, row, ac.getProductName());
                sheet.addCell(pName);
                /*Label dates = new Label(4, row, ac.getDate() + " - From: " + ac.getFrom() + " - To: " + ac.getTo());
                sheet.addCell(dates);*/
                if (ac.getAddTo().matches("Golfer Sharing")) {
                    Label addTo = new Label(5, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Non-Golfer Sharing")) {
                    Label addTo = new Label(6, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Golfer and Non-Golfer Sharing")) {
                    Label addTo = new Label(7, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Golfer Single")) {
                    Label addTo = new Label(8, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Non-Golfer Single")){
                    Label addTo = new Label(9, row, "x");
                    sheet.addCell(addTo);
                }
                Number quantity = new Number(10, row, ac.getQuantity());
                sheet.addCell(quantity);
                Number nights = new Number(11, row, 1);
                sheet.addCell(nights);
                Number sto = new Number(12, row, ac.getCostPricePerUnit());
                sheet.addCell(sto);
                Number commission = new Number(17, row, (ac.getCostPricePerUnit() - ac.getSellPricePerUnit()) / (- ac.getSellPricePerUnit()));
                sheet.addCell(commission);
                row++;
            }
            for (BookingTransport ac: booking.getBookingTransport()) {
                Label category = new Label(1, row, "Transport");
                sheet.addCell(category);
                Label sName = new Label(2, row, ac.getSupplierName());
                sheet.addCell(sName);
                Label pName = new Label(3, row, ac.getProductName());
                sheet.addCell(pName);
                Label dates = new Label(4, row, ac.getDate() + " - From: " + ac.getFrom() + " - To: " + ac.getTo());
                sheet.addCell(dates);
                if (ac.getAddTo().matches("Golfer Sharing")) {
                    Label addTo = new Label(5, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Non-Golfer Sharing")) {
                    Label addTo = new Label(6, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Golfer and Non-Golfer Sharing")) {
                    Label addTo = new Label(7, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Golfer Single")) {
                    Label addTo = new Label(8, row, "x");
                    sheet.addCell(addTo);
                } else if (ac.getAddTo().matches("Non-Golfer Single")){
                    Label addTo = new Label(9, row, "x");
                    sheet.addCell(addTo);
                }
                Number quantity = new Number(10, row, ac.getQuantity());
                sheet.addCell(quantity);
                Number nights = new Number(11, row, 1);
                sheet.addCell(nights);
                Number sto = new Number(12, row, ac.getCostPricePerUnit());
                sheet.addCell(sto);
                Number commission = new Number(17, row, (ac.getCostPricePerUnit() - ac.getSellPricePerUnit()) / (- ac.getSellPricePerUnit()));
                sheet.addCell(commission);
                row++;
            }
            Number golfersSharingPrice = new Number(20, 56, booking.getGolferSharingPrice());
            sheet.addCell(golfersSharingPrice);
            Number nonGolfersSharingPrice = new Number(20, 57, booking.getNonGolferSharingPrice());
            sheet.addCell(nonGolfersSharingPrice);
            Number golfersSinglePrice = new Number(20, 58, booking.getGolferSinglePrice());
            sheet.addCell(golfersSinglePrice);
            Number nonGolfersSinglePrice = new Number(20, 59, booking.getNonGolferSinglePrice());
            sheet.addCell(nonGolfersSinglePrice);
            wb.write();
            wb.close();
            log("Server> Successfully Updated Booking: " + booking.getClientName());
            return Integer.parseInt(booking.getGsNumber());
            //notifyUpdatedStudent(s[0]);
        } catch (Exception ex) {
            ex.printStackTrace();
            log("Server> updateBooking> " + ex);
            return -1;
        }
    }

    void updatePackage(TripPackage tripPackage) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE PACKAGES SET PackageName = ?, Category = ?, GolfersSharing = ?, NonGolfersSharing = ?, GolfersSingle = ?, NonGolfersSsingle = ?, TotalPackageAmount = ?, ExpiryDate = ?, Province = ? WHERE PackageID = ?");
            preparedStatement.setString(1, tripPackage.getPackageName());
            preparedStatement.setString(2, tripPackage.getCategory());
            preparedStatement.setInt(4, tripPackage.getGolfersSharing());
            preparedStatement.setInt(5, tripPackage.getNongolfersSharing());
            preparedStatement.setInt(6, tripPackage.getGolfersSingle());
            preparedStatement.setInt(7, tripPackage.getGolfersSingle());
            preparedStatement.setDouble(8, tripPackage.getTotalPackageAmount());
            preparedStatement.setString(9, tripPackage.getExpiryDate());
            preparedStatement.setString(10, tripPackage.getProvince());
            preparedStatement.executeUpdate();
            preparedStatement = con.prepareStatement("DELETE FROM PACKAGESINCLUDES WHERE PackageID = ?");
            preparedStatement.setInt(1, tripPackage.getPackageID());
            preparedStatement.execute();
            for (BookingAccommodation ac : tripPackage.getBookingAccommodation()) {
                preparedStatement = con.prepareStatement("INSERT INTO PACKAGESINCLUDE (PackageID, Province, SupplierName, ProductName, MaxCapacity, Category, NightsRounds, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setInt(1, tripPackage.getPackageID());
                preparedStatement.setString(2, ac.getProvince());
                preparedStatement.setString(3, ac.getSupplierName());
                preparedStatement.setString(4, ac.getProductName());
                preparedStatement.setString(5, ac.getMaxSleep());
                preparedStatement.setString(6, "Accommodation");
                preparedStatement.setInt(7, ac.getNights());
                preparedStatement.setInt(8, ac.getQuantity());
                preparedStatement.setString(9, ac.getAddTo());
                preparedStatement.setDouble(10, ac.getCostPricePerUnit());
                preparedStatement.setDouble(11, ac.getSellPricePerUnit());
                preparedStatement.setInt(12, 0);
                preparedStatement.execute();
            }
            for (BookingGolf gf : tripPackage.getBookingGolf()) {
                preparedStatement = con.prepareStatement("INSERT INTO PACKAGESINCLUDE (PackageID, Province, SupplierName, ProductName, MaxCapacity, Category, NightsRounds, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setInt(1, tripPackage.getPackageID());
                preparedStatement.setString(2, gf.getProvince());
                preparedStatement.setString(3, gf.getSupplierName());
                preparedStatement.setString(4, gf.getProductName());
                preparedStatement.setString(5, "1");
                preparedStatement.setString(6, "Golf");
                preparedStatement.setInt(7, gf.getRounds());
                preparedStatement.setInt(8, gf.getQuantity());
                preparedStatement.setString(9, gf.getAddTo());
                preparedStatement.setDouble(10, gf.getCostPricePerUnit());
                preparedStatement.setDouble(11, gf.getSellPricePerUnit());
                preparedStatement.setInt(12, 0);
                preparedStatement.execute();
            }
            for (BookingActivity at : tripPackage.getBookingActivities()) {
                preparedStatement = con.prepareStatement("INSERT INTO PACKAGESINCLUDE (PackageID, Province, SupplierName, ProductName, MaxCapacity, Category, NightsRounds, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setInt(1, tripPackage.getPackageID());
                preparedStatement.setString(2, at.getProvince());
                preparedStatement.setString(3, at.getSupplierName());
                preparedStatement.setString(4, at.getProductName());
                preparedStatement.setString(5, "1");
                preparedStatement.setString(6, "Activity");
                preparedStatement.setInt(7, 1);
                preparedStatement.setInt(8, at.getQuantity());
                preparedStatement.setString(9, at.getAddTo());
                preparedStatement.setDouble(10, at.getCostPricePerUnit());
                preparedStatement.setDouble(11, at.getSellPricePerUnit());
                preparedStatement.setInt(12, 0);
                preparedStatement.execute();
            }
            for (BookingTransport at : tripPackage.getBookingTransport()) {
                preparedStatement = con.prepareStatement("INSERT INTO PACKAGESINCLUDE (PackageID, Province, SupplierName, ProductName, MaxCapacity, Category, NightsRounds, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setInt(1, tripPackage.getPackageID());
                preparedStatement.setString(2, at.getProvince());
                preparedStatement.setString(3, at.getSupplierName());
                preparedStatement.setString(4, at.getProductName());
                preparedStatement.setString(5, "1");
                preparedStatement.setString(6, "Transport");
                preparedStatement.setInt(7, 1);
                preparedStatement.setInt(8, at.getQuantity());
                preparedStatement.setString(9, at.getAddTo());
                preparedStatement.setDouble(10, at.getCostPricePerUnit());
                preparedStatement.setDouble(11, at.getSellPricePerUnit());
                preparedStatement.setInt(12, 0);
                preparedStatement.execute();
            }
            log("Server> Successfully Updated Package: " + tripPackage.getPackageName());
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updatePackage> " + ex);
        }
    }

    void updateSupplier(Supplier supplier) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE SUPPLIERS SET SupplierName = ?, Category = ?, Province = ?, Address = ? WHERE SupplierNumber = ?");
            preparedStatement.setString(1, supplier.getSupplierName());
            preparedStatement.setString(2, supplier.getCategory());
            preparedStatement.setString(3, supplier.getProvince());
            preparedStatement.setString(4, supplier.getAddress());
            preparedStatement.setInt(5, supplier.getSupplierNumber());
            preparedStatement.executeUpdate();
            log("Server> Successfully Updated Supplier: " + supplier.getSupplierName());
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updateSupplier> " + ex);
        }
    }

    void updateContactDetails(ContactDetails cd) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE CONTACTDETAILS SET PersonName = ?, PersonPosition = ?, ContactNumber = ?, Email = ? WHERE ContactDetailsID = ?");
            preparedStatement.setString(1, cd.getPersonName());
            preparedStatement.setString(2, cd.getPosition());
            preparedStatement.setString(3, cd.getNumber());
            preparedStatement.setString(4, cd.getEmail());
            preparedStatement.setInt(5, cd.getContactDetailsID());
            preparedStatement.executeUpdate();
            log("Server> Successfully Updated Contact Details: " + cd.getContactDetailsID());
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updateContactDetails> " + ex);
        }
    }

    void updateLogin(Login login) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE LOGINS SET LoginName = ?, Username = ?, Password = ? WHERE LoginID = ?");
            preparedStatement.setString(1, login.getLoginName());
            preparedStatement.setString(2, login.getUsername());
            preparedStatement.setString(3, login.getPassword());
            preparedStatement.setInt(4, login.getLoginID());
            preparedStatement.executeUpdate();
            log("Server> Successfully Updated Login: " + login.getLoginName());
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updateLogin> " + ex);
        }
    }

    void updateTransaction(Transaction transaction) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE TRANSACTIONS SET TransactionType = ?, GSNumber = ?, Other = ?, Reference = ?, Amount = ?, TransactionDate = ? WHERE TransactionID = ?");
            preparedStatement.setString(1, transaction.getTransactionType());
            preparedStatement.setString(2, transaction.getGsNumber());
            preparedStatement.setString(3, transaction.getOther());
            preparedStatement.setString(4, transaction.getReference());
            preparedStatement.setDouble(5, transaction.getAmount());
            preparedStatement.setString(6, transaction.getTransactionDate());
            preparedStatement.setInt(7, transaction.getID());
            preparedStatement.executeUpdate();

            List<Transaction>transactions = new ArrayList<>();
            preparedStatement = con.prepareStatement("SELECT * FROM TRANSACTIONS WHERE GSNumber = ?;");
            preparedStatement.setString(1, transaction.getGsNumber());
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                transactions.add(new Transaction(rs.getInt("TransactionID"), rs.getString("TransactionType"), rs.getString("GSNumber"), rs.getString("Other"), rs.getString("Reference"), rs.getDouble("Amount"), rs.getString("TransactionDate")));
            }

            double totalRecieved = 0.0;
            double totalPaid = 0.0;
            for (Transaction t:transactions){
                if(t.getTransactionType().matches("Money Came In")){
                    totalRecieved += t.getAmount();
                } else if(t.getTransactionType().matches("Supplier Paid")){
                    totalPaid += t.getAmount();
                }
            }

            double totalClientInvoice = 0.0;
            preparedStatement = con.prepareStatement("SELECT BookingAmount FROM BOOKINGS WHERE GSNumber = ?;");
            preparedStatement.setString(1, transaction.getGsNumber().substring(2));
            rs = preparedStatement.executeQuery();
            rs.next();
            totalClientInvoice = rs.getDouble("BookingAmount");
            double totalSupplierInvoice = 0.0;
            Booking booking = getBooking(transaction.getGsNumber().substring(2));
            totalSupplierInvoice = getTotalSupplierInvoice(booking);
            String process = "";
            if(totalRecieved >= (totalClientInvoice * 0.4)) {
                process = "PendingDepositRecieved";
                preparedStatement = con.prepareStatement("UPDATE Bookings SET DepositPaid = ? WHERE GSNUMBER = ?");
                preparedStatement.setInt(1, 1);
                preparedStatement.setString(2, transaction.getGsNumber().substring(2));
                preparedStatement.execute();
            }
            if (totalPaid >= (totalSupplierInvoice * 0.1)) {
                process = "PendingDepositPaid";
            }
            if (totalRecieved >= (totalClientInvoice * 0.95)) {
                process = "PendingFullRecieved";
                preparedStatement = con.prepareStatement("UPDATE Bookings SET FullPaid = ? WHERE GSNUMBER = ?");
                preparedStatement.setInt(1, 1);
                preparedStatement.setString(2, transaction.getGsNumber().substring(2));
                preparedStatement.execute();
            }
            if (totalPaid >= (totalSupplierInvoice * 0.95)) {
                process = "ConfirmedFullPaid";
            }
            if(!process.matches("")){
                updateBookingProcess(transaction.getGsNumber(), process, booking.getArrival(), booking.getClientName());
            }
            log("Server> Successfully Updated Transaction: " + transaction.getTransactionType());
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updateTransaction> " + ex);
        }
    }

    void updateBookingProcess(String gsNumber, String process, String arrival, String name){
        try {
            arrival = arrival.substring(2);
            String oldfolder = "";
            Booking booking = getBooking(gsNumber);
            if(booking.getProcess().matches("Quote")){
                oldfolder = "a. Quotes";
            } else if(booking.getProcess().matches("PendingBookingMade")){
                oldfolder = "b. PendingBookingMade";
            } else if(booking.getProcess().matches("PendingDepositRecieved")){
                oldfolder = "c. PendingDepositRecieved";
            } else if(booking.getProcess().matches("PendingDepositPaid")){
                oldfolder = "d. PendingDepositPaid";
            } else if(booking.getProcess().matches("PendingFullRecieved")){
                oldfolder = "e. PendingFullRecieved";
            } else if(booking.getProcess().matches("ConfirmedFullPaid")){
                oldfolder = "f. ConfirmedFullPaid";
            } else if(booking.getProcess().matches("ArchiveQuote")){
                oldfolder = "z. Archive/ArchiveQuote";
            } else if(booking.getProcess().matches("ArchiveComplete")){
                oldfolder = "z. Archive/ArchiveComplete";
            }

            if(gsNumber.startsWith("GS")){
                gsNumber = gsNumber.substring(2);
            }
            File oldFolder = new File(Server.BOOKINGS_FOLDER.getAbsolutePath() + "/" + oldfolder + "/" + arrival + " " + name + " GS" + gsNumber);
            String newfolder = "";
            if(process.matches("Quote")){
                newfolder = "a. Quotes";
                removeBookingsMade(gsNumber);
            } else if(process.matches("PendingBookingMade")){
                newfolder = "b. PendingBookingMade";
            } else if(process.matches("PendingDepositRecieved")){
                newfolder = "c. PendingDepositRecieved";
            } else if(process.matches("PendingDepositPaid")){
                newfolder = "d. PendingDepositPaid";
            } else if(process.matches("PendingFullRecieved")){
                newfolder = "e. PendingFullRecieved";
            } else if(process.matches("ConfirmedFullPaid")){
                newfolder = "f. ConfirmedFullPaid";
            } else if(process.matches("Archive")){
                newfolder = "z. Archive";
            } else if(process.matches("ArchiveQuote")){
                newfolder = "z. Archive/ArchiveQuote";
            } else if(process.matches("ArchiveComplete")){
                newfolder = "z. Archive/ArchiveComplete";
            }

            String source = oldFolder.getAbsolutePath();
            File srcDir = new File(source);
            String destination = Server.BOOKINGS_FOLDER.getAbsolutePath() + "/" + newfolder + "/" + arrival + " " + name + " GS" + gsNumber;
            File destDir = new File(destination);
            if(!destDir.exists()) {
                destDir.mkdirs();
            }
            FileUtils.copyDirectory(srcDir, destDir);
            FileUtils.deleteDirectory(oldFolder);
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE BOOKINGS SET Process = ? WHERE GSNumber = ?");
            preparedStatement.setString(1, process);
            preparedStatement.setString(2, gsNumber);
            preparedStatement.execute();
            log("Server> Successfully Suppliers Booked : " + gsNumber);
            //notifyUpdatedStudent(s[0]);
        } catch (Exception ex) {
            ex.printStackTrace();
            log("Server> updateBookingProcess> " + ex);
        }
    }

    void updateSuppliersBookedAccommodation(int id, int process){
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE BOOKINGSACCOMMODATION SET SupplierBooked = ? WHERE BAID = ?");
            preparedStatement.setInt(1, process);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
            log("Server> Successfully Suppliers Booked : " + id);
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updateSuppliersBookedAccommodation> " + ex);
        }
    }

    void updateSuppliersBookedGolf(int id, int process){
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE BOOKINGSGOLF SET SupplierBooked = ? WHERE BGID = ?");
            preparedStatement.setInt(1, process);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
            log("Server> Successfully Suppliers Booked : " + id);
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updateSuppliersBookedGolf> " + ex);
        }
    }

    void updateSuppliersBookedTransport(int id, int process){
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE BOOKINGSTRANSPORT SET SupplierBooked = ? WHERE BRID = ?");
            preparedStatement.setInt(1, process);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
            log("Server> Successfully Suppliers Booked : " + id);
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updateSuppliersBookedTransport> " + ex);
        }
    }

    void updateSuppliersBookedActivities(int id, int process){
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE BOOKINGSACTIVITIES SET SupplierBooked = ? WHERE BTID = ?");
            preparedStatement.setInt(1, process);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
            log("Server> Successfully Suppliers Booked : " + id);
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updateSuppliersBookedActivities> " + ex);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Remove">
    void removeBooking(int gsNumber) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT Process FROM BOOKINGS WHERE GSNumber = ?;");
            preparedStatement.setInt(1, gsNumber);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            String process = rs.getString("Process");
            if(process.matches("Quote")){
                process = "a. Quotes";
            } else if(process.matches("PendingBookingMade")){
                process = "b. PendingBookingMade";
            } else if(process.matches("PendingDepositRecieved")){
                process = "c. PendingDepositRecieved";
            } else if(process.matches("PendingDepositPaid")){
                process = "d. PendingDepositPaid";
            } else if(process.matches("PendingFullRecieved")){
                process = "e. PendingFullRecieved";
            } else if(process.matches("ConfirmedFullPaid")){
                process = "f. ConfirmedFullPaid";
            }
            preparedStatement = con.prepareStatement("DELETE FROM BOOKINGS WHERE GSNumber = ?;");
            preparedStatement.setInt(1, gsNumber);
            preparedStatement.executeUpdate();
            preparedStatement = con.prepareStatement("DELETE FROM BOOKINGSACCOMMODATION WHERE GSNumber = ?;");
            preparedStatement.setInt(1, gsNumber);
            preparedStatement.executeUpdate();
            preparedStatement = con.prepareStatement("DELETE FROM BOOKINGSGOLF WHERE GSNumber = ?;");
            preparedStatement.setInt(1, gsNumber);
            preparedStatement.executeUpdate();
            preparedStatement = con.prepareStatement("DELETE FROM BOOKINGSACTIVITIES WHERE GSNumber = ?;");
            preparedStatement.setInt(1, gsNumber);
            preparedStatement.executeUpdate();
            File file = new File(Server.BOOKINGS_FOLDER + "/" + process);
            File [] files = file.listFiles();
            for(File f:files) {
                if(f.getAbsolutePath().endsWith("GS" + gsNumber)){
                    FileUtils.deleteDirectory(new File(f.getAbsolutePath()));
                    break;
                }
            }
            log("Server> Successfully Removed Booking: " + gsNumber);
            //notifyUpdatedStudent(studentNumber);
        } catch (Exception ex) {
            ex.printStackTrace();
            log("Server> removeBooking> " + ex);
        }
    }

    void removePackage(int gsNumber) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("DELETE FROM PACKAGES WHERE PackageID = ?;");
            preparedStatement.setInt(1, gsNumber);
            preparedStatement.executeUpdate();
            preparedStatement = con.prepareStatement("DELETE FROM PACKAGESINCLUDE WHERE PackageID = ?;");
            preparedStatement.setInt(1, gsNumber);
            preparedStatement.executeUpdate();
            log("Server> Successfully Removed Package: " + gsNumber);
            //notifyUpdatedStudent(studentNumber);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> removePackage> " + ex);
        }
    }

    void removeSupplier(int supplierNumber) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("DELETE FROM SUPPLIERS WHERE SupplierNumber = ?;");
            preparedStatement.setInt(1, supplierNumber);
            preparedStatement.executeUpdate();
            //TODO remove from packages bookings and delete rates
            log("Server> Successfully Removed Booking: " + supplierNumber);
            //notifyUpdatedStudent(studentNumber);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> removeBooking> " + ex);
        }
    }

    void removeContactDetails(int contactDetailsID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("DELETE FROM CONTACTDETAILS WHERE ContactDetailsID = ?;");
            preparedStatement.setInt(1, contactDetailsID);
            preparedStatement.executeUpdate();
            log("Server> Successfully Removed Contact Details: " + contactDetailsID);
            //notifyUpdatedStudent(studentNumber);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> removeContactDetails> " + ex);
        }
    }

    void removeLogin(int loginID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("DELETE FROM LOGINS WHERE LoginID = ?;");
            preparedStatement.setInt(1, loginID);
            preparedStatement.executeUpdate();
            log("Server> Successfully Removed Login: " + loginID);
            //notifyUpdatedStudent(studentNumber);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> removeLogin> " + ex);
        }
    }

    void removeBookingsMade(String gsNumber) {
        if(gsNumber.startsWith("GS")){
            gsNumber = gsNumber.substring(2);
        }
        try {
            int intGSNumber = Integer.parseInt(gsNumber);
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE BOOKINGSACCOMMODATION SET SuppplierBooked = ? WHERE GSNumber = ?;");
            preparedStatement.setInt(1, 0);
            preparedStatement.setInt(2, intGSNumber);
            preparedStatement.executeUpdate();
            preparedStatement = con.prepareStatement("UPDATE BOOKINGSGOLF SET SuppplierBooked = ? WHERE GSNumber = ?;");
            preparedStatement.setInt(1, 0);
            preparedStatement.setInt(2, intGSNumber);
            preparedStatement.executeUpdate();
            preparedStatement = con.prepareStatement("UPDATE BOOKINGSTRANSPORT SET SuppplierBooked = ? WHERE GSNumber = ?;");
            preparedStatement.setInt(1, 0);
            preparedStatement.setInt(2, intGSNumber);
            preparedStatement.executeUpdate();
            preparedStatement = con.prepareStatement("UPDATE BOOKINGSACTIVITIES SET SuppplierBooked = ? WHERE GSNumber = ?;");
            preparedStatement.setInt(1, 0);
            preparedStatement.setInt(2, intGSNumber);
            preparedStatement.executeUpdate();
            log("Server> Successfully Removed Bookings Made for : GS" + gsNumber);
            //notifyUpdatedStudent(studentNumber);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> removeLogin> " + ex);
        }
    }

    void removeTransaction(int transactionID, String gsNumber) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("DELETE FROM TRANSACTIONS WHERE TransactionID = ?;");
            preparedStatement.setInt(1, transactionID);
            preparedStatement.executeUpdate();

            List<Transaction>transactions = new ArrayList<>();
            preparedStatement = con.prepareStatement("SELECT * FROM TRANSACTIONS WHERE GSNumber = ?;");
            preparedStatement.setString(1, gsNumber);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                transactions.add(new Transaction(rs.getInt("TransactionID"), rs.getString("TransactionType"), rs.getString("GSNumber"), rs.getString("Other"), rs.getString("Reference"), rs.getDouble("Amount"), rs.getString("TransactionDate")));
            }

            double totalRecieved = 0.0;
            double totalPaid = 0.0;
            for (Transaction t:transactions){
                if(t.getTransactionType().matches("Money Came In")){
                    totalRecieved += t.getAmount();
                } else if(t.getTransactionType().matches("Supplier Paid")){
                    totalPaid += t.getAmount();
                }
            }

            double totalClientInvoice = 0.0;
            preparedStatement = con.prepareStatement("SELECT BookingAmount FROM BOOKINGS WHERE GSNumber = ?;");
            preparedStatement.setString(1, gsNumber);
            rs = preparedStatement.executeQuery();
            rs.next();
            totalClientInvoice = rs.getDouble("BookingAmount");
            double totalSupplierInvoice = 0.0;
            Booking booking = getBooking(gsNumber);
            totalSupplierInvoice = getTotalSupplierInvoice(booking);
            String process = "";
            if(totalRecieved >= (totalClientInvoice * 0.4)) {
                process = "PendingDepositRecieved";
                preparedStatement = con.prepareStatement("UPDATE Bookings SET DepositPaid = ? WHERE GSNUMBER = ?");
                preparedStatement.setInt(1, 1);
                preparedStatement.setString(2, gsNumber);
                preparedStatement.execute();
            }
            if (totalPaid >= (totalSupplierInvoice * 0.1)) {
                process = "PendingDepositPaid";
            }
            if (totalRecieved >= (totalClientInvoice * 0.95)) {
                process = "PendingFullRecieved";
                preparedStatement = con.prepareStatement("UPDATE Bookings SET FullPaid = ? WHERE GSNUMBER = ?");
                preparedStatement.setInt(1, 1);
                preparedStatement.setString(2, gsNumber);
                preparedStatement.execute();
            }
            if (totalPaid >= (totalSupplierInvoice * 0.95)) {
                process = "ConfirmedFullPaid";
            }
            if(process.matches("")){
                process = "Quote";
                if(getBookingsMade(gsNumber)){
                    process = "PendingBookingMade";
                }
            }
            updateBookingProcess(gsNumber, process, booking.getArrival(), booking.getClientName());
            log("Server> Successfully Removed Transaction: " + transactionID);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> removeTransaction> " + ex);
        }
    }
    //</editor-fold>

    /*private void saveLecturerImage(String lecturerNumber, byte[] imageBytes) {
        try {
            if (imageBytes != null) {
                File newFile = new File(Server.LECTURER_IMAGES + "/" + lecturerNumber + "/profile.jpg");
                newFile.getParentFile().mkdirs();
                Files.write(newFile.toPath(), imageBytes);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log("Server> saveLecturerImage> " + ex);
        }
    }*/

    void deleteFile(String location, String fileName) {
        File fileToDelete = new File(location + "/" + fileName);
        if (fileToDelete.exists()) {
            fileToDelete.delete();
            try {
                Thread.sleep(50);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /*public void generateInvoice(String gsNumber){
        try {
            Booking booking = getBooking(gsNumber);
            StringBuilder packageIncludes = new StringBuilder();
            if(booking.getBookingAccommodation().size()>0){
                for(BookingAccommodation ba:booking.getBookingAccommodation()){
                    packageIncludes.append("Accommodation:");
                    packageIncludes.append(ba.getNights() + " Nights stay at " + ba.getSupplierName() + " in a " + ba.getProductName());
                    /*packageIncludes.append("Dates: ");//get Date
                    packageIncludes.append("Meal Basis:" )//get Meal Basis;
                }
            }
            if(booking.getBookingGolf().size()>0) {
                packageIncludes.append("Golf:");
                for (BookingGolf bg : booking.getBookingGolf()) {
                    if(bg.getProductName().contains("18 Holes Non-Affiliated")) {
                        packageIncludes.append(bg.getQuantity() + " round(s) of Golf at " + bg.getSupplierName());
                    } else {
                        packageIncludes.append(bg.getQuantity() + " " + bg.getProductName() + " at " + bg.getSupplierName());
                    }
                }
            }
            if(booking.getBookingActivities().size()>0) {
                packageIncludes.append("Activities:");
                for (BookingActivity ba : booking.getBookingActivities()) {
                    packageIncludes.append(ba.getQuantity() + " x " + ba.getProductName() + " at " + ba.getSupplierName());
                }
            }
            if(booking.getBookingTransport().size()>0) {
                packageIncludes.append("Transport:");
                for (BookingTransport bt : booking.getBookingTransport()) {
                    packageIncludes.append("From: " + bt.getFrom() + " To: " + bt.getTo() + " on " + bt.getDate());
                }
            }

            Map<String, Object> dataSource = new HashMap<String, Object>();
            dataSource.put("name", booking.getClientName());
            dataSource.put("email", booking.getEmail());
            dataSource.put("contactnumber", booking.getContactNumber());
            dataSource.put("gsnumbertop", booking.getGsNumber());
            dataSource.put("packagename", booking.getPackageName());
            dataSource.put("packageincludes", packageIncludes);
            dataSource.put("total", "R " + booking.getBookingAmount());
            dataSource.put("gsnumberbottom", booking.getGsNumber());
            InputStream inputStream = new FileInputStream(new File("G:/My Drive/b. Templates/Invoice/GolfInSouthAfricaInvoice.jrxml"));
            JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, dataSource, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfFile(jasperPrint, "G:/My Drive/InvoiceTest.pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public Double getTotalSupplierInvoice(Booking booking){
        double x = 0.0;
        for(BookingAccommodation a:booking.getBookingAccommodation()){
            x += a.getCostPricePerUnit()*a.getQuantity()*a.getNights();
        }
        for(BookingGolf a:booking.getBookingGolf()){
            x += a.getCostPricePerUnit()*a.getQuantity()*a.getRounds();
        }
        for(BookingTransport a:booking.getBookingTransport()){
            x += a.getCostPricePerUnit()*a.getQuantity();
        }
        for(BookingActivity a:booking.getBookingActivities()){
            x += a.getCostPricePerUnit()*a.getQuantity();
        }
        return x;
    }

    public Double[] getBookingPerPerson(String GSNumber){
        Double[] pp = new Double[4];
        pp[0] = 0.00;
        pp[1] = 0.00;
        pp[2] = 0.00;
        pp[3] = 0.00;
        Booking booking = null;
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGS WHERE GSNumber = ?;");
            preparedStatement.setInt(1, Integer.parseInt(GSNumber));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                booking = new Booking(Integer.toString(rs.getInt("GSNumber")), rs.getString("ClientName"), rs.getString("ContactNumber"), rs.getString("Email"), rs.getInt("GolfersSharing"), rs.getDouble("GolfersSharingPrice"), rs.getInt("NonGolfersSharing"), rs.getDouble("NonGolfersSharingPrice"), rs.getInt("GolfersSingle"), rs.getDouble("GolfersSinglePrice"), rs.getInt("NonGolfersSingle"), rs.getDouble("NonGolfersSinglePrice"), rs.getString("Arrival"), rs.getString("Departure"), rs.getString("Process"), rs.getDouble("BookingAmount"), rs.getString("Consultant"), rs.getString("DepositDate"), rs.getInt("DepositPaid"), rs.getInt("FullPaid"), rs.getString("PackageName"), rs.getString("BookingMadeDate"), null, getBookingAccommodation(rs.getString("GSNumber")), getBookingGolf(rs.getString("GSNumber")), getBookingActivities(rs.getString("GSNumber")), getBookingTransport(rs.getString("GSNumber")), getBookingTransactions("GS" + rs.getString("GSNumber")));
            }
            if(booking!=null) {
                for (BookingAccommodation b : booking.getBookingAccommodation()) {
                    if(b.getAddTo().matches("Golfer Sharing")) {
                        pp[0] = pp[0] + (b.getSellPricePerUnit() * b.getQuantity() * b.getNights());
                    } else if (b.getAddTo().matches("Non-Golfer Sharing")) {
                        pp[1] = pp[1] + (b.getSellPricePerUnit() * b.getQuantity() * b.getNights());
                    } else if (b.getAddTo().matches("Golfer and Non-Golfer Sharing")) {
                        pp[0] = pp[0] + ((b.getSellPricePerUnit() * b.getQuantity() * b.getNights())/2);
                        pp[1] = pp[1] + ((b.getSellPricePerUnit() * b.getQuantity() * b.getNights())/2);
                    } else if (b.getAddTo().matches("Golfer Single")) {
                        pp[2] = pp[2] + (b.getSellPricePerUnit() * b.getQuantity() * b.getNights());
                    } else if (b.getAddTo().matches("Non-Golfer Single")){
                        pp[3] = pp[3] + (b.getSellPricePerUnit() * b.getQuantity() * b.getNights());
                    }
                }
                for (BookingGolf b : booking.getBookingGolf()) {
                    if(b.getAddTo().matches("Golfer Sharing")) {
                        pp[0] = pp[0] + (b.getSellPricePerUnit() * b.getQuantity() * b.getRounds());
                    } else if (b.getAddTo().matches("Non-Golfer Sharing")) {
                        pp[1] = pp[1] + (b.getSellPricePerUnit() * b.getQuantity() * b.getRounds());
                    } else if (b.getAddTo().matches("Golfer and Non-Golfer Sharing")) {
                        pp[0] = pp[0] + ((b.getSellPricePerUnit() * b.getQuantity() * b.getRounds())/2);
                        pp[1] = pp[1] + ((b.getSellPricePerUnit() * b.getQuantity() * b.getRounds())/2);
                    } else if (b.getAddTo().matches("Golfer Single")) {
                        pp[2] = pp[2] + (b.getSellPricePerUnit() * b.getQuantity() * b.getRounds());
                    } else if (b.getAddTo().matches("Non-Golfer Single")){
                        pp[3] = pp[3] + (b.getSellPricePerUnit() * b.getQuantity() * b.getRounds());
                    }
                }
                for (BookingTransport b : booking.getBookingTransport()) {
                    if(b.getAddTo().matches("Golfer Sharing")) {
                        pp[0] = pp[0] + (b.getSellPricePerUnit() * b.getQuantity());
                    } else if (b.getAddTo().matches("Non-Golfer Sharing")) {
                        pp[1] = pp[1] + (b.getSellPricePerUnit() * b.getQuantity() );
                    } else if (b.getAddTo().matches("Golfer and Non-Golfer Sharing")) {
                        pp[0] = pp[0] + ((b.getSellPricePerUnit() * b.getQuantity())/2);
                        pp[1] = pp[1] + ((b.getSellPricePerUnit() * b.getQuantity())/2);
                    } else if (b.getAddTo().matches("Golfer Single")) {
                        pp[2] = pp[2] + (b.getSellPricePerUnit() * b.getQuantity());
                    } else if (b.getAddTo().matches("Non-Golfer Single")){
                        pp[3] = pp[3] + (b.getSellPricePerUnit() * b.getQuantity());
                    }
                }
                for (BookingActivity b : booking.getBookingActivities()) {
                    if(b.getAddTo().matches("Golfer Sharing")) {
                        pp[0] = pp[0] + (b.getSellPricePerUnit() * b.getQuantity());
                    } else if (b.getAddTo().matches("Non-Golfer Sharing")) {
                        pp[1] = pp[1] + (b.getSellPricePerUnit() * b.getQuantity());
                    } else if (b.getAddTo().matches("Golfer and Non-Golfer Sharing")) {
                        pp[0] = pp[0] + ((b.getSellPricePerUnit() * b.getQuantity())/2);
                        pp[1] = pp[1] + ((b.getSellPricePerUnit() * b.getQuantity())/2);
                    } else if (b.getAddTo().matches("Golfer Single")) {
                        pp[2] = pp[2] + (b.getSellPricePerUnit() * b.getQuantity());
                    } else if (b.getAddTo().matches("Non-Golfer Single")){
                        pp[3] = pp[3] + (b.getSellPricePerUnit() * b.getQuantity());
                    }
                }
            }
            if(booking.getGolfersSharing()!=0){
                pp[0] = pp[0] / booking.getGolfersSharing();
                if(pp[0]%50!=0){
                    pp[0] = pp[0] + (50 - pp[0]%50);
                }
            }
            if(booking.getNongolfersSharing()!=0){
                pp[1] = pp[1] / booking.getNongolfersSharing();
                if(pp[1]%50!=0){
                    pp[1] = pp[1] + (50 - pp[1]%50);
                }
            }
            if(booking.getGolfersSingle()!=0){
                pp[2] = pp[2] / booking.getGolfersSingle();
                if(pp[2]%50!=0){
                    pp[2] = pp[2] + (50 - pp[2]%50);
                }
            }
            if(booking.getNongolfersSingle()!=0){
                pp[3] = pp[3] / booking.getNongolfersSingle();
                if(pp[3]%50!=0){
                    pp[3] = pp[3] + (50 - pp[3]%50);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getBookingPerPerson> " + ex);
            return null;
        }
        return pp;
    }

    void log(String logDetails) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            System.out.println(dateFormat.format(date) + " : " + logDetails);
            /*File logFile = Server.LOG_FILE.getAbsoluteFile();
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            /*FileWriter fw = new FileWriter(logFile.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(dateFormat.format(date) + " : " + logDetails);
            bw.newLine();
            bw.close();
            fw.close();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

