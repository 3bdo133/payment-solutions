package com.paymentsolutions.paymentsolutions;

public class InvoiceHistoryModel {


    private String billTo;
    private String invoiceDate;
    private String balance;
    private String status;
    private String id;

    public InvoiceHistoryModel(String billTo, String invoiceDate, String balance, String status,String id) {
        this.billTo = billTo;
        this.invoiceDate = invoiceDate;
        this.balance = balance;
        this.status = status;
        this.id = id;
    }

    public String getBillTo() {
        return billTo;
    }

    public void setBillTo(String billTo) {
        this.billTo = billTo;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
