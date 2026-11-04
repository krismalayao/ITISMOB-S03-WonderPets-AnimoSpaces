package com.itismob.s03.wonder.pets.animospaces.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.itismob.s03.wonder.pets.animospaces.R;
import com.itismob.s03.wonder.pets.animospaces.data.UserManager;
import com.itismob.s03.wonder.pets.animospaces.model.UserRole;
import com.itismob.s03.wonder.pets.animospaces.ui.auth.LoginActivity;
import com.itismob.s03.wonder.pets.animospaces.util.BottomNavHelper;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileRole;
    private EditText etName;
    private EditText etEmail;
    private EditText etId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        View rootLayout = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnBack = findViewById(R.id.btn_profile_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        tvProfileRole = findViewById(R.id.tv_profile_role);
        etName = findViewById(R.id.et_profile_name);
        etEmail = findViewById(R.id.et_profile_email);
        etId = findViewById(R.id.et_profile_id);

        updateProfileDisplay();

        TextView tvLogout = findViewById(R.id.tv_logout);
        if (tvLogout != null) {
            tvLogout.setOnClickListener(v -> {
                Toast.makeText(ProfileActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        BottomNavHelper.setupBottomNav(this, 3); // Highlight Profile Tab (Index 3)
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavHelper.setupBottomNav(this, 3);
        updateProfileDisplay();
    }

    private void updateProfileDisplay() {
        UserManager user = UserManager.getInstance();
        if (etName != null) etName.setText(user.getName());
        if (etEmail != null) etEmail.setText(user.getEmail());
        if (etId != null) etId.setText(user.getIdNumber());

        if (tvProfileRole != null) {
            UserRole role = user.getUserRole();
            tvProfileRole.setText(role != null ? role.getDisplayName() : "Regular Student");
        }
    }
}