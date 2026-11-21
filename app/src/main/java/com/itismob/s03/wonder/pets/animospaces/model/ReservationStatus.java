package com.itismob.s03.wonder.pets.animospaces.model;

public enum ReservationStatus {
    PENDING("Pending Approval", "#D97706", "#FEF3C7"),
    APPROVED("Approved", "#15803D", "#DCFCE7"),
    REJECTED("Rejected", "#B91C1C", "#FEE2E2"),
    CANCELLED("Cancelled", "#6B7280", "#F3F4F6");

    private final String label;
    private final String textColorHex;
    private final String bgColorHex;

    ReservationStatus(String label, String textColorHex, String bgColorHex) {
        this.label = label;
        this.textColorHex = textColorHex;
        this.bgColorHex = bgColorHex;
    }

    public String getLabel() {
        return label;
    }

    public String getTextColorHex() {
        return textColorHex;
    }

    public String getBgColorHex() {
        return bgColorHex;
    }
}