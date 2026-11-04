package com.itismob.s03.wonder.pets.animospaces.ui.dashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.data.ReservationRepository;
import com.itismob.s03.wonder.pets.animospaces.model.Reservation;
import com.itismob.s03.wonder.pets.animospaces.ui.adapter.AdminReservationAdapter;

import java.util.List;

public class ReservationDashboardActivity extends AppCompatActivity {

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

        RecyclerView rvDashboard = findViewById(R.id.rv_dashboard_reservations);
        TextView tvEmpty = findViewById(R.id.tv_dashboard_empty);

        rvDashboard.setLayoutManager(new LinearLayoutManager(this));
        AdminReservationAdapter adapter = new AdminReservationAdapter(null);
        rvDashboard.setAdapter(adapter);

        List<Reservation> reservations = ReservationRepository.getInstance().getAllReservations();
        adapter.setReservations(reservations);

        if (reservations.isEmpty()) {
            rvDashboard.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvDashboard.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }
}