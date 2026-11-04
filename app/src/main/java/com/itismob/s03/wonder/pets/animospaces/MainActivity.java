package com.itismob.s03.wonder.pets.animospaces;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itismob.s03.wonder.pets.animospaces.data.CampusRepository;
import com.itismob.s03.wonder.pets.animospaces.data.ReservationRepository;
import com.itismob.s03.wonder.pets.animospaces.data.UserManager;
import com.itismob.s03.wonder.pets.animospaces.map.MapboxManager;
import com.itismob.s03.wonder.pets.animospaces.model.Building;
import com.itismob.s03.wonder.pets.animospaces.model.Campus;
import com.itismob.s03.wonder.pets.animospaces.model.Room;
import com.itismob.s03.wonder.pets.animospaces.model.UserRole;
import com.itismob.s03.wonder.pets.animospaces.ui.admin.AdminActivity;
import com.itismob.s03.wonder.pets.animospaces.ui.adapter.BuildingAdapter;
import com.itismob.s03.wonder.pets.animospaces.ui.adapter.FloorAdapter;
import com.itismob.s03.wonder.pets.animospaces.ui.adapter.RoomAdapter;
import com.itismob.s03.wonder.pets.animospaces.util.BottomNavHelper;
import com.mapbox.maps.MapView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private MapboxManager mapboxManager;
    private final Campus campus = Campus.DLSU_MANILA;

    private BuildingAdapter buildingAdapter;
    private FloorAdapter mainFloorAdapter;
    private Building selectedBuilding;

    private CampusRepository campusRepository;
    private ReservationRepository reservationRepository;

    private EditText etSearch;
    private RecyclerView rvBuildingChips;
    private View includeFloorSelector;
    private RecyclerView rvVerticalFloorSelector;
    private FloatingActionButton fabRecenter;
    private MaterialButton btnAdminPortal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        campusRepository = CampusRepository.getInstance();
        reservationRepository = ReservationRepository.getInstance();
        reservationRepository.initNotificationHelper(this);

        initViews();
        setupSearch();
        setupBuildingChips();
        setupFloorSelector();
        setupMap();
        BottomNavHelper.setupBottomNav(this, 1); // Highlight Map Tab (Index 1)
        updateAdminButtonVisibility();
    }

    private void initViews() {
        mapView = findViewById(R.id.map_view);
        etSearch = findViewById(R.id.et_search);
        rvBuildingChips = findViewById(R.id.rv_building_chips);
        includeFloorSelector = findViewById(R.id.include_floor_selector);
        rvVerticalFloorSelector = findViewById(R.id.rv_vertical_floor_selector);
        fabRecenter = findViewById(R.id.fab_recenter);

        btnAdminPortal = findViewById(R.id.btn_admin_portal);

        fabRecenter.setOnClickListener(v -> {
            if (mapboxManager != null) {
                mapboxManager.moveCameraToCampus(campus);
            }
        });

        if (btnAdminPortal != null) {
            btnAdminPortal.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AdminActivity.class)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavHelper.setupBottomNav(this, 1);
        updateAdminButtonVisibility();
        if (mapboxManager != null) {
            updateBuildingsForCampus();
        }
    }

    private void updateAdminButtonVisibility() {
        UserManager user = UserManager.getInstance();
        boolean isAdmin = user.isAdmin();

        // Admin Portal button is ONLY visible for Admins on the map if needed, otherwise hidden since Tab 2 handles Approvals
        if (btnAdminPortal != null) {
            btnAdminPortal.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        }
    }

    private void setupMap() {
        mapboxManager = new MapboxManager(mapView, this);
        mapboxManager.moveCameraToCampus(campus);
        updateBuildingsForCampus();

        mapboxManager.setOnBuildingClickListener(building -> selectBuilding(building, true));
    }

    private void updateBuildingsForCampus() {
        List<Building> buildings = campusRepository.getAllBuildings();
        buildingAdapter.setBuildings(buildings);
        if (mapboxManager != null) {
            mapboxManager.displayBuildingMarkers(buildings);
        }
    }

    private void setupBuildingChips() {
        rvBuildingChips.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        buildingAdapter = new BuildingAdapter(building -> selectBuilding(building, true));
        rvBuildingChips.setAdapter(buildingAdapter);
    }

    private void setupFloorSelector() {
        mainFloorAdapter = new FloorAdapter(floorNumber -> {
            if (selectedBuilding != null) {
                showBuildingDetailsBottomSheet(selectedBuilding, floorNumber);
            }
        });
        rvVerticalFloorSelector.setAdapter(mainFloorAdapter);
        hideFloorSelector();
    }

    private void hideFloorSelector() {
        includeFloorSelector.setVisibility(View.GONE);
    }

    private void showFloorSelectorForBuilding(Building building) {
        if (building.getAvailableFloors() != null && !building.getAvailableFloors().isEmpty()) {
            includeFloorSelector.setVisibility(View.VISIBLE);
            mainFloorAdapter.setFloors(building.getAvailableFloors());
        } else {
            hideFloorSelector();
        }
    }

    private void selectBuilding(Building building, boolean openBottomSheet) {
        this.selectedBuilding = building;
        buildingAdapter.selectBuilding(building);
        showFloorSelectorForBuilding(building);

        if (mapboxManager != null) {
            mapboxManager.moveCameraToBuilding(building);
        }

        if (openBottomSheet) {
            int defaultFloor = (building.getAvailableFloors() != null && !building.getAvailableFloors().isEmpty())
                    ? building.getAvailableFloors().get(0) : 1;
            showBuildingDetailsBottomSheet(building, defaultFloor);
        }
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                List<Building> filtered = campusRepository.searchBuildings(query);
                buildingAdapter.setBuildings(filtered);
                if (mapboxManager != null) {
                    mapboxManager.displayBuildingMarkers(filtered);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void showBuildingDetailsBottomSheet(Building building, int initialFloor) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.layout_room_details_bottom_sheet, null);
        dialog.setContentView(dialogView);

        TextView tvName = dialogView.findViewById(R.id.tv_bs_building_name);
        TextView tvCode = dialogView.findViewById(R.id.tv_bs_building_code);
        TextView tvDescription = dialogView.findViewById(R.id.tv_bs_building_description);
        RecyclerView rvFloors = dialogView.findViewById(R.id.rv_floor_selector);
        ChipGroup chipGroupAvailability = dialogView.findViewById(R.id.chip_group_availability);
        RecyclerView rvRooms = dialogView.findViewById(R.id.rv_rooms);
        TextView tvEmptyRooms = dialogView.findViewById(R.id.tv_empty_rooms);

        tvName.setText(building.getName());
        tvCode.setText(building.getCode());
        tvDescription.setText(building.getDescription());

        // Floor selector inside bottom sheet
        FloorAdapter bsFloorAdapter = new FloorAdapter(floorNumber -> {
            loadRoomsForBottomSheet(building.getId(), floorNumber, chipGroupAvailability.getCheckedChipId(), rvRooms, tvEmptyRooms);
        });
        rvFloors.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFloors.setAdapter(bsFloorAdapter);
        bsFloorAdapter.setFloors(building.getAvailableFloors());

        // Room adapter
        RoomAdapter roomAdapter = new RoomAdapter(room -> {
            if (!UserManager.getInstance().canReserve()) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Reservation Restricted")
                        .setMessage("Under DLSU Role-Based Access Control (RBAC) policy, Regular Students may only VIEW venue availability. Room reservation requests are reserved for Faculty and Student Organizations.")
                        .setPositiveButton("Got it", null)
                        .show();
            } else {
                dialog.dismiss();
                showReservationFormDialog(room);
            }
        });
        rvRooms.setLayoutManager(new LinearLayoutManager(this));
        rvRooms.setAdapter(roomAdapter);

        chipGroupAvailability.setOnCheckedStateChangeListener((group, checkedIds) -> {
            int checkedId = checkedIds.isEmpty() ? R.id.chip_filter_all : checkedIds.get(0);
            int currentFloor = bsFloorAdapter.getSelectedFloor();
            loadRoomsForBottomSheet(building.getId(), currentFloor, checkedId, rvRooms, tvEmptyRooms);
        });

        loadRoomsForBottomSheet(building.getId(), initialFloor, R.id.chip_filter_all, rvRooms, tvEmptyRooms);

        dialog.show();
    }

    private void loadRoomsForBottomSheet(String buildingId, int floorLevel, int filterChipId, RecyclerView rvRooms, TextView tvEmptyRooms) {
        List<Room> allRooms = campusRepository.getRoomsForBuildingAndFloor(buildingId, floorLevel);
        List<Room> filtered = new ArrayList<>();

        for (Room r : allRooms) {
            if (filterChipId == R.id.chip_filter_available) {
                if (r.isAvailable()) filtered.add(r);
            } else if (filterChipId == R.id.chip_filter_reserved) {
                if (!r.isAvailable()) filtered.add(r);
            } else {
                filtered.add(r);
            }
        }

        RoomAdapter adapter = (RoomAdapter) rvRooms.getAdapter();
        if (adapter != null) {
            adapter.setRooms(filtered);
        }

        if (filtered.isEmpty()) {
            rvRooms.setVisibility(View.GONE);
            tvEmptyRooms.setVisibility(View.VISIBLE);
        } else {
            rvRooms.setVisibility(View.VISIBLE);
            tvEmptyRooms.setVisibility(View.GONE);
        }
    }

    private void showReservationFormDialog(Room room) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_reservation_form, null);
        dialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tv_form_title);
        TextView tvRoleBadge = view.findViewById(R.id.tv_form_role_badge);
        EditText etTitle = view.findViewById(R.id.et_event_title);
        EditText etPurpose = view.findViewById(R.id.et_event_purpose);
        EditText etDate = view.findViewById(R.id.et_date);
        EditText etTimeSlot = view.findViewById(R.id.et_time_slot);
        EditText etAttendees = view.findViewById(R.id.et_attendees);
        MaterialButton btnSubmit = view.findViewById(R.id.btn_submit_request);

        UserRole activeRole = UserManager.getInstance().getUserRole();
        tvTitle.setText("Reserve " + room.getRoomCode() + " (" + room.getBuildingName() + ")");
        tvRoleBadge.setText("Submitting as: " + UserManager.getInstance().getName() + " (" + (activeRole != null ? activeRole.getDisplayName() : "Org/Faculty") + ")");

        btnSubmit.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String purpose = etPurpose.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String time = etTimeSlot.getText().toString().trim();
            int count = 30;
            try {
                count = Integer.parseInt(etAttendees.getText().toString().trim());
            } catch (Exception ignored) {}

            if (title.isEmpty() || purpose.isEmpty()) {
                Toast.makeText(this, "Please enter event title and purpose.", Toast.LENGTH_SHORT).show();
                return;
            }

            reservationRepository.submitReservation(
                    room,
                    UserManager.getInstance().getName(),
                    UserManager.getInstance().getUserRole(),
                    title,
                    purpose,
                    date,
                    time,
                    count
            );

            dialog.dismiss();

            Toast.makeText(this, "Reservation request submitted for " + room.getRoomCode() + "! Awaiting Admin approval.", Toast.LENGTH_LONG).show();
        });

        dialog.show();
    }
}