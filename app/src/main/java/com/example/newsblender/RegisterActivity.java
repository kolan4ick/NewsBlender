package com.example.newsblender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private EditText fullNameInput;
    private EditText passwordInput;
    private EditText emailInput;
    private Button signUpButton;
    private TextView loginTextView;
    private ProgressBar progressBar;
    private FirebaseFirestore fStore;
    private String userID;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fullNameInput = findViewById(R.id.full_name_input_sign_up);
        passwordInput = findViewById(R.id.password_input_sign_up);
        emailInput = findViewById(R.id.email_input_sign_up);
        signUpButton = findViewById(R.id.sign_up_button);
        loginTextView = findViewById(R.id.login_text_view);
        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        signUpButton.setOnClickListener(view -> {
            final String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            final String fullName = fullNameInput.getText().toString();

            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Email is Required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passwordInput.setError("Password is Required.");
                return;
            }

            if (password.length() < 6) {
                fullNameInput.setError("Password Must be >= 6 Characters");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // register the user in firebase

            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    // send verification link

                    FirebaseUser fuser = fAuth.getCurrentUser();
                    fuser.sendEmailVerification()
                            .addOnSuccessListener(aVoid -> Toast.makeText(RegisterActivity.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Log.d(TAG, "onFailure: Email not sent " + e.getMessage()));

                    Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                    userID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    user.put("fName", fullName);
                    user.put("email", email);
                    documentReference.set(user)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: user Profile is created for " + userID))
                            .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e));
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(RegisterActivity.this, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        });

        loginTextView.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        });
    }
}