package com.paymentsolutions.paymentsolutions;

public class CustomerModel {

    private String name;
    private String phone;
    private String email;

    public CustomerModel(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }


    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
