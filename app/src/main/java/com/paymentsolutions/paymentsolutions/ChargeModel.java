package com.paymentsolutions.paymentsolutions;

public class ChargeModel {

    private String amount;
    private String date;
    private String status;

    public ChargeModel(String amount, String date, String status) {
        this.amount = amount;
        this.date = date;
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}
