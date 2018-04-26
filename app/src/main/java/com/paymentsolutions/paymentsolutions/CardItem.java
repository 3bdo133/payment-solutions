package com.paymentsolutions.paymentsolutions;

public class CardItem {


    private String productName;
    private String productPrice;
    private String productQuantity;
    private int productImage;


    public CardItem(String productName, String productPrice, String productQuantity, int productImage) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public int getProductImage() {
        return productImage;
    }
}
