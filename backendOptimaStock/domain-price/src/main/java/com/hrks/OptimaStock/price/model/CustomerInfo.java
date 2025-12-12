package com.hrks.OptimaStock.price.model;

public class CustomerInfo {

    private String name;
    private String email;
    private String phone;
    private String company;
    private String address;

    public CustomerInfo() {
    }

    public CustomerInfo(String name, String email, String phone, String company, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.address = address;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
