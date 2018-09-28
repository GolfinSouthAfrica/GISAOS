package main;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.*;

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
    volatile BooleanProperty updateTransactions = new SimpleBooleanProperty(false);
    volatile BooleanProperty updateNotifications = new SimpleBooleanProperty(false);

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
        updateTransactions.addListener((obs, oldV, newV) -> {
            if (newV) {
                updateTransactions();
                updateTransactions.set(false);
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
        updateNotifications.addListener((obs, oldV, newV) -> {
            if (newV) {
                updateNotifications();
                updateNotifications.set(false);
            }
        });
        user.addListener(e -> {
            outputQueue.add(0, user.get());
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
        updateTransactions();
        updateNotifications();
        updateAccommodation();
        updateGolf();
        updateTransport();
        updateActivities();
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
                            Server.updateBookings();
                        } else if (text.startsWith("rs:")) {//RemoveSupplier
                            dh.log("User " + username + "> Removed Supplier: " + text.substring(3));
                            dh.removeSupplier(Integer.parseInt(text.substring(3)));
                            Server.updateSuppliers();
                        } else if (text.startsWith("rp:")) {//RemoveSupplier
                            dh.log("User " + username + "> Package: " + text.substring(3));
                            dh.removePackage(Integer.parseInt(text.substring(3)));
                            Server.updatePackages();
                        } else if (text.startsWith("rd:")) {//RemoveDocument
                            dh.log("User " + username + "> Removed Document: " + text.substring(3));
                            dh.deleteFile(Server.DOCUMENTS_FOLDER.getAbsolutePath(), text.substring(3).split(":")[0]);
                            Server.updateDocuments();
                        } else if (text.startsWith("rl:")) {//RemoveLogin
                            dh.log("User " + username + "> Removed Login: " + text.substring(3));
                            dh.removeLogin(Integer.parseInt(text.substring(3)));
                            Server.updateLogins();
                        } else if (text.startsWith("rcd:")) {//RemoveContactDetails
                            dh.log("User " + username + "> Removed Contact Details: " + text.substring(3));
                            dh.removeContactDetails(Integer.parseInt(text.substring(4)));
                            Server.updateSuppliers();
                        }  else if (text.startsWith("rtr:")) {//RemoveTransaction
                            dh.log("User " + username + "> Removed Transaction: " + text.substring(4, text.lastIndexOf(":")));
                            dh.removeTransaction(Integer.parseInt(text.substring(4).split(":")[0]), text.substring(4).split(":")[1].substring(2));
                            Server.updateTransactions();
                            Server.updateBookings();
                        } else if(text.startsWith("snd:")){
                            if(text.substring(4).startsWith("Costing")){//TODO
                                dh.log("User " + username + "> Send Costing for: GS" + text.substring(12));
                                //new Thread(() -> Email.sendCosting("GS" + text.substring(12).split(":")[0], text.substring(12).split(":")[1], text.substring(12).split(":")[2], text.substring(12).split(":")[3], user.get().getFirstName(), dh.getBookingPerPerson(text.substring(12).split(":")[0]))).start();
                            }
                        } else if (text.startsWith("gm:")) {//GetMails
                            /*if(text.substring(3).split(":")[0].matches("Quotes") && text.substring(3).split(":")[1].matches("unread")){
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
                            }*/
                        }else if (text.startsWith("usba:")) {//UpdateSupplierBookedAccommodation TODO
                            dh.updateSuppliersBookedAccommodation(Integer.parseInt(text.substring(5).split(":")[0]), Integer.parseInt(text.substring(5).split(":")[1]));
                            Server.updateBookings();
                        } else if (text.startsWith("usbg:")) {//UpdateSupplierBookedGolf
                            dh.updateSuppliersBookedGolf(Integer.parseInt(text.substring(5).split(":")[0]), Integer.parseInt(text.substring(5).split(":")[1]));
                            Server.updateBookings();
                        } else if (text.startsWith("usbr:")) {//UpdateSupplierBookedTransport
                            dh.updateSuppliersBookedTransport(Integer.parseInt(text.substring(5).split(":")[0]), Integer.parseInt(text.substring(5).split(":")[1]));
                            Server.updateBookings();
                        } else if (text.startsWith("usbt:")) {//UpdateSupplierBookedActivities
                            dh.updateSuppliersBookedActivities(Integer.parseInt(text.substring(5).split(":")[0]), Integer.parseInt(text.substring(5).split(":")[1]));
                            Server.updateBookings();
                        } else if (text.startsWith("bpn:")) {//UpdateBookingNext
                            dh.updateBookingProcess(text.substring(4).split(":")[0], text.substring(4).split(":")[1], text.substring(4).split(":")[2], text.substring(4).split(":")[3]);
                            Server.updateBookings();
                        } else if (text.startsWith("bpp:")) {//UpdateBookingPrevious
                            dh.updateBookingProcess(text.substring(4).split(":")[0], text.substring(4).split(":")[1], text.substring(4).split(":")[2], text.substring(4).split(":")[3]);
                            Server.updateBookings();
                        } else {
                            dh.log("User " + username + "> Requested Unknown Command: " + input);
                            System.out.println("Server> Unknown command: " + input);
                        }
                    } else if (input instanceof Booking) {//AddUpdateBookings
                        if (((Booking) input).getGsNumber().matches("New")) {
                            int gs = dh.addBooking((Booking)input);
                            outputQueue.add("nbgs:" + gs + ":" + getCostingFileLength(gs));
                            //dh.generateInvoice("GS25");//TODO
                        } else {
                            int gs = dh.updateBooking((Booking)input);
                            outputQueue.add("nbgs:" + gs + ":" + getCostingFileLength(gs));
                        }
                        Server.updateBookings();
                    } else if (input instanceof Supplier) {//AddUpdateSuppliers
                        if (((Supplier) input).getSupplierNumber() == -1) {
                            dh.addSupplier((Supplier)input);
                        } else {
                            dh.updateSupplier((Supplier)input);
                        }
                        Server.updateSuppliers();
                    } else if (input instanceof UploadFile){//UploadFile
                        try {
                            UploadFile uploadFile = (UploadFile) input;
                            File newFile = new File(Server.DOCUMENTS_FOLDER + "/" + uploadFile.getFileName());//IF Others
                            newFile.getParentFile().mkdirs();
                            Files.write(newFile.toPath(), uploadFile.getFileData());
                            if (uploadFile.getFileType().matches("Documents")) {
                                Server.updateDocuments();
                            } //other
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } /*else if (input instanceof Mail) {//TODO Mails
                        /*if (((Mail) input).getToMailAddress() != null) {
                            dh.sendMail((Mail) input);
                        } else {
                            dh.updateQuotation((Quotation)input);
                        }*/
                        //updateMails.setValue(true);
                   /* }*/ else if (input instanceof Login) {//AddUpdateLogins
                        if (((Login) input).getLoginID() == -1) {
                            dh.addLogin((Login) input);
                        } else {
                            dh.updateLogin((Login) input);
                        }
                        Server.updateLogins();
                    } else if (input instanceof TripPackage) {//TODO
                        if (((TripPackage) input).getPackageID() == -1) {
                            dh.addPackage((TripPackage)input);
                        } else {
                            dh.updatePackage((TripPackage)input);
                        }
                        Server.updatePackages();
                    } else if (input instanceof ContactDetails){//AddUpdateContactDetails
                        if (((ContactDetails) input).getContactDetailsID() > 100000) {
                            dh.addContactDetails((ContactDetails) input);
                        } else {
                            dh.updateContactDetails((ContactDetails) input);
                        }
                        Server.updateSuppliers();
                    } else if (input instanceof Transaction){//AddUpdateTransaction
                        if (((Transaction) input).getID() == -1) {
                            dh.addTransaction((Transaction) input);
                        } else {
                            dh.updateTransaction((Transaction) input);
                        }
                        Server.updateTransactions();
                        Server.updateBookings();
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
        outputQueue.add(0, Arrays.asList(Server.suppliers.toArray()));
    }

    private void updateBookings() {
        outputQueue.add(0, Arrays.asList(Server.bookings.toArray()));
    }

    private void updateLogins() {
        outputQueue.add(0, Arrays.asList(Server.logins.toArray()));
    }

    private void updateDocuments() {
        outputQueue.add(0, Arrays.asList(Server.documents.toArray()));
    }

    private void updatePackages() {
        outputQueue.add(0, Arrays.asList(Server.packages.toArray()));
    }

    private void updateAccommodation() {
        outputQueue.add(0, Arrays.asList(Server.accommodation.toArray()));
    }

    private void updateGolf() {
        outputQueue.add(0, Arrays.asList(Server.golf.toArray()));
    }

    private void updateTransport() {
        outputQueue.add(0, Arrays.asList(Server.transport.toArray()));
    }

    private void updateActivities() {
        outputQueue.add(0, Arrays.asList(Server.activities.toArray()));
    }

    private void updateTransactions() {
        outputQueue.add(0, Arrays.asList(Server.transactions.toArray()));
    }

    private void updateNotifications() {
        outputQueue.add(0, Arrays.asList(Server.notifications.toArray()));
    }

    public String getUsername() {
        return username;
    }

}
