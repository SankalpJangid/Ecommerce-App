package com.example.giftswala;

public class AddressInformation {

    public AddressInformation() {
    }

    private String address_id;

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    private String user_area;

    public String getUser_location() {
        return user_location;
    }

    public void setUser_location(String user_location) {
        this.user_location = user_location;
    }

    private String user_location;

    public String getUser_area() {
        return user_area;
    }

    public void setUser_area(String user_area) {
        this.user_area = user_area;
    }

    public String getUser_home() {
        return user_home;
    }

    public void setUser_home(String user_home) {
        this.user_home = user_home;
    }

    public String getUser_street() {
        return user_street;
    }

    public void setUser_street(String user_street) {
        this.user_street = user_street;
    }

    public AddressInformation(String address_id, String user_area, String user_location, String user_home, String user_street) {
        this.address_id = address_id;
        this.user_area = user_area;
        this.user_location = user_location;
        this.user_home = user_home;
        this.user_street = user_street;
    }

    private String user_home;
    private String user_street;

}
