package com.itismob.s03.wonder.pets.animospaces.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class RegisterActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> googleSignUpLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        View rootLayout = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnBack = findViewById(R.id.btn_register_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        EditText etEmail = findViewById(R.id.et_reg_email);
        EditText etPassword = findViewById(R.id.et_reg_password);
        EditText etConfirmPassword = findViewById(R.id.et_reg_confirm_password);
        MaterialButton btnRegister = findViewById(R.id.btn_register);
        MaterialButton btnGoogleSignUp = findViewById(R.id.btn_google_sign_up);
        TextView tvGotoLogin = findViewById(R.id.tv_goto_login);

        googleSignUpLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) {
                                handleGoogleSignUpAccount(account);
                            }
                        } catch (ApiException e) {
                            Toast.makeText(this, "Google Sign-Up failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        if (btnGoogleSignUp != null) {
            btnGoogleSignUp.setOnClickListener(v -> {
                Intent signUpIntent = FirebaseAuthManager.getInstance().getGoogleSignInClient(this).getSignInIntent();
                googleSignUpLauncher.launch(signUpIntent);
            });
        }

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all registration fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            UserManager.getInstance().loginByEmail(email);

            Toast.makeText(this, "Account created successfully! Welcome to Animo Spaces.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        if (tvGotoLogin != null) {
            tvGotoLogin.setOnClickListener(v -> finish());
        }
    }

    private void handleGoogleSignUpAccount(GoogleSignInAccount account) {
        FirebaseAuthManager.getInstance().firebaseAuthWithGoogle(account, new FirebaseAuthManager.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user, UserRole role) {
                Toast.makeText(RegisterActivity.this, "Welcome " + UserManager.getInstance().getFirstName() + "! Account created.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}