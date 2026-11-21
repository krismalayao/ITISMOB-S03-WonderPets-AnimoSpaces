package com.itismob.s03.wonder.pets.animospaces.util;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.widget.Toast;

import com.itismob.s03.wonder.pets.animospaces.model.Reservation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarExporter {

    public static void exportToGoogleCalendar(Context context, Reservation reservation) {
        if (context == null || reservation == null) return;

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);

        String title = reservation.getEventTitle();
        if (title == null || title.trim().isEmpty()) {
            title = "Animo Spaces: " + reservation.getRoomCode();
        } else {
            title = title + " (" + reservation.getRoomCode() + ")";
        }

        intent.putExtra(CalendarContract.Events.TITLE, title);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, reservation.getBuildingName() + " - Room " + reservation.getRoomCode());
        intent.putExtra(CalendarContract.Events.DESCRIPTION,
                "Animo Spaces Approved Reservation Details\n" +
                "• Building: " + reservation.getBuildingName() + "\n" +
                "• Room: " + reservation.getRoomCode() + "\n" +
                "• Reserved By: " + reservation.getRequestedBy() + "\n" +
                "• Purpose: " + reservation.getEventPurpose() + "\n" +
                "• Expected Guests: " + reservation.getAttendeeCount());

        long[] times = parseStartAndEndMillis(reservation.getDateString(), reservation.getTimeSlot());
        if (times[0] > 0) {
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, times[0]);
        }
        if (times[1] > 0) {
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, times[1]);
        }

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "No calendar application found on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    private static long[] parseStartAndEndMillis(String dateStr, String timeSlot) {
        long startMillis = -1;
        long endMillis = -1;
        try {
            if (dateStr == null || timeSlot == null) return new long[]{startMillis, endMillis};

            // Parse timeSlot e.g. "2:00 PM - 4:00 PM" or "10:00 AM - 12:00 PM"
            String[] times = timeSlot.split("-");
            if (times.length < 2) return new long[]{startMillis, endMillis};

            String startTimeStr = times[0].trim();
            String endTimeStr = times[1].trim();

            Date baseDate = null;
            String[] datePatterns = new String[]{"MMM dd, yyyy", "yyyy-MM-dd", "MM/dd/yyyy"};
            for (String pattern : datePatterns) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
                    baseDate = dateFormat.parse(dateStr);
                    if (baseDate != null) break;
                } catch (Exception ignored) {}
            }

            if (baseDate == null) {
                baseDate = new Date();
            }

            startMillis = combineDateAndTime(baseDate, startTimeStr);
            endMillis = combineDateAndTime(baseDate, endTimeStr);

        } catch (Exception ignored) {}

        return new long[]{startMillis, endMillis};
    }

    private static long combineDateAndTime(Date baseDate, String timeStr) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);
            Date timeDate;
            try {
                timeDate = timeFormat.parse(timeStr);
            } catch (Exception e) {
                timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
                timeDate = timeFormat.parse(timeStr);
            }

            if (timeDate == null) return baseDate.getTime();

            Calendar baseCal = Calendar.getInstance();
            baseCal.setTime(baseDate);

            Calendar timeCal = Calendar.getInstance();
            timeCal.setTime(timeDate);

            baseCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
            baseCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
            baseCal.set(Calendar.SECOND, 0);
            baseCal.set(Calendar.MILLISECOND, 0);

            return baseCal.getTimeInMillis();
        } catch (Exception e) {
            return baseDate.getTime();
        }
    }
}
