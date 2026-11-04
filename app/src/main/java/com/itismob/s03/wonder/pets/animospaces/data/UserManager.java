package com.itismob.s03.wonder.pets.animospaces.data;

import com.itismob.s03.wonder.pets.animospaces.model.UserRole;

public class UserManager {

    private static UserManager instance;

    private String name;
    private String idNumber;
    private String email;
    private UserRole userRole;

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
    }

    public void loginByEmail(String inputEmail) {
        String emailLower = inputEmail.toLowerCase().trim();
        if (emailLower.contains("admin")) {
            setSession("Francis Reyes (Admin)", "12299999", emailLower, UserRole.ADMIN);
        } else if (emailLower.contains("faculty") || emailLower.contains("prof")) {
            setSession("Prof. Kristopher Malayao", "12233333", emailLower, UserRole.FACULTY);
        } else if (emailLower.contains("csorg") || emailLower.contains("org")) {
            setSession("Angelyn Chua (CSORG)", "12222222", emailLower, UserRole.STUDENT_ORGANIZATION);
        } else {
            // Default Regular Student
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

    public void loginPreset(UserRole role) {
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
}