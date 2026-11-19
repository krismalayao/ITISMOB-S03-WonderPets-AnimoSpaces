package com.itismob.s03.wonder.pets.animospaces.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
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
    private ImageView ivProfileAvatar;
    private MaterialButton btnSaveProfile;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

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
        ivProfileAvatar = findViewById(R.id.iv_profile_avatar);
        btnSaveProfile = findViewById(R.id.btn_save_profile);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            try {
                                getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } catch (Exception ignored) {}
                            UserManager.getInstance().setProfileImageUri(imageUri.toString());
                            if (ivProfileAvatar != null) {
                                ivProfileAvatar.setImageURI(imageUri);
                            }
                            Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        View avatarContainer = findViewById(R.id.layout_avatar_container);
        if (avatarContainer != null) {
            avatarContainer.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
            });
        }

        if (btnSaveProfile != null) {
            btnSaveProfile.setOnClickListener(v -> {
                String name = etName.getText() != null ? etName.getText().toString().trim() : "";
                String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
                String id = etId.getText() != null ? etId.getText().toString().trim() : "";

                if (name.isEmpty() || email.isEmpty() || id.isEmpty()) {
                    Toast.makeText(this, "Please fill in all profile details.", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserManager user = UserManager.getInstance();
                user.setSession(name, id, email, user.getUserRole());

                Toast.makeText(this, "Profile changes saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }

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
        if (etName != null && !etName.hasFocus()) etName.setText(user.getName());
        if (etEmail != null && !etEmail.hasFocus()) etEmail.setText(user.getEmail());
        if (etId != null && !etId.hasFocus()) etId.setText(user.getIdNumber());

        if (tvProfileRole != null) {
            UserRole role = user.getUserRole();
            tvProfileRole.setText(role != null ? role.getDisplayName() : "Regular Student");
        }

        if (ivProfileAvatar != null) {
            String uriStr = user.getProfileImageUri();
            if (uriStr != null && !uriStr.isEmpty()) {
                try {
                    ivProfileAvatar.setImageURI(Uri.parse(uriStr));
                } catch (Exception e) {
                    ivProfileAvatar.setImageResource(R.drawable.ic_user_avatar);
                }
            } else {
                ivProfileAvatar.setImageResource(R.drawable.ic_user_avatar);
            }
        }
    }
}
