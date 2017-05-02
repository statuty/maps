package co.example.yuliya.maps.domain;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

public class Location implements Serializable {
    private String id;
    private String name;
    private String description;
    private GeoPoint coordinates;
    private Category category;
    private List<WorkingDay> workingDays;

    public Location(String id, String name, String description, GeoPoint coordinates, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.coordinates = coordinates;
        this.category = category;
    }

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

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(final GeoPoint coordinates) {
        this.coordinates = coordinates;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(final Category category) {
        this.category = category;
    }

    public LatLng getLatLng() {
        return new LatLng(coordinates.getLat(), coordinates.getLon());
    }

    public List<WorkingDay> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(List<WorkingDay> workingDays) {
        this.workingDays = workingDays;
    }
}