package models;

import java.io.File;
import java.io.Serializable;
import java.util.List;


public class Mail implements Serializable {

    private String toMailAddress;
    private String fromMailAddress;
    private String subject;
    private String message;
    private List<File> attachments;

    public Mail(String toMailAddress, String fromMailAddress, String subject, String message, List<File> attachments) {
        this.toMailAddress = toMailAddress;
        this.fromMailAddress = fromMailAddress;
        this.subject = subject;
        this.message = message;
        this.attachments = attachments;
    }

    public String getToMailAddress() {
        return toMailAddress;
    }

    public String getFromMailAddress() {
        return fromMailAddress;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public List<File> getAttachments() {
        return attachments;
    }
}
