package com.itismob.s03.wonder.pets.animospaces.model;

import com.itismob.s03.wonder.pets.animospaces.R;

import java.util.List;

public class Building {
    private String id;
    private String name;
    private String code;
    private Campus campus;
    private double latitude;
    double longitude;
    private List<Integer> availableFloors;
    private String description;
    private String category; // e.g., "Academic", "Administrative", "Laboratory", "Sports"
    private int imageResId;
    private String imageUrl;

    public Building(String id, String name, String code, Campus campus, double latitude, double longitude,
                    List<Integer> availableFloors, String description, String category) {
        this(id, name, code, campus, latitude, longitude, availableFloors, description, category, R.drawable.img_building_placeholder);
    }

    public Building(String id, String name, String code, Campus campus, double latitude, double longitude,
                    List<Integer> availableFloors, String description, String category, int imageResId) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.campus = campus;
        this.latitude = latitude;
        this.longitude = longitude;
        this.availableFloors = availableFloors;
        this.description = description;
        this.category = category;
        this.imageResId = imageResId > 0 ? imageResId : R.drawable.img_building_placeholder;
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

    public int getImageResId() {
        return imageResId > 0 ? imageResId : R.drawable.img_building_placeholder;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}