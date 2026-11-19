package com.itismob.s03.wonder.pets.animospaces.model;

import java.util.List;

public class Room {
    private final String id;
    private final String buildingId;
    private final String buildingName;
    private final String roomCode; // e.g. "A0801", "G302", "Y501"
    private final int floorLevel;
    private final int capacity;
    private final String roomType; // Classroom, Lab, Open Space, Event Hall
    private boolean isAvailable;
    private final String scheduleSummary;
    private final List<String> amenities;
    private String imageUrl;

    public Room(String id, String buildingId, String buildingName, String roomCode, int floorLevel,
                int capacity, String roomType, boolean isAvailable, String scheduleSummary, List<String> amenities) {
        this.id = id;
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.roomCode = roomCode;
        this.floorLevel = floorLevel;
        this.capacity = capacity;
        this.roomType = roomType;
        this.isAvailable = isAvailable;
        this.scheduleSummary = scheduleSummary;
        this.amenities = amenities;
    }

    public String getId() {
        return id;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public int getFloorLevel() {
        return floorLevel;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getRoomType() {
        return roomType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getScheduleSummary() {
        return scheduleSummary;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}