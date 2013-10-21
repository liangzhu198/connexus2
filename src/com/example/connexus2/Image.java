package com.example.connexus2;

public class Image {
    double latitude;
    double longitude;
    byte[] physicalImage;

    //Since the server does not need latitude and longitude, here I didn't get the data
    public Image(byte[] c) {
            latitude = 0d;
            longitude = 0d;
            physicalImage = c;
    }

    public Image() {
    }
}
