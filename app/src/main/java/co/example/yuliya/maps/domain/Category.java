package co.example.yuliya.maps.domain;

import java.io.Serializable;

/**
 * Created by Yuliya on 4/29/2017.
 */

public class Category implements Serializable {
    private String id;
    private String name;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
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
}
