package com.itismob.s03.wonder.pets.animospaces.util;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itismob.s03.wonder.pets.animospaces.MainActivity;
import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.data.UserManager;
import com.itismob.s03.wonder.pets.animospaces.ui.admin.AdminActivity;
import com.itismob.s03.wonder.pets.animospaces.ui.home.HomeActivity;
import com.itismob.s03.wonder.pets.animospaces.ui.notifications.NotificationsActivity;
import com.itismob.s03.wonder.pets.animospaces.ui.profile.ProfileActivity;

public class BottomNavHelper {

    public static void setupBottomNav(Activity activity, int activeIndex) {
        View itemHome = activity.findViewById(R.id.nav_item_home);
        View itemReserve = activity.findViewById(R.id.nav_item_reserve);
        View itemNotif = activity.findViewById(R.id.nav_item_notifications);
        View itemProfile = activity.findViewById(R.id.nav_item_profile);

        ImageView ivHome = activity.findViewById(R.id.iv_nav_home);
        ImageView ivReserve = activity.findViewById(R.id.iv_nav_reserve);
        ImageView ivNotif = activity.findViewById(R.id.iv_nav_notifications);
        ImageView ivProfile = activity.findViewById(R.id.iv_nav_profile);

        TextView tvHome = activity.findViewById(R.id.tv_nav_home);
        TextView tvReserve = activity.findViewById(R.id.tv_nav_reserve);
        TextView tvNotif = activity.findViewById(R.id.tv_nav_notifications);
        TextView tvProfile = activity.findViewById(R.id.tv_nav_profile);

        UserManager userManager = UserManager.getInstance();
        boolean isAdmin = userManager.isAdmin();
        boolean canReserve = userManager.canReserve();

        // Configure Tab 2: Approvals (Admin) vs Notifications (Faculty/Org) vs Hidden (Student)
        if (itemNotif != null) {
            if (isAdmin) {
                itemNotif.setVisibility(View.VISIBLE);
                if (tvNotif != null) tvNotif.setText("Approvals");
                if (ivNotif != null) ivNotif.setImageResource(android.R.drawable.ic_menu_agenda);
            } else if (canReserve) {
                itemNotif.setVisibility(View.VISIBLE);
                if (tvNotif != null) tvNotif.setText("Notifications");
                if (ivNotif != null) ivNotif.setImageResource(android.R.drawable.ic_popup_reminder);
            } else {
                itemNotif.setVisibility(View.GONE);
            }
        }

        int activeColor = Color.parseColor("#005A36");
        int inactiveColor = Color.parseColor("#6B7280");

        // Reset all tabs
        resetTab(itemHome, ivHome, tvHome, inactiveColor);
        resetTab(itemReserve, ivReserve, tvReserve, inactiveColor);
        resetTab(itemNotif, ivNotif, tvNotif, inactiveColor);
        resetTab(itemProfile, ivProfile, tvProfile, inactiveColor);

        // Highlight active tab
        if (activeIndex == 0 && itemHome != null) {
            highlightTab(itemHome, ivHome, tvHome, activeColor);
        } else if (activeIndex == 1 && itemReserve != null) {
            highlightTab(itemReserve, ivReserve, tvReserve, activeColor);
        } else if (activeIndex == 2 && itemNotif != null) {
            highlightTab(itemNotif, ivNotif, tvNotif, activeColor);
        } else if (activeIndex == 3 && itemProfile != null) {
            highlightTab(itemProfile, ivProfile, tvProfile, activeColor);
        }

        // Navigation click listeners
        if (itemHome != null) {
            itemHome.setOnClickListener(v -> {
                if (activeIndex != 0) {
                    Intent intent = new Intent(activity, HomeActivity.class);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    if (activeIndex != 1) activity.finish();
                }
            });
        }

        if (itemReserve != null) {
            itemReserve.setOnClickListener(v -> {
                if (activeIndex != 1) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    if (activeIndex != 0) activity.finish();
                }
            });
        }

        if (itemNotif != null) {
            itemNotif.setOnClickListener(v -> {
                if (isAdmin) {
                    // Admin Tab 2 opens Admin Approval Queue directly
                    Intent intent = new Intent(activity, AdminActivity.class);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else if (canReserve) {
                    // Faculty/Org Tab 2 opens User Notifications
                    if (activeIndex != 2) {
                        Intent intent = new Intent(activity, NotificationsActivity.class);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }
            });
        }

        if (itemProfile != null) {
            itemProfile.setOnClickListener(v -> {
                if (activeIndex != 3) {
                    Intent intent = new Intent(activity, ProfileActivity.class);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        }
    }

    private static void resetTab(View container, ImageView icon, TextView label, int color) {
        if (container != null) container.setBackground(null);
        if (icon != null) icon.setImageTintList(ColorStateList.valueOf(color));
        if (label != null) {
            label.setTextColor(color);
            label.setTextSize(10f);
        }
    }

    private static void highlightTab(View container, ImageView icon, TextView label, int color) {
        if (container != null) container.setBackgroundResource(R.drawable.bg_nav_active);
        if (icon != null) icon.setImageTintList(ColorStateList.valueOf(color));
        if (label != null) {
            label.setTextColor(color);
            label.setTextSize(11f);
        }
    }
}