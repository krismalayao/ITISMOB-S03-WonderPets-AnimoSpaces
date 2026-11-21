package com.itismob.s03.wonder.pets.animospaces.ui.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.model.Reservation;
import com.itismob.s03.wonder.pets.animospaces.model.ReservationStatus;

import java.util.ArrayList;
import java.util.List;

public class UserReservationAdapter extends RecyclerView.Adapter<UserReservationAdapter.UserReservationViewHolder> {

    public interface OnReservationClickListener {
        void onReservationClick(Reservation reservation);
    }

    private final List<Reservation> reservations = new ArrayList<>();
    private final OnReservationClickListener clickListener;
    private final boolean isFullWidth;

    public UserReservationAdapter() {
        this(null, false);
    }

    public UserReservationAdapter(OnReservationClickListener clickListener) {
        this(clickListener, false);
    }

    public UserReservationAdapter(OnReservationClickListener clickListener, boolean isFullWidth) {
        this.clickListener = clickListener;
        this.isFullWidth = isFullWidth;
    }

    public void setReservations(List<Reservation> reservationList) {
        this.reservations.clear();
        if (reservationList != null) {
            this.reservations.addAll(reservationList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isFullWidth ? R.layout.item_user_reservation_card_full : R.layout.item_user_reservation_card;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new UserReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserReservationViewHolder holder, int position) {
        Reservation res = reservations.get(position);

        holder.tvVenue.setText(res.getRoomCode() + " • " + res.getBuildingName());
        holder.tvTitle.setText(res.getEventTitle());
        holder.tvDate.setText("📅 " + res.getDateString());
        holder.tvTime.setText("⏰ " + res.getTimeSlot());
        holder.tvAttendees.setText("👥 " + res.getAttendeeCount() + " Attendees • " + res.getEventPurpose());

        ReservationStatus status = res.getStatus();
        if (status != null) {
            holder.tvStatus.setText(status.getLabel().toUpperCase());
            try {
                int textColor = Color.parseColor(status.getTextColorHex());
                int bgColor = Color.parseColor(status.getBgColorHex());

                holder.tvStatus.setTextColor(textColor);

                GradientDrawable drawable = new GradientDrawable();
                drawable.setColor(bgColor);
                drawable.setCornerRadius(16f);
                holder.tvStatus.setBackground(drawable);
            } catch (Exception e) {
                holder.tvStatus.setTextColor(Color.parseColor("#15803D"));
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onReservationClick(res);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    static class UserReservationViewHolder extends RecyclerView.ViewHolder {
        TextView tvVenue;
        TextView tvStatus;
        TextView tvTitle;
        TextView tvDate;
        TextView tvTime;
        TextView tvAttendees;

        UserReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVenue = itemView.findViewById(R.id.tv_res_venue);
            tvStatus = itemView.findViewById(R.id.tv_res_status);
            tvTitle = itemView.findViewById(R.id.tv_res_title);
            tvDate = itemView.findViewById(R.id.tv_res_date);
            tvTime = itemView.findViewById(R.id.tv_res_time);
            tvAttendees = itemView.findViewById(R.id.tv_res_attendees);
        }
    }
}