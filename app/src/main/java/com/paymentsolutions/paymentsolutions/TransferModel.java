package com.paymentsolutions.paymentsolutions;

public class TransferModel {

    private String name;
    private String date;
    private String status;
    private String amount;

    public TransferModel(String name, String date, String status, String amount) {
        this.name = name;
        this.date = date;
        this.status = status;
        this.amount = amount;
    }


    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getAmount() {
        return amount;
    }
}
