package com.itismob.s03.wonder.pets.animospaces.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.model.Reservation;
import com.itismob.s03.wonder.pets.animospaces.model.ReservationStatus;

import java.util.ArrayList;
import java.util.List;

public class AdminReservationAdapter extends RecyclerView.Adapter<AdminReservationAdapter.AdminViewHolder> {

    public interface OnAdminActionListener {
        void onApprove(Reservation reservation);
        void onReject(Reservation reservation);
        void onModify(Reservation reservation);
        void onDelete(Reservation reservation);
    }

    private final List<Reservation> reservations = new ArrayList<>();
    private final OnAdminActionListener actionListener;

    public AdminReservationAdapter(OnAdminActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setReservations(List<Reservation> list) {
        this.reservations.clear();
        if (list != null) {
            this.reservations.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_reservation, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        Reservation item = reservations.get(position);
        holder.tvTitle.setText(item.getEventTitle());
        holder.tvRoomCode.setText(item.getRoomCode());
        holder.tvRequester.setText("Requested by: " + item.getRequestedBy() + " (" + item.getRequesterRole().getDisplayName() + ")");
        holder.tvSchedule.setText(item.getDateString() + " • " + item.getTimeSlot() + " (" + item.getAttendeeCount() + " Attendees)");
        holder.tvPurpose.setText("Purpose: " + item.getEventPurpose());

        ReservationStatus status = item.getStatus();
        holder.tvStatusBadge.setText("Status: " + status.getLabel());
        holder.tvStatusBadge.setTextColor(Color.parseColor(status.getTextColorHex()));

        if (status == ReservationStatus.PENDING) {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
        } else {
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onModify(item);
            }
        });

        holder.btnApprove.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onApprove(item);
            }
        });

        holder.btnReject.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onReject(item);
            }
        });

        holder.btnModify.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onModify(item);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDelete(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvRoomCode;
        TextView tvRequester;
        TextView tvSchedule;
        TextView tvPurpose;
        TextView tvStatusBadge;
        MaterialButton btnApprove;
        MaterialButton btnReject;
        MaterialButton btnModify;
        ImageButton btnDelete;

        AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_admin_event_title);
            tvRoomCode = itemView.findViewById(R.id.tv_admin_room_code);
            tvRequester = itemView.findViewById(R.id.tv_admin_requester);
            tvSchedule = itemView.findViewById(R.id.tv_admin_schedule);
            tvPurpose = itemView.findViewById(R.id.tv_admin_purpose);
            tvStatusBadge = itemView.findViewById(R.id.tv_admin_status_badge);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
            btnModify = itemView.findViewById(R.id.btn_modify);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}