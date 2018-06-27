package models;

import java.io.File;
import java.io.Serializable;
import java.util.List;


public class Mail implements Serializable {

    private List<String> recipients;
    private List<String> fromMailAddress;
    private String subject;
    private String date;
    private String message;
    private List<File> attachments;
    private boolean read;

    public Mail(List<String> recipients, List<String> fromMailAddress, String subject, String date, String message, List<File> attachments, boolean read) {
        this.recipients = recipients;
        this.fromMailAddress = fromMailAddress;
        this.subject = subject;
        this.date = date;
        this.message = message;
        this.attachments = attachments;
        this.read = read;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public List<String> getFromMailAddress() {
        return fromMailAddress;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public List<File> getAttachments() {
        return attachments;
    }

    public boolean isRead() {
        return read;
    }
}
