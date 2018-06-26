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
    private ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
    private ObservableList<Booking> bookings = FXCollections.observableArrayList();
    private ObservableList<Mail> mails = FXCollections.observableArrayList();
    private ObservableList<Login> logins = FXCollections.observableArrayList();
    private ObservableList<DataFile> documents = FXCollections.observableArrayList();
    private volatile ObservableList<Object> outputQueue = FXCollections.observableArrayList();
    volatile BooleanProperty updateUser = new SimpleBooleanProperty(false);
    volatile BooleanProperty updateSuppliers = new SimpleBooleanProperty(false);
    volatile BooleanProperty updateBookings = new SimpleBooleanProperty(false);
    volatile BooleanProperty updateMails = new SimpleBooleanProperty(false);
    volatile BooleanProperty updateLogins = new SimpleBooleanProperty(false);
    volatile BooleanProperty updateDocuments = new SimpleBooleanProperty(false);

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
        updateMails.addListener((obs, oldV, newV) -> {
            if (newV) {
                updateMails();
                updateMails.set(false);
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
        user.addListener(e -> {
            outputQueue.add(0, user.get());
        });
        suppliers.addListener((InvalidationListener) e -> {
            outputQueue.add(0, Arrays.asList(suppliers.toArray()));
        });
        bookings.addListener((InvalidationListener) e -> {
            outputQueue.add(0, Arrays.asList(bookings.toArray()));
        });
        mails.addListener((InvalidationListener) e -> {
            outputQueue.add(0, Arrays.asList(mails.toArray()));
        });
        logins.addListener((InvalidationListener) e -> {
            outputQueue.add(0, Arrays.asList(logins.toArray()));
        });
        documents.addListener((InvalidationListener) e -> {
            outputQueue.add(0, Arrays.asList(documents.toArray()));
        });
        updateUser();
        updateSuppliers();
        updateBookings();
        updateMails();
        updateLogins();
        updateDocuments();
        new InputProcessor().start();
        new OutputProcessor().start();
    }

    private class InputProcessor extends Thread {
        public void run() {
            while (running.get()) {
                Object input;
                if ((input = getReply()) != null) {
                    if (input instanceof String) {
                        String text = input.toString();
                        if (text.startsWith("lo:")) {

                        } else if (text.startsWith("cp:")) {
                            dh.log("User " + username + "> Requested Change Password");
                            changePassword(text.substring(3).split(":")[0], text.substring(3).split(":")[1]);
                        }  else if (text.startsWith("gf:")) {
                            dh.log("User " + username + "> Requested File: " + text.substring(3).split(":")[1] + " From FileType: " + text.substring(3).split(":")[0]);
                            getFile(text.substring(3).split(":")[0], text.substring(3).split(":")[1]);
                        } else if (text.startsWith("rb:")) {
                            dh.log("User " + username + "> Removed Booking: " + text.substring(3));
                            dh.removeBooking(Integer.parseInt(text.substring(3)));
                            updateBookings.setValue(true);
                        } else if (text.startsWith("rs:")) {
                            dh.log("User " + username + "> Removed Supplier: " + text.substring(3));
                            dh.removeSupplier(Integer.parseInt(text.substring(3)));
                            updateSuppliers.setValue(true);
                        } else if (text.startsWith("rd:")) {
                            dh.log("User " + username + "> Removed Document: " + text.substring(3));
                            dh.deleteFile(text.substring(3).split(":")[0], text.substring(3).split(":")[1]);
                            updateDocuments.setValue(true);
                        } else if (text.startsWith("rl:")) {
                            dh.log("User " + username + "> Removed Login: " + text.substring(3));
                            dh.removeLogin(Integer.parseInt(text.substring(3)));
                            updateLogins.setValue(true);
                        } else if (text.startsWith("se:")) {
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
                        } else if (text.startsWith("pq:")) {
                            /*dh.log("User " + username + "> Processed Quotation " + text.substring(3).split(":")[0] + " to an invoice.");
                            dh.processQuotationToInvoice(text.substring(3).split(":")[0]);
                            updateLogins.setValue(true);*/
                        } else if (text.startsWith("usba:")) {
                            dh.updateSuppliersBookedAccommodation();
                        } else if (text.startsWith("usba:")) {
                            dh.updateSuppliersBookedGolf();
                        } else if (text.startsWith("usba:")) {
                            dh.updateSuppliersBookedTransport();
                        } else if (text.startsWith("usba:")) {
                            dh.updateSuppliersBookedActivities();
                        } else {
                            dh.log("User " + username + "> Requested Unknown Command: " + input);
                            System.out.println("Server> Unknown command: " + input);
                        }
                    } else if (input instanceof Booking) {
                        if (((Booking) input).getGsNumber().matches("New")) {
                            dh.addBooking((Booking)input);
                        } else {
                            dh.updateBooking((Booking)input);
                        }
                        updateBookings.setValue(true);
                    } else if (input instanceof Supplier) {
                        if (((Supplier) input).getSupplierNumber() == -1) {
                            dh.addSupplier((Supplier)input);
                        } else {
                            dh.updateSupplier((Supplier)input);
                        }
                        updateSuppliers.set(true);
                    } else if (input instanceof UploadFile){
                        try {
                            UploadFile uploadFile = (UploadFile) input;
                            File newFile = new File(Server.APPLICATION_FOLDER.getAbsolutePath() + "/" + uploadFile.getFileType() + "/" + uploadFile.getFileName());
                            newFile.getParentFile().mkdirs();
                            Files.write(newFile.toPath(), uploadFile.getFileData());
                            if (uploadFile.getFileType().matches("Documents")) {
                                updateDocuments.setValue(true);
                            } //TODO other
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else if (input instanceof Mail) {//TODO Mails
                        /*if (((Mail) input).getToMailAddress() != null) {
                            dh.sendMail((Mail) input);
                        } else {
                            dh.updateQuotation((Quotation)input);
                        }*/
                        updateMails.setValue(true);
                    }  else if (input instanceof Login) {
                        if (((Login) input).getLoginID() == -1) {
                            dh.addLogin((Login) input);
                        } else {
                            dh.updateLogin((Login) input);
                        }
                        updateLogins.setValue(true);
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

    private void changePassword(String prevPassword, String newPassword) {
        String sPassword = dh.getUserPassword(username);
        if (prevPassword.matches(sPassword) && dh.changeUserPassword(username, newPassword)) {
            outputQueue.add(0, "cp:y");
        } else {
            outputQueue.add(0, "cp:n");
        }
    }

    private void getFile(String fileType, String fileName) {
        File file = new File(Server.APPLICATION_FOLDER + "/" + fileType + "/" + fileName);
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            int size = 0;
            while (size < fileBytes.length) {
                System.out.println(Math.min(Server.BUFFER_SIZE, fileBytes.length - size));
                outputQueue.add(new FilePart(Arrays.copyOfRange(fileBytes, size, size + Math.min(Server.BUFFER_SIZE, fileBytes.length - size)), fileName));
                size += Math.min(Server.BUFFER_SIZE, fileBytes.length - size);
                dh.log("User " + username + "> Successfully Downloaded " + fileType + ": " + fileName);
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
    }

    private void updateBookings() {
        bookings.clear();
        bookings.addAll(dh.getBookings());
    }

    private void updateMails() {
        mails.clear();
        mails.addAll(dh.getMails());
    }

    private void updateLogins() {
        logins.clear();
        logins.addAll(dh.getLogins());
    }

    private void updateDocuments() {
        documents.clear();
        documents.addAll(dh.getDocuments());
    }

    public String getUsername() {
        return username;
    }

}
