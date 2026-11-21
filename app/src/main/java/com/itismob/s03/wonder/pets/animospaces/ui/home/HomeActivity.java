package com.itismob.s03.wonder.pets.animospaces.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.itismob.s03.wonder.pets.animospaces.MainActivity;
import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.data.CampusRepository;
import com.itismob.s03.wonder.pets.animospaces.data.ReservationRepository;
import com.itismob.s03.wonder.pets.animospaces.data.UserManager;
import com.itismob.s03.wonder.pets.animospaces.map.MapboxManager;
import com.itismob.s03.wonder.pets.animospaces.model.Campus;
import com.itismob.s03.wonder.pets.animospaces.model.Reservation;
import com.itismob.s03.wonder.pets.animospaces.model.ReservationStatus;
import com.itismob.s03.wonder.pets.animospaces.model.UserRole;
import com.itismob.s03.wonder.pets.animospaces.ui.adapter.AdminReservationAdapter;
import com.itismob.s03.wonder.pets.animospaces.ui.adapter.UserReservationAdapter;
import com.itismob.s03.wonder.pets.animospaces.ui.admin.AdminActivity;
import com.itismob.s03.wonder.pets.animospaces.ui.dashboard.ReservationDashboardActivity;
import com.itismob.s03.wonder.pets.animospaces.ui.notifications.NotificationsActivity;
import com.itismob.s03.wonder.pets.animospaces.ui.profile.ProfileActivity;
import com.itismob.s03.wonder.pets.animospaces.util.BottomNavHelper;
import com.itismob.s03.wonder.pets.animospaces.util.CalendarExporter;
import com.mapbox.maps.MapView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView tvGreeting;
    private TextView tvRoleBadge;

    // User Reservations Section
    private View layoutReservationsSection;
    private View btnViewAllReservations;
    private RecyclerView rvUserReservations;
    private UserReservationAdapter reservationAdapter;
    private TextView tvNoReservations;

    // Admin Executive Command Center Section
    private View layoutAdminHomeSection;
    private TextView tvAdminHomePendingCount;
    private TextView tvAdminHomeApprovedCount;
    private TextView tvAdminHomeBuildingsCount;
    private MaterialButton btnAdminHomeApprovals;
    private MaterialButton btnAdminHomeDashboard;
    private MaterialButton btnAdminHomeViewQueue;
    private RecyclerView rvAdminHomePending;
    private TextView tvAdminHomeEmptyPending;
    private AdminReservationAdapter adminHomePendingAdapter;

    private MapView homeMapView;
    private MapboxManager homeMapboxManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        View rootLayout = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupReservationRecyclerView();
        setupAdminHomeSection();

        homeMapView = findViewById(R.id.home_map_view);
        homeMapboxManager = new MapboxManager(homeMapView, this);
        homeMapboxManager.moveCameraToCampus(Campus.DLSU_MANILA);
        homeMapboxManager.displayBuildingMarkers(CampusRepository.getInstance().getAllBuildings());

        setupMapLauncher();
        setupQuickActions();
        BottomNavHelper.setupBottomNav(this, 0); // Highlight Home Tab (Index 0)
        updateUserDisplay();
    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tv_greeting);
        tvRoleBadge = findViewById(R.id.tv_role_badge);

        layoutReservationsSection = findViewById(R.id.layout_reservations_section);
        btnViewAllReservations = findViewById(R.id.btn_view_all_reservations);
        rvUserReservations = findViewById(R.id.rv_user_reservations);
        tvNoReservations = findViewById(R.id.tv_home_no_reservations);

        if (btnViewAllReservations != null) {
            btnViewAllReservations.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ReservationDashboardActivity.class)));
        }

        // Admin Home Views
        layoutAdminHomeSection = findViewById(R.id.layout_admin_home_section);
        tvAdminHomePendingCount = findViewById(R.id.tv_admin_home_pending_count);
        tvAdminHomeApprovedCount = findViewById(R.id.tv_admin_home_approved_count);
        tvAdminHomeBuildingsCount = findViewById(R.id.tv_admin_home_buildings_count);
        btnAdminHomeApprovals = findViewById(R.id.btn_admin_home_approvals);
        btnAdminHomeDashboard = findViewById(R.id.btn_admin_home_dashboard);
        btnAdminHomeViewQueue = findViewById(R.id.btn_admin_home_view_queue);
        rvAdminHomePending = findViewById(R.id.rv_admin_home_pending);
        tvAdminHomeEmptyPending = findViewById(R.id.tv_admin_home_empty_pending);

        View.OnClickListener openApprovalsListener = v -> startActivity(new Intent(HomeActivity.this, AdminActivity.class));
        if (btnAdminHomeApprovals != null) btnAdminHomeApprovals.setOnClickListener(openApprovalsListener);
        if (btnAdminHomeViewQueue != null) btnAdminHomeViewQueue.setOnClickListener(openApprovalsListener);

        if (btnAdminHomeDashboard != null) {
            btnAdminHomeDashboard.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ReservationDashboardActivity.class)));
        }
    }

    private void setupAdminHomeSection() {
        if (rvAdminHomePending != null) {
            rvAdminHomePending.setLayoutManager(new LinearLayoutManager(this));
            adminHomePendingAdapter = new AdminReservationAdapter(new AdminReservationAdapter.OnAdminActionListener() {
                @Override
                public void onApprove(Reservation reservation) {
                    ReservationRepository.getInstance().approveReservation(reservation.getId());
                    Toast.makeText(HomeActivity.this, "Reservation Approved!", Toast.LENGTH_SHORT).show();
                    updateUserDisplay();
                }

                @Override
                public void onReject(Reservation reservation) {
                    ReservationRepository.getInstance().rejectReservation(reservation.getId(), "Rejected by Admin");
                    Toast.makeText(HomeActivity.this, "Reservation Rejected.", Toast.LENGTH_SHORT).show();
                    updateUserDisplay();
                }

                @Override
                public void onModify(Reservation reservation) {
                    showReservationDetailsBottomSheet(reservation);
                }

                @Override
                public void onDelete(Reservation reservation) {
                    new AlertDialog.Builder(HomeActivity.this)
                            .setTitle("Delete Reservation")
                            .setMessage("Delete reservation for " + reservation.getRoomCode() + "?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                ReservationRepository.getInstance().deleteReservation(reservation.getId());
                                updateUserDisplay();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
            rvAdminHomePending.setAdapter(adminHomePendingAdapter);
        }
    }

    private void setupReservationRecyclerView() {
        if (rvUserReservations != null) {
            rvUserReservations.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            reservationAdapter = new UserReservationAdapter(reservation -> showReservationDetailsBottomSheet(reservation));
            rvUserReservations.setAdapter(reservationAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavHelper.setupBottomNav(this, 0);
        updateUserDisplay();
    }

    private void updateUserDisplay() {
        UserManager userManager = UserManager.getInstance();
        boolean isAdmin = userManager.isAdmin();

        if (tvGreeting != null) {
            tvGreeting.setText("Hey there,\n" + userManager.getFirstName() + "!");
        }
        if (tvRoleBadge != null) {
            UserRole role = userManager.getUserRole();
            tvRoleBadge.setText(role != null ? role.getDisplayName() : "Student");
        }

        ImageView ivHomeAvatar = findViewById(R.id.iv_home_avatar);
        if (ivHomeAvatar != null) {
            String uriStr = userManager.getProfileImageUri();
            if (uriStr != null && !uriStr.isEmpty()) {
                try {
                    ivHomeAvatar.setImageURI(Uri.parse(uriStr));
                } catch (Exception e) {
                    ivHomeAvatar.setImageResource(R.drawable.ic_user_avatar);
                }
            } else {
                ivHomeAvatar.setImageResource(R.drawable.ic_user_avatar);
            }
        }

        // Admin Executive Command Center vs Regular User Reservations
        if (isAdmin) {
            if (layoutAdminHomeSection != null) {
                layoutAdminHomeSection.setVisibility(View.VISIBLE);
                loadAdminHomeData();
            }
            if (layoutReservationsSection != null) {
                layoutReservationsSection.setVisibility(View.GONE);
            }
        } else {
            if (layoutAdminHomeSection != null) {
                layoutAdminHomeSection.setVisibility(View.GONE);
            }
            if (layoutReservationsSection != null) {
                UserRole role = userManager.getUserRole();
                if (role == UserRole.FACULTY || role == UserRole.STUDENT_ORGANIZATION) {
                    layoutReservationsSection.setVisibility(View.VISIBLE);
                    loadPersonalReservations(userManager.getName());
                } else {
                    layoutReservationsSection.setVisibility(View.GONE);
                }
            }
        }

        // My Bookings Quick Action visible ONLY for Faculty and Student Organizations
        View qaMyReservations = findViewById(R.id.qa_my_reservations);
        if (qaMyReservations != null) {
            UserRole role = userManager.getUserRole();
            boolean canSeeMyReservations = (role == UserRole.FACULTY || role == UserRole.STUDENT_ORGANIZATION);
            qaMyReservations.setVisibility(canSeeMyReservations ? View.VISIBLE : View.GONE);
        }

        // Notifications Quick Action visible for Faculty, Student Orgs, and Admin
        View qaNotifications = findViewById(R.id.qa_notifications);
        if (qaNotifications != null) {
            boolean canSeeNotif = userManager.canReserve() || isAdmin;
            qaNotifications.setVisibility(canSeeNotif ? View.VISIBLE : View.GONE);
        }
    }

    private void loadAdminHomeData() {
        ReservationRepository repo = ReservationRepository.getInstance();
        List<Reservation> allReservations = repo.getAllReservations();

        int pendingCount = 0;
        int approvedCount = 0;
        List<Reservation> pendingQueue = new ArrayList<>();

        for (Reservation r : allReservations) {
            if (r.getStatus() == ReservationStatus.PENDING) {
                pendingCount++;
                pendingQueue.add(r);
            } else if (r.getStatus() == ReservationStatus.APPROVED) {
                approvedCount++;
            }
        }

        int totalBuildings = CampusRepository.getInstance().getAllBuildings().size();

        if (tvAdminHomePendingCount != null) tvAdminHomePendingCount.setText(String.valueOf(pendingCount));
        if (tvAdminHomeApprovedCount != null) tvAdminHomeApprovedCount.setText(String.valueOf(approvedCount));
        if (tvAdminHomeBuildingsCount != null) tvAdminHomeBuildingsCount.setText(String.valueOf(totalBuildings));

        if (adminHomePendingAdapter != null) {
            adminHomePendingAdapter.setReservations(pendingQueue);
        }

        if (pendingQueue.isEmpty()) {
            if (rvAdminHomePending != null) rvAdminHomePending.setVisibility(View.GONE);
            if (tvAdminHomeEmptyPending != null) tvAdminHomeEmptyPending.setVisibility(View.VISIBLE);
        } else {
            if (rvAdminHomePending != null) rvAdminHomePending.setVisibility(View.VISIBLE);
            if (tvAdminHomeEmptyPending != null) tvAdminHomeEmptyPending.setVisibility(View.GONE);
        }
    }

    private void loadPersonalReservations(String userName) {
        List<Reservation> userReservations = ReservationRepository.getInstance().getReservationsForUser(userName);

        if (userReservations == null || userReservations.isEmpty()) {
            if (rvUserReservations != null) rvUserReservations.setVisibility(View.GONE);
            if (tvNoReservations != null) tvNoReservations.setVisibility(View.VISIBLE);
            return;
        }

        if (tvNoReservations != null) tvNoReservations.setVisibility(View.GONE);
        if (rvUserReservations != null) {
            rvUserReservations.setVisibility(View.VISIBLE);
            if (reservationAdapter != null) {
                reservationAdapter.setReservations(userReservations);
            }
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
                btnExportCalendar.setOnClickListener(v -> CalendarExporter.exportToGoogleCalendar(HomeActivity.this, reservation));
            } else {
                btnExportCalendar.setVisibility(View.GONE);
            }
        }

        boolean isAdmin = UserManager.getInstance().isAdmin();
        if (!isAdmin && (status == ReservationStatus.PENDING || status == ReservationStatus.APPROVED)) {
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setOnClickListener(v -> {
                ReservationRepository.getInstance().cancelReservation(reservation.getId());
                dialog.dismiss();
                Toast.makeText(HomeActivity.this, "Reservation cancelled successfully.", Toast.LENGTH_SHORT).show();
                updateUserDisplay();
            });
        } else {
            btnCancel.setVisibility(View.GONE);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void setupQuickActions() {
        View qaEditProfile = findViewById(R.id.qa_edit_profile);
        View qaMyReservations = findViewById(R.id.qa_my_reservations);
        View qaViewMap = findViewById(R.id.qa_view_map);
        View qaNotifications = findViewById(R.id.qa_notifications);

        if (qaEditProfile != null) {
            qaEditProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }

        if (qaMyReservations != null) {
            qaMyReservations.setOnClickListener(v -> startActivity(new Intent(this, ReservationDashboardActivity.class)));
        }

        if (qaViewMap != null) {
            qaViewMap.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        }

        if (qaNotifications != null) {
            qaNotifications.setOnClickListener(v -> {
                UserManager userManager = UserManager.getInstance();
                if (userManager.isAdmin()) {
                    startActivity(new Intent(this, AdminActivity.class));
                } else {
                    startActivity(new Intent(this, NotificationsActivity.class));
                }
            });
        }
    }

    private void setupMapLauncher() {
        View cardMap = findViewById(R.id.card_launch_map);
        View overlay = findViewById(R.id.view_map_overlay);
        View btnExpandMap = findViewById(R.id.btn_expand_map);

        View.OnClickListener launchMapListener = v -> startActivity(new Intent(HomeActivity.this, MainActivity.class));

        if (cardMap != null) cardMap.setOnClickListener(launchMapListener);
        if (overlay != null) overlay.setOnClickListener(launchMapListener);
        if (btnExpandMap != null) btnExpandMap.setOnClickListener(launchMapListener);
    }
}