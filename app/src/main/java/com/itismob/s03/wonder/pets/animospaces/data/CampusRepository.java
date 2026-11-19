package com.itismob.s03.wonder.pets.animospaces.data;

import com.itismob.s03.wonder.pets.animospaces.model.Building;
import com.itismob.s03.wonder.pets.animospaces.model.Campus;
import com.itismob.s03.wonder.pets.animospaces.model.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CampusRepository {

    private static CampusRepository instance;
    private final List<Building> buildings = new ArrayList<>();
    private final List<Room> rooms = new ArrayList<>();

    private CampusRepository() {
        initData();
    }

    public static synchronized CampusRepository getInstance() {
        if (instance == null) {
            instance = new CampusRepository();
        }
        return instance;
    }

    private static List<Integer> generateFloorList(int totalFloors) {
        List<Integer> floorList = new ArrayList<>();
        for (int i = 1; i <= totalFloors; i++) {
            floorList.add(i);
        }
        return floorList;
    }

    private void initData() {
        // --- DLSU MANILA BUILDINGS WITH PRECISE FOOTPRINT COORDINATES ---

        // 1. St. La Salle Hall (LS): 3 floors (North Taft Ave flagship)
        Building laSalle = new Building(
                "bld_lasalle", "St. La Salle Hall", "LS", Campus.DLSU_MANILA,
                14.5658, 120.9930,
                generateFloorList(3),
                "Historic flagship building featuring central administrative offices, chapel, and classical lecture rooms.",
                "Administrative / Academic"
        );

        // 2. St. Joseph Hall (SJ): 3 floors (East quadrangle)
        Building stJoseph = new Building(
                "bld_sj", "St. Joseph Hall", "SJ", Campus.DLSU_MANILA,
                14.5652, 120.9934,
                generateFloorList(3),
                "Academic building housing science classrooms, psychology laboratories, and department suites.",
                "Academic / Lab"
        );

        // 3. Henry Sy Sr. Hall (HENRY_SY): 15 floors (Central Learning Commons / Library)
        Building henrySy = new Building(
                "bld_henrysy", "Henry Sy Sr. Hall", "HENRY_SY", Campus.DLSU_MANILA,
                14.5649, 120.9932,
                generateFloorList(15),
                "Central Learning Commons, university library, innovation spaces, research centers, and executive offices.",
                "Library / Academic"
        );

        // 4. Yuchengco Hall (YUCHENGCO): 9 floors (Fronting Freedom Park)
        Building yuchengco = new Building(
                "bld_yuchengco", "Yuchengco Hall", "YUCHENGCO", Campus.DLSU_MANILA,
                14.5644, 120.9938,
                generateFloorList(9),
                "Rizal Auditorium, College of Liberal Arts classrooms, museum, and executive conference suites.",
                "Auditorium / Academic"
        );

        // 5. Miguel Hall (MIGUEL): 4 floors (South of Yuchengco)
        Building miguel = new Building(
                "bld_miguel", "Miguel Hall", "MIGUEL", Campus.DLSU_MANILA,
                14.5640, 120.9937,
                generateFloorList(4),
                "Humanities classrooms, student organization hubs, and faculty consultation offices.",
                "Academic"
        );

        // 6. Velasco Hall (VELASCO): 5 floors (Engineering labs)
        Building velasco = new Building(
                "bld_velasco", "Velasco Hall", "VELASCO", Campus.DLSU_MANILA,
                14.5636, 120.9934,
                generateFloorList(5),
                "Gokongwei College of Engineering (GCOE) building with engineering laboratories, workshops, and classrooms.",
                "Engineering Lab"
        );

        // 7. Gokongwei Hall (GOKONGWEI): 4 floors (CCS building)
        Building gokongwei = new Building(
                "bld_gokongwei", "Gokongwei Hall", "GOKONGWEI", Campus.DLSU_MANILA,
                14.5643, 120.9926,
                generateFloorList(4),
                "College of Computer Studies (CCS) headquarters, advanced computer laboratories, and research centers.",
                "Laboratory / Academic"
        );

        // 8. Mutien Marie Hall (MUTIEN_MARIE): 12 floors (South-west facility)
        Building mutienMarie = new Building(
                "bld_mutien", "Mutien Marie Hall", "MUTIEN_MARIE", Campus.DLSU_MANILA,
                14.5637, 120.9927,
                generateFloorList(12),
                "Modern multi-storey facility featuring state-of-the-art interactive classrooms, offices, and study spaces.",
                "Academic"
        );

        // 9. Andrew Gonzalez Hall (ANDREW): 20 floors (Estrada corner)
        Building andrew = new Building(
                "bld_andrew", "Andrew Gonzalez Hall", "ANDREW", Campus.DLSU_MANILA,
                14.5639, 120.9930,
                generateFloorList(20),
                "20-storey multi-purpose academic and administrative building housing high-capacity classrooms and multi-media halls.",
                "Academic"
        );

        // 10. Enrique M. Razon Sports Complex (RAZON): 10 floors (Sports complex)
        Building razon = new Building(
                "bld_razon", "Enrique M. Razon Sports Complex", "RAZON", Campus.DLSU_MANILA,
                14.5633, 120.9931,
                generateFloorList(10),
                "10-storey sports facility featuring basketball courts, Olympic pool, martial arts gym, and athletic training centers.",
                "Sports Complex"
        );

        buildings.addAll(Arrays.asList(
                laSalle, stJoseph, henrySy, yuchengco, miguel, velasco, gokongwei, mutienMarie, andrew, razon
        ));

        // --- SAMPLE ROOMS ---
        List<String> standardAmenities = Arrays.asList("Projector", "Aircon", "Whiteboard", "High-speed Wi-Fi");
        List<String> labAmenities = Arrays.asList("Desktop PCs", "Aircon", "Whiteboard", "Dual Monitors", "LAN");
        List<String> hallAmenities = Arrays.asList("Sound System", "Stage Lighting", "Projector", "Podium", "Aircon");

        // Andrew Rooms (20 Floors)
        rooms.add(new Room("r_a801", "bld_andrew", "Andrew Gonzalez Hall", "A0801", 8, 45, "Classroom", true, "Available - Free until 4:00 PM", standardAmenities));
        rooms.add(new Room("r_a802", "bld_andrew", "Andrew Gonzalez Hall", "A0802", 8, 45, "Classroom", false, "Reserved: CSORG General Assembly", standardAmenities));
        rooms.add(new Room("r_a901", "bld_andrew", "Andrew Gonzalez Hall", "A0901", 9, 50, "Classroom", true, "Available", standardAmenities));
        rooms.add(new Room("r_a2001", "bld_andrew", "Andrew Gonzalez Hall", "A2001", 20, 60, "Multipurpose Hall", true, "Available High Floor Suite", hallAmenities));

        // Henry Sy Rooms (15 Floors)
        rooms.add(new Room("r_h401", "bld_henrysy", "Henry Sy Sr. Hall", "H401", 4, 60, "Learning Commons", true, "Available Open Study Area", Arrays.asList("Power Outlets", "Aircon", "Ergonomic Chairs")));
        rooms.add(new Room("r_h501", "bld_henrysy", "Henry Sy Sr. Hall", "H501", 5, 20, "Discussion Room", false, "Reserved: Thesis Group 4 (2:00 PM - 4:00 PM)", Arrays.asList("Display Screen", "Whiteboard", "Aircon")));
        rooms.add(new Room("r_h1501", "bld_henrysy", "Henry Sy Sr. Hall", "H1501", 15, 30, "Executive Conference Room", true, "Available", hallAmenities));

        // Gokongwei Rooms (4 Floors)
        rooms.add(new Room("r_g301", "bld_gokongwei", "Gokongwei Hall", "G301", 3, 35, "Computer Lab", false, "Reserved: Software Engineering Lab", labAmenities));
        rooms.add(new Room("r_g302", "bld_gokongwei", "Gokongwei Hall", "G302", 3, 35, "Computer Lab", true, "Available - Free until 5:00 PM", labAmenities));
        rooms.add(new Room("r_g401", "bld_gokongwei", "Gokongwei Hall", "G401", 4, 30, "Discussion Room", true, "Available", standardAmenities));

        // St. La Salle Hall Rooms (3 Floors)
        rooms.add(new Room("r_ls101", "bld_lasalle", "St. La Salle Hall", "LS101", 1, 40, "Lecture Room", true, "Available", standardAmenities));
        rooms.add(new Room("r_ls201", "bld_lasalle", "St. La Salle Hall", "LS201", 2, 35, "Conference Suite", false, "Reserved: Faculty Meeting", standardAmenities));

        // St. Joseph Hall Rooms (3 Floors)
        rooms.add(new Room("r_sj101", "bld_sj", "St. Joseph Hall", "SJ101", 1, 40, "Psychology Lab", true, "Available", labAmenities));
        rooms.add(new Room("r_sj201", "bld_sj", "St. Joseph Hall", "SJ201", 2, 45, "Classroom", true, "Available", standardAmenities));

        // Yuchengco Rooms (9 Floors)
        rooms.add(new Room("r_y501", "bld_yuchengco", "Yuchengco Hall", "Y501", 5, 500, "Auditorium", false, "Reserved: General Assembly", hallAmenities));
        rooms.add(new Room("r_y401", "bld_yuchengco", "Yuchengco Hall", "Y401", 4, 45, "Classroom", true, "Available", standardAmenities));

        // Velasco Rooms (5 Floors)
        rooms.add(new Room("r_v201", "bld_velasco", "Velasco Hall", "V201", 2, 40, "Engineering Lab", true, "Available", Arrays.asList("Oscilloscopes", "Projector", "Aircon")));
        rooms.add(new Room("r_v301", "bld_velasco", "Velasco Hall", "V301", 3, 45, "Classroom", false, "Reserved: Fluid Mechanics Lecture", standardAmenities));

        // Mutien Marie Rooms (12 Floors)
        rooms.add(new Room("r_m101", "bld_mutien", "Mutien Marie Hall", "M0101", 1, 50, "Interactive Classroom", true, "Available", standardAmenities));
        rooms.add(new Room("r_m601", "bld_mutien", "Mutien Marie Hall", "M0601", 6, 45, "Classroom", true, "Available", standardAmenities));

        // Razon Rooms (10 Floors)
        rooms.add(new Room("r_razon901", "bld_razon", "Razon Sports Complex", "R0901", 9, 200, "Basketball Court", false, "Reserved: Varsity Practice", hallAmenities));
    }

    public List<Building> getAllBuildings() {
        return new ArrayList<>(buildings);
    }

    public Building getBuildingById(String buildingId) {
        for (Building b : buildings) {
            if (b.getId().equalsIgnoreCase(buildingId)) {
                return b;
            }
        }
        return null;
    }

    public List<Room> getRoomsForBuildingAndFloor(String buildingId, int floorLevel) {
        List<Room> result = new ArrayList<>();
        for (Room r : rooms) {
            if (r.getBuildingId().equalsIgnoreCase(buildingId) && r.getFloorLevel() == floorLevel) {
                result.add(r);
            }
        }
        return result;
    }

    public List<Room> getRoomsForBuilding(String buildingId) {
        List<Room> result = new ArrayList<>();
        for (Room r : rooms) {
            if (r.getBuildingId().equalsIgnoreCase(buildingId)) {
                result.add(r);
            }
        }
        return result;
    }

    public int getAvailableRoomsCount(String buildingId) {
        int count = 0;
        for (Room r : rooms) {
            if (r.getBuildingId().equalsIgnoreCase(buildingId) && r.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    public List<Building> searchBuildings(String query) {
        return searchBuildings(query, "All");
    }

    public List<Building> searchBuildings(String query, String categoryFilter) {
        String q = (query == null) ? "" : query.toLowerCase().trim();
        String cat = (categoryFilter == null) ? "All" : categoryFilter.trim();

        List<Building> result = new ArrayList<>();
        for (Building b : buildings) {
            // Category check
            boolean matchesCategory = true;
            if (!cat.equalsIgnoreCase("All") && !cat.isEmpty()) {
                String buildingCat = b.getCategory().toLowerCase();
                String filterCat = cat.toLowerCase();
                matchesCategory = buildingCat.contains(filterCat) ||
                        (filterCat.contains("lab") && buildingCat.contains("lab")) ||
                        (filterCat.contains("academic") && buildingCat.contains("academic")) ||
                        (filterCat.contains("library") && buildingCat.contains("library")) ||
                        (filterCat.contains("sports") && buildingCat.contains("sports")) ||
                        (filterCat.contains("auditorium") && buildingCat.contains("auditorium"));
            }

            if (!matchesCategory) continue;

            if (q.isEmpty()) {
                result.add(b);
                continue;
            }

            // Text Query matching against Building fields
            boolean matchesText = b.getName().toLowerCase().contains(q) ||
                    b.getCode().toLowerCase().contains(q) ||
                    b.getCategory().toLowerCase().contains(q) ||
                    b.getDescription().toLowerCase().contains(q);

            // Check if any room inside this building matches query
            if (!matchesText) {
                for (Room r : getRoomsForBuilding(b.getId())) {
                    if (r.getRoomCode().toLowerCase().contains(q) ||
                        r.getRoomType().toLowerCase().contains(q) ||
                        r.getScheduleSummary().toLowerCase().contains(q)) {
                        matchesText = true;
                        break;
                    }
                    if (r.getAmenities() != null) {
                        for (String am : r.getAmenities()) {
                            if (am.toLowerCase().contains(q)) {
                                matchesText = true;
                                break;
                            }
                        }
                    }
                    if (matchesText) break;
                }
            }

            if (matchesText) {
                result.add(b);
            }
        }
        return result;
    }

    public String getMatchingRoomHighlight(Building building, String query) {
        if (query == null || query.trim().isEmpty() || building == null) {
            return null;
        }
        String q = query.toLowerCase().trim();
        for (Room r : getRoomsForBuilding(building.getId())) {
            if (r.getRoomCode().toLowerCase().contains(q)) {
                return "Matches Room " + r.getRoomCode() + " (" + r.getRoomType() + ")";
            }
            if (r.getRoomType().toLowerCase().contains(q)) {
                return "Matches Room Type: " + r.getRoomType() + " (" + r.getRoomCode() + ")";
            }
            if (r.getAmenities() != null) {
                for (String am : r.getAmenities()) {
                    if (am.toLowerCase().contains(q)) {
                        return "Matches Amenity: " + am + " in " + r.getRoomCode();
                    }
                }
            }
        }
        return null;
    }
}