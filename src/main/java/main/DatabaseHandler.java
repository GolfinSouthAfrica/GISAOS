package main;

import com.sun.mail.imap.IMAPFolder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.*;
import models.Transport;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.security.auth.Subject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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
                        "People TEXT, " +
                        "Arrival TEXT, " +
                        "Departure TEXT, " +
                        "Process TEXT, " +
                        "BookingAmount TEXT, " +
                        "Consultant INTEGER, " +
                        "DepositDate TEXT, " +
                        "DepositPaid INTEGER, " +
                        "FullPaid INTEGER, " +
                        "BookingMadeDate INTEGER, " +
                        "PackageID INTEGER, " + //Bespoke Custom
                        "PackageQuantity TEXT);"); //Bespoke 1
                stmt.execute("CREATE TABLE BOOKINGSACCOMMODATION (" +
                        "BAID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "GSNumber INTEGER, " +
                        "SupplierNumber INTEGER, " +
                        "AccomodationID INTEGER, " +
                        "Nights INTEGER, " +
                        "People INTEGER);");
                stmt.execute("CREATE TABLE BOOKINGSGOLF (" +
                        "BGID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "GSNumber INTEGER, " +
                        "SupplierNumber INTEGER, " +
                        "GolfID INTEGER, " +
                        "Rounds INTEGER, " +
                        "People INTEGER);");
                stmt.execute("CREATE TABLE BOOKINGSACTIVITIES (" +
                        "BTID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "GSNumber INTEGER, " +
                        "SupplierNumber INTEGER, " +
                        "ActivityID INTEGER, " +
                        "People INTEGER);");
                stmt.execute("CREATE TABLE BOOKINGSTRANSPORT (" +
                        "BRID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "GSNumber INTEGER, " +
                        "SupplierNumber INTEGER, " +
                        "TransportID INTEGER, " +
                        "Quantity INTEGER);");
                stmt.execute("CREATE TABLE SUPPLIERS (" +
                        "SupplierNumber INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "SupplierName TEXT, " +
                        "Category TEXT, " +
                        "Province TEXT, " +
                        "Address TEXT, " +
                        "CoOrdinates TEXT, " +
                        "ContactPerson TEXT, " +
                        "ContactNumber TEXT, " +
                        "Email TEXT);");
                stmt.execute("CREATE TABLE PACKAGES (" +
                        "PackageID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "PackageName TEXT," +
                        "Category TEXT," +//TODO Ex short break exc.
                        "People TEXT," +
                        "TotalPackageAmount TEXT," +
                        "ExpiryDate TEXT," +
                        "Extra TEXT);");
                stmt.execute("CREATE TABLE PACKAGESINCLUDE (" +
                        "PackageIncludeID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "PackageID INTEGER," +
                        "SupplierID TEXT," +
                        "ProductID TEXT," +
                        "People TEXT," +
                        "Extra TEXT);");
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
                suppliers.add(new Supplier(rs.getInt("SupplierNumber"), rs.getString("SupplierName"), rs.getString("Category"), rs.getString("Province"), rs.getString("Address"), rs.getString("coOrdinates"), rs.getString("ContactPerson"), rs.getString("ContactNumber"), rs.getString("Email")));
            }
            log("Server> Successfully Created Suppliers");
            return suppliers;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getSuppliers> " + ex);
            return null;
        }
    }

    List<Booking> getBookings() {//TODO Get File separately when requested
        List<Booking> bookings = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGS;");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                bookings.add(new Booking(rs.getString("GSNumber"), rs.getString("ClientName"), rs.getString("ContactNumber"), rs.getString("Email"), rs.getString("People"), rs.getString("Arrival"), rs.getString("Departure"), rs.getString("Process"), rs.getString("BookingAmount"), rs.getString("Consultant"), rs.getString("DepositDate"), rs.getInt("DepositAmount"), rs.getInt("FullPaid"), rs.getString("BookingMadeDate"), rs.getInt("PackageID"), rs.getInt("PackageQuantity"), getBookingAccommodation(rs.getString("GSNumber")), getBookingGolf(rs.getString("GSNumber")), getBookingActivities(rs.getString("GSNumber")), getBookingTransport(rs.getString("GSNumber"))));
            }
            log("Server> Successfully Got all Bookings");
            return bookings;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getBookings> " + ex);
            return null;
        }
    }

    private List<Accommodation> getBookingAccommodation (String gsNumber) {
        List<Accommodation> accommodation = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGSACCOMMODATION WHERE GSNumber = ?;");
            preparedStatement.setString(1, gsNumber);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                accommodation.add(new Accommodation(rs.getInt("BAID"), rs.getInt("SupplierNumber"), rs.getInt("AccommodationID"), rs.getInt("Nights"), rs.getInt("People")));
            }
            log("Server> Successfully Got all Accommodation for Booking: " + gsNumber);
            return accommodation;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getBookingAccommodation> " + ex);
            return null;
        }
    }

    private List<Golf> getBookingGolf (String gsNumber) {
        List<Golf> golf = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGSGOLF WHERE GSNumber = ?;");
            preparedStatement.setString(1, gsNumber);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                golf.add(new Golf(rs.getInt("BGID"), rs.getInt("SupplierNumber"), rs.getInt("GolfID"), rs.getInt("People"), rs.getInt("Rounds"), rs.getInt("Carts")));
            }
            log("Server> Successfully Got all Golf for Booking: " + gsNumber);
            return golf;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getBookingGolf> " + ex);
            return null;
        }
    }

    private List<Activity> getBookingActivities (String gsNumber) {
        List<Activity> activities = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGSACTIVITIES WHERE GSNumber = ?;");
            preparedStatement.setString(1, gsNumber);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                activities.add(new Activity(rs.getInt("BTID"), rs.getInt("SupplierNumber"), rs.getInt("ActivityID"), rs.getInt("People")));
            }
            log("Server> Successfully Got all Activities for Booking: " + gsNumber);
            return activities;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getBookingActivities> " + ex);
            return null;
        }
    }

    private List<Transport> getBookingTransport (String gsNumber) {
        List<Transport> transport = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM BOOKINGSTRANSPORT WHERE GSNumber = ?;");
            preparedStatement.setString(1, gsNumber);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                transport.add(new Transport(rs.getInt("BRID"), rs.getInt("SupplierNumber"), rs.getInt("TransportID"), rs.getInt("Quantity")));
            }
            log("Server> Successfully Got all Activities for Booking: " + gsNumber);
            return transport;
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> getBookingTransport> " + ex);
            return null;
        }
    }

    List<Mail> getMails(){
        /*Store store = null;
        IMAPFolder folder = null;
        try {
            Properties props = System.getProperties();
            props.put("mail.store.protocol", "imaps");
            Session session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect("imap.googlemail.com", "info@golfinsouthafrica.com", "password");//TODO
            folder = (IMAPFolder) store.getFolder("[Gmail]/Inbox");
            if(!folder.isOpen()){
                folder.open(Folder.READ_WRITE);
            }
            Message[] messages = folder.getMessages();

            Message msg = messages[0];
            String sub = msg.getSubject();
            Address[] from = msg.getFrom();
            Address[] to = msg.getAllRecipients();
            Date date = msg.getReceivedDate();
            int size = msg.getSize();
            Flags flags = msg.getFlags();
            String con = msg.getContentType();
            Object bod = msg.getContent();


        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
        return null;//TODO
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

    ObservableList<DataFile> getDocuments(){
        ObservableList<DataFile> documents = FXCollections.observableArrayList();
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
    void addBooking(Booking booking) {//TODO Create File
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO BOOKINGS (ClientName, ContactNumber, Email, People, Arrival, Departure, Process, BookingAount, Consultant, DepositDate, DepositPaid, FullPaid, BookingMadeDate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);");
            preparedStatement.setString(1, booking.getClientName());
            preparedStatement.setString(2, booking.getContactNumber());
            preparedStatement.setString(3, booking.getEmail());
            preparedStatement.setString(4, booking.getPeople());
            preparedStatement.setString(5, booking.getArrival());
            preparedStatement.setString(6, booking.getDeparture());
            preparedStatement.setString(7, booking.getProcess());
            preparedStatement.setString(8, booking.getBookingAmount());
            preparedStatement.setString(9, booking.getConsultant());
            preparedStatement.setString(10, booking.getDepositDate());
            preparedStatement.setInt(11, booking.getDepositPaid());
            preparedStatement.setInt(12, booking.getFullPaid());
            preparedStatement.setString(13, booking.getBookingMadeDate());
            preparedStatement.execute();
            preparedStatement = con.prepareStatement("SELECT MAX(GSNumber) AS LastGSNumber FROM BOOKINGS;");
            ResultSet rs = preparedStatement.executeQuery();
            int gsNumber = -1;
            if (rs.next()) {
                gsNumber = rs.getInt("LastGSNumber");
            }
            for (Accommodation ac: booking.getAccommodation()) {
                preparedStatement = con.prepareStatement("INSERT INTO BOOKINGSACCOMMODATION (GSNumber, SupplierNumber, AccommodationID, Nights, People) VALUES (?,?,?,?,?);");
                preparedStatement.setInt(1, gsNumber);
                preparedStatement.setInt(2, ac.getSupplierNumber());
                preparedStatement.setInt(3, ac.getAccommodationId());
                preparedStatement.setInt(4, ac.getNights());
                preparedStatement.setInt(5, ac.getPeople());
                preparedStatement.execute();
            }
            for (Golf gf: booking.getGolf()) {
                preparedStatement = con.prepareStatement("INSERT INTO BOOKINGSGOLF (GSNumber, SupplierNumber, GolfID, Rounds, People) VALUES (?,?,?,?,?);");
                preparedStatement.setInt(1, gsNumber);
                preparedStatement.setInt(2, gf.getSupplierNumber());
                preparedStatement.setInt(3, gf.getGolfID());
                preparedStatement.setInt(4, gf.getRounds());
                preparedStatement.setInt(5, gf.getPeople());
                preparedStatement.execute();
            }
            for (Activity at: booking.getActivities()) {
                preparedStatement = con.prepareStatement("INSERT INTO BOOKINGSACTIVITIES (GSNumber, SupplierNumber, ActivityID, People) VALUES (?,?,?,?);");
                preparedStatement.setInt(1, gsNumber);
                preparedStatement.setInt(2, at.getSupplierNumber());
                preparedStatement.setInt(3, at.getActivityId());
                preparedStatement.setInt(4, at.getPeople());
                preparedStatement.execute();
            }
            log("Server> Successfully Added Booking: " + booking.getClientName());

            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> addBooking> " + ex);
        }
    }

    void addSupplier(Supplier supplier) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO SUPPLIERS (SupplierName, Category, Province, Address, CoOrdinates, ContactPerson, ContactNumber, Email) VALUES (?,?,?,?,?,?,?,?);");
            preparedStatement.setString(1, supplier.getSupplierName());
            preparedStatement.setString(2, supplier.getCategory());
            preparedStatement.setString(3, supplier.getProvince());
            preparedStatement.setString(4, supplier.getAddress());
            preparedStatement.setString(5, supplier.getCoOrdinates());
            preparedStatement.setString(6, supplier.getContactPerson());
            preparedStatement.setString(7, supplier.getContactNumber());
            preparedStatement.setString(8, supplier.getEmail());
            preparedStatement.execute();
            log("Server> Successfully Added Supplier: " + supplier.getSupplierName());
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> addSupplier> " + ex);
        }
    }

    void newMail(String category) {//TODO
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO CATEGORIES (CategoryName) VALUES (?);");
            preparedStatement.setString(1, category);
            log("Server> Successfully Added Category: " + category);
            preparedStatement.execute();
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> addSupplier> " + ex);
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
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE BOOKINGS SET ClientName = ?, ContactNumber = ?, Email = ?, People = ?, Arrival = ?, Departure = ?, Process = ?, BookingAount = ?, Consultant = ?, DepositDate = ?, DepositPaid = ?, FullPaid = ?, BookingMadeDate = ? WHERE GSNumber = ?");
            preparedStatement.setString(1, booking.getClientName());
            preparedStatement.setString(2, booking.getContactNumber());
            preparedStatement.setString(3, booking.getEmail());
            preparedStatement.setString(4, booking.getPeople());
            preparedStatement.setString(5, booking.getArrival());
            preparedStatement.setString(6, booking.getDeparture());
            preparedStatement.setString(7, booking.getProcess());
            preparedStatement.setString(8, booking.getBookingAmount());
            preparedStatement.setString(9, booking.getConsultant());
            preparedStatement.setString(10, booking.getDepositDate());
            preparedStatement.setInt(11, booking.getDepositPaid());
            preparedStatement.setInt(12, booking.getFullPaid());
            preparedStatement.setString(13, booking.getBookingMadeDate());
            preparedStatement.setInt(14, Integer.parseInt(booking.getGsNumber().substring(2)));
            preparedStatement.execute();
            for (Accommodation ac: booking.getAccommodation()) {
                preparedStatement = con.prepareStatement("UPDATE BOOKINGSACCOMMODATION SET SupplierNumber = ?, AccommodationID = ?, Nights = ?, People = ? WHERE BAID = ?;");
                preparedStatement.setInt(1, ac.getSupplierNumber());
                preparedStatement.setInt(2, ac.getAccommodationId());
                preparedStatement.setInt(3, ac.getNights());
                preparedStatement.setInt(4, ac.getPeople());
                preparedStatement.setInt(5, ac.getBAID());
                preparedStatement.execute();
            }
            for (Golf gf: booking.getGolf()) {
                preparedStatement = con.prepareStatement("UPDATE BOOKINGSGOLF SET SupplierNumber = ?, GolfID = ?, Rounds = ?, People = ? WHERE BGID = ?;");
                preparedStatement.setInt(1, gf.getSupplierNumber());
                preparedStatement.setInt(2, gf.getGolfID());
                preparedStatement.setInt(3, gf.getRounds());
                preparedStatement.setInt(4, gf.getPeople());
                preparedStatement.setInt(5, gf.getBGID());
                preparedStatement.execute();
            }
            for (Activity at: booking.getActivities()) {
                preparedStatement = con.prepareStatement("UPDATE BOOKINGSACTIVITIES SET GSNumber = ?, SupplierNumber = ?, ActivityID = ?, People = ? WHERE BTID = ?;");
                preparedStatement.setInt(1, at.getSupplierNumber());
                preparedStatement.setInt(2, at.getActivityId());
                preparedStatement.setInt(3, at.getPeople());
                preparedStatement.setInt(4, at.getPeople());
                preparedStatement.execute();
            }
            log("Server> Successfully Updated Booking: " + booking.getClientName());

            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updateBooking> " + ex);
        }
    }

    void updateSupplier(Supplier supplier) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE SUPPLIER SET SupplierName = ?, Category = ?, Province = ?, Address = ?, CoOrdinates = ?, ContactPerson = ?, ContactNumber = ?, Email = ? WHERE SupplierNumber = ?");
            preparedStatement.setString(1, supplier.getSupplierName());
            preparedStatement.setString(2, supplier.getCategory());
            preparedStatement.setString(3, supplier.getProvince());
            preparedStatement.setString(4, supplier.getAddress());
            preparedStatement.setString(5, supplier.getCoOrdinates());
            preparedStatement.setString(6, supplier.getContactPerson());
            preparedStatement.setString(7, supplier.getContactNumber());
            preparedStatement.setString(8, supplier.getEmail());
            preparedStatement.setInt(9, supplier.getSupplierNumber());
            preparedStatement.execute();
            log("Server> Successfully Updated Supplier: " + supplier.getSupplierName());
            //notifyUpdatedStudent(s[0]);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("Server> updateSupplier> " + ex);
        }
    }

    void updateLogin(Login login) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE BOOKINGS SET ClientName = ?, ContactNumber = ?, Email = ?, People = ?, Arrival = ?, Departure = ?, Process = ?, BookingAount = ?, Consultant = ?, DepositDate = ?, DepositPaid = ?, FullPaid = ?, BookingMadeDate = ? WHERE GSNumber = ?");
            preparedStatement.setString(1, login.getLoginName());
            preparedStatement.setString(2, login.getUsername());
            preparedStatement.setString(3, login.getPassword());
            preparedStatement.execute();
            log("Server> Successfully Updated Login: " + login.getLoginName());
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
            PreparedStatement preparedStatement = con.prepareStatement("DELETE FROM BOOKING WHERE GSNumber = ?;");
            preparedStatement.setInt(1, gsNumber);
            preparedStatement.executeUpdate();
            preparedStatement = con.prepareStatement("DELETE FROM BOOKINGACCOMMODATION WHERE GSNumber = ?;");
            preparedStatement.setInt(1, gsNumber);
            preparedStatement.executeUpdate();
            preparedStatement = con.prepareStatement("DELETE FROM BOOKINGGOLF WHERE GSNumber = ?;");
            preparedStatement.setInt(1, gsNumber);
            preparedStatement.executeUpdate();
            preparedStatement = con.prepareStatement("DELETE FROM BOOKINGACTIVITIES WHERE GSNumber = ?;");
            preparedStatement.setInt(1, gsNumber);
            preparedStatement.executeUpdate();
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

    void deleteFile(String fileType, String fileName) {
        File fileToDelete = new File(Server.APPLICATION_FOLDER + "/" + fileType + "/" + fileName);
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

    void processQuotationToInvoice(String quoteNumber) {

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

