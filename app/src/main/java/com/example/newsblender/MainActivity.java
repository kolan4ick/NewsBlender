package com.example.newsblender;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private StorageReference mStorageReference;
    private GoogleSignInClient mSignInClient;
    //    private final int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
////            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
////            | View.SYSTEM_UI_FLAG_FULLSCREEN
//            | View.SYSTEM_UI_FLAG_IMMERSIVE;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private NavController mNavController;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Setting main variables */
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavController = Navigation.findNavController(this, R.id.activity_main_nav_host_fragment);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.nav_open, R.string.nav_close);

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // to make the Navigation drawer icon always appear on the action bar

        NavigationUI.setupWithNavController(mNavigationView, mNavController);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this, gso);

        checkUserSignedIn();

//        StorageReference profileRef = mStorageReference.child("users/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid() + "/profile.jpg");
        navigationInit();
    }

    @SuppressLint("NonConstantResourceId")
    private void navigationInit() {
        mNavigationView.getMenu().getItem(0).setChecked(true);
        mNavigationView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.equals(mNavigationView.getCheckedItem())) {
                mDrawerLayout.close();
                return false;
            }
            Toast.makeText(getApplicationContext(), menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
            switch (menuItem.getItemId()) {
                case R.id.nav_news:
                    mNavController.navigate(R.id.newsFragment);
                    break;
                case R.id.nav_settings:
                    mNavController.navigate(R.id.settingsFragment);
                    break;
                default:
                    mNavController.navigate(R.id.newsFragment);
            }
            mDrawerLayout.close();
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
/*        View mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(uiFlags);
        mDecorView.setOnSystemUiVisibilityChangeListener(view -> mDecorView.setSystemUiVisibility(uiFlags));*/
    }

    protected void checkUserSignedIn() {
        if (fAuth.getCurrentUser() != null)
            Toast.makeText(getApplicationContext(), "YES", Toast.LENGTH_SHORT).show();
        else Toast.makeText(getApplicationContext(), "NO", Toast.LENGTH_SHORT).show();
    }
}