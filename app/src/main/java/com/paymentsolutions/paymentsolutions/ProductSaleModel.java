package com.paymentsolutions.paymentsolutions;

public class ProductSaleModel {

    String productName;
    String productQuantity;
    String productTotal;


    public ProductSaleModel(String productName, String productQuantity, String productTotal) {
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productTotal = productTotal;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductTotal() {
        return productTotal;
    }

    public void setProductTotal(String productTotal) {
        this.productTotal = productTotal;
    }
}
