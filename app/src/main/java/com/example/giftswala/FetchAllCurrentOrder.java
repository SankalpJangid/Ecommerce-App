package com.example.giftswala;

public class FetchAllCurrentOrder {

    private String user_mail;

    private String user_address;

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getUser_location() {
        return user_location;
    }

    public void setUser_location(String user_location) {
        this.user_location = user_location;
    }

    private String user_location;

    public String getUser_mail() {
        return user_mail;
    }

    public void setUser_mail(String user_mail) {
        this.user_mail = user_mail;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(String product_quantity) {
        this.product_quantity = product_quantity;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getOrder_ID() {
        return order_ID;
    }

    public void setOrder_ID(String order_ID) {
        this.order_ID = order_ID;
    }

    private String product_name;

    public FetchAllCurrentOrder(String user_mail, String user_address, String user_location, String product_name, String product_price, String product_quantity, String product_image, String order_ID) {
        this.user_mail = user_mail;
        this.user_address = user_address;
        this.user_location = user_location;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_quantity = product_quantity;
        this.product_image = product_image;
        this.order_ID = order_ID;
    }

    private String product_price;
    private String product_quantity;
    private String product_image;
    private String order_ID;

    public FetchAllCurrentOrder() {
    }
}
