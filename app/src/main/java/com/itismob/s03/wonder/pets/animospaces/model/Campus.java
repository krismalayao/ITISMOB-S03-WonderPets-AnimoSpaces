package com.itismob.s03.wonder.pets.animospaces.model;

public enum Campus {
    DLSU_MANILA("DLSU Manila (DLSU-M)", 14.5648, 120.9933, 17.5);

    private final String displayName;
    private final double latitude;
    private final double longitude;
    private final double defaultZoom;

    Campus(String displayName, double latitude, double longitude, double defaultZoom) {
        this.displayName = displayName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.defaultZoom = defaultZoom;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDefaultZoom() {
        return defaultZoom;
    }
}