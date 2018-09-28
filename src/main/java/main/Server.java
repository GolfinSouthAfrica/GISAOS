package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class Server {

    static final File APPLICATION_FOLDER = new File("G:/My Drive/e. Office/OfficeAppServerData");
    static final File TEMPLATES_FOLDER = new File("G:/My Drive/b. Templates");
    static final File BOOKINGS_FOLDER = new File("G:/My Drive/a. Bookings");
    static final File DOCUMENTS_FOLDER = new File("G:/My Drive/d. Documents");
    static final File SUPPLIER_FOLDER = new File("G:/My Drive/c. Suppliers");
    static final File OFFICE_FOLDER = new File("G:/My Drive/e. Office");
    static final File DATABASE_FILE = new File(APPLICATION_FOLDER.getAbsolutePath() + "/GolfInSouthAfricaDB.db");
    static final File LOG_FILE = new File(APPLICATION_FOLDER.getAbsolutePath() + "/GolfInSouthAfricaLogFile.txt");
    /*public static List<Mail> unreadNewQuotesMails = new ArrayList<>();
    public static List<Mail> readNewQuotesMails = new ArrayList<>();
    public static List<Mail> unreadContactMails = new ArrayList<>();
    public static List<Mail> readContactMails = new ArrayList<>();
    public static List<Mail> unreadFinanceMails = new ArrayList<>();
    public static List<Mail> readFinanceMails = new ArrayList<>();
    public static List<Mail> unreadOtherMails = new ArrayList<>();
    public static List<Mail> readOtherMails = new ArrayList<>();*/
    public static List<ProductAccommodation> accommodation = new ArrayList<>();
    public static List<ProductGolf> golf = new ArrayList<>();
    public static List<ProductTransport> transport = new ArrayList<>();
    public static List<ProductActivity> activities = new ArrayList<>();
    public static List<Supplier> suppliers = new ArrayList<>();
    public static List<Booking> bookings = new ArrayList<>();
    public static List<Login> logins = new ArrayList<>();
    public static List<DataFile> documents = new ArrayList<>();
    public static List<TripPackage> packages = new ArrayList<>();
    public static List<Transaction> transactions = new ArrayList<>();
    public static List<Notification> notifications = new ArrayList<>();
    static final int BUFFER_SIZE = 4194304;
    public static ObservableList<ConnectionHandler> connectionsList = FXCollections.observableArrayList();
    public static final int PORT = 1521;
    public static final int MAX_CONNECTIONS = 5;
    public static DatabaseHandler dh = new DatabaseHandler();
    private Timer updatetimer;
    private Timer expirytimer;
    //private Store store;
    private int unread = 0;
    private int read = 0;
    public static List<Integer> unreadMails = FXCollections.observableArrayList();

    public Server() {
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("G:/My Drive/e. Office/OfficeAppServerData/GISALogo.png"));
        trayIcon.setImageAutoSize(true);
        final SystemTray tray = SystemTray.getSystemTray();
        // Create a pop-up menu components
        //MenuItem homeItem = new MenuItem("Home");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }});
        //Add components to pop-up menu
        /*popup.add(homeItem);
        popup.addSeparator();
        popup.add(displayMenu);
        displayMenu.add(calenderItem);
        displayMenu.add(quotationsItem);
        displayMenu.add(logInDetalsItem);
        popup.addSeparator();*/
        popup.add(exitItem);
        trayIcon.setPopupMenu(popup);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }

        if (!TEMPLATES_FOLDER.exists()) {
            TEMPLATES_FOLDER.mkdirs();
            dh.log("Server> Local Templates Files Folder Created");
        }
        if (!BOOKINGS_FOLDER.exists()) {
            BOOKINGS_FOLDER.mkdirs();
            dh.log("Server> Local Bookings Files Folders Created");
        }
        if (!DOCUMENTS_FOLDER.exists()) {
            DOCUMENTS_FOLDER.mkdirs();
            dh.log("Server> Local Documents Files Folders Created");
        }
        if (!SUPPLIER_FOLDER.exists()) {
            SUPPLIER_FOLDER.mkdirs();
            dh.log("Server> Local Suppliers Files Folders Created");
        }
        if (!OFFICE_FOLDER.exists()) {
            OFFICE_FOLDER.mkdirs();
            dh.log("Server> Local Office Files Folders Created");
        }
        new ClientListener().start();
        updatetimer = new Timer();
        updatetimer.schedule(new UpdateChecker(), 0, 600000);
        expirytimer = new Timer();
        expirytimer.schedule(new ExpiryChecker(), 0, 600000);
        /*try {
            /*Properties props = System.getProperties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.host", "imap.gmail.com");
            props.put("mail.imaps.port", "993");
            Session session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect("info@golfinsouthafrica.com", "GISADefault1234@");
            System.out.println(store);*/
        /*} catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }*/
    }

    public class ClientListener extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(25000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                dh.log("Server> Trying to set up client on port " + PORT);
                /*System.setProperty("javax.net.ssl.keyStore", APPLICATION_FOLDER.getAbsolutePath() + "/campuslive.store");
                System.setProperty("javax.net.ssl.keyStorePassword", "campuslivepassword1");*/
                dh.log("Server> Set up client on port " + PORT);
                //ServerSocket ss = SSLServerSocketFactory.getDefault().createServerSocket(PORT);
                File logFile = new File("G:/My Drive/e. Office/OfficeAppServerData/GolfInSouthAfricaOfficeServerIP.txt");
                if(logFile.exists()){
                    logFile.delete();
                }
                logFile.createNewFile();
                FileWriter fw = new FileWriter(logFile.getAbsoluteFile(), true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("Local IP Address:" + InetAddress.getLocalHost().getHostAddress());
                bw.newLine();
                bw.write("Internet IP Address:" + new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream())).readLine());
                bw.close();
                fw.close();
                ServerSocket ss = new ServerSocket(PORT);
                while (true) {
                    while (connectionsList.size() <= MAX_CONNECTIONS) {
                        dh.log("Server> Waiting for new connection");
                        Socket s = ss.accept();
                        s.setKeepAlive(true);
                        dh.log("Server> Connection established on " + s.getInetAddress().getHostAddress() + ":" + s.getPort());
                        new LoginManager(s).start();
                    }
                }
            } catch (Exception ex) {
                dh.log("Server> ClientListener> " + ex);
                ex.printStackTrace();
            }
        }
    }

    public class LoginManager extends Thread {

        private Socket s;
        private ObjectInputStream objectInputStream;
        private ObjectOutputStream objectOutputStream;

        public LoginManager(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try {
                objectInputStream = new ObjectInputStream(s.getInputStream());
                objectOutputStream = new ObjectOutputStream(s.getOutputStream());
                StopClass:
                while (true) {
                    Object inputObject;
                    try {
                        while ((inputObject = objectInputStream.readObject()) == null) ;
                        if (inputObject instanceof String) {
                            String input = (String) inputObject;
                            if (input.startsWith("au:")) {
                                dh.log("Server> Authorising User : " + input.substring(3).split(":")[0]);
                                if (authoriseUser(input.substring(3).split(":")[0], input.substring(3).split(":")[1])) {
                                    dh.log("Server> Authorised User : " + input.substring(3).split(":")[0]);
                                    objectOutputStream.writeObject("au:y");
                                    objectOutputStream.flush();
                                    UserConnectionHandler studentConnectionHandler = new UserConnectionHandler(s, objectInputStream, objectOutputStream, input.substring(3).split(":")[0], connectionsList, dh);
                                    Thread t = new Thread(studentConnectionHandler);
                                    t.start();
                                    connectionsList.add(studentConnectionHandler);
                                    break StopClass;
                                } else {
                                    dh.log("Server> Authorising User : " + input.substring(3).split(":")[0] + " Failed");
                                    objectOutputStream.writeObject("au:n");
                                    objectOutputStream.flush();
                                }
                            } else if (input.startsWith("fp:")) {
                                dh.log("User > Requested Forgot Password");
                                dh.emailUserPassword(input.substring(3));
                            }
                        }
                    } catch (SocketException e) {
                        dh.log("Server> User Disconnected");
                        this.stop();
                        connectionsList.remove(this);
                    } catch (EOFException e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception ex) {
                dh.log("Server> LoginManager> " + ex);
                ex.printStackTrace();
            }
        }
    }

    private Boolean authoriseUser(String username, String password) {
        return dh.authoriseUser(username, password);
    }

    /*public static void sortQuotesMailsByDate(List<Mail> readNewQuotesMails){
        Collections.sort(readNewQuotesMails, new Comparator<Mail>() {
            DateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd '@' hh:mm a");
            @Override
            public int compare(Mail mail, Mail t1) {
                try{
                    return dateFormat.parse(mail.getDate()).compareTo(dateFormat.parse(t1.getDate()));
                }catch(ParseException ex) {
                    ex.printStackTrace();
                    throw new IllegalArgumentException(ex);
                }
            }
        });
    }*/

    /*public static void sortContactMailsByDate(List<Mail> readContactMails){
        Collections.sort(readContactMails, new Comparator<Mail>() {
            DateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd '@' hh:mm a");
            @Override
            public int compare(Mail mail, Mail t1) {
                try{
                    return dateFormat.parse(mail.getDate()).compareTo(dateFormat.parse(t1.getDate()));
                }catch(ParseException ex) {
                    ex.printStackTrace();
                    throw new IllegalArgumentException(ex);
                }
            }
        });
    }*/

    /*public static void sortFinanceMailsByDate(List<Mail> readFinanceMails){
        Collections.sort(readFinanceMails, new Comparator<Mail>() {
            DateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd '@' hh:mm a");
            @Override
            public int compare(Mail mail, Mail t1) {
                try{
                    return dateFormat.parse(mail.getDate()).compareTo(dateFormat.parse(t1.getDate()));
                }catch(ParseException ex) {
                    ex.printStackTrace();
                    throw new IllegalArgumentException(ex);
                }
            }
        });
    }*/

    /*public static void sortOtherMailsByDate(List<Mail> readOtherMails){
        Collections.sort(readOtherMails, new Comparator<Mail>() {
            DateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd '@' hh:mm a");
            @Override
            public int compare(Mail mail, Mail t1) {
                try{
                    return dateFormat.parse(mail.getDate()).compareTo(dateFormat.parse(t1.getDate()));
                }catch(ParseException ex) {
                    ex.printStackTrace();
                    throw new IllegalArgumentException(ex);
                }
            }
        });
    }*/

    public static void updateAccommodation() {
        List<ProductAccommodation> temp = dh.getProductAccommodation();
        if (!temp.equals(accommodation)) {
            if(temp.size()==0){
                accommodation.clear();
                accommodation.add(new ProductAccommodation("NoAccommodation", "NoAccommodation", "NoAccommodation", "NoAccommodation", 0, "NoAccommodation", null));
            } else {
                accommodation.clear();
                accommodation.addAll(temp);
            }
            notifyAll("Accommodation");
        }
    }

    public static void updateGolf() {
        List<ProductGolf> temp = dh.getProductGolf();
        if (!temp.equals(golf)) {
            if(temp.size()==0){
                golf.clear();
                golf.add(new ProductGolf("NoGolf", "NoGolf", "NoGolf", "NoGolf", "NoGolf", null));
            } else {
                golf.clear();
                golf.addAll(temp);
            }
            notifyAll("Golf");
        }
    }

    public static void updateTransport() {
        List<ProductTransport> temp = dh.getProductTransport();
        if (!temp.equals(transport)) {
            if(temp.size()==0){
                transport.clear();
                transport.add(new ProductTransport("NoTransport", "NoTransport", "NoTransport", "NoTransport", "NoTransport", null));
            } else {
                transport.clear();
                transport.addAll(temp);
            }
            notifyAll("Transport");
        }

    }

    public static void updateActivities() {
        List<ProductActivity> temp = dh.getProductActivities();
        if (!temp.equals(activities)) {
            if (temp.size() == 0) {
                activities.clear();
                activities.add(new ProductActivity("NoActivities", "NoActivities", "NoActivities", "NoActivities", "NoActivities", null));
            } else {
                activities.clear();
                activities.addAll(temp);
            }
            notifyAll("Activities");
        }
    }

    public static void updateSuppliers() {
        List<Supplier> temp = dh.getSuppliers();
        if (!temp.equals(suppliers)) {
            if (temp.size() == 0) {
                suppliers.clear();
                suppliers.add(new Supplier(-10, "NoSuppliers", "NoSuppliers", "NoSuppliers", "NoSuppliers", null));
            } else {
                suppliers.clear();
                suppliers.addAll(temp);
            }
            notifyAll("Suppliers");
        }
    }

    public static void updateBookings() {
        List<Booking> temp = dh.getBookings();
        if (!temp.equals(bookings)) {
            if (temp.size() == 0) {
                bookings.clear();
                bookings.add(new Booking("NoBookings", "NoBookings", "NoBookings", "NoBookings", 0, 0, 0, 0, 0, 0, 0, 0, "NoBookings", "NoBookings", "NoBookings", 0, "NoBookings", "NoBookings", 0, 0, "NoBookings", "NoBookings", "NoBookings", null, null, null, null, null));
            } else {
                bookings.clear();
                bookings.addAll(temp);
            }
            notifyAll("Bookings");
        }
    }

    public static void updateLogins() {
        List<Login> temp = dh.getLogins();
        if (!temp.equals(logins)) {
            if (temp.size() == 0) {
                logins.clear();
                logins.add(new Login(-10, "NoLogins", "NoLogins", "NoLogins"));
            } else {
                logins.clear();
                logins.addAll(temp);
            }
            notifyAll("Logins");
        }
    }

    public static void updateDocuments() {
        List<DataFile> temp = dh.getDocuments();
        if (!temp.equals(documents)) {
            if (temp.size() == 0) {
                documents.clear();
                documents.add(new DataFile("Documents", "NoDocuments", "NoDocuments", 0));
            } else {
                documents.clear();
                documents.addAll(temp);
            }
            notifyAll("Documents");
        }
    }

    public static void updatePackages() {
        List<TripPackage> temp = dh.getPackages();
        if (!temp.equals(packages)) {
            if (temp.size() == 0) {
                packages.clear();
                packages.add(new TripPackage(-10, "NoPackages", 0, "NoPackages", 0, 0, 0, 0, "NoPackages", "NoPackages", null, null, null, null));
            } else {
                packages.clear();
                packages.addAll(temp);
            }
            notifyAll("Packages");
        }
    }

    public static void updateTransactions() {
        List<Transaction> temp = dh.getTransactions();
        if (!temp.equals(transactions)) {
            if (temp.size() == 0) {
                transactions.clear();
                transactions.add(new Transaction(-10, "NoTransactions", "NoTransactions", "NoPackages", "NoPackages", 0.00, "NoTransactions"));
            } else {
                transactions.clear();
                transactions.addAll(temp);
            }
            notifyAll("Transactions");
        }
    }

    private static void notifyAll(String cat){
        for (ConnectionHandler ch: connectionsList) {
            if(ch instanceof UserConnectionHandler) {
                if(cat.matches("Activities")) {
                    ((UserConnectionHandler) ch).updateActivities.setValue(true);
                }
                if(cat.matches("Transport")) {
                    ((UserConnectionHandler) ch).updateTransport.setValue(true);
                }
                if(cat.matches("Golf")) {
                    ((UserConnectionHandler) ch).updateGolf.setValue(true);
                }
                if(cat.matches("Accommodation")) {
                    ((UserConnectionHandler) ch).updateAccommodation.setValue(true);
                }
                if(cat.matches("Packages")) {
                    ((UserConnectionHandler) ch).updatePackages.setValue(true);
                }
                if(cat.matches("Documnents")) {
                    ((UserConnectionHandler) ch).updateDocuments.setValue(true);
                }
                if(cat.matches("Logins")) {
                    ((UserConnectionHandler) ch).updateLogins.setValue(true);
                }
                if(cat.matches("Bookings")) {
                    ((UserConnectionHandler) ch).updateBookings.setValue(true);
                }
                if(cat.matches("Suppliers")) {
                    ((UserConnectionHandler) ch).updateSuppliers.setValue(true);
                }
                if(cat.matches("Transactions")) {
                    ((UserConnectionHandler) ch).updateTransactions.setValue(true);
                }
            }
        }
    }

    private class UpdateChecker extends TimerTask {
        @Override
        public void run() {
            updateAccommodation();
            updateGolf();
            updateTransport();
            updateActivities();
            updateSuppliers();
            updateBookings();
            updateLogins();
            updateDocuments();
            updatePackages();
            updateTransactions();
            //unreadNewQuotesMails.clear();
            //unreadContactMails.clear();
            //unreadFinanceMails.clear();
            //unreadOtherMails.clear();
            //readNewQuotesMails.clear();
            //readContactMails.clear();
            //readFinanceMails.clear();
            //readOtherMails.clear();
            /*try {
                /*IMAPFolder folder = (IMAPFolder) store.getFolder("inbox");//TODO sent
                if(!folder.isOpen()){
                    folder.open(Folder.READ_ONLY);
                }

                Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                for (int i = 0; i < messages.length; i++) {
                    /*Object obj = messages[i].getContent();
                    Multipart mp = (Multipart) messages[i].getContent();
                    BodyPart bp = ((Multipart) messages[i].getContent()).getBodyPart(0);
                    String msg = (String) bp.getContent();//TODO
                    List<String> toAddresses = new ArrayList<String>();
                    Address[] recipients = messages[i].getRecipients(Message.RecipientType.TO);
                    for (Address address : recipients) {
                        toAddresses.add(address.toString());
                    }
                    List<String> fromAddresses = new ArrayList<String>();
                    Address[] from = messages[i].getFrom();
                    for (Address address : from) {
                        fromAddresses.add(address.toString());
                    }
                    Mail mail = new Mail(toAddresses, fromAddresses, messages[i].getSubject(), messages[i].getReceivedDate().toString(), null, null, false);
                    System.out.println(mail.getSubject() + " - " + mail.getDate());
                    //System.out.println("Attachments: " + mail.getAttachments().size());//TODO
                    if(messages[i].getFrom()[0].equals("info@golfinsouthafrica.com")){
                        if(messages[i].getSubject().contains("GISA Enquiry About:")){
                            //new enquiry
                            unreadNewQuotesMails.add(mail);
                            readNewQuotesMails.add(mail);
                        } else if (messages[i].getSubject().contains("GISA Contact Page Enquiry")){
                            //Contact Page enquiry
                            unreadContactMails.add(mail);
                            readContactMails.add(mail);
                        } else {
                            //other
                            unreadOtherMails.add(mail);
                            readOtherMails.add(mail);
                        }
                    } else if(messages[i].getFrom()[0].equals("welmar@golfinsouthafrica.com")){
                        //TODO if GSNumber Finance
                        unreadFinanceMails.add(mail);
                        readFinanceMails.add(mail);
                    } else {
                        //other
                        unreadOtherMails.add(mail);
                        readOtherMails.add(mail);
                    }
                    unread++;*/
                //}
                unreadMails.clear();
                /*unreadMails.add(unreadNewQuotesMails.size());
                unreadMails.add(unreadContactMails.size());
                unreadMails.add(unreadFinanceMails.size());
                unreadMails.add(unreadOtherMails.size());*/
                for(ConnectionHandler ch: connectionsList){
                    if(ch instanceof UserConnectionHandler){
                        ((UserConnectionHandler) ch).unreadMails.clear();
                        ((UserConnectionHandler) ch).unreadMails.addAll(unreadMails);
                    }
                }
                /*messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), true));
                for (int i = 0; i < messages.length; i++) {
                    /*Object obj = messages[i].getContent();
                    Multipart mp = (Multipart) messages[i].getContent();
                    BodyPart bp = ((Multipart) messages[i].getContent()).getBodyPart(0);
                    String msg = (String) bp.getContent();
                    List<String> toAddresses = new ArrayList<String>();
                    Address[] recipients = messages[i].getRecipients(Message.RecipientType.TO);
                    for (Address address : recipients) {
                        toAddresses.add(address.toString());
                    }
                    List<String> fromAddresses = new ArrayList<String>();
                    Address[] from = messages[i].getFrom();
                    for (Address address : from) {
                        fromAddresses.add(address.toString());
                    }
                    Mail mail = new Mail(toAddresses, fromAddresses, messages[i].getSubject(), messages[i].getReceivedDate().toString(), null, null, false);
                    //System.out.println(mail.getSubject() + " - " + mail.getDate());
                    //System.out.println("Attachments: " + mail.getAttachments().size());
                    if(messages[i].getFrom()[0].equals("info@golfinsouthafrica.com")){
                        if(messages[i].getSubject().contains("GISA Enquiry About:")){
                            //new enquiry
                            readNewQuotesMails.add(mail);
                        } else if (messages[i].getSubject().contains("GISA Contact Page Enquiry")){
                            //Contact Page enquiry
                            readContactMails.add(mail);
                        } else {
                            //other
                            readOtherMails.add(mail);
                        }
                    } else if(messages[i].getFrom()[0].equals("welmar@golfinsouthafrica.com")){
                        //TODO if GSNumber Finance
                        readFinanceMails.add(mail);
                    } else {
                        //other
                        readOtherMails.add(mail);
                    }
                    read++;*/
                //}
                /*sortQuotesMailsByDate(readNewQuotesMails);
                sortContactMailsByDate(readContactMails);
                sortFinanceMailsByDate(readFinanceMails);
                sortOtherMailsByDate(readOtherMails);*/
                /*for(ConnectionHandler ch: connectionsList){
                    if(ch instanceof UserConnectionHandler){
                        ((UserConnectionHandler)ch).unreadMails.clear();
                        ((UserConnectionHandler)ch).unreadMails.add(unreadNewQuotesMails.size());
                        ((UserConnectionHandler)ch).unreadMails.add(unreadContactMails.size());
                        ((UserConnectionHandler)ch).unreadMails.add(unreadFinanceMails.size());
                        ((UserConnectionHandler)ch).unreadMails.add(unreadOtherMails.size());
                    }
                }*/
                /*Message msg = messages[0];
                String sub = msg.getSubject();
                Address[] from = msg.getFrom();
                Address[] to = msg.getAllRecipients();
                Date date = msg.getReceivedDate();
                int size = msg.getSize();
                Flags flags = msg.getFlags();
                String con = msg.getContentType();
                Object bod = msg.getContent();*/
            /*} catch (Exception ex) {
                ex.printStackTrace();
            }*/
        }
    }

    private class ExpiryChecker extends TimerTask {
        @Override
        public void run() {
            List<Booking> bookings = dh.getBookings();
            notifications.clear();
            for (Booking booking: bookings){
                if(LocalDate.parse(booking.getArrival()).isBefore(LocalDate.now())) {
                    if (booking.getProcess().matches("Quote")) {
                        dh.updateBookingProcess(booking.getGsNumber(), "ArchiveQuote", booking.getArrival(), booking.getClientName());
                        notifications.add(new Notification("Quote Expires", "The Quote of " + booking.getClientName() + " Expired And Have Been Moved To ArchiveQuote."));
                        dh.log("Quote Expired, Moved to ArchiveQuote");
                    } else if(booking.getProcess().matches("PendingBookingMade")) {
                        notifications.add(new Notification("PendingBookingMade Expires", "Please Update The Booking As Booking Expires and No Payments Have Been Made."));
                    } else if(booking.getProcess().matches("PendingDepositRecieved")) {
                        notifications.add(new Notification("PendingDepositReceived Expires", "Please Update The Booking As Booking Have Received Deposit And Is Expiring."));
                    } else if(booking.getProcess().matches("PendingDepositPaid")) {
                        notifications.add(new Notification("PendingDepositPaid Expires", "Please Update The Booking As Booking Deposit Have been Paid And Is Expiring."));
                    } else if(booking.getProcess().matches("PendingFullRecieved")) {
                        notifications.add(new Notification("PendingFullRecieved Expires", "Please Update The Booking As Booking Have Received Full Payment And Is Expiring."));
                    } else if(booking.getProcess().matches("ConfirmedFullPaid")) {
                        dh.updateBookingProcess(booking.getGsNumber(), "ArchiveComplete", booking.getArrival(), booking.getClientName());
                        notifications.add(new Notification("Booking Have Been Completed", "The Booking of " + booking.getClientName() + " Have Been Completed And been Moved To ArchiveComplete."));
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new Server();
    }

}
