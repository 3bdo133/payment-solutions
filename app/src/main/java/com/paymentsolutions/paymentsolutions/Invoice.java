package com.paymentsolutions.paymentsolutions;

public class Invoice {
    private String description;
    private String quantity;
    private String price;
    private int image;

    public Invoice(String description, String quantity, String price, int image) {
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.image = image;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
