package main;

import com.sun.mail.imap.IMAPFolder;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Mail;
import models.ProductAccomodation;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.EOFException;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {

    static final File APPLICATION_FOLDER = new File("G:/My Drive/e. Office/OfficeAppServerData");
    static final File TEMPLATES_FOLDER = new File("G:/My Drive/b. Templates");
    static final File BOOKINGS_FOLDER = new File("G:/My Drive/a. Bookings");
    static final File DOCUMENTS_FOLDER = new File("G:/My Drive/d. Documents");
    static final File SUPPLIER_FOLDER = new File("G:/My Drive/c. Suppliers");
    static final File OFFICE_FOLDER = new File("G:/My Drive/e. Office");
    static final File DATABASE_FILE = new File(APPLICATION_FOLDER.getAbsolutePath() + "/GolfInSouthAfricaDB.db");
    static final File LOG_FILE = new File(APPLICATION_FOLDER.getAbsolutePath() + "/GolfInSouthAfricaLogFile.txt");
    public static List<Mail> unreadNewQuotesMails = FXCollections.observableArrayList();
    public static List<Mail> readNewQuotesMails = FXCollections.observableArrayList();
    public static List<Mail> unreadContactMails = FXCollections.observableArrayList();
    public static List<Mail> readContactMails = FXCollections.observableArrayList();
    public static List<Mail> unreadFinanceMails = FXCollections.observableArrayList();
    public static List<Mail> readFinanceMails = FXCollections.observableArrayList();
    public static List<Mail> unreadOtherMails = FXCollections.observableArrayList();
    public static List<Mail> readOtherMails = FXCollections.observableArrayList();
    static final int BUFFER_SIZE = 4194304;
    public static ObservableList<ConnectionHandler> connectionsList = FXCollections.observableArrayList();
    public static final int PORT = 1521;
    public static final int MAX_CONNECTIONS = 5;
    public DatabaseHandler dh = new DatabaseHandler();
    private Timer timer;
    private Store store;

    public Server() {
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
        try {
            Properties props = System.getProperties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.host", "imap.gmail.com");
            props.put("mail.imaps.port", "993");
            Session session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect("info@golfinsouthafrica.com", "GISADefault1234@");//TODO
            System.out.println(store);
            timer = new Timer();
            timer.schedule(new UpdateChecker(), 3000);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public class ClientListener extends Thread {
        @Override
        public void run() {
            try {
                dh.log("Server> Trying to set up client on port " + PORT);
                /*System.setProperty("javax.net.ssl.keyStore", APPLICATION_FOLDER.getAbsolutePath() + "/campuslive.store");//TODO
                System.setProperty("javax.net.ssl.keyStorePassword", "campuslivepassword1");*/
                dh.log("Server> Set up client on port " + PORT);
                //ServerSocket ss = SSLServerSocketFactory.getDefault().createServerSocket(PORT);
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
                                    //dh.createQuotation("Test123");
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
                        connectionsList.remove(this);//TODO
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

    public static void sortQuotesMailsByDate(List<Mail> readNewQuotesMails){
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
    }

    public static void sortContactMailsByDate(List<Mail> readContactMails){
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
    }

    public static void sortFinanceMailsByDate(List<Mail> readFinanceMails){
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
    }

    public static void sortOtherMailsByDate(List<Mail> readOtherMails){
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
    }

    private class UpdateChecker extends TimerTask {
        @Override
        public void run() {
            unreadNewQuotesMails.clear();
            unreadContactMails.clear();
            unreadFinanceMails.clear();
            unreadOtherMails.clear();
            readNewQuotesMails.clear();
            readContactMails.clear();
            readFinanceMails.clear();
            readOtherMails.clear();
            try {
                Folder folder = store.getFolder("inbox");//TODO sent
                if(!folder.isOpen()){
                    folder.open(Folder.READ_ONLY);
                }

                Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                for (int i = 0; i < messages.length; i++) {
                    Object obj = messages[i].getContent();
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
                    Mail mail = new Mail(toAddresses, fromAddresses, messages[i].getSubject(), messages[i].getReceivedDate().toString(), msg, null, false);
                    System.out.println("------------------------------");
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
                }

                messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), true));
                for (int i = 0; i < messages.length; i++) {
                    Object obj = messages[i].getContent();
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
                    Mail mail = new Mail(toAddresses, fromAddresses, messages[i].getSubject(), messages[i].getReceivedDate().toString(), msg, null, false);
                    System.out.println("------------------------------");
                    System.out.println("Attachments: " + mail.getAttachments().size());
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
                }

                sortQuotesMailsByDate(readNewQuotesMails);
                sortContactMailsByDate(readContactMails);
                sortFinanceMailsByDate(readFinanceMails);
                sortOtherMailsByDate(readOtherMails);

                for(ConnectionHandler ch: connectionsList){
                    if(ch instanceof UserConnectionHandler){
                        ((UserConnectionHandler)ch).unreadMails.clear();
                        ((UserConnectionHandler)ch).unreadMails.add(unreadNewQuotesMails.size());
                        ((UserConnectionHandler)ch).unreadMails.add(unreadContactMails.size());
                        ((UserConnectionHandler)ch).unreadMails.add(unreadFinanceMails.size());
                        ((UserConnectionHandler)ch).unreadMails.add(unreadOtherMails.size());
                    }
                }

                /*Message msg = messages[0];
                String sub = msg.getSubject();
                Address[] from = msg.getFrom();
                Address[] to = msg.getAllRecipients();
                Date date = msg.getReceivedDate();
                int size = msg.getSize();
                Flags flags = msg.getFlags();
                String con = msg.getContentType();
                Object bod = msg.getContent();*/


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Server();
    }

}
