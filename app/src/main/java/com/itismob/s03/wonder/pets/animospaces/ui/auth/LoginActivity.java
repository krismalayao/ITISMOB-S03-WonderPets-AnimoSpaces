package com.itismob.s03.wonder.pets.animospaces.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.data.UserManager;
import com.itismob.s03.wonder.pets.animospaces.model.UserRole;
import com.itismob.s03.wonder.pets.animospaces.ui.home.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Spinner spinnerDemoAccount;
    private EditText etEmail;
    private EditText etPassword;

    private final String[] presetEmails = {
            "student@dlsu.edu.ph",
            "csorg@dlsu.edu.ph",
            "faculty@dlsu.edu.ph",
            "admin@dlsu.edu.ph"
    };

    private final UserRole[] presetRoles = {
            UserRole.REGULAR_STUDENT,
            UserRole.STUDENT_ORGANIZATION,
            UserRole.FACULTY,
            UserRole.ADMIN
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        View rootLayout = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerDemoAccount = findViewById(R.id.spinner_demo_account);
        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        MaterialButton btnLogin = findViewById(R.id.btn_login);
        TextView tvGotoSignup = findViewById(R.id.tv_goto_signup);

        setupDemoSpinner();

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!email.endsWith("@dlsu.edu.ph")) {
                Toast.makeText(this, "Please enter a valid DLSU institutional email (@dlsu.edu.ph).", Toast.LENGTH_LONG).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Assign User Session
            int selectedIndex = spinnerDemoAccount.getSelectedItemPosition();
            UserRole activeRole = presetRoles[selectedIndex];
            UserManager.getInstance().loginPreset(activeRole);

            Toast.makeText(this, "Welcome back! Logged in as " + activeRole.getDisplayName() + ".", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        if (tvGotoSignup != null) {
            tvGotoSignup.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupDemoSpinner() {
        List<String> options = new ArrayList<>();
        options.add("Regular Student (student@dlsu.edu.ph)");
        options.add("Student Organization (csorg@dlsu.edu.ph)");
        options.add("Faculty Member (faculty@dlsu.edu.ph)");
        options.add("Administrator (admin@dlsu.edu.ph)");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDemoAccount.setAdapter(adapter);

        spinnerDemoAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                etEmail.setText(presetEmails[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}