package com.itismob.s03.wonder.pets.animospaces.data;

import com.itismob.s03.wonder.pets.animospaces.model.UserRole;

import java.util.HashMap;
import java.util.Map;

public class UserManager {

    private static UserManager instance;

    private String firstName;
    private String lastName;
    private String idNumber;
    private String email;
    private UserRole userRole;
    private String profileImageUri;

    // Persist custom profile details per role across logouts
    private final Map<UserRole, CustomProfile> savedProfiles = new HashMap<>();

    private static class CustomProfile {
        String firstName;
        String lastName;
        String email;
        String idNumber;
        String profileImageUri;

        CustomProfile(String firstName, String lastName, String email, String idNumber, String profileImageUri) {
            this.firstName = firstName;
            this.lastName = lastName;
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

    public void setSession(String firstName, String lastName, String idNumber, String email, UserRole role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.idNumber = idNumber;
        this.email = email;
        this.userRole = role;

        // Save/update in persistent role cache
        savedProfiles.put(role, new CustomProfile(firstName, lastName, email, idNumber, profileImageUri));
    }

    public void setSession(String fullName, String idNumber, String email, UserRole role) {
        String[] parts = splitName(fullName);
        setSession(parts[0], parts[1], idNumber, email, role);
    }

    private String[] splitName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return new String[]{"User", ""};
        }
        String trimmed = fullName.trim();
        int firstSpace = trimmed.indexOf(' ');
        if (firstSpace == -1) {
            return new String[]{trimmed, ""};
        }
        return new String[]{trimmed.substring(0, firstSpace), trimmed.substring(firstSpace + 1).trim()};
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
            this.firstName = custom.firstName;
            this.lastName = custom.lastName;
            this.email = emailLower; // Keep entered email or custom email
            this.idNumber = custom.idNumber;
            this.userRole = targetRole;
            this.profileImageUri = custom.profileImageUri;
        } else {
            if (targetRole == UserRole.ADMIN) {
                setSession("Francis", "Reyes", "12299999", emailLower, UserRole.ADMIN);
            } else if (targetRole == UserRole.FACULTY) {
                setSession("Kristopher", "Malayao", "12233333", emailLower, UserRole.FACULTY);
            } else if (targetRole == UserRole.STUDENT_ORGANIZATION) {
                setSession("Computer Studies Government (CSORG)", "", "12222222", emailLower, UserRole.STUDENT_ORGANIZATION);
            } else {
                String first = "Juan";
                String last = "Dela Cruz";
                if (emailLower.contains("@")) {
                    String prefix = emailLower.split("@")[0].replace("_", " ").replace(".", " ");
                    if (!prefix.trim().isEmpty()) {
                        String[] parts = splitName(capitalizeWords(prefix));
                        first = parts[0];
                        last = parts[1];
                    }
                }
                setSession(first, last, "12211111", emailLower, UserRole.REGULAR_STUDENT);
            }
        }
    }

    public void loginPreset(UserRole role) {
        CustomProfile custom = savedProfiles.get(role);
        if (custom != null) {
            this.firstName = custom.firstName;
            this.lastName = custom.lastName;
            this.email = custom.email;
            this.idNumber = custom.idNumber;
            this.userRole = role;
            this.profileImageUri = custom.profileImageUri;
            return;
        }

        if (role == UserRole.REGULAR_STUDENT) {
            setSession("Juan", "Dela Cruz", "12211111", "student@dlsu.edu.ph", UserRole.REGULAR_STUDENT);
        } else if (role == UserRole.STUDENT_ORGANIZATION) {
            setSession("Computer Studies Government (CSORG)", "", "12222222", "csorg@dlsu.edu.ph", UserRole.STUDENT_ORGANIZATION);
        } else if (role == UserRole.FACULTY) {
            setSession("Kristopher", "Malayao", "12233333", "faculty@dlsu.edu.ph", UserRole.FACULTY);
        } else if (role == UserRole.ADMIN) {
            setSession("Francis", "Reyes", "12299999", "admin@dlsu.edu.ph", UserRole.ADMIN);
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
        if (lastName == null || lastName.isEmpty()) {
            return getFirstName();
        }
        return getFirstName() + " " + lastName;
    }

    public String getFirstName() {
        if (firstName == null || firstName.isEmpty()) return "User";
        if (lastName == null || lastName.isEmpty()) return firstName;
        return firstName;
    }

    public String getLastName() {
        return lastName != null ? lastName : "";
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateCustomProfile();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateCustomProfile();
    }

    public void setName(String fullName) {
        String[] parts = splitName(fullName);
        this.firstName = parts[0];
        this.lastName = parts[1];
        updateCustomProfile();
    }

    public void setEmail(String email) {
        this.email = email;
        updateCustomProfile();
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
        updateCustomProfile();
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
        updateCustomProfile();
    }

    private void updateCustomProfile() {
        if (userRole != null) {
            CustomProfile custom = savedProfiles.get(userRole);
            if (custom != null) {
                custom.firstName = this.firstName;
                custom.lastName = this.lastName;
                custom.email = this.email;
                custom.idNumber = this.idNumber;
                custom.profileImageUri = this.profileImageUri;
            } else {
                savedProfiles.put(userRole, new CustomProfile(firstName, lastName, email, idNumber, profileImageUri));
            }
        }
    }
}
