package com.itismob.s03.wonder.pets.animospaces.ui.dashboard;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.data.ReservationRepository;
import com.itismob.s03.wonder.pets.animospaces.data.UserManager;
import com.itismob.s03.wonder.pets.animospaces.model.Reservation;
import com.itismob.s03.wonder.pets.animospaces.model.ReservationStatus;
import com.itismob.s03.wonder.pets.animospaces.ui.adapter.AdminReservationAdapter;
import com.itismob.s03.wonder.pets.animospaces.ui.adapter.UserReservationAdapter;
import com.itismob.s03.wonder.pets.animospaces.util.CalendarExporter;

import java.util.List;

public class ReservationDashboardActivity extends AppCompatActivity {

    private RecyclerView rvDashboard;
    private TextView tvEmpty;
    private AdminReservationAdapter adminAdapter;
    private UserReservationAdapter userAdapter;
    private boolean isAdminUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reservation_dashboard);

        View rootLayout = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnBack = findViewById(R.id.btn_dashboard_back);
        btnBack.setOnClickListener(v -> finish());

        rvDashboard = findViewById(R.id.rv_dashboard_reservations);
        tvEmpty = findViewById(R.id.tv_dashboard_empty);

        rvDashboard.setLayoutManager(new LinearLayoutManager(this));
        
        UserManager user = UserManager.getInstance();
        isAdminUser = user.isAdmin();

        if (isAdminUser) {
            // Admin users get the admin adapter with approval/rejection controls
            adminAdapter = new AdminReservationAdapter(new AdminReservationAdapter.OnAdminActionListener() {
                @Override
                public void onApprove(Reservation reservation) {
                    ReservationRepository.getInstance().approveReservation(reservation.getId());
                    Toast.makeText(ReservationDashboardActivity.this, "Reservation Approved!", Toast.LENGTH_SHORT).show();
                    loadReservations();
                }

                @Override
                public void onReject(Reservation reservation) {
                    ReservationRepository.getInstance().rejectReservation(reservation.getId(), "Rejected by Admin");
                    Toast.makeText(ReservationDashboardActivity.this, "Reservation Rejected.", Toast.LENGTH_SHORT).show();
                    loadReservations();
                }

                @Override
                public void onModify(Reservation reservation) {
                    showReservationDetailsBottomSheet(reservation);
                }

                @Override
                public void onDelete(Reservation reservation) {
                    ReservationRepository.getInstance().deleteReservation(reservation.getId());
                    Toast.makeText(ReservationDashboardActivity.this, "Reservation Deleted.", Toast.LENGTH_SHORT).show();
                    loadReservations();
                }
            });
            rvDashboard.setAdapter(adminAdapter);
        } else {
            // Student Org and Faculty users get clean user cards with NO admin control buttons
            userAdapter = new UserReservationAdapter(reservation -> showReservationDetailsBottomSheet(reservation), true);
            rvDashboard.setAdapter(userAdapter);
        }

        loadReservations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReservations();
    }

    private void loadReservations() {
        UserManager user = UserManager.getInstance();
        List<Reservation> reservations;
        if (isAdminUser) {
            reservations = ReservationRepository.getInstance().getAllReservations();
            if (adminAdapter != null) adminAdapter.setReservations(reservations);
        } else {
            reservations = ReservationRepository.getInstance().getReservationsForUser(user.getName());
            if (userAdapter != null) userAdapter.setReservations(reservations);
        }

        if (reservations.isEmpty()) {
            rvDashboard.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvDashboard.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void showReservationDetailsBottomSheet(Reservation reservation) {
        if (reservation == null) return;

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.layout_reservation_details_dialog, null);
        dialog.setContentView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tv_detail_event_title);
        TextView tvStatusBadge = dialogView.findViewById(R.id.tv_detail_status_badge);
        TextView tvRoomCode = dialogView.findViewById(R.id.tv_detail_room_code);
        TextView tvBuildingName = dialogView.findViewById(R.id.tv_detail_building_name);
        TextView tvRequester = dialogView.findViewById(R.id.tv_detail_requester);
        TextView tvDate = dialogView.findViewById(R.id.tv_detail_date);
        TextView tvTime = dialogView.findViewById(R.id.tv_detail_time);
        TextView tvAttendees = dialogView.findViewById(R.id.tv_detail_attendees);
        TextView tvPurpose = dialogView.findViewById(R.id.tv_detail_purpose);
        MaterialButton btnExportCalendar = dialogView.findViewById(R.id.btn_export_calendar);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel_reservation);
        MaterialButton btnClose = dialogView.findViewById(R.id.btn_close_details);

        tvTitle.setText(reservation.getEventTitle());
        tvRoomCode.setText(reservation.getRoomCode());
        tvBuildingName.setText(reservation.getBuildingName());
        tvRequester.setText(reservation.getRequestedBy() + " (" + (reservation.getRequesterRole() != null ? reservation.getRequesterRole().getDisplayName() : "User") + ")");
        tvDate.setText("📅 " + reservation.getDateString());
        tvTime.setText("⏰ " + reservation.getTimeSlot());
        tvAttendees.setText("👥 " + reservation.getAttendeeCount() + " Expected Attendees");
        tvPurpose.setText(reservation.getEventPurpose());

        ReservationStatus status = reservation.getStatus();
        if (status != null) {
            tvStatusBadge.setText(status.getLabel().toUpperCase());
            try {
                int textColor = Color.parseColor(status.getTextColorHex());
                int bgColor = Color.parseColor(status.getBgColorHex());
                tvStatusBadge.setTextColor(textColor);

                GradientDrawable drawable = new GradientDrawable();
                drawable.setColor(bgColor);
                drawable.setCornerRadius(16f);
                tvStatusBadge.setBackground(drawable);
            } catch (Exception ignored) {}
        }

        if (btnExportCalendar != null) {
            if (status == ReservationStatus.APPROVED) {
                btnExportCalendar.setVisibility(View.VISIBLE);
                btnExportCalendar.setOnClickListener(v -> CalendarExporter.exportToGoogleCalendar(ReservationDashboardActivity.this, reservation));
            } else {
                btnExportCalendar.setVisibility(View.GONE);
            }
        }

        // Show Cancel Request button only for non-admin users with active reservations
        if (!isAdminUser && (status == ReservationStatus.PENDING || status == ReservationStatus.APPROVED)) {
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setOnClickListener(v -> {
                ReservationRepository.getInstance().cancelReservation(reservation.getId());
                dialog.dismiss();
                Toast.makeText(ReservationDashboardActivity.this, "Reservation cancelled successfully.", Toast.LENGTH_SHORT).show();
                loadReservations();
            });
        } else {
            btnCancel.setVisibility(View.GONE);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}