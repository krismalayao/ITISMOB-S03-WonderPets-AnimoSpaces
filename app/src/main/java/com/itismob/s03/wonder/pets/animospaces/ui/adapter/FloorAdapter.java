package com.itismob.s03.wonder.pets.animospaces.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itismob.s03.wonder.pets.animospaces.R;

import java.util.ArrayList;
import java.util.List;

public class FloorAdapter extends RecyclerView.Adapter<FloorAdapter.FloorViewHolder> {

    public interface OnFloorSelectedListener {
        void onFloorSelected(int floorNumber);
    }

    private final List<Integer> floors = new ArrayList<>();
    private final OnFloorSelectedListener listener;
    private int selectedPosition = 0;

    public FloorAdapter(OnFloorSelectedListener listener) {
        this.listener = listener;
    }

    public void setFloors(List<Integer> floorList) {
        this.floors.clear();
        if (floorList != null) {
            this.floors.addAll(floorList);
        }
        this.selectedPosition = 0;
        notifyDataSetChanged();
    }

    public int getSelectedFloor() {
        if (selectedPosition >= 0 && selectedPosition < floors.size()) {
            return floors.get(selectedPosition);
        }
        return 1;
    }

    @NonNull
    @Override
    public FloorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_floor_chip, parent, false);
        return new FloorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FloorViewHolder holder, int position) {
        int floorNum = floors.get(position);
        String label = (floorNum == 1) ? "GF" : floorNum + "F";
        holder.tvFloorNum.setText(label);

        boolean isSelected = (position == selectedPosition);
        if (isSelected) {
            holder.tvFloorNum.setBackgroundResource(R.drawable.bg_floor_selected);
            holder.tvFloorNum.setTextColor(Color.WHITE);
        } else {
            holder.tvFloorNum.setBackgroundResource(R.drawable.bg_floor_unselected);
            holder.tvFloorNum.setTextColor(Color.parseColor("#005A36"));
        }

        holder.itemView.setOnClickListener(v -> {
            int currentPos = holder.getBindingAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                int oldPos = selectedPosition;
                selectedPosition = currentPos;
                notifyItemChanged(oldPos);
                notifyItemChanged(selectedPosition);
                if (listener != null) {
                    listener.onFloorSelected(floors.get(currentPos));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return floors.size();
    }

    static class FloorViewHolder extends RecyclerView.ViewHolder {
        TextView tvFloorNum;

        FloorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFloorNum = itemView.findViewById(R.id.tv_floor_number);
        }
    }
}