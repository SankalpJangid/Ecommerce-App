package com.example.giftswala;

import com.google.firebase.Timestamp;

public class FetchAllProductCollection {

    private Timestamp product_timestamp;

    private String product_collection;

    public String getProduct_collection() {
        return product_collection;
    }

    public void setProduct_collection(String product_collection) {
        this.product_collection = product_collection;
    }

    public Timestamp getProduct_timestamp() {
        return product_timestamp;
    }

    public void setProduct_timestamp(Timestamp product_timestamp) {
        this.product_timestamp = product_timestamp;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    private String product_price;

    public String getProduct_rating() {
        return product_rating;
    }

    public void setProduct_rating(String product_rating) {
        this.product_rating = product_rating;
    }

    private String product_rating;


    public FetchAllProductCollection(Timestamp product_timestamp, String product_collection, String product_name, String product_image, String product_rating, String product_price) {
        this.product_timestamp = product_timestamp;
        this.product_collection = product_collection;
        this.product_name = product_name;
        this.product_image = product_image;
        this.product_rating = product_rating;
        this.product_price = product_price;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    private String product_name;
    private String product_image;

    public FetchAllProductCollection() {
    }
}
