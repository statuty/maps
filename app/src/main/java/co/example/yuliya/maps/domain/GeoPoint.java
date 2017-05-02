package co.example.yuliya.maps.domain;

import java.io.Serializable;

public class GeoPoint implements Serializable {
    private double lat;
    private double lon;

    private GeoPoint() {
    }

    public GeoPoint(double latitude, double longitude) {
        this.lat = latitude;
        this.lon = longitude;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

}