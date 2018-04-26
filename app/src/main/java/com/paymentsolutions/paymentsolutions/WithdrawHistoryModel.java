package com.paymentsolutions.paymentsolutions;

public class WithdrawHistoryModel {

    private String amount;
    private String date;
    private String status;
    private String fawry;

    public WithdrawHistoryModel(String amount, String date, String status, String fawry) {
        this.amount = amount;
        this.date = date;
        this.status = status;
        this.fawry = fawry;
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

    public String getFawry() {
        return fawry;
    }
}
