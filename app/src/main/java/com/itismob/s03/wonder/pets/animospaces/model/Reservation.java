package com.itismob.s03.wonder.pets.animospaces.model;

public class Reservation {
    private final String id;
    private final String roomId;
    private final String roomCode;
    private final String buildingName;
    private final String requestedBy; // e.g. "Angelyn Chua (CSORG)"
    private final UserRole requesterRole;
    private final String eventTitle;
    private final String eventPurpose;
    private final String dateString;
    private final String timeSlot;
    private final int attendeeCount;
    private ReservationStatus status;
    private String rejectionReason;
    private final long timestamp;

    public Reservation(String id, String roomId, String roomCode, String buildingName,
                       String requestedBy, UserRole requesterRole, String eventTitle,
                       String eventPurpose, String dateString, String timeSlot,
                       int attendeeCount, ReservationStatus status, long timestamp) {
        this.id = id;
        this.roomId = roomId;
        this.roomCode = roomCode;
        this.buildingName = buildingName;
        this.requestedBy = requestedBy;
        this.requesterRole = requesterRole;
        this.eventTitle = eventTitle;
        this.eventPurpose = eventPurpose;
        this.dateString = dateString;
        this.timeSlot = timeSlot;
        this.attendeeCount = attendeeCount;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public UserRole getRequesterRole() {
        return requesterRole;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventPurpose() {
        return eventPurpose;
    }

    public String getDateString() {
        return dateString;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public int getAttendeeCount() {
        return attendeeCount;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public long getTimestamp() {
        return timestamp;
    }
}