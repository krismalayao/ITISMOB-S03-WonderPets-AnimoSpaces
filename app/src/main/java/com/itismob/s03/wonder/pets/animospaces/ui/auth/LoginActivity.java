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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;
import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.data.FirebaseAuthManager;
import com.itismob.s03.wonder.pets.animospaces.data.UserManager;
import com.itismob.s03.wonder.pets.animospaces.model.UserRole;
import com.itismob.s03.wonder.pets.animospaces.ui.home.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Spinner spinnerDemoAccount;
    private EditText etEmail;
    private EditText etPassword;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

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
        MaterialButton btnGoogleSignIn = findViewById(R.id.btn_google_sign_in);
        TextView tvGotoSignup = findViewById(R.id.tv_goto_signup);

        setupDemoSpinner();

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) {
                                handleGoogleSignInAccount(account);
                            }
                        } catch (ApiException e) {
                            Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        if (btnGoogleSignIn != null) {
            btnGoogleSignIn.setOnClickListener(v -> {
                Intent signInIntent = FirebaseAuthManager.getInstance().getGoogleSignInClient(this).getSignInIntent();
                googleSignInLauncher.launch(signInIntent);
            });
        }

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Assign User Session
            int selectedIndex = spinnerDemoAccount.getSelectedItemPosition();
            UserRole activeRole = presetRoles[selectedIndex];
            UserManager.getInstance().loginByEmail(email);

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

    private void handleGoogleSignInAccount(GoogleSignInAccount account) {
        FirebaseAuthManager.getInstance().firebaseAuthWithGoogle(account, new FirebaseAuthManager.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user, UserRole role) {
                Toast.makeText(LoginActivity.this, "Welcome " + UserManager.getInstance().getFirstName() + "! Logged in as " + role.getDisplayName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
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