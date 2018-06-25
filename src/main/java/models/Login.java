package models;

import java.io.Serializable;

public class Login implements Serializable {

    private int loginID;
    private String loginName;
    private String username;
    private String password;

    public Login(int loginID, String loginName, String username, String password) {
        this.loginID = loginID;
        this.loginName = loginName;
        this.username = username;
        this.password = password;
    }

    public int getLoginID() {
        return loginID;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
