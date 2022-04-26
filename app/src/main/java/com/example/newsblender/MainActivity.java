package com.example.newsblender;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

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
    private Toolbar mToolbar;
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseUser fUser;

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
        mToolbar = findViewById(R.id.topAppBar);
        NavigationUI.setupWithNavController(mNavigationView, mNavController);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this, gso);

        firebaseUserInit();

        setSupportActionBar(mToolbar);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_news, R.id.nav_settings).setOpenableLayout(mDrawerLayout).build();
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavigationView, mNavController);
        navigationInit();
        mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
        mToolbar.setNavigationOnClickListener(view -> mDrawerLayout.open());
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
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_settings:
                    mNavController.navigate(R.id.settingsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                default:
                    mNavController.navigate(R.id.newsFragment);
            }
            mDrawerLayout.close();
            return true;
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void firebaseUserInit() {
        fUser = fAuth.getCurrentUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility"})
    public void onPopUpUserMenu(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setOnDismissListener(() -> mDrawerLayout.setBackgroundColor(getColor(R.color.clear)));
        /* Generating popup content relatively to user anonymity*/
        if (Objects.requireNonNull(fUser).isAnonymous()) {
            ((ImageView) popupWindow.getContentView().findViewById(R.id.userMenuImageView)).setImageDrawable(getDrawable(R.drawable.ic_avatar_incognito_24dp));
            ((TextView) popupWindow.getContentView().findViewById(R.id.userMenuUsername)).setText(R.string.incognito);
            Button btnTag = new Button(this);
            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnTag.setText("Exit");
            btnTag.setOnClickListener(v -> {/*some operation*/});
            ((RelativeLayout) popupWindow.getContentView().findViewById(R.id.userMenuRelativeLayout)).addView(btnTag);
        } else {
            /*TODO change it to user avatar*/
            ((ImageView) popupWindow.getContentView().findViewById(R.id.userMenuImageView)).setImageDrawable(getDrawable(R.drawable.ic_user_menu_24dp));
            ((TextView) popupWindow.getContentView().findViewById(R.id.userMenuUsername)).setText(fUser.getDisplayName());
            /*TODO create menu items*/
        }
        popupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP, 0, 0);
        mDrawerLayout.setBackgroundColor(getColor(R.color.gray_tint));
        // dismiss the popup window when touched
//        popupView.setOnTouchListener((v, event) -> {
//            popupWindow.dismiss();
//            return true;
//        });

    }
}