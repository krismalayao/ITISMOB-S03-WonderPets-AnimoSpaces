package com.itismob.s03.wonder.pets.animospaces.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.model.Building;

import java.util.ArrayList;
import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.BuildingViewHolder> {

    public interface OnBuildingSelectedListener {
        void onBuildingSelected(Building building);
    }

    private final List<Building> buildingList = new ArrayList<>();
    private final OnBuildingSelectedListener listener;
    private int selectedPosition = -1;

    public BuildingAdapter(OnBuildingSelectedListener listener) {
        this.listener = listener;
    }

    public void setBuildings(List<Building> buildings) {
        this.buildingList.clear();
        if (buildings != null) {
            this.buildingList.addAll(buildings);
        }
        this.selectedPosition = -1;
        notifyDataSetChanged();
    }

    public void selectBuilding(Building building) {
        if (building == null) return;
        for (int i = 0; i < buildingList.size(); i++) {
            if (buildingList.get(i).getId().equalsIgnoreCase(building.getId())) {
                int oldPos = selectedPosition;
                selectedPosition = i;
                notifyItemChanged(oldPos);
                notifyItemChanged(selectedPosition);
                break;
            }
        }
    }

    @NonNull
    @Override
    public BuildingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_building_chip, parent, false);
        return new BuildingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingViewHolder holder, int position) {
        Building building = buildingList.get(position);
        holder.tvCode.setText(building.getCode());
        holder.tvName.setText(building.getName());

        boolean isSelected = (position == selectedPosition);
        if (isSelected) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#005A36"));
            holder.tvName.setTextColor(Color.WHITE);
            holder.tvCode.setTextColor(Color.parseColor("#005A36"));
            holder.tvCode.setBackgroundColor(Color.WHITE);
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.tvName.setTextColor(Color.parseColor("#1F2937"));
            holder.tvCode.setTextColor(Color.parseColor("#005A36"));
            holder.tvCode.setBackgroundResource(R.drawable.bg_chip_code);
        }

        holder.itemView.setOnClickListener(v -> {
            int currentPos = holder.getBindingAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                int oldPos = selectedPosition;
                selectedPosition = currentPos;
                notifyItemChanged(oldPos);
                notifyItemChanged(selectedPosition);
                if (listener != null) {
                    listener.onBuildingSelected(building);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return buildingList.size();
    }

    static class BuildingViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvCode;
        TextView tvName;

        BuildingViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_building_chip);
            tvCode = itemView.findViewById(R.id.tv_building_code);
            tvName = itemView.findViewById(R.id.tv_building_name);
        }
    }
}