package com.example.newsblender;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private StorageReference mStorageReference;
    private GoogleSignInClient mSignInClient;
    private final int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this, gso);

        if (fAuth.getCurrentUser() != null)
            Toast.makeText(getApplicationContext(), "YES", Toast.LENGTH_SHORT).show();
        else Toast.makeText(getApplicationContext(), "NO", Toast.LENGTH_SHORT).show();
        StorageReference profileRef = mStorageReference.child("users/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid() + "/profile.jpg");
//        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(profileImage));
        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Poka");
            alert.setCancelable(false);
            alert.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                fAuth.signOut();
                mSignInClient.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            });
            alert.setNegativeButton(android.R.string.no, (dialogInterface, i) -> dialogInterface.dismiss());
            alert.show();
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