package com.itismob.s03.wonder.pets.animospaces.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.itismob.s03.wonder.pets.animospaces.MainActivity;
import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.data.ReservationRepository;
import com.itismob.s03.wonder.pets.animospaces.data.UserManager;
import com.itismob.s03.wonder.pets.animospaces.model.Reservation;
import com.itismob.s03.wonder.pets.animospaces.model.UserRole;
import com.itismob.s03.wonder.pets.animospaces.ui.profile.ProfileActivity;
import com.itismob.s03.wonder.pets.animospaces.util.BottomNavHelper;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView tvGreeting;
    private TextView tvRoleBadge;
    private View layoutReservationsSection;
    private LinearLayout containerUserReservations;
    private TextView tvNoReservations;

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

        tvGreeting = findViewById(R.id.tv_greeting);
        tvRoleBadge = findViewById(R.id.tv_role_badge);
        layoutReservationsSection = findViewById(R.id.layout_reservations_section);
        containerUserReservations = findViewById(R.id.container_user_reservations);
        tvNoReservations = findViewById(R.id.tv_home_no_reservations);

        setupMapLauncher();
        BottomNavHelper.setupBottomNav(this, 0); // Highlight Home Tab (Index 0)
        updateUserDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavHelper.setupBottomNav(this, 0);
        updateUserDisplay();
    }

    private void updateUserDisplay() {
        UserManager userManager = UserManager.getInstance();
        if (tvGreeting != null) {
            tvGreeting.setText("Hey there,\n" + userManager.getFirstName() + "!");
        }
        if (tvRoleBadge != null) {
            UserRole role = userManager.getUserRole();
            tvRoleBadge.setText(role != null ? role.getDisplayName() : "Student");
        }

        // Show Reservations Table ONLY for Faculty and Student Organizations
        if (layoutReservationsSection != null) {
            UserRole role = userManager.getUserRole();
            if (role == UserRole.FACULTY || role == UserRole.STUDENT_ORGANIZATION) {
                layoutReservationsSection.setVisibility(View.VISIBLE);
                loadPersonalReservationsTable(userManager.getName());
            } else {
                // Hidden for Regular Students and Administrators
                layoutReservationsSection.setVisibility(View.GONE);
            }
        }
    }

    private void loadPersonalReservationsTable(String userName) {
        if (containerUserReservations == null) return;
        containerUserReservations.removeAllViews();

        List<Reservation> userReservations = ReservationRepository.getInstance().getReservationsForUser(userName);

        if (userReservations.isEmpty()) {
            if (tvNoReservations != null) tvNoReservations.setVisibility(View.VISIBLE);
            return;
        }

        if (tvNoReservations != null) tvNoReservations.setVisibility(View.GONE);

        for (int i = 0; i < userReservations.size(); i++) {
            Reservation res = userReservations.get(i);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setMinimumHeight(dpToPx(38));

            TextView tvTitle = createTableCell(res.getEventTitle(), 1.2f);
            TextView tvDate = createTableCell(res.getDateString(), 1.0f);
            TextView tvTime = createTableCell(res.getTimeSlot(), 1.1f);
            TextView tvVenue = createTableCell(res.getRoomCode() + " (" + res.getBuildingName() + ")", 1.0f);

            row.addView(tvTitle);
            row.addView(tvDate);
            row.addView(tvTime);
            row.addView(tvVenue);

            containerUserReservations.addView(row);

            if (i < userReservations.size() - 1) {
                View divider = new View(this);
                divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
                divider.setBackgroundColor(Color.parseColor("#E5E7EB"));
                containerUserReservations.addView(divider);
            }
        }
    }

    private TextView createTableCell(String text, float weight) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        tv.setLayoutParams(params);
        tv.setText(text);
        tv.setTextSize(9.5f);
        tv.setTextColor(Color.parseColor("#1F2937"));
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(4, 8, 4, 8);
        return tv;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void setupMapLauncher() {
        View cardMap = findViewById(R.id.card_launch_map);
        if (cardMap != null) {
            cardMap.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, MainActivity.class)));
        }
    }
}