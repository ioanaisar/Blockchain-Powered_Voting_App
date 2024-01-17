package ro.pub.cs.systems.eim.votingapp;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String address;
    private String password;

    private String privateKey;

    private String fileName;

    public User(String username, String address, String password, String privateKey, String fileName) {
        this.username = username;
        this.address = address;
        this.password = password;
        this.privateKey = privateKey;
        this.fileName = fileName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
