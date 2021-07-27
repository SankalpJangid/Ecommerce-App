package com.example.giftswala;

import com.google.firebase.Timestamp;

public class trending_product {

    public trending_product() {
    }

    private String product_name;
    private String product_image;
    private String product_price;
    private String product_collection;

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

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

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

    public trending_product(String product_name, String product_image, String product_price, String product_collection, Timestamp product_timestamp) {
        this.product_name = product_name;
        this.product_image = product_image;
        this.product_price = product_price;
        this.product_collection = product_collection;
        this.product_timestamp = product_timestamp;
    }

    private Timestamp product_timestamp;
}
