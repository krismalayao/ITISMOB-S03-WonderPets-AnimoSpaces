package com.itismob.s03.wonder.pets.animospaces.data;

import com.itismob.s03.wonder.pets.animospaces.model.UserRole;

import java.util.HashMap;
import java.util.Map;

public class UserManager {

    private static UserManager instance;

    private String name;
    private String idNumber;
    private String email;
    private UserRole userRole;
    private String profileImageUri;

    // Persist custom profile details per role across logouts
    private final Map<UserRole, CustomProfile> savedProfiles = new HashMap<>();

    private static class CustomProfile {
        String name;
        String email;
        String idNumber;
        String profileImageUri;

        CustomProfile(String name, String email, String idNumber, String profileImageUri) {
            this.name = name;
            this.email = email;
            this.idNumber = idNumber;
            this.profileImageUri = profileImageUri;
        }
    }

    private UserManager() {
        // Default session: Regular Student
        loginPreset(UserRole.REGULAR_STUDENT);
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void setSession(String name, String idNumber, String email, UserRole role) {
        this.name = name;
        this.idNumber = idNumber;
        this.email = email;
        this.userRole = role;

        // Save/update in persistent role cache
        savedProfiles.put(role, new CustomProfile(name, email, idNumber, profileImageUri));
    }

    public void loginByEmail(String inputEmail) {
        String emailLower = inputEmail.toLowerCase().trim();
        UserRole targetRole;
        if (emailLower.contains("admin")) {
            targetRole = UserRole.ADMIN;
        } else if (emailLower.contains("faculty") || emailLower.contains("prof")) {
            targetRole = UserRole.FACULTY;
        } else if (emailLower.contains("csorg") || emailLower.contains("org")) {
            targetRole = UserRole.STUDENT_ORGANIZATION;
        } else {
            targetRole = UserRole.REGULAR_STUDENT;
        }

        // Check if we have a saved custom profile for this role
        CustomProfile custom = savedProfiles.get(targetRole);
        if (custom != null) {
            this.name = custom.name;
            this.email = emailLower; // Keep entered email or custom email
            this.idNumber = custom.idNumber;
            this.userRole = targetRole;
            this.profileImageUri = custom.profileImageUri;
        } else {
            if (targetRole == UserRole.ADMIN) {
                setSession("Francis Reyes (Admin)", "12299999", emailLower, UserRole.ADMIN);
            } else if (targetRole == UserRole.FACULTY) {
                setSession("Prof. Kristopher Malayao", "12233333", emailLower, UserRole.FACULTY);
            } else if (targetRole == UserRole.STUDENT_ORGANIZATION) {
                setSession("Angelyn Chua (CSORG)", "12222222", emailLower, UserRole.STUDENT_ORGANIZATION);
            } else {
                String nameFromEmail = "Juan Dela Cruz";
                if (emailLower.contains("@")) {
                    String prefix = emailLower.split("@")[0].replace("_", " ").replace(".", " ");
                    if (!prefix.trim().isEmpty()) {
                        nameFromEmail = capitalizeWords(prefix);
                    }
                }
                setSession(nameFromEmail, "12211111", emailLower, UserRole.REGULAR_STUDENT);
            }
        }
    }

    public void loginPreset(UserRole role) {
        CustomProfile custom = savedProfiles.get(role);
        if (custom != null) {
            this.name = custom.name;
            this.email = custom.email;
            this.idNumber = custom.idNumber;
            this.userRole = role;
            this.profileImageUri = custom.profileImageUri;
            return;
        }

        if (role == UserRole.REGULAR_STUDENT) {
            setSession("Juan Dela Cruz", "12211111", "student@dlsu.edu.ph", UserRole.REGULAR_STUDENT);
        } else if (role == UserRole.STUDENT_ORGANIZATION) {
            setSession("Angelyn Chua (CSORG)", "12222222", "csorg@dlsu.edu.ph", UserRole.STUDENT_ORGANIZATION);
        } else if (role == UserRole.FACULTY) {
            setSession("Prof. Kristopher Malayao", "12233333", "faculty@dlsu.edu.ph", UserRole.FACULTY);
        } else if (role == UserRole.ADMIN) {
            setSession("Francis Reyes (Admin)", "12299999", "admin@dlsu.edu.ph", UserRole.ADMIN);
        }
    }

    private String capitalizeWords(String str) {
        String[] words = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.trim().isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0)))
                  .append(w.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        if (name == null || name.isEmpty()) return "User";
        String[] parts = name.split(" ");
        return parts[0];
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public boolean canReserve() {
        return userRole != null && userRole.canReserve();
    }

    public boolean isAdmin() {
        return userRole != null && userRole.isAdmin();
    }

    public void setName(String name) {
        this.name = name;
        if (userRole != null) {
            CustomProfile custom = savedProfiles.get(userRole);
            if (custom != null) {
                custom.name = name;
            } else {
                savedProfiles.put(userRole, new CustomProfile(name, email, idNumber, profileImageUri));
            }
        }
    }

    public void setEmail(String email) {
        this.email = email;
        if (userRole != null) {
            CustomProfile custom = savedProfiles.get(userRole);
            if (custom != null) {
                custom.email = email;
            } else {
                savedProfiles.put(userRole, new CustomProfile(name, email, idNumber, profileImageUri));
            }
        }
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
        if (userRole != null) {
            CustomProfile custom = savedProfiles.get(userRole);
            if (custom != null) {
                custom.idNumber = idNumber;
            } else {
                savedProfiles.put(userRole, new CustomProfile(name, email, idNumber, profileImageUri));
            }
        }
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
        if (userRole != null) {
            CustomProfile custom = savedProfiles.get(userRole);
            if (custom != null) {
                custom.profileImageUri = profileImageUri;
            } else {
                savedProfiles.put(userRole, new CustomProfile(name, email, idNumber, profileImageUri));
            }
        }
    }
}
