package com.itismob.s03.wonder.pets.animospaces.model;

public enum UserRole {
    REGULAR_STUDENT("Regular Student", false, false),
    STUDENT_ORGANIZATION("Student Organization", true, false),
    FACULTY("Faculty", true, false),
    ADMIN("Administrator", true, true);

    private final String displayName;
    private final boolean canReserve;
    private final boolean isAdmin;

    UserRole(String displayName, boolean canReserve, boolean isAdmin) {
        this.displayName = displayName;
        this.canReserve = canReserve;
        this.isAdmin = isAdmin;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canReserve() {
        return canReserve;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}