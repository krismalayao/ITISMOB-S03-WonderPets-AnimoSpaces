package com.itismob.s03.wonder.pets.animospaces.model;

import java.util.List;

public class Building {
    private String id;
    private String name;
    private String code;
    private Campus campus;
    private double latitude;
    private double longitude;
    private List<Integer> availableFloors;
    private String description;
    private String category; // e.g., "Academic", "Administrative", "Laboratory", "Sports"

    public Building(String id, String name, String code, Campus campus, double latitude, double longitude,
                    List<Integer> availableFloors, String description, String category) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.campus = campus;
        this.latitude = latitude;
        this.longitude = longitude;
        this.availableFloors = availableFloors;
        this.description = description;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public Campus getCampus() {
        return campus;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public List<Integer> getAvailableFloors() {
        return availableFloors;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
}