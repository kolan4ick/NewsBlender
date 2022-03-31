package com.example.newsblender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
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
    private EditText mFullNameInput;
    private EditText mPasswordInput;
    private EditText mEmailInput;
    private Button mSignUpButton;
    private TextView mLoginTextView;
    private ProgressBar mProgressBar;
    private FirebaseFirestore fStore;
    private String mUserID;
    FirebaseAuth fAuth;
    private final int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullNameInput = findViewById(R.id.full_name_input_sign_up);
        mPasswordInput = findViewById(R.id.password_input_sign_up);
        mEmailInput = findViewById(R.id.email_input_sign_up);
        mSignUpButton = findViewById(R.id.sign_up_button);
        mLoginTextView = findViewById(R.id.login_text_view);
        mProgressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mSignUpButton.setOnClickListener(view -> {
            final String email = mEmailInput.getText().toString().trim();
            String password = mPasswordInput.getText().toString().trim();
            final String fullName = mFullNameInput.getText().toString();

            if (TextUtils.isEmpty(email)) {
                mEmailInput.setError("Email is Required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                mPasswordInput.setError("Password is Required.");
                return;
            }

            if (password.length() < 6) {
                mFullNameInput.setError("Password Must be >= 6 Characters");
                return;
            }

            mProgressBar.setVisibility(View.VISIBLE);

            // register the user in firebase

            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    // send verification link

                    FirebaseUser fuser = fAuth.getCurrentUser();
                    fuser.sendEmailVerification()
                            .addOnSuccessListener(aVoid -> Toast.makeText(RegisterActivity.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Log.d(TAG, "onFailure: Email not sent " + e.getMessage()));

                    Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                    mUserID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("users").document(mUserID);
                    Map<String, Object> user = new HashMap<>();
                    user.put("fName", fullName);
                    user.put("email", email);
                    documentReference.set(user)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: user Profile is created for " + mUserID))
                            .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e));
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(RegisterActivity.this, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        });

        mLoginTextView.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        View mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(uiFlags);
        mDecorView.setOnSystemUiVisibilityChangeListener(view -> mDecorView.setSystemUiVisibility(uiFlags));
    }

}