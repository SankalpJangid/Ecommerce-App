package com.example.giftswala;

import com.google.firebase.Timestamp;

public class All_Collections {

    private Timestamp collection_timestamp;

    public Timestamp getCollection_timestamp() {
        return collection_timestamp;
    }

    public void setCollection_timestamp(Timestamp collection_timestamp) {
        this.collection_timestamp = collection_timestamp;
    }

    private String collection_name;

    private String collection_image;

    public String getCollection_image() {
        return collection_image;
    }

    public void setCollection_image(String collection_image) {
        this.collection_image = collection_image;
    }

    public String getCollection_name() {
        return collection_name;
    }

    public void setCollection_name(String collection_name) {
        this.collection_name = collection_name;
    }


    public All_Collections(String collection_name, Timestamp collection_timestamp, String collection_image) {
        this.collection_name = collection_name;
        this.collection_timestamp = collection_timestamp;
        this.collection_image = collection_image;
    }

    public All_Collections() {
    }
}
