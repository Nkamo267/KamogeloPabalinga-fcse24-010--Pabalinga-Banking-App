package com.pabalinga.bankingsystem.model;

public class Customer {
    private int id;
    private String firstname;
    private String surname;
    private String address;
    private String phone;

    public Customer(int id, String firstname, String surname, String address, String phone) {
        this.id = id; this.firstname = firstname; this.surname = surname; this.address = address; this.phone = phone;
    }
    public Customer(String firstname, String surname, String address, String phone) {
        this.firstname = firstname; this.surname = surname; this.address = address; this.phone = phone;
    }

    public int getId() { return id; }
    public String getFirstname() { return firstname; }
    public String getSurname() { return surname; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }

    public String toString() { return id + " - " + firstname + " " + surname; }
}
