package com.itismob.s03.wonder.pets.animospaces.ui.notifications;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.data.ReservationRepository;
import com.itismob.s03.wonder.pets.animospaces.model.NotificationItem;
import com.itismob.s03.wonder.pets.animospaces.ui.adapter.NotificationAdapter;
import com.itismob.s03.wonder.pets.animospaces.util.BottomNavHelper;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private ReservationRepository reservationRepository;
    private RecyclerView rvNotifications;
    private TextView tvUnreadSubtitle;
    private TextView tvEmpty;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);

        View rootLayout = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reservationRepository = ReservationRepository.getInstance();

        rvNotifications = findViewById(R.id.rv_notifications_list);
        tvUnreadSubtitle = findViewById(R.id.tv_notif_unread_subtitle);
        tvEmpty = findViewById(R.id.tv_empty_notifications_view);
        MaterialButton btnClearAll = findViewById(R.id.btn_clear_all_notifs);

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter((item, position) -> {
            item.setRead(true);
            adapter.notifyItemChanged(position);
            updateUnreadCounter();
        });
        rvNotifications.setAdapter(adapter);

        btnClearAll.setOnClickListener(v -> {
            reservationRepository.markAllNotificationsAsRead();
            loadNotifications();
            Toast.makeText(this, "All notifications marked as read.", Toast.LENGTH_SHORT).show();
        });

        BottomNavHelper.setupBottomNav(this, 2); // Highlight Notifications Tab (Index 2)
        loadNotifications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavHelper.setupBottomNav(this, 2);
        loadNotifications();
    }

    private void loadNotifications() {
        List<NotificationItem> items = reservationRepository.getNotifications();
        adapter.setNotifications(items);
        updateUnreadCounter();

        if (items.isEmpty()) {
            rvNotifications.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvNotifications.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void updateUnreadCounter() {
        int unreadCount = reservationRepository.getUnreadNotificationCount();
        if (unreadCount > 0) {
            tvUnreadSubtitle.setText(unreadCount + " unread alert" + (unreadCount > 1 ? "s" : ""));
        } else {
            tvUnreadSubtitle.setText("All caught up!");
        }
    }
}