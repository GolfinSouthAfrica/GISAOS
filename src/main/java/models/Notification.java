package models;

import java.io.Serializable;

public class Notification implements Serializable {

    String messageHeader;
    String messageBody;

    public Notification(String messageHeader, String messageBody) {
        this.messageHeader = messageHeader;
        this.messageBody = messageBody;
    }

    public String getMessageHeader() {
        return messageHeader;
    }

    public String getMessageBody() {
        return messageBody;
    }

    @Override
    public String toString(){
        return messageHeader;
    }
}
