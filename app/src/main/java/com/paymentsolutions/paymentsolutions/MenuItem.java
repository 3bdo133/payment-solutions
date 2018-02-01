package com.paymentsolutions.paymentsolutions;

/**
 * Created by Abdelrahman Hesham on 1/22/2018.
 */

public class MenuItem {

    private String id;
    private String title;
    private int image;

    public MenuItem(String id, String title, int image) {
        this.id = id;
        this.title = title;
        this.image = image;
    }

    public MenuItem(String title, int image) {
        this.title = title;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getImage() {
        return image;
    }
}
