package com.itismob.s03.wonder.pets.animospaces.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.data.CampusRepository;
import com.itismob.s03.wonder.pets.animospaces.model.Building;
import com.itismob.s03.wonder.pets.animospaces.model.Room;

import java.util.ArrayList;
import java.util.List;

public class BuildingSearchAdapter extends RecyclerView.Adapter<BuildingSearchAdapter.SearchCardViewHolder> {

    public interface OnBuildingSearchActionListener {
        void onShowOnMap(Building building);
        void onExploreRooms(Building building);
    }

    private final List<Building> buildings = new ArrayList<>();
    private final OnBuildingSearchActionListener actionListener;
    private String currentQuery = "";

    public BuildingSearchAdapter(OnBuildingSearchActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setBuildings(List<Building> buildingList, String query) {
        this.buildings.clear();
        if (buildingList != null) {
            this.buildings.addAll(buildingList);
        }
        this.currentQuery = query != null ? query : "";
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_building_search_card, parent, false);
        return new SearchCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchCardViewHolder holder, int position) {
        Building building = buildings.get(position);
        CampusRepository repo = CampusRepository.getInstance();

        if (holder.ivBuildingImage != null) {
            holder.ivBuildingImage.setImageResource(building.getImageResId());
        }

        holder.tvCode.setText(building.getCode());
        holder.tvName.setText(building.getName());
        holder.tvCategory.setText(building.getCategory());
        holder.tvDescription.setText(building.getDescription());

        // Floor count
        int totalFloors = building.getAvailableFloors() != null ? building.getAvailableFloors().size() : 0;
        holder.tvFloors.setText("🏢 " + totalFloors + " Floors");

        // Room count stats
        List<Room> allRooms = repo.getRoomsForBuilding(building.getId());
        int availableRooms = repo.getAvailableRoomsCount(building.getId());
        if (allRooms.isEmpty()) {
            holder.tvRooms.setText("🚪 Rooms Info N/A");
        } else {
            holder.tvRooms.setText("🚪 " + allRooms.size() + " Rooms • " + availableRooms + " Available");
        }

        // Campus text
        holder.tvCampus.setText("📍 " + (building.getCampus() != null ? building.getCampus().getDisplayName() : "DLSU Manila"));

        // Match Highlight Tag
        String matchHighlight = repo.getMatchingRoomHighlight(building, currentQuery);
        if (matchHighlight != null && !matchHighlight.isEmpty()) {
            holder.tvMatchHighlight.setVisibility(View.VISIBLE);
            holder.tvMatchHighlight.setText(matchHighlight);
        } else {
            holder.tvMatchHighlight.setVisibility(View.GONE);
        }

        // Action Buttons
        holder.btnLocateMap.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onShowOnMap(building);
            }
        });

        holder.btnViewDetails.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onExploreRooms(building);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onExploreRooms(building);
            }
        });
    }

    @Override
    public int getItemCount() {
        return buildings.size();
    }

    static class SearchCardViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBuildingImage;
        TextView tvCode;
        TextView tvName;
        TextView tvCategory;
        TextView tvDescription;
        TextView tvMatchHighlight;
        TextView tvFloors;
        TextView tvRooms;
        TextView tvCampus;
        MaterialButton btnLocateMap;
        MaterialButton btnViewDetails;

        SearchCardViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBuildingImage = itemView.findViewById(R.id.iv_search_building_image);
            tvCode = itemView.findViewById(R.id.tv_search_building_code);
            tvName = itemView.findViewById(R.id.tv_search_building_name);
            tvCategory = itemView.findViewById(R.id.tv_search_building_category);
            tvDescription = itemView.findViewById(R.id.tv_search_building_description);
            tvMatchHighlight = itemView.findViewById(R.id.tv_search_match_highlight);
            tvFloors = itemView.findViewById(R.id.tv_search_stat_floors);
            tvRooms = itemView.findViewById(R.id.tv_search_stat_rooms);
            tvCampus = itemView.findViewById(R.id.tv_search_stat_campus);
            btnLocateMap = itemView.findViewById(R.id.btn_locate_map);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
        }
    }
}