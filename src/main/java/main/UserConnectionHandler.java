package main;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.*;
import org.apache.poi.hssf.record.PageBreakRecord;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class UserConnectionHandler extends ConnectionHandler implements Runnable {

    private String username;
    private ObjectProperty<User> user = new SimpleObjectProperty<>();
    private ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
    private ObservableList<Booking> bookings = FXCollections.observableArrayList();
    private ObservableList<Login> logins = FXCollections.observableArrayList();
    private ObservableList<DataFile> documents = FXCollections.observableArrayList();
    private ObservableList<TripPackage> packages = FXCollections.observableArrayList();
    public ObservableList<Integer> unreadMails = FXCollections.observableArrayList();
    private volatile ObservableList<Object> outputQueue = FXCollections.observableArrayList();
    volatile BooleanProperty updateUser = new SimpleBooleanProperty(false);
    volatile BooleanProperty updateSuppliers = new SimpleBooleanProperty(false);
    volatile BooleanProperty updateBookings = new SimpleBooleanProperty(false);
    volatile BooleanProperty updateLogins = new SimpleBooleanProperty(false);
    volatile BooleanProperty updateDocuments = new SimpleBooleanProperty(false);
    volatile BooleanProperty updatePackages = new SimpleBooleanProperty(false);
    volatile BooleanProperty updateAccommodation = new SimpleBooleanProperty(false);
    volatile BooleanProperty updateGolf = new SimpleBooleanProperty(false);
    volatile BooleanProperty updateTransport = new SimpleBooleanProperty(false);
    volatile BooleanProperty updateActivities = new SimpleBooleanProperty(false);

    public UserConnectionHandler(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, String username, ObservableList<ConnectionHandler> connectionsList, DatabaseHandler dh) {
        super(socket, objectInputStream, objectOutputStream, connectionsList, dh);
        this.username = username;
    }

    public void run() {
        updateUser.addListener((obs, oldV, newV) -> {
            if (newV) {
                updateUser();
                updateUser.set(false);
            }
        });
        updateSuppliers.addListener((obs, oldV, newV) -> {
            if (newV) {
                updateSuppliers();
                updateSuppliers.set(false);
            }
        });
        updateBookings.addListener((obs, oldV, newV) -> {
            if (newV) {
                updateBookings();
                updateBookings.set(false);
            }
        });
        updateLogins.addListener((obs, oldV, newV) -> {
            if (newV) {
                updateLogins();
                updateLogins.set(false);
            }
        });
        updateDocuments.addListener((obs, oldV, newV) -> {
            if (newV) {
                updateDocuments();
                updateDocuments.set(false);
            }
        });
        updatePackages.addListener((obs, oldV, newV) -> {
            if (newV) {
                updatePackages();
                updatePackages.set(false);
            }
        });
        updateAccommodation.addListener((obs, oldV, newV) -> {
            if (newV) {
                updateAccommodation();
                updateAccommodation.set(false);
            }
        });
        updateGolf.addListener((obs, oldV, newV) -> {
            if (newV) {
                updateGolf();
                updateGolf.set(false);
            }
        });
        updateTransport.addListener((obs, oldV, newV) -> {
            if (newV) {
                updateTransport();
                updateTransport.set(false);
            }
        });
        updateActivities.addListener((obs, oldV, newV) -> {
            if (newV) {
                updateActivities();
                updateActivities.set(false);
            }
        });
        user.addListener(e -> {
            outputQueue.add(0, user.get());
        });
        suppliers.addListener((InvalidationListener) e -> {
            outputQueue.add(0, Arrays.asList(suppliers.toArray()));
        });
        bookings.addListener((InvalidationListener) e -> {
            outputQueue.add(0, Arrays.asList(bookings.toArray()));
        });
        logins.addListener((InvalidationListener) e -> {
            outputQueue.add(0, Arrays.asList(logins.toArray()));
        });
        documents.addListener((InvalidationListener) e -> {
            outputQueue.add(0, Arrays.asList(documents.toArray()));
        });
        packages.addListener((InvalidationListener) e -> {
            outputQueue.add(0, Arrays.asList(packages.toArray()));
        });
        unreadMails.addListener((InvalidationListener) e -> {
            outputQueue.add(0, Arrays.asList(unreadMails.toArray()));
        });
        Server.accommodation.addListener((InvalidationListener) e -> {
            outputQueue.add(0, Arrays.asList(Server.accommodation.toArray()));
        });
        Server.golf.addListener((InvalidationListener) e -> {
            outputQueue.add(0, Arrays.asList(Server.golf.toArray()));
        });
        Server.transport.addListener((InvalidationListener) e -> {
            outputQueue.add(0, Arrays.asList(Server.transport.toArray()));
        });
        Server.activities.addListener((InvalidationListener) e -> {
            outputQueue.add(0, Arrays.asList(Server.activities.toArray()));
        });
        unreadMails.addListener((InvalidationListener) e -> {
            outputQueue.add(0, Arrays.asList(unreadMails.toArray()));
        });
        updateUser();
        updateSuppliers();
        updateBookings();
        updateLogins();
        updateDocuments();
        updatePackages();
        outputQueue.add(0, Arrays.asList(Server.accommodation.toArray()));
        outputQueue.add(0, Arrays.asList(Server.golf.toArray()));
        outputQueue.add(0, Arrays.asList(Server.transport.toArray()));
        outputQueue.add(0, Arrays.asList(Server.activities.toArray()));
        unreadMails.clear();
        unreadMails.addAll(Server.unreadMails);
        new InputProcessor().start();
        new OutputProcessor().start();
    }

    private class InputProcessor extends Thread {
        public void run() {
            while (running.get()) {
                Object input;
                if ((input = getReply()) != null) {
                    System.out.println(input);
                    if (input instanceof String) {
                        String text = input.toString();
                        if (text.startsWith("cp:")) {//ChangePassword
                            dh.log("User " + username + "> Requested Change Password");
                            changePassword(text.substring(3).split(":")[0], text.substring(3).split(":")[1]);
                        }  else if (text.startsWith("gf:")) {//GetFile
                            dh.log("User " + username + "> Requested File: " + text.substring(3).split(":")[0]);
                            getFile(text.substring(3).split(":")[0]);
                        }  else if (text.startsWith("gq:")) {//GetFile
                            dh.log("User " + username + "> Requested Costing: " + text.substring(3).split(":")[0]);
                            getCosting(text.substring(3).split(":")[0]);
                        } else if (text.startsWith("rb:")) {//RemoveBooking
                            dh.log("User " + username + "> Removed Booking: " + text.substring(3));
                            dh.removeBooking(Integer.parseInt(text.substring(3)));
                            updateBookings.setValue(true);
                        } else if (text.startsWith("rs:")) {//RemoveSupplier
                            dh.log("User " + username + "> Removed Supplier: " + text.substring(3));
                            dh.removeSupplier(Integer.parseInt(text.substring(3)));
                            updateSuppliers.setValue(true);
                        } else if (text.startsWith("rd:")) {//RemoveDocument
                            dh.log("User " + username + "> Removed Document: " + text.substring(3));
                            dh.deleteFile(Server.DOCUMENTS_FOLDER.getAbsolutePath(), text.substring(3).split(":")[0]);
                            updateDocuments.setValue(true);
                        } else if (text.startsWith("rl:")) {//RemoveLogin
                            dh.log("User " + username + "> Removed Login: " + text.substring(3));
                            dh.removeLogin(Integer.parseInt(text.substring(3)));
                            updateLogins.setValue(true);
                        } else if (text.startsWith("rcd:")) {//RemoveContactDetails
                            dh.log("User " + username + "> Removed Contact Details: " + text.substring(3));
                            dh.removeContactDetails(Integer.parseInt(text.substring(4)));
                            updateSuppliers.setValue(true);
                        } else if(text.startsWith("snd:")){
                            if(text.substring(4).startsWith("Costing")){
                                dh.log("User " + username + "> Send Costing for: GS" + text.substring(12));
                                new Thread(() -> Email.sendCosting("GS" + text.substring(12).split(":")[0], text.substring(12).split(":")[1], text.substring(12).split(":")[2], text.substring(12).split(":")[3], user.get().getFirstName())).start();
                            }
                        } else if (text.startsWith("se:")) {//TODO
                            /*if((!text.substring(3).split(":")[3].matches(""))) {
                                dh.log("User " + username + "> Emailed " + text.substring(3).split(":")[4] + "(" + text.substring(3).split(":")[3] + " to: " + text.substring(3).split(":")[0]);
                                if (new Email().email(text.substring(3).split(":")[0], text.substring(3).split(":")[1], text.substring(3).split(":")[2], text.substring(3).split(":")[3], text.substring(3).split(":")[4])) {
                                    outputQueue.add("es:t");
                                } else {
                                    outputQueue.add("es:f");
                                }
                            } else {
                                dh.log("User " + username + "> Emailed: " + text.substring(3).split(":")[0]);
                                if (new Email().email(text.substring(3).split(":")[0], text.substring(3).split(":")[1], text.substring(3).split(":")[2], null, null)) {
                                    outputQueue.add("es:t");
                                } else {
                                    outputQueue.add("es:f");
                                }
                            }*/
                        } else if (text.startsWith("pq:")) {//TODO
                            /*dh.log("User " + username + "> Processed Quotation " + text.substring(3).split(":")[0] + " to an invoice.");
                            dh.processQuotationToInvoice(text.substring(3).split(":")[0]);
                            updateLogins.setValue(true);*/
                        } else if (text.startsWith("gm:")) {//GetMails
                            if(text.substring(3).split(":")[0].matches("Quotes") && text.substring(3).split(":")[1].matches("unread")){
                                outputQueue.addAll(Server.unreadNewQuotesMails);
                            } else if (text.substring(3).split(":")[0].matches("Quotes") && text.substring(3).split(":")[1].matches("all")){
                                outputQueue.addAll(Server.readNewQuotesMails);
                            } else if (text.substring(3).split(":")[0].matches("Contact") && text.substring(3).split(":")[1].matches("unread")){
                                outputQueue.addAll(Server.unreadContactMails);
                            } else if (text.substring(3).split(":")[0].matches("Contact") && text.substring(3).split(":")[1].matches("all")){
                                outputQueue.addAll(Server.readContactMails);
                            } else if (text.substring(3).split(":")[0].matches("Finance") && text.substring(3).split(":")[1].matches("unread")){
                                outputQueue.addAll(Server.unreadFinanceMails);
                            } else if (text.substring(3).split(":")[0].matches("Finance") && text.substring(3).split(":")[1].matches("all")){
                                outputQueue.addAll(Server.readFinanceMails);
                            } else if (text.substring(3).split(":")[0].matches("Other") && text.substring(3).split(":")[1].matches("unread")){
                                outputQueue.addAll(Server.unreadOtherMails);
                            } else if (text.substring(3).split(":")[0].matches("Other") && text.substring(3).split(":")[1].matches("all")){
                                outputQueue.addAll(Server.readOtherMails);
                            }
                        }else if (text.startsWith("usba:")) {//UpdateSupplierBookedAccommodation TODO
                            dh.updateSuppliersBookedAccommodation();
                        } else if (text.startsWith("usbg:")) {//UpdateSupplierBookedGolf
                            dh.updateSuppliersBookedGolf();
                        } else if (text.startsWith("usbr:")) {//UpdateSupplierBookedTransport
                            dh.updateSuppliersBookedTransport();
                        } else if (text.startsWith("usbt:")) {//UpdateSupplierBookedActivities
                            dh.updateSuppliersBookedActivities();
                        } else {
                            dh.log("User " + username + "> Requested Unknown Command: " + input);
                            System.out.println("Server> Unknown command: " + input);
                        }
                    } else if (input instanceof Booking) {//AddUpdateBookings
                        if (((Booking) input).getGsNumber().matches("New")) {
                            int gs = dh.addBooking((Booking)input);
                            outputQueue.add("nbgs:" + gs + ":" + getCostingFileLength(gs));
                        } else {
                            dh.updateBooking((Booking)input);
                            outputQueue.add("nbqs:-1:" + getCostingFileLength(Integer.parseInt(((Booking)input).getGsNumber())));
                        }
                        updateBookings.setValue(true);
                    } else if (input instanceof Supplier) {//AddUpdateSuppliers
                        if (((Supplier) input).getSupplierNumber() == -1) {
                            dh.addSupplier((Supplier)input);
                        } else {
                            dh.updateSupplier((Supplier)input);
                        }
                        updateSuppliers.set(true);
                    } else if (input instanceof UploadFile){//UploadFile
                        try {
                            UploadFile uploadFile = (UploadFile) input;
                            File newFile = new File(Server.DOCUMENTS_FOLDER + "/" + uploadFile.getFileName());//IF Others
                            newFile.getParentFile().mkdirs();
                            Files.write(newFile.toPath(), uploadFile.getFileData());
                            if (uploadFile.getFileType().matches("Documents")) {
                                updateDocuments.setValue(true);
                            } //other
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else if (input instanceof Mail) {//TODO Mails
                        /*if (((Mail) input).getToMailAddress() != null) {
                            dh.sendMail((Mail) input);
                        } else {
                            dh.updateQuotation((Quotation)input);
                        }*/
                        //updateMails.setValue(true);
                    }  else if (input instanceof Login) {//AddUpdateLogins
                        if (((Login) input).getLoginID() == -1) {
                            dh.addLogin((Login) input);
                        } else {
                            dh.updateLogin((Login) input);
                        }
                        updateLogins.setValue(true);
                    } else if (input instanceof TripPackage) {//TODO
                        if (((TripPackage) input).getPackageID() == -1) {
                            dh.addPackage((TripPackage)input);
                        } else {
                            dh.updatePackage((TripPackage)input);
                        }
                        updatePackages.set(true);
                    } else if (input instanceof ContactDetails){//AddUpdateContactDetails
                        if (((ContactDetails) input).getContactDetailsID() > 1000000) {
                            dh.addContactDetails((ContactDetails) input);
                        } else {
                            dh.updateContactDetails((ContactDetails) input);
                        }
                        updateSuppliers.setValue(true);
                    }
                }
            }
        }
    }

    private class OutputProcessor extends Thread {
        public void run() {
            while (running.get()) {
                try {
                    if (!outputQueue.isEmpty()) {
                        Object out = outputQueue.get(0);
                        if (out instanceof List && (((List) out).isEmpty() || ((List) out).get(0) == null)) {
                            outputQueue.remove(out);
                        } else {
                            sendData(out);
                            dh.log("User " + username + "> OutputProcessor> Sent: " + out + " (" + out.getClass().toString() + ")");
                            outputQueue.remove(out);
                        }
                    }
                    Thread.sleep(20);
                } catch (Exception ex) {
                    dh.log("Server> OutputProcessor> " + ex);
                    ex.printStackTrace();
                }
            }
        }
    }

    public void sendData(Object data) {
        try {
            synchronized (objectOutputStream) {
                System.out.println(data);
                objectOutputStream.writeObject(data);
                objectOutputStream.flush();
                objectOutputStream.reset();
            }
        } catch (Exception ex) {
            terminateConnection();
            dh.log("Server> sendData> " + ex);
            ex.printStackTrace();
        }
    }

    private int getCostingFileLength(int GSNumber) {
        File file = new File(Server.BOOKINGS_FOLDER + "/a. Quotes");
        File [] files = file.listFiles();
        for(File f:files) {
            if(f.getAbsolutePath().contains("GS" + GSNumber)){
                f = new File(f.getAbsolutePath() + "/Costing.xls");
                return (int) f.length();
            }
        }
        return -1;
    }

    private void changePassword(String prevPassword, String newPassword) {
        String sPassword = dh.getUserPassword(username);
        if (prevPassword.matches(sPassword) && dh.changeUserPassword(username, newPassword)) {
            outputQueue.add(0, "cp:y");
        } else {
            outputQueue.add(0, "cp:n");
        }
    }

    private void getFile(String fileName) {
        File file = new File(Server.DOCUMENTS_FOLDER + "/" + fileName);
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            int size = 0;
            while (size < fileBytes.length) {
                System.out.println(Math.min(Server.BUFFER_SIZE, fileBytes.length - size));
                outputQueue.add(new FilePart(Arrays.copyOfRange(fileBytes, size, size + Math.min(Server.BUFFER_SIZE, fileBytes.length - size)), fileName));
                size += Math.min(Server.BUFFER_SIZE, fileBytes.length - size);
                dh.log("User " + username + "> Successfully Downloaded : " + fileName);
            }
        } catch (Exception ex) {
            dh.log("Server> getFile> " + ex);
            ex.printStackTrace();
        }
    }

    private void getCosting(String GSNumber) {
        File file = new File(Server.BOOKINGS_FOLDER + "/a. Quotes");
        File [] files = file.listFiles();
        for(File f:files) {
            System.out.println(GSNumber);
            System.out.println(f.getAbsolutePath());
            if(f.getAbsolutePath().contains(GSNumber)){
                file = new File(f.getAbsolutePath() + "/Costing.xls");
                break;
            }
        }
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            int size = 0;
            while (size < fileBytes.length) {
                System.out.println(Math.min(Server.BUFFER_SIZE, fileBytes.length - size));
                outputQueue.add(new FilePart(Arrays.copyOfRange(fileBytes, size, size + Math.min(Server.BUFFER_SIZE, fileBytes.length - size)), GSNumber));
                size += Math.min(Server.BUFFER_SIZE, fileBytes.length - size);
                dh.log("User " + username + "> Successfully Downloaded Costing: " + GSNumber);
            }
        } catch (Exception ex) {
            dh.log("Server> getFile> " + ex);
            ex.printStackTrace();
        }
    }

    public User getUser() {
        return user.getValue();
    }

    private void updateUser() {
        user.setValue(dh.getUser(username));
    }

    private void updateSuppliers() {
        suppliers.clear();
        suppliers.addAll(dh.getSuppliers());
        if(suppliers.isEmpty()){
            suppliers.addAll(new Supplier(-10, "NoSuppliers", "NoSuppliers", "NoSuppliers", "NoSuppliers", "NoSuppliers", null));
        }
    }

    private void updateBookings() {
        bookings.clear();
        bookings.addAll(dh.getBookings());
        if(bookings.isEmpty()){
            bookings.addAll(new Booking("NoBookings", "NoBookings", "NoBookings", "NoBookings", 0, 0, 0, 0, "NoBookings", "NoBookings", "NoBookings", 0, "NoBookings", "NoBookings", 0, 0, "NoBookings", "NoBookings", "NoBookings", null, null, null, null));
        }
    }

    private void updateLogins() {
        logins.clear();
        logins.addAll(dh.getLogins());
        if(logins.isEmpty()){
            logins.addAll(new Login(-10, "NoLogins", "NoLogins", "NoLogins"));
        }
    }

    private void updateDocuments() {
        documents.clear();
        documents.addAll(dh.getDocuments());
        if(documents.isEmpty()){
            documents.addAll(new DataFile("Documents", "NoDocuments", "NoDocuments", 0));
        }
    }

    private void updatePackages() {
        packages.clear();
        packages.addAll(dh.getPackages());
        if(packages.isEmpty()){
            packages.addAll(new TripPackage(-10, "NoPackages", 0, "NoPackages", 0, 0, 0, 0, "NoPackages", "NoPackages", null, null, null, null));
        }
    }

    private void updateAccommodation() {
        outputQueue.addAll(Server.accommodation);
    }

    private void updateGolf() {
        outputQueue.addAll(Server.golf);
    }

    private void updateTransport() {
        outputQueue.addAll(Server.transport);
    }

    private void updateActivities() {
        outputQueue.addAll(Server.activities);
    }

    public String getUsername() {
        return username;
    }

}
