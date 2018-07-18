package main;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

public class Email {

    public static Boolean emailPassword(String username, String email, String password) {
        return email(email, "Golf in South Africa - Forgot password", "Dear " + username + "\n\nYou requested to send your Golf in South Africa password to your email.\nIf this wasn't you please contact us.\n\nPassword:\t" + password, null, null);
    }

    public static Boolean email(String email, String emailSubject, String emailMessage, String documentType, String attachment) {
        try {
            Properties props = System.getProperties();
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.user", "infochampley@gmail.com");
            props.put("mail.smtp.password", "Champley2018RonCar");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", true);
            Session session = Session.getInstance(props, null);
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", "infochampley", "Champley2018RonCar");
            MimeMessage message = new MimeMessage(session);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(emailSubject);
            message.setText(emailMessage);
            if (attachment != null) {
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText("This is message body");
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                String filename = Server.APPLICATION_FOLDER.getAbsolutePath() + "/" + documentType + "/" + attachment;
                DataSource source = new FileDataSource(filename);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filename);
                multipart.addBodyPart(messageBodyPart);
                message.setContent(multipart);
                transport.sendMessage(message, message.getAllRecipients());
            } else {
                transport.sendMessage(message, message.getAllRecipients());
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static Boolean sendCosting(String GSNumber, String email, String clientName, String textMessage, String name){
        //TODO add Booking Name

        String costingTable = "<html><body><table border=\"1\"><tr><td></td><td>Short Quote</td><td></td><td></td></tr><tr><td>Pax</td><td>Package</td><td>Per Person</td><td>Total</td></tr>";
        File file = new File(Server.BOOKINGS_FOLDER + "/a. Quotes");
        File [] files = file.listFiles();
        for(File f:files) {
            if(f.getAbsolutePath().contains(GSNumber)){
                file = new File(f.getAbsolutePath() + "/Costing.xls");
            }
        }
        System.out.println(file.getAbsolutePath());
        try {
            Properties props = System.getProperties();
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", true);
            Session session = Session.getDefaultInstance(props, null);
            Transport transport = session.getTransport("smtp");
            transport.connect("info@golfinsouthafrica.com", "GISADefault1234@");
            MimeMessage message = new MimeMessage(session);
            MimeMultipart multipart = new MimeMultipart("related");
            /*BodyPart text = new MimeBodyPart();
            text.setContent(textMessage, "text");
            multipart.addBodyPart(text);*/

            BodyPart quoteTable = new MimeBodyPart();
            Workbook wb;
            if(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1).matches("xls")) {
                wb = Workbook.getWorkbook(file);
                Sheet sheet = wb.getSheet(0);
                Cell cell;
                for (int i = 0; i < 4; i++) {
                    cell = sheet.getCell(24, 56 + i);
                    /*if (Integer.parseInt(cell.getContents()) != 0) {*/
                        costingTable = costingTable + "<tr><td>" + cell.getContents() + "</td>";
                        cell = sheet.getCell(25, 56 + i);
                        costingTable = costingTable + "<td>" + cell.getContents() + "</td>";
                        cell = sheet.getCell(28, 56 + i);
                        costingTable = costingTable + "<td>R " + cell.getContents() + "</td>";
                        cell = sheet.getCell(29, 56 + i);
                        costingTable = costingTable + "<td>R " + cell.getContents() + "</td></tr>";
                    //}
                }
                cell = sheet.getCell(29, 60);
                costingTable = costingTable + "<tr><td></td><td></td><td>Package Total</td><td>R " + cell.getContents() + "</td></tr></table></body></html>";
                cell = sheet.getCell(12, 55);
                message.setSubject("Golf in South Africa " + cell.getContents() + " Quote for " + clientName);
                message.setFrom(new InternetAddress("info@golfinsouthafrica.com", "Golf in South Africa"));
                wb.close();
                quoteTable.setContent(textMessage + "<br><br>" + costingTable + "<br><br>Kind Regards<br>" + name, "text/html");
                multipart.addBodyPart(quoteTable);
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, clientName));

                //message.addRecipient(Message.RecipientType.BCC, new InternetAddress("my.address@mycomp.com"));
                    /*messageBodyPart = new MimeBodyPart();
                    DataSource fds = new FileDataSource("C:\\temp\\test\\Example_1.gif");
                    messageBodyPart.setDataHandler(new DataHandler(fds));
                    messageBodyPart.setHeader("Content-ID","<image>");
                    multipart.addBodyPart(messageBodyPart);*/
                    /*messageBodyPart = new MimeBodyPart();
                    DataSource fds2 = new FileDataSource("C:\\temp\\test\\Example_2.gif");
                    messageBodyPart.setDataHandler(new DataHandler(fds2));
                    messageBodyPart.setHeader("Content-ID","<image2>");
                    multipart.addBodyPart(messageBodyPart);
                    DataSource fds3 = new FileDataSource("C:\\temp\\test\\Example_3.gif");
                    messageBodyPart.setDataHandler(new DataHandler(fds3));
                    messageBodyPart.setHeader("Content-ID","<bg>");
                    multipart.addBodyPart(messageBodyPart);*/

                message.setContent(multipart);
                transport.sendMessage(message, message.getAllRecipients());
                System.out.println("Successfully Send Mail");
                return true;

                    /*System.out.println("html text: \n" + htmltext);
                    messageBodyPart.setContent(htmltext, "text/html");

                    multipart.addBodyPart(messageBodyPart);
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress("ronniemllr1@gmail.com"));
                    //message.addRecipient(Message.RecipientType.BCC, new InternetAddress("my.address@mycomp.com"));

                    /*messageBodyPart = new MimeBodyPart();
                    DataSource fds = new FileDataSource("C:\\temp\\test\\Example_1.gif");
                    messageBodyPart.setDataHandler(new DataHandler(fds));
                    messageBodyPart.setHeader("Content-ID","<image>");
                    multipart.addBodyPart(messageBodyPart);*/

                    /*messageBodyPart = new MimeBodyPart();
                    DataSource fds2 = new FileDataSource("C:\\temp\\test\\Example_2.gif");
                    messageBodyPart.setDataHandler(new DataHandler(fds2));
                    messageBodyPart.setHeader("Content-ID","<image2>");
                    multipart.addBodyPart(messageBodyPart);
                    DataSource fds3 = new FileDataSource("C:\\temp\\test\\Example_3.gif");
                    messageBodyPart.setDataHandler(new DataHandler(fds3));
                    messageBodyPart.setHeader("Content-ID","<bg>");
                    multipart.addBodyPart(messageBodyPart);

                    message.setContent(multipart);
                    System.out.println("message " + message.toString());
                    transport.sendMessage(message, message.getAllRecipients());;
                    System.out.println("message sent");*/

            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        return false;
    }
}


