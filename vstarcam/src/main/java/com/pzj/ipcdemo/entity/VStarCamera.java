package com.pzj.ipcdemo.entity;

import java.io.Serializable;

public class VStarCamera implements Serializable {
    private String name;
    private String id;
    private String username;
    private String password;
    private byte[] bImage;

    public VStarCamera(String name, String id, String username, String password) {
        this.name = name;
        this.id = id;
        this.username = username;
        this.password = password;
        this.bImage=null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getbImage() {
        return bImage;
    }

    public void setbImage(byte[] bImage) {
        this.bImage = bImage;
    }

    @Override
    public String toString() {
        return "VStarCamera{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
