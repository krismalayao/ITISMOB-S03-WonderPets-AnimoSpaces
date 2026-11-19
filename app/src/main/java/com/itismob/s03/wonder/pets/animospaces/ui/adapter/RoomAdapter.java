package com.itismob.s03.wonder.pets.animospaces.ui.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.data.UserManager;
import com.itismob.s03.wonder.pets.animospaces.model.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    public interface OnReserveClickListener {
        void onReserveClick(Room room);
    }

    private final List<Room> rooms = new ArrayList<>();
    private final OnReserveClickListener reserveClickListener;

    public RoomAdapter(OnReserveClickListener reserveClickListener) {
        this.reserveClickListener = reserveClickListener;
    }

    public void setRooms(List<Room> roomList) {
        this.rooms.clear();
        if (roomList != null) {
            this.rooms.addAll(roomList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = rooms.get(position);
        holder.tvRoomCode.setText(room.getRoomCode());
        holder.tvRoomType.setText("• " + room.getRoomType());
        holder.tvCapacity.setText("Capacity: " + room.getCapacity() + " seats");
        holder.tvSchedule.setText(room.getScheduleSummary());

        if (room.getAmenities() != null && !room.getAmenities().isEmpty()) {
            holder.tvAmenities.setText(TextUtils.join(", ", room.getAmenities()));
        } else {
            holder.tvAmenities.setText("Standard Facilities");
        }

        boolean canUserBook = UserManager.getInstance().canReserve();

        if (room.isAvailable()) {
            holder.tvStatus.setText(R.string.status_available);
            holder.tvStatus.setTextColor(Color.parseColor("#15803D"));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_available);

            if (canUserBook) {
                holder.btnReserve.setText(R.string.reserve_room);
                holder.btnReserve.setEnabled(true);
                holder.btnReserve.setAlpha(1.0f);
            } else {
                holder.btnReserve.setText("View Only (Student)");
                holder.btnReserve.setEnabled(true); // Allow click to show RBAC policy dialog
                holder.btnReserve.setAlpha(0.7f);
            }
        } else {
            holder.tvStatus.setText(R.string.status_reserved);
            holder.tvStatus.setTextColor(Color.parseColor("#B91C1C"));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_reserved);
            holder.btnReserve.setText("Reserved");
            holder.btnReserve.setEnabled(false);
            holder.btnReserve.setAlpha(0.5f);
        }

        holder.btnReserve.setOnClickListener(v -> {
            if (reserveClickListener != null && room.isAvailable()) {
                reserveClickListener.onReserveClick(room);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomCode;
        TextView tvRoomType;
        TextView tvStatus;
        TextView tvCapacity;
        TextView tvSchedule;
        TextView tvAmenities;
        MaterialButton btnReserve;

        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomCode = itemView.findViewById(R.id.tv_room_code);
            tvRoomType = itemView.findViewById(R.id.tv_room_type);
            tvStatus = itemView.findViewById(R.id.tv_room_status);
            tvCapacity = itemView.findViewById(R.id.tv_room_capacity);
            tvSchedule = itemView.findViewById(R.id.tv_schedule_summary);
            tvAmenities = itemView.findViewById(R.id.tv_amenities);
            btnReserve = itemView.findViewById(R.id.btn_reserve_room);
        }
    }
}