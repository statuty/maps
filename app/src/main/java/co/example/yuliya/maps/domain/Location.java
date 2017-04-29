package co.example.yuliya.maps.domain;

public class Location {
    private String id;
    private String name;
    private String description;
    private GeoPoint coordinates;

    public Location(String id, String name, String description, GeoPoint coordinates) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.coordinates = coordinates;
    }

    public Location() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }
}