package com.itismob.s03.wonder.pets.animospaces.model;

public class NotificationItem {
    public enum Type {
        APPROVAL,
        REJECTION,
        REMINDER,
        SYSTEM
    }

    private final String id;
    private final String title;
    private final String message;
    private final String timestampFormatted;
    private final Type type;
    private boolean isRead;
    private final String reservationId;

    public NotificationItem(String id, String title, String message, String timestampFormatted, Type type, boolean isRead, String reservationId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestampFormatted = timestampFormatted;
        this.type = type;
        this.isRead = isRead;
        this.reservationId = reservationId;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestampFormatted() {
        return timestampFormatted;
    }

    public Type getType() {
        return type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getReservationId() {
        return reservationId;
    }
}