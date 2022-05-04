package com.example.newsblender;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsblender.classes.Util;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1234;
    private EditText mEmailInput;
    private EditText mPasswordInput;
    private TextView mRegisterTextView;
    private TextView mPassForgotTextView;
    private Button mSignInButton;
    private SignInButton mSignInButtonGoogle;
    private Button mSignInButtonIncognito;
    private ProgressBar mProgressBar;
    private FirebaseAuth fAuth;
    private GoogleSignInClient mSignInClient;
/*
    private final int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE;
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Util.isNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
            mEmailInput = findViewById(R.id.email_input_sign_in);
            mPasswordInput = findViewById(R.id.password_input_sign_in);
            mRegisterTextView = findViewById(R.id.register_text_view);
            mPassForgotTextView = findViewById(R.id.pass_forgot_textview);
            mSignInButton = findViewById(R.id.sign_in_button);
            mSignInButtonGoogle = findViewById(R.id.sign_in_google_button);
            mSignInButtonIncognito = findViewById(R.id.sign_in_incognito);
            mProgressBar = findViewById(R.id.progressBar);
            fAuth = FirebaseAuth.getInstance();

            if (fAuth.getCurrentUser() != null) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mSignInClient = GoogleSignIn.getClient(this, gso);

            mSignInButtonIncognito.setOnClickListener(view -> {
                mProgressBar.setVisibility(View.VISIBLE);

                fAuth.signInAnonymously().addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
            });

            mSignInButton.setOnClickListener(view -> {

                String email = mEmailInput.getText().toString().trim();
                String password = mPasswordInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmailInput.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPasswordInput.setError("Password is Required.");
                    return;
                }

                if (password.length() < 6) {
                    mPasswordInput.setError("Password Must be >= 6 Characters");
                    return;
                }

                mProgressBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    } else {
                        Toast.makeText(LoginActivity.this, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            });

            mSignInButtonGoogle.setOnClickListener(view -> {
                Intent signInIntent = mSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            });

            mRegisterTextView.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

            mPassForgotTextView.setOnClickListener(view -> {
                final EditText resetMail = new EditText(view.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", (dialog, which) -> {
                    // extract the email and send reset link
                    String mail = resetMail.getText().toString();
                    fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(aVoid ->
                            Toast.makeText(LoginActivity.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show());

                });
                passwordResetDialog.setNegativeButton("No", (dialog, which) -> {
                    // close the dialog
                });
                passwordResetDialog.create().show();
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You do not have access to the Internet, turn it on and restart the application")
                    .setTitle("Error");
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, authResult -> {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(this, e -> Toast.makeText(LoginActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
/*
        View mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(uiFlags);
        mDecorView.setOnSystemUiVisibilityChangeListener(view -> mDecorView.setSystemUiVisibility(uiFlags));
*/
    }
}