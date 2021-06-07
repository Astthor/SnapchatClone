package com.example.snapchat.model;

public class Snap {
    private String from;
    private String imageURL;
    private String imageName;
    private String message;

    public Snap(String from, String imageURL, String imageName, String message) {
        this.from = from;
        this.imageURL = imageURL;
        this.imageName = imageName;
        this.message = message;
    }

    public Snap() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
