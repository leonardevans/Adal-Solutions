package com.adalsolutions.models;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;

    @NotEmpty
    private String mobile;
    @NotEmpty
    private String country = "Kenya";
    @NotEmpty
    private String city;

    @NotEmpty
    private String street;
    private String house;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private User user;

    public Address() {
    }

    public Address(@NotEmpty String firstName, @NotEmpty String lastName,@NotEmpty String mobile, @NotEmpty String country, @NotEmpty String city, @NotEmpty String street) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.country = country;
        this.city = city;
        this.street = street;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }
}
