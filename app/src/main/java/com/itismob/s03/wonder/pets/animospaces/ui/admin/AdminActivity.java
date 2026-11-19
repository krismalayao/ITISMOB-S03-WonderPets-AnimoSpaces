package com.itismob.s03.wonder.pets.animospaces.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.data.ReservationRepository;
import com.itismob.s03.wonder.pets.animospaces.model.Reservation;
import com.itismob.s03.wonder.pets.animospaces.model.ReservationStatus;
import com.itismob.s03.wonder.pets.animospaces.ui.adapter.AdminReservationAdapter;
import com.itismob.s03.wonder.pets.animospaces.util.BottomNavHelper;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private ReservationRepository reservationRepository;
    private RecyclerView rvAdminReservations;
    private TextView tvPendingCount;
    private TextView tvApprovedCount;
    private TextView tvEmpty;
    private AdminReservationAdapter adapter;

    private boolean showOnlyPending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        View rootLayout = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reservationRepository = ReservationRepository.getInstance();

        tvPendingCount = findViewById(R.id.tv_stat_pending_count);
        tvApprovedCount = findViewById(R.id.tv_stat_approved_count);
        tvEmpty = findViewById(R.id.tv_admin_empty);
        rvAdminReservations = findViewById(R.id.rv_admin_reservations);

        TabLayout tabLayout = findViewById(R.id.tab_layout_admin);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showOnlyPending = (tab.getPosition() == 0);
                loadReservations();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        setupRecyclerView();
        BottomNavHelper.setupBottomNav(this, 2); // Highlight Approvals Tab (Index 2)
        loadReservations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavHelper.setupBottomNav(this, 2);
        loadReservations();
    }

    private void setupRecyclerView() {
        rvAdminReservations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminReservationAdapter(new AdminReservationAdapter.OnAdminActionListener() {
            @Override
            public void onApprove(Reservation reservation) {
                reservationRepository.approveReservation(reservation.getId());
                Toast.makeText(AdminActivity.this, "Reservation APPROVED!", Toast.LENGTH_SHORT).show();
                loadReservations();
            }

            @Override
            public void onReject(Reservation reservation) {
                showRejectDialog(reservation);
            }

            @Override
            public void onModify(Reservation reservation) {
                showModifyDialog(reservation);
            }

            @Override
            public void onDelete(Reservation reservation) {
                new AlertDialog.Builder(AdminActivity.this)
                        .setTitle("Delete Reservation")
                        .setMessage("Are you sure you want to delete the reservation for " + reservation.getRoomCode() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            reservationRepository.deleteReservation(reservation.getId());
                            Toast.makeText(AdminActivity.this, "Reservation deleted.", Toast.LENGTH_SHORT).show();
                            loadReservations();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        rvAdminReservations.setAdapter(adapter);
    }

    private void showRejectDialog(Reservation reservation) {
        EditText input = new EditText(this);
        input.setHint("Reason for rejection (e.g. Schedule conflict)");

        new AlertDialog.Builder(this)
                .setTitle("Reject Reservation")
                .setMessage("Enter rejection reason for " + reservation.getRoomCode() + ":")
                .setView(input)
                .setPositiveButton("Reject", (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    reservationRepository.rejectReservation(reservation.getId(), reason);
                    Toast.makeText(AdminActivity.this, "Reservation REJECTED.", Toast.LENGTH_SHORT).show();
                    loadReservations();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showModifyDialog(Reservation reservation) {
        EditText input = new EditText(this);
        input.setText(reservation.getTimeSlot());
        input.setHint("New Time Slot (e.g. 2:00 PM - 4:00 PM)");

        new AlertDialog.Builder(this)
                .setTitle("Modify Reservation Time")
                .setMessage("Update time slot for " + reservation.getRoomCode() + ":")
                .setView(input)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newTime = input.getText().toString().trim();
                    if (!newTime.isEmpty()) {
                        reservationRepository.modifyReservation(reservation.getId(), newTime, reservation.getDateString());
                        Toast.makeText(AdminActivity.this, "Reservation time updated.", Toast.LENGTH_SHORT).show();
                        loadReservations();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadReservations() {
        List<Reservation> all = reservationRepository.getAllReservations();
        int pendingNum = 0;
        int approvedNum = 0;

        for (Reservation r : all) {
            if (r.getStatus() == ReservationStatus.PENDING) pendingNum++;
            if (r.getStatus() == ReservationStatus.APPROVED) approvedNum++;
        }

        tvPendingCount.setText(String.valueOf(pendingNum));
        tvApprovedCount.setText(String.valueOf(approvedNum));

        List<Reservation> displayed = new ArrayList<>();
        if (showOnlyPending) {
            for (Reservation r : all) {
                if (r.getStatus() == ReservationStatus.PENDING) displayed.add(r);
            }
        } else {
            displayed.addAll(all);
        }

        adapter.setReservations(displayed);

        if (displayed.isEmpty()) {
            rvAdminReservations.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvAdminReservations.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }
}