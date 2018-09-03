package main;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
/*import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;*/

public class Email {

    public static void emailPassword(String username, String email, String password) {
        email(email, "Golf in South Africa - Forgot password", "Dear " + username + "\n\nYou requested to send your Golf in South Africa password to your email.\nIf this wasn't you please contact us.\n\nPassword:\t" + password, null, null);
    }

    public static void email(String email, String emailSubject, String emailMessage, String documentType, String attachment) {
        /*try {
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
        }*/
    }

    public static void sendCosting(String GSNumber, String email, String clientName, String textMessage, String name, Double[] x){
        //TODO add Booking Name

        /*String costingTable = "<html><body><table style=\"border:3px solid black;border-collapse:collapse;\" width = 40%><tr><th colspan=\"4\" align=\"center\" style=\"border:2px solid black;\"><font size=\"4\"><b>Short Quote</b></font></th></tr><tr><th align=\"center\" style=\"border:2px solid black;\"><font size=\"3\"><b>Pax</b></font></th><th align=\"center\" style=\"border:2px solid black;\"><font size=\"3\"><b>Package Name</b></font></th><th align=\"center\" style=\"border:2px solid black;\"><font size=\"3\"><b>Price Per Person</b></font></th><th align=\"center\" style=\"border:2px solid black;\"><font size=\"3\"><b>Package Total</b></font></th></tr>";
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
            MimeMessage text = new MimeMessage(session);

            MimeMessage message = new MimeMessage(session);
            MimeMultipart multipart = new MimeMultipart("related");

            BodyPart quoteTable = new MimeBodyPart();
            Workbook wb;
            if(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1).matches("xls")) {
                wb = Workbook.getWorkbook(file);
                Sheet sheet = wb.getSheet(0);
                Cell cell;

                Double packageTotal = 0.00;

                cell = sheet.getCell(12, 56);
                if(!cell.getContents().matches("0")) {
                    int pax = Integer.parseInt(cell.getContents());
                    costingTable = costingTable + "<tr><th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>" + cell.getContents() +"</b></font></th>";
                    cell = sheet.getCell(12, 55);
                    costingTable = costingTable + "<th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>" + cell.getContents() +" - Golfer Sharing</b></font></th>";
                    //Calculate per GolferSharing
                    Double perGolferSharing = x[0];
                    costingTable = costingTable + "<th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>R " + perGolferSharing +"</b></font></th>";
                    Double perTotal = pax * perGolferSharing;
                    costingTable = costingTable + "<th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>R " + perTotal +"</b></font></th></tr>";
                    packageTotal += perTotal;
                }
                cell = sheet.getCell(12, 57);
                if(!cell.getContents().matches("0")) {
                    int pax = Integer.parseInt(cell.getContents());
                    costingTable = costingTable + "<tr><th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>" + cell.getContents() +"</b></font></th>";
                    cell = sheet.getCell(12, 55);
                    costingTable = costingTable + "<th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>" + cell.getContents() +" - Non-Golfer Sharing</b></font></th>";
                    //Calculate per GolferSharing
                    Double perGolferSharing = x[1];
                    costingTable = costingTable + "<th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>R " + perGolferSharing +"</b></font></th>";
                    Double perTotal = pax * perGolferSharing;
                    costingTable = costingTable + "<th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>R " + perTotal +"</b></font></th></tr>";
                    packageTotal += perTotal;
                }
                cell = sheet.getCell(12, 58);
                if(!cell.getContents().matches("0")) {
                    int pax = Integer.parseInt(cell.getContents());
                    costingTable = costingTable + "<tr><th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>" + cell.getContents() +"</b></font></th>";
                    cell = sheet.getCell(12, 55);
                    costingTable = costingTable + "<th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>" + cell.getContents() +" - Golfer Single</b></font></th>";
                    //Calculate per GolferSharing
                    Double perGolferSharing = x[2];
                    costingTable = costingTable + "<th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>R " + perGolferSharing +"</b></font></th>";
                    Double perTotal = pax * perGolferSharing;
                    costingTable = costingTable + "<th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>R " + perTotal +"</b></font></th></tr>";
                    packageTotal += perTotal;
                }
                cell = sheet.getCell(12, 59);
                if(!cell.getContents().matches("0")) {
                    int pax = Integer.parseInt(cell.getContents());
                    costingTable = costingTable + "<tr><th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>" + cell.getContents() +"</b></font></th>";
                    cell = sheet.getCell(12, 55);
                    costingTable = costingTable + "<th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>" + cell.getContents() +" - Non-Golfer Single</b></font></th>";
                    //Calculate per GolferSharing
                    Double perGolferSharing = x[3];
                    costingTable = costingTable + "<th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>R " + perGolferSharing +"</b></font></th>";
                    Double perTotal = pax * perGolferSharing;
                    costingTable = costingTable + "<th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>R " + perTotal +"</b></font></th></tr>";
                    packageTotal += perTotal;
                }
                costingTable = costingTable + "<tr><th colspan=\"3\" align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>Package Total</b></font></th><th align=\"center\" style=\"border:2px solid black;\"><font size=\"2\"><b>R " + packageTotal + "</b></font></th></tr></table></body></html>";
                cell = sheet.getCell(12, 55);
                message.setSubject("Golf in South Africa - " + cell.getContents() + " Quote for " + clientName);
                message.setFrom(new InternetAddress("info@golfinsouthafrica.com", "Golf in South Africa"));
                wb.close();
                textMessage = textMessage.replace(System.lineSeparator(), "<br>");
                String footerText = "<html><b>Yours in Golf & Travel, </b><br><br><img src=\"https://www.golfinsouthafrica.com/wp-content/uploads/2017/10/2015/09/golf-in-south-africa-logo_top_mobi.jpg\" alt=\"GISALogo\"></img><br><br><b>Office:</b> +27 (0)21 553 4132<br><br><b>Mobile:</b> +27 (0)79 501 9152 / +27 (0)71 083 4355 / +27 (0)82 521 6046<br><br><b>Email:</b> info@golfinsouthafrica.com<br><br><b>CK 2007/018764/07</b><br<br> </html>";
                /*Map<String, String> inlineImages = new HashMap<String, String>();
                inlineImages.put("GISALogo", "G:/My Drive/z. OldDrive/i. Office/LOGO/golf-in-south-africa-logo.jpg");
                if (inlineImages != null && inlineImages.size() > 0) {
                    Set<String> setImageID = inlineImages.keySet();
                    for (String contentId : setImageID) {
                        MimeBodyPart imagePart = new MimeBodyPart();
                        imagePart.setHeader("Content-ID", "<" + contentId + ">");
                        imagePart.setDisposition(MimeBodyPart.INLINE);
                        String imageFilePath = inlineImages.get(contentId);
                        try {
                            imagePart.attachFile(imageFilePath);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        multipart.addBodyPart(imagePart);
                    }
                }*/
                /*quoteTable.setContent(textMessage + "<br>" + costingTable + "<br>Kind Regards<br>" + name + "<br><br>" + footerText, "text/html");
                multipart.addBodyPart(quoteTable);

                message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, clientName));
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

            /*}
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        return false;
    }*/
}
}


