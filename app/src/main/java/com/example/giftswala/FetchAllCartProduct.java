package com.example.giftswala;

public class FetchAllCartProduct {

    private String product_name;
    private String product_price;
    private String product_image;
    private String product_rating;

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

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_rating() {
        return product_rating;
    }

    public void setProduct_rating(String product_rating) {
        this.product_rating = product_rating;
    }

    public String getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(String product_quantity) {
        this.product_quantity = product_quantity;
    }

    public FetchAllCartProduct(String product_name, String product_price, String product_image, String product_rating, String product_quantity) {
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_image = product_image;
        this.product_rating = product_rating;
        this.product_quantity = product_quantity;
    }

    private String product_quantity;


    public FetchAllCartProduct() {
    }
}
