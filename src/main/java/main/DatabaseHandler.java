package main;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import models.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
                        "NonGolfersSharing INTEGER , " +
                        "GolfersSingle INTEGER , " +
                        "NonGolfersSingle INTEGER , " +
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
                        "SupplierBooked INTEGER ," +
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
                        "Carts INTEGER, " +
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
                        "SupplierBooked INTEGER ," +
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
                        "SupplierBooked INTEGER ," +
                        "AmountPaid REAL," +
                        "AddTo TEXT);");
                stmt.execute("CREATE TABLE SUPPLIERS (" +
                        "SupplierNumber INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "SupplierName TEXT, " +
                        "Category TEXT, " +
                        "Province TEXT, " +
                        "Address TEXT, " +
                        "CoOrdinates TEXT);");
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
                suppliers.add(new Supplier(rs.getInt("SupplierNumber"), rs.getString("SupplierName"), rs.getString("Category"), rs.getString("Province"), rs.getString("Address"), rs.getString("coOrdinates"), getSuppliersContactDetails(rs.getInt("SupplierNumber"))));
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

    List<Booking> getBookings() {
        List<Booking> bookings = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGS;");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                bookings.add(new Booking(Integer.toString(rs.getInt("GSNumber")), rs.getString("ClientName"), rs.getString("ContactNumber"), rs.getString("Email"), rs.getInt("GolfersSharing"), rs.getInt("NonGolfersSharing"), rs.getInt("GolfersSingle"), rs.getInt("NonGolfersSingle"), rs.getString("Arrival"), rs.getString("Departure"), rs.getString("Process"), rs.getDouble("BookingAmount"), rs.getString("Consultant"), rs.getString("DepositDate"), rs.getInt("DepositPaid"), rs.getInt("FullPaid"), rs.getString("PackageName"), rs.getString("BookingMadeDate"), null, getBookingAccommodation(rs.getString("GSNumber")), getBookingGolf(rs.getString("GSNumber")), getBookingActivities(rs.getString("GSNumber")), getBookingTransport(rs.getString("GSNumber"))));
            }
            log("Server> Successfully Got all Bookings");
            return bookings;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getBookings> " + ex);
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
                bookingAccommodation.add(new BookingAccommodation(rs.getString("SupplierName"), rs.getString("Province"), rs.getString("ProductName"), rs.getString("MaxSleep"), rs.getString("Arrival"), rs.getInt("Nights"), rs.getInt("Quantity"), rs.getDouble("CostPricePerUnit"), rs.getDouble("SellPricePerUnit"), rs.getString("AddTo"),0, rs.getDouble("AmountPaid")));
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
                bookingGolf.add(new BookingGolf(rs.getString("SupplierName"), rs.getString("Province"), rs.getString("ProductName"), Arrays.asList(rs.getString("Dates").substring(1, rs.getString("Dates").length() - 1).split(", ")), rs.getInt("Quantity"), rs.getInt("Rounds"), rs.getInt("Carts"), rs.getDouble("CostPricePerUnit"), rs.getDouble("SellPricePerUnit"), rs.getString("AddTo"),0, rs.getDouble("AmountPaid")));
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
                activities.add(new BookingActivity(rs.getString("SupplierName"), rs.getString("Province"), rs.getString("ProductNumber"), rs.getString("ActivityDate"), rs.getInt("Quantity"), rs.getDouble("CostPricePerUnit"), rs.getDouble("SellPricePerUnit"), rs.getString("AddTo"), 0, rs.getDouble("AmountPaid")));
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
                bookingTransport.add(new BookingTransport(rs.getString("SupplierName"), rs.getString("Province"), rs.getString("ProductName"), rs.getString("TravelDate"), rs.getInt("Quantity"), rs.getString("TravelFrom"), rs.getString("TravelTo"), rs.getDouble("CostPricePerUnit"), rs.getDouble("SellPricePerUnit"), rs.getString("AddTo"),0, rs.getDouble("AmountPaid")));
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
            Workbook wb;
            if(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1).matches("xls")) {
                try {
                    wb = Workbook.getWorkbook(file);
                    Sheet sheet = wb.getSheet(0);
                    Cell cell;//Start 0,0 column, row
                    int rows = sheet.getRows();
                    int columns = sheet.getColumns();
                    for (int i = 0; i < rows-1; i++) {//rows
                        for (int j = 0; j < columns-1; j++) {//columns
                            try {
                                cell = sheet.getCell(j, i);
                                if (cell.getContents().contains("#")) {
                                    LocalDate start = LocalDate.parse(cell.getContents().split("#")[0]);
                                    LocalDate expire = LocalDate.parse(cell.getContents().split("#")[1]);
                                    LocalDate today = LocalDate.now();
                                    if (today.isEqual(start) || today.isAfter(start)) {
                                        if (today.isEqual(expire) || today.isBefore(expire)) {
                                            accommodation.add(new ProductAccommodation(supplierName, category, province, sheet.getCell(0, i).getContents(), expire.toString(), Integer.parseInt(sheet.getCell(2, i).getContents()), sheet.getCell(j + 1, i).getContents(), sheet.getCell(1, i).getContents()));
                                        }
                                    }
                                }
                                if (cell.getContents().matches("")) {
                                    if (j == 0) {
                                        i = rows;
                                    } else {
                                        j = columns;
                                    }
                                }
                            } catch (ArrayIndexOutOfBoundsException ex) {

                            }
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
            Workbook wb;
            if(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1).matches("xls")) {
                try {
                    wb = Workbook.getWorkbook(file);
                    Sheet sheet = wb.getSheet(0);
                    Cell cell;//Start 0,0 column, row
                    int rows = sheet.getRows();
                    int columns = sheet.getColumns();
                    for (int i = 0; i < rows; i++) {//rows
                        for (int j = 0; j < columns; j++) {//columns
                            cell = sheet.getCell(j, i);
                            if (cell.getContents().contains("#")) {
                                LocalDate start = LocalDate.parse(cell.getContents().split("#")[0]);
                                LocalDate expire = LocalDate.parse(cell.getContents().split("#")[1]);
                                LocalDate today = LocalDate.now();
                                if (today.isEqual(start) || today.isAfter(start)) {
                                    if (today.isEqual(expire) || today.isBefore(expire)) {
                                        golf.add(new ProductGolf(supplierName, category, province, sheet.getCell(0, i).getContents(), expire.toString(), sheet.getCell(j + 1, i).getContents(), sheet.getCell(1, i).getContents(), Integer.parseInt(sheet.getCell(2, i).getContents())));
                                    }
                                }
                            }
                            if (cell.getContents().matches("")) {
                                if (j == 0) {
                                    i = rows;
                                } else {
                                    j = columns;
                                }
                            }
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
                    for (int i = 0; i < rows; i++) {//rows
                        for (int j = 0; j < columns; j++) {//columns
                            cell = sheet.getCell(j, i);
                            if (cell.getContents().contains("#")) {
                                LocalDate start = LocalDate.parse(cell.getContents().split("#")[0]);
                                LocalDate expire = LocalDate.parse(cell.getContents().split("#")[1]);
                                LocalDate today = LocalDate.now();
                                if (today.isEqual(start) || today.isAfter(start)) {
                                    if (today.isEqual(expire) || today.isBefore(expire)) {
                                        activities.add(new ProductActivity(supplierName, category, province, sheet.getCell(0, i).getContents(), expire.toString(), sheet.getCell(j + 1, i).getContents(), sheet.getCell(1, i).getContents()));
                                    }
                                }
                            }
                            if (cell.getContents().matches("")) {
                                if (j == 0) {
                                    i = rows;
                                } else {
                                    j = columns;
                                }
                            }
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
                    for (int i = 0; i < rows; i++) {//rows
                        for (int j = 0; j < columns; j++) {//columns
                            cell = sheet.getCell(j, i);
                            if (cell.getContents().contains("#")) {
                                LocalDate start = LocalDate.parse(cell.getContents().split("#")[0]);
                                LocalDate expire = LocalDate.parse(cell.getContents().split("#")[1]);
                                LocalDate today = LocalDate.now();
                                if (today.isEqual(start) || today.isAfter(start)) {
                                    if (today.isEqual(expire) || today.isBefore(expire)) {
                                        transport.add(new ProductTransport(supplierName, category, province, sheet.getCell(0, i).getContents(), expire.toString(), sheet.getCell(j + 1, i).getContents(), sheet.getCell(1, i).getContents()));
                                    }
                                }
                            }
                            if (cell.getContents().matches("")) {
                                if (j == 0) {
                                    i = rows;
                                } else {
                                    j = columns;
                                }
                            }
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
                bookingAccommodation.add(new BookingAccommodation(rs.getString("SupplierName"), rs.getString("Province"), rs.getString("ProductName"), rs.getString("MaxSleep"), rs.getString("Arrival"), rs.getInt("Nights"), rs.getInt("Quantity"), rs.getDouble("CostPricePerUnit"), rs.getDouble("SellPricePerUnit"), rs.getString("AddTo"),0, 0.0));
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
                bookingGolf.add(new BookingGolf(rs.getString("SupplierName"), rs.getString("Province"), rs.getString("ProductName"), null, rs.getInt("Quantity"), rs.getInt("Rounds"), rs.getInt("Carts"), rs.getDouble("CostPricePerUnit"), rs.getDouble("SellPricePerUnit"), rs.getString("AddTo"),0, 0.0));
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
                activities.add(new BookingActivity(rs.getString("SupplierName"), rs.getString("Province"), rs.getString("ProductNumber"), rs.getString("ActivityDate"), rs.getInt("Quantity"), rs.getDouble("CostPricePerUnit"), rs.getDouble("SellPricePerUnit"), rs.getString("AddTo"), 0, 0.0));
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
                bookingTransport.add(new BookingTransport(rs.getString("SupplierName"), rs.getString("Province"), rs.getString("ProductName"), rs.getString("TravelDate"), rs.getInt("Quantity"), rs.getString("From"), rs.getString("To"), rs.getDouble("CostPricePerUnit"), rs.getDouble("SellPricePerUnit"), rs.getString("AddTo"),0, 0.0));
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
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO BOOKINGS (ClientName, ContactNumber, Email, GolfersSharing, NonGolfersSharing, GolfersSingle, NonGolfersSingle, Arrival, Departure, Process, BookingAmount, Consultant, DepositDate, DepositPaid, FullPaid, PackageName, BookingMadeDate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
            preparedStatement.setString(1, booking.getClientName());
            preparedStatement.setString(2, booking.getContactNumber());
            preparedStatement.setString(3, booking.getEmail());
            preparedStatement.setInt(4, booking.getGolfersSharing());
            preparedStatement.setInt(5, booking.getNongolfersSharing());
            preparedStatement.setInt(6, booking.getGolfersSingle());
            preparedStatement.setInt(7, booking.getNongolfersSingle());
            preparedStatement.setString(8, booking.getArrival());
            preparedStatement.setString(9, booking.getDeparture());
            preparedStatement.setString(10, booking.getProcess());
            preparedStatement.setDouble(11, booking.getBookingAmount());
            preparedStatement.setString(12, booking.getConsultant());
            preparedStatement.setString(13, booking.getDepositDate());
            preparedStatement.setInt(14, booking.getDepositPaid());
            preparedStatement.setInt(15, booking.getFullPaid());
            preparedStatement.setString(16, booking.getPackageName());
            preparedStatement.setString(17, booking.getBookingMadeDate());
            preparedStatement.execute();
            preparedStatement = con.prepareStatement("SELECT MAX(GSNumber) AS LastGSNumber FROM BOOKINGS;");
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                gsNumber = rs.getInt("LastGSNumber");
            }
            for (BookingAccommodation ac: booking.getBookingAccommodation()) {
                preparedStatement = con.prepareStatement("INSERT INTO BOOKINGSACCOMMODATION (GSNumber, SupplierName, Province, ProductName, MaxSleep, Nights, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, SupplierBooked) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setString(1, Integer.toString(gsNumber));
                preparedStatement.setString(2, ac.getProvince());
                preparedStatement.setString(3, ac.getSupplierName());
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
        File costing = new File("G:/My Drive/a. Bookings/a. Quotes/" + booking.getArrival() + " " + booking.getClientName() + " GS" + gsNumber);
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
            preparedStatement = con.prepareStatement("SELECT MAX(GSNumber) AS LastGSNumber FROM BOOKINGS;");
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                packageID = rs.getInt("LastGSNumber");
            }
            for (BookingAccommodation ac : tripPackage.getBookingAccommodation()) {
                preparedStatement = con.prepareStatement("INSERT INTO PACKAGESINCLUDE (PackageID, SupplierName, Province, ProductName, Category, Quantity, NightsRounds, AddTo, CostPricePerUnit, SellPricePerUnit, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setString(1, Integer.toString(packageID));
                preparedStatement.setString(2, ac.getSupplierName());
                preparedStatement.setString(3, ac.getProvince());
                preparedStatement.setString(4, ac.getProductName());
                preparedStatement.setString(5, "Accommodation");
                preparedStatement.setInt(6, ac.getQuantity());
                preparedStatement.setInt(7, ac.getNights());
                preparedStatement.setString(8, ac.getAddTo());
                preparedStatement.setDouble(9, ac.getCostPricePerUnit());
                preparedStatement.setDouble(10, ac.getSellPricePerUnit());
                preparedStatement.setInt(11, 0);
                preparedStatement.execute();
            }
            for (BookingGolf gf : tripPackage.getBookingGolf()) {
                preparedStatement = con.prepareStatement("INSERT INTO PACKAGESINCLUDE (PackageID, SupplierName, Province, ProductName, Category, Quantity, NightsRounds, AddTo, CostPricePerUnit, SellPricePerUnit, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setString(1, Integer.toString(packageID));
                preparedStatement.setString(2, gf.getSupplierName());
                preparedStatement.setString(3, gf.getProvince());
                preparedStatement.setString(4, gf.getProductName());
                preparedStatement.setString(5, "Golf");
                preparedStatement.setInt(6, gf.getQuantity());
                preparedStatement.setInt(7, gf.getRounds());
                preparedStatement.setString(8, gf.getAddTo());
                preparedStatement.setDouble(9, gf.getCostPricePerUnit());
                preparedStatement.setDouble(10, gf.getSellPricePerUnit());
                preparedStatement.setInt(11, 0);
                preparedStatement.execute();
            }
            for (BookingActivity at : tripPackage.getBookingActivities()) {
                preparedStatement = con.prepareStatement("INSERT INTO PACKAGESINCLUDE (PackageID, SupplierName, Province, ProductName, Category, Quantity, NightsRounds, AddTo, CostPricePerUnit, SellPricePerUnit, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setString(1, Integer.toString(packageID));
                preparedStatement.setString(2, at.getSupplierName());
                preparedStatement.setString(3, at.getProvince());
                preparedStatement.setString(4, at.getProductName());
                preparedStatement.setString(5, "Activity");
                preparedStatement.setInt(6, at.getQuantity());
                preparedStatement.setInt(7, 1);
                preparedStatement.setString(8, at.getAddTo());
                preparedStatement.setDouble(9, at.getCostPricePerUnit());
                preparedStatement.setDouble(10, at.getSellPricePerUnit());
                preparedStatement.setInt(11, 0);
                preparedStatement.execute();
            }
            for (BookingTransport at : tripPackage.getBookingTransport()) {
                preparedStatement = con.prepareStatement("INSERT INTO PACKAGESINCLUDE (PackageID, SupplierName, Province, ProductName, Category, Quantity, NightsRounds, AddTo, CostPricePerUnit, SellPricePerUnit, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setString(1, Integer.toString(packageID));
                preparedStatement.setString(2, at.getSupplierName());
                preparedStatement.setString(3, at.getProvince());
                preparedStatement.setString(4, at.getProductName());
                preparedStatement.setString(5, "Transport");
                preparedStatement.setInt(6, at.getQuantity());
                preparedStatement.setInt(7, 1);
                preparedStatement.setString(8, at.getAddTo());
                preparedStatement.setDouble(9, at.getCostPricePerUnit());
                preparedStatement.setDouble(10, at.getSellPricePerUnit());
                preparedStatement.setInt(11, 0);
                preparedStatement.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addSupplier(Supplier supplier) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO SUPPLIERS (SupplierName, Category, Province, Address, CoOrdinates) VALUES (?,?,?,?,?);");
            preparedStatement.setString(1, supplier.getSupplierName());
            preparedStatement.setString(2, supplier.getCategory());
            preparedStatement.setString(3, supplier.getProvince());
            preparedStatement.setString(4, supplier.getAddress());
            preparedStatement.setString(5, supplier.getCoOrdinates());
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
            preparedStatement.setInt(1, cd.getContactDetailsID() - 1000000);
            preparedStatement.setString(2, cd.getPersonName());
            preparedStatement.setString(3, cd.getPosition());
            preparedStatement.setString(4, cd.getNumber());
            preparedStatement.setString(5, cd.getEmail());
            preparedStatement.setString(6, cd.getDateAdded());
            preparedStatement.execute();
            log("Server> Successfully Added Contact Details for Supplier: " + (cd.getContactDetailsID() - 1000000));
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> addContactDetails> " + ex);
        }
    }

    void addLogin(Login login){
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO Logins (LoginName, Username, Password) VALUES (?,?,?);");
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

    //</editor-fold>

    //<editor-fold desc="Notify">

    //</editor-fold>

    //<editor-fold desc="Updaters">
    void updateBooking(Booking booking) {//TODO Update File
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE BOOKINGS SET ClientName = ?, ContactNumber = ?, Email = ?, GolfersSharing = ?, NonGolfersSharing = ?, GolfersSingle = ?, NonGolfersSsingle = ?, Arrival = ?, Departure = ?, Process = ?, BookingAmount = ?, Consultant = ?, DepositDate = ?, DepositPaid = ?, FullPaid = ?, PackageName = ?, BookingMadeDate = ? WHERE GSNumber = ?");
            preparedStatement.setString(1, booking.getClientName());
            preparedStatement.setString(2, booking.getContactNumber());
            preparedStatement.setString(3, booking.getEmail());
            preparedStatement.setInt(4, booking.getGolfersSharing());
            preparedStatement.setInt(5, booking.getNongolfersSharing());
            preparedStatement.setInt(6, booking.getGolfersSingle());
            preparedStatement.setInt(7, booking.getGolfersSingle());
            preparedStatement.setString(8, booking.getArrival());
            preparedStatement.setString(9, booking.getDeparture());
            preparedStatement.setString(10, booking.getProcess());
            preparedStatement.setDouble(11, booking.getBookingAmount());
            preparedStatement.setString(12, booking.getConsultant());
            preparedStatement.setString(13, booking.getDepositDate());
            preparedStatement.setInt(14, booking.getDepositPaid());
            preparedStatement.setInt(15, booking.getFullPaid());
            preparedStatement.setString(16, booking.getPackageName());
            preparedStatement.setString(17, booking.getBookingMadeDate());
            preparedStatement.setInt(18, Integer.parseInt(booking.getGsNumber().substring(2)));
            preparedStatement.execute();
            preparedStatement = con.prepareStatement("DELETE FROM BOOKINGSACCOMMODATION WHERE GSNumber = ?");
            preparedStatement.setString(1, booking.getGsNumber());
            preparedStatement.execute();
            for (BookingAccommodation ac: booking.getBookingAccommodation()) {
                preparedStatement = con.prepareStatement("INSERT INTO BOOKINGSACCOMMODATION (GSNumber, SupplierName, Province, ProductName, MaxSleep, Nights, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, SupplierBooked) VALUES (?,?,?,?,?,?,?,?,?,??);");
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
            preparedStatement = con.prepareStatement("DELETE FROM BOOKINGSGOLF WHERE GSNumber = ?");
            preparedStatement.setString(1, booking.getGsNumber());
            preparedStatement.execute();
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
            preparedStatement = con.prepareStatement("DELETE FROM BOOKINGSACTIVITIES WHERE GSNumber = ?");
            preparedStatement.setString(1, booking.getGsNumber());
            preparedStatement.execute();
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
            preparedStatement = con.prepareStatement("DELETE FROM BOOKINGSTRANSPORT WHERE GSNumber = ?");
            preparedStatement.setString(1, booking.getGsNumber());
            preparedStatement.execute();
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
            log("Server> Successfully Updated Booking: " + booking.getClientName());

            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updateBooking> " + ex);
        }
    }

    void updatePackage(TripPackage tripPackage) {//TODO Update File
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
            preparedStatement.execute();
            preparedStatement = con.prepareStatement("DELETE FROM PACKAGESINCLUDES WHERE PackageID = ?");
            preparedStatement.setInt(1, tripPackage.getPackageID());
            preparedStatement.execute();
            for (BookingAccommodation ac : tripPackage.getBookingAccommodation()) {
                preparedStatement = con.prepareStatement("INSERT INTO PACKAGESINCLUDE (PackageID, SupplierName, ProductName, Category, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setInt(1, tripPackage.getPackageID());
                preparedStatement.setString(2, ac.getProvince());
                preparedStatement.setString(3, ac.getSupplierName());
                preparedStatement.setString(4, ac.getProductName());
                preparedStatement.setString(5, ac.getMaxSleep());
                preparedStatement.setInt(6, ac.getNights());
                preparedStatement.setInt(7, ac.getQuantity());
                preparedStatement.setString(8, ac.getAddTo());
                preparedStatement.setDouble(9, ac.getCostPricePerUnit());
                preparedStatement.setDouble(10, ac.getSellPricePerUnit());
                preparedStatement.setInt(11, 0);
                preparedStatement.execute();
            }
            for (BookingGolf gf : tripPackage.getBookingGolf()) {
                preparedStatement = con.prepareStatement("INSERT INTO PACKAGESINCLUDE (PackageID, SupplierName, ProductName, Category, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setInt(1, tripPackage.getPackageID());
                preparedStatement.setString(2, gf.getSupplierName());
                preparedStatement.setString(3, gf.getProvince());
                preparedStatement.setString(4, gf.getProductName());
                preparedStatement.setInt(5, gf.getQuantity());
                preparedStatement.setInt(6, gf.getRounds());
                preparedStatement.setString(7, gf.getAddTo());
                preparedStatement.setDouble(8, gf.getCostPricePerUnit());
                preparedStatement.setDouble(9, gf.getSellPricePerUnit());
                if (gf.getCarts() == 1) {
                    preparedStatement.setInt(10, 1);
                } else {
                    preparedStatement.setInt(10, 0);
                }
                preparedStatement.execute();
            }
            for (BookingActivity at : tripPackage.getBookingActivities()) {
                preparedStatement = con.prepareStatement("INSERT INTO PACKAGESINCLUDE (PackageID, SupplierName, ProductName, Category, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setInt(1, tripPackage.getPackageID());
                preparedStatement.setString(2, at.getSupplierName());
                preparedStatement.setString(3, at.getProvince());
                preparedStatement.setString(4, at.getProductName());
                preparedStatement.setInt(5, at.getQuantity());
                preparedStatement.setString(6, at.getAddTo());
                preparedStatement.setDouble(7, at.getCostPricePerUnit());
                preparedStatement.setDouble(8, at.getSellPricePerUnit());
                preparedStatement.setInt(9, 0);
                preparedStatement.execute();
            }
            for (BookingTransport at : tripPackage.getBookingTransport()) {
                preparedStatement = con.prepareStatement("INSERT INTO PACKAGESINCLUDE (PackageID, SupplierName, ProductName, Category, Quantity, AddTo, CostPricePerUnit, SellPricePerUnit, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
                preparedStatement.setInt(1, tripPackage.getPackageID());
                preparedStatement.setString(2, at.getSupplierName());
                preparedStatement.setString(3, at.getProductName());
                preparedStatement.setString(4, at.getProvince());
                preparedStatement.setInt(5, at.getQuantity());
                preparedStatement.setString(6, at.getAddTo());
                preparedStatement.setDouble(7, at.getCostPricePerUnit());
                preparedStatement.setDouble(8, at.getSellPricePerUnit());
                preparedStatement.setInt(9, 0);
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
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE SUPPLIERS SET SupplierName = ?, Category = ?, Province = ?, Address = ?, CoOrdinates = ? WHERE SupplierNumber = ?");
            preparedStatement.setString(1, supplier.getSupplierName());
            preparedStatement.setString(2, supplier.getCategory());
            preparedStatement.setString(3, supplier.getProvince());
            preparedStatement.setString(4, supplier.getAddress());
            preparedStatement.setString(5, supplier.getCoOrdinates());
            preparedStatement.setInt(6, supplier.getSupplierNumber());
            preparedStatement.execute();
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
            preparedStatement.execute();
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
            preparedStatement.execute();
            log("Server> Successfully Updated Login: " + login.getLoginName());
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updateLogin> " + ex);
        }
    }

    void updateSuppliersBookedAccommodation(){
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE SUPPLIERSBOOKEDACCOMMODATION SET Process = ?, PaidAmount = ? WHERE SBAID = ?");
            //preparedStatement.setString(1, login.getLoginName());
            //preparedStatement.setString(2, login.getUsername());
            preparedStatement.execute();
            //log("Server> Successfully Suppliers Booked : " + login.getLoginName());
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updateLogin> " + ex);
        }
    }

    void updateSuppliersBookedGolf(){
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE SUPPLIERSBOOKEDGOLF SET Process = ?, PaidAmount = ? WHERE SBGID = ?");
            //preparedStatement.setString(1, login.getLoginName());
            //preparedStatement.setString(2, login.getUsername());
            preparedStatement.execute();
            //log("Server> Successfully Suppliers Booked : " + login.getLoginName());
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updateLogin> " + ex);
        }
    }

    void updateSuppliersBookedTransport(){
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE SUPPLIERSBOOKEDTRANSPORT SET Process = ?, PaidAmount = ? WHERE SBRID = ?");
            //preparedStatement.setString(1, login.getLoginName());
            //preparedStatement.setString(2, login.getUsername());
            preparedStatement.execute();
            //log("Server> Successfully Suppliers Booked : " + login.getLoginName());
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updateLogin> " + ex);
        }
    }

    void updateSuppliersBookedActivities(){
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE SUPPLIERSBOOKEDACTIVITIES SET Process = ?, PaidAmount = ? WHERE SBTID = ?");
            //preparedStatement.setString(1, login.getLoginName());
            //preparedStatement.setString(2, login.getUsername());
            preparedStatement.execute();
            //log("Server> Successfully Suppliers Booked : " + login.getLoginName());
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updateLogin> " + ex);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Remove">
    void removeBooking(int gsNumber) {//TODO Delete File
        try {
            PreparedStatement preparedStatement = con.prepareStatement("DELETE FROM BOOKINGS WHERE GSNumber = ?;");
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
            File file = new File(Server.BOOKINGS_FOLDER + "/a. Quotes");
            File [] files = file.listFiles();
            for(File f:files) {
                if(f.getAbsolutePath().contains("GS" + gsNumber)){
                    file = new File(f.getAbsolutePath());
                    break;
                }
            }
            file.delete();
            log("Server> Successfully Removed Booking: " + gsNumber);
            //notifyUpdatedStudent(studentNumber);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> removeBooking> " + ex);
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
            notifyUpdatedDocuments();
        }
    }

    void notifyUpdatedDocuments() {
        for (ConnectionHandler ch : Server.connectionsList) {
            if (ch instanceof UserConnectionHandler) {
                ((UserConnectionHandler) ch).updateDocuments.setValue(true);
                break;
            }
        }
    }

    public void convertExceltoPDF(File excelFile){//TODO
        /*try {
            FileInputStream input_document = new FileInputStream(new File("C:\\excel_to_pdf.xls"));
            // Read workbook into XSSFWorkbook
            XSSFWorkbook my_xls_workbook = new XSSFWorkbook(input_document);
            // Read worksheet into XSSFSheet
            XSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
            // To iterate over the rows
            Iterator<Row> rowIterator = my_worksheet.iterator();
            //We will create output PDF document objects at this point
            Document iText_xls_2_pdf = new Document();
            PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream("PDFOutput.pdf"));
            iText_xls_2_pdf.open();
            //we have two columns in the Excel sheet, so we create a PDF table with two columns
            PdfPTable my_table = new PdfPTable(2);
            //cell object to capture data
            PdfPCell table_cell;
            //Loop through rows.
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while(cellIterator.hasNext()) {
                    Cell cell = cellIterator.next(); //Fetch CELL
                    switch(cell.getCellType()) { //Identify CELL type

                        case Cell.CELL_TYPE_STRING:
                            //Push the data from Excel to PDF Cell
                            table_cell=new PdfPCell(new Phrase(cell.getStringCellValue()));
                            my_table.addCell(table_cell);
                            break;
                    }
                    //next line
                }

            }
            //Finally add the table to PDF document
            iText_xls_2_pdf.add(my_table);
            iText_xls_2_pdf.close();
            //we created our pdf file..
            input_document.close(); //close xlsx
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

    }

    void log(String logDetails) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            System.out.println(dateFormat.format(date) + " : " + logDetails);
            File logFile = Server.LOG_FILE.getAbsoluteFile();
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            FileWriter fw = new FileWriter(logFile.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(dateFormat.format(date) + " : " + logDetails);
            bw.newLine();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

