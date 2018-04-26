package com.paymentsolutions.paymentsolutions;

/**
 * Created by Abdelrahman Hesham on 3/8/2018.
 */

public class Mobile {

    private String id;
    private String name;
    private String price;
    private String image;

    public Mobile(String id, String name, String price, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }
}
