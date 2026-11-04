package com.itismob.s03.wonder.pets.animospaces.data;

import android.content.Context;

import com.itismob.s03.wonder.pets.animospaces.model.NotificationItem;
import com.itismob.s03.wonder.pets.animospaces.model.Reservation;
import com.itismob.s03.wonder.pets.animospaces.model.ReservationStatus;
import com.itismob.s03.wonder.pets.animospaces.model.Room;
import com.itismob.s03.wonder.pets.animospaces.model.UserRole;
import com.itismob.s03.wonder.pets.animospaces.service.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {

    private static ReservationRepository instance;
    private final List<Reservation> reservations = new ArrayList<>();
    private final List<NotificationItem> notifications = new ArrayList<>();
    private NotificationHelper notificationHelper;

    private ReservationRepository() {
        initSampleData();
    }

    public static synchronized ReservationRepository getInstance() {
        if (instance == null) {
            instance = new ReservationRepository();
        }
        return instance;
    }

    public void initNotificationHelper(Context context) {
        if (notificationHelper == null) {
            notificationHelper = new NotificationHelper(context);
        }
    }

    private void initSampleData() {
        // Pending reservation requests
        Reservation res1 = new Reservation(
                "res_101", "r_a802", "A0802", "Andrew Gonzalez Hall",
                "Angelyn Chua (CSORG)", UserRole.STUDENT_ORGANIZATION,
                "CSORG Mobile App Dev General Assembly",
                "Annual orientation and project presentation for computer science students.",
                "Oct 15, 2026", "1:00 PM - 3:00 PM", 45,
                ReservationStatus.PENDING, System.currentTimeMillis() - 3600000
        );

        Reservation res2 = new Reservation(
                "res_102", "r_g301", "G301", "Gokongwei Hall",
                "Prof. Kristopher Malayao", UserRole.FACULTY,
                "Software Engineering Lab Exam",
                "Final hands-on lab examination for ITISMOB students.",
                "Oct 16, 2026", "9:00 AM - 12:00 PM", 35,
                ReservationStatus.PENDING, System.currentTimeMillis() - 7200000
        );

        // Pre-approved reservation
        Reservation res3 = new Reservation(
                "res_103", "r_y501", "Y501", "Yuchengco Hall",
                "Prof. Kristopher Malayao", UserRole.FACULTY,
                "DLSU Innovation Summit 2026",
                "University-wide keynote conference on emerging technology.",
                "Oct 20, 2026", "8:00 AM - 5:00 PM", 500,
                ReservationStatus.APPROVED, System.currentTimeMillis() - 86400000
        );

        reservations.add(res1);
        reservations.add(res2);
        reservations.add(res3);

        // Initial sample notifications
        notifications.add(new NotificationItem(
                "notif_1",
                "Reservation Submitted",
                "Your request for A0802 (Andrew Gonzalez Hall) has been submitted for admin approval.",
                "1 hour ago",
                NotificationItem.Type.SYSTEM,
                false,
                "res_101"
        ));

        notifications.add(new NotificationItem(
                "notif_2",
                "Booking Confirmed!",
                "Reservation for Y501 (Yuchengco Hall - Auditorium) was approved by Admin.",
                "Yesterday",
                NotificationItem.Type.APPROVAL,
                true,
                "res_103"
        ));
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public List<Reservation> getReservationsForUser(String username) {
        List<Reservation> userRes = new ArrayList<>();
        if (username == null || username.isEmpty()) return userRes;

        String query = username.toLowerCase().trim();
        for (Reservation r : reservations) {
            if (r.getRequestedBy() != null && r.getRequestedBy().toLowerCase().contains(query)) {
                userRes.add(r);
            }
        }
        return userRes;
    }

    public List<Reservation> getPendingReservations() {
        List<Reservation> pending = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.PENDING) {
                pending.add(r);
            }
        }
        return pending;
    }

    public List<NotificationItem> getNotifications() {
        return new ArrayList<>(notifications);
    }

    public int getUnreadNotificationCount() {
        int count = 0;
        for (NotificationItem n : notifications) {
            if (!n.isRead()) {
                count++;
            }
        }
        return count;
    }

    public void markAllNotificationsAsRead() {
        for (NotificationItem n : notifications) {
            n.setRead(true);
        }
    }

    public Reservation submitReservation(Room room, String requestedBy, UserRole role, String eventTitle, String eventPurpose, String dateString, String timeSlot, int attendees) {
        String resId = "res_" + (System.currentTimeMillis() % 10000);
        Reservation newRes = new Reservation(
                resId, room.getId(), room.getRoomCode(), room.getBuildingName(),
                requestedBy, role, eventTitle, eventPurpose, dateString, timeSlot,
                attendees, ReservationStatus.PENDING, System.currentTimeMillis()
        );

        reservations.add(0, newRes);

        NotificationItem notif = new NotificationItem(
                "notif_" + System.currentTimeMillis(),
                "Reservation Request Received",
                "Request for " + room.getRoomCode() + " (" + eventTitle + ") submitted for admin approval.",
                "Just now",
                NotificationItem.Type.SYSTEM,
                false,
                resId
        );
        notifications.add(0, notif);

        if (notificationHelper != null) {
            notificationHelper.sendNotification(
                    "Reservation Submitted",
                    "Request for " + room.getRoomCode() + " (" + eventTitle + ") submitted for admin review.",
                    (int) (System.currentTimeMillis() % 100000)
            );
        }

        return newRes;
    }

    public void approveReservation(String reservationId) {
        for (Reservation r : reservations) {
            if (r.getId().equalsIgnoreCase(reservationId)) {
                r.setStatus(ReservationStatus.APPROVED);

                // Add Approval Notification
                NotificationItem notif = new NotificationItem(
                        "notif_" + System.currentTimeMillis(),
                        "Reservation Approved! 🎉",
                        "Your request for " + r.getRoomCode() + " (" + r.getEventTitle() + ") has been APPROVED by Admin.",
                        "Just now",
                        NotificationItem.Type.APPROVAL,
                        false,
                        reservationId
                );
                notifications.add(0, notif);

                if (notificationHelper != null) {
                    notificationHelper.sendNotification(
                            "Reservation Approved! 🎉",
                            "Your request for " + r.getRoomCode() + " (" + r.getEventTitle() + ") has been approved.",
                            (int) (System.currentTimeMillis() % 100000)
                    );
                }
                break;
            }
        }
    }

    public void rejectReservation(String reservationId, String reason) {
        for (Reservation r : reservations) {
            if (r.getId().equalsIgnoreCase(reservationId)) {
                r.setStatus(ReservationStatus.REJECTED);
                r.setRejectionReason(reason);

                // Add Rejection Notification
                NotificationItem notif = new NotificationItem(
                        "notif_" + System.currentTimeMillis(),
                        "Reservation Request Rejected",
                        "Your request for " + r.getRoomCode() + " was rejected. Reason: " + (reason.isEmpty() ? "Schedule conflict" : reason),
                        "Just now",
                        NotificationItem.Type.REJECTION,
                        false,
                        reservationId
                );
                notifications.add(0, notif);

                if (notificationHelper != null) {
                    notificationHelper.sendNotification(
                            "Reservation Request Rejected",
                            "Your request for " + r.getRoomCode() + " was rejected. Reason: " + (reason.isEmpty() ? "Schedule conflict" : reason),
                            (int) (System.currentTimeMillis() % 100000)
                    );
                }
                break;
            }
        }
    }

    public void deleteReservation(String reservationId) {
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getId().equalsIgnoreCase(reservationId)) {
                reservations.remove(i);
                break;
            }
        }
    }

    public void modifyReservation(String reservationId, String newTimeSlot, String newDateString) {
        for (Reservation r : reservations) {
            if (r.getId().equalsIgnoreCase(reservationId)) {
                NotificationItem notif = new NotificationItem(
                        "notif_" + System.currentTimeMillis(),
                        "Reservation Schedule Modified",
                        "Admin resolved reservation " + r.getRoomCode() + " to " + newDateString + " (" + newTimeSlot + ").",
                        "Just now",
                        NotificationItem.Type.SYSTEM,
                        false,
                        reservationId
                );
                notifications.add(0, notif);
                break;
            }
        }
    }
}