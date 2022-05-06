package com.example.newsblender;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.newsblender.classes.ItemViewModel;
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
    /* Constants for type of news */
    private static final int ALL_NEWS = R.id.nav_news;
    private static final int VINNUTSIA = R.id.nav_vinnutsia;
    private static final int VOLYN = R.id.nav_volyn;
    private static final int DNIPROPETROVSK = R.id.nav_dnipropetrovsk;
    private static final int TRANSCARPATHIAN = R.id.nav_transcarpathian;
    private static final int ZAPORIZHZHIA = R.id.nav_zaporizhzhia;
    private static final int IVANO_FRANKIVSK = R.id.nav_ivano_frankivsk;
    private static final int KYIV = R.id.nav_kyiv;
    private static final int KIROVOHRAD = R.id.nav_kirovohrad;
    private static final int LVIV = R.id.nav_lviv;
    private static final int MYKOLAYIV = R.id.nav_mykolayiv;
    private static final int ODESA = R.id.nav_odesa;
    private static final int RIVNE = R.id.nav_rivne;
    private static final int TERNOPIL = R.id.nav_ternopil;
    private static final int KHARKIV = R.id.nav_kharkiv;
    private static final int KHERSON = R.id.nav_kherson;
    private static final int KHMELNYTSKY = R.id.nav_khmelnytsky;
    private static final int CHERKASY = R.id.nav_cherkasy;
    private static final int CHERNIHIV = R.id.nav_chernihiv;
    private static final int CHERNIVTSI = R.id.nav_chernivtsi;
    private static final int ZHYTOMYR = R.id.nav_zhytomyr;
    private static final int POLTAVA = R.id.nav_poltava;
    private static final int LUHANSK = R.id.nav_luhansk;
    private static final int DONETSK = R.id.nav_donetsk;
    private static final int SUMY = R.id.nav_sumy;

    /* Variables */
    private FirebaseAuth fAuth;
    private FirebaseStorage fStorage;
    private StorageReference mStorageReference;
    private GoogleSignInClient mSignInClient;

    //    private final int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    //            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    //            | View.SYSTEM_UI_FLAG_FULLSCREEN
    //            | View.SYSTEM_UI_FLAG_IMMERSIVE;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private NavController mNavController;
    private Toolbar mToolbar;
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseUser fUser;
    private ItemViewModel viewModel;
    private TextView mTextViewUserInfo;
    private TextView mTextViewNewsResources;
    private TextView mTextViewSaved;
    private TextView mTextViewProfileSettings;
    private TextView mTextViewApplicationSettings;
    private ImageView popUpButtonImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.setNewsNavigationTypeValue(LVIV);
        setContentView(R.layout.activity_main);

        /* Setting main variables */
        fAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavController = Navigation.findNavController(this, R.id.activity_main_nav_host_fragment);
        viewModel.setNavigationView(mNavController);
        viewModel.setfAuth(fAuth);
        mToolbar = findViewById(R.id.topAppBar);
        NavigationUI.setupWithNavController(mNavigationView, mNavController);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this, gso);

        firebaseUserInit();

        setSupportActionBar(mToolbar);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_news).setOpenableLayout(mDrawerLayout).build();
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavigationView, mNavController);
        navigationInit();
        mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
        mToolbar.setNavigationOnClickListener(view -> mDrawerLayout.open());

        /* Initialize image view */
        popUpButtonImageView = findViewById(R.id.popUpButtonImageView);
        mStorageReference = fStorage.getReference();
        mStorageReference.child("images/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this).load(uri).into(popUpButtonImageView);
        });


//        Toast.makeText(this, fStore.collection("/telegram_links").document().toString(), Toast.LENGTH_SHORT).show();
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
            viewModel.setNewsNavigationTypeValue(menuItem.getItemId());
            mNavController.navigate(R.id.newsFragment);
            mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
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

        /* If last view element is tint view - delete it */
        popupWindow.setOnDismissListener(() -> {
            if (mDrawerLayout.getChildAt(mDrawerLayout.getChildCount() - 1).getClass().equals(View.class))
                mDrawerLayout.removeViewAt(mDrawerLayout.getChildCount() - 1);
        });

        TextView userName = popupView.findViewById(R.id.userMenuUsername);
        ImageView imageView = popupView.findViewById(R.id.userMenuImageView);
        ViewStub viewStub = popupView.findViewById(R.id.view_stub_pop_up_window);
        /* Generating popup content relatively to user anonymity*/
        if (Objects.requireNonNull(fUser).isAnonymous()) {
            viewStub.setLayoutResource(R.layout.pop_up_params_anonym);

            /* Inflate  */
            viewStub.inflate();

            imageView.setImageDrawable(getDrawable(R.drawable.ic_avatar_incognito_24dp));
            View view1 = popupView.findViewById(R.id.popUpHeader);
            view1.setBackgroundResource(R.drawable.rectangle_gradient_incognito);
            userName.setText(R.string.incognito);
            userName.setTextColor(Color.WHITE);
            Button mButtonSignOut = popupView.findViewById(R.id.buttonSignOutAnonymous);
            mButtonSignOut.setOnClickListener(item -> {
                fAuth.signOut();
                Intent login_activity = new Intent(this, LoginActivity.class);
                startActivity(login_activity);
            });
        } else {
            imageView.setImageDrawable(popUpButtonImageView.getDrawable());
            userName.setText(fUser.getDisplayName());
            viewStub.setLayoutResource(R.layout.pop_up_params);

            /* Inflate  */
            viewStub.inflate();

            /* Setting up popUp variables and set onClickListeners */
            mTextViewUserInfo = popupView.findViewById(R.id.user_profile);
            mTextViewUserInfo.setOnClickListener(item -> {
                mNavController.navigate(R.id.profileFragment);
                popupWindow.dismiss();
            });

            mTextViewNewsResources = popupView.findViewById(R.id.news_resources);

            mTextViewSaved = popupView.findViewById(R.id.saved);
            mTextViewSaved.setOnClickListener(item -> {
                mNavController.navigate(R.id.savedNewsFragment);
                popupWindow.dismiss();
            });

            mTextViewProfileSettings = popupView.findViewById(R.id.settings_profile);
            mTextViewProfileSettings.setOnClickListener(item -> {
                mNavController.navigate(R.id.profileSettingsFragment);
                popupWindow.dismiss();
            });

            mTextViewApplicationSettings = popupView.findViewById(R.id.application_settings);
            mTextViewApplicationSettings.setOnClickListener(item -> {
                mNavController.navigate(R.id.settingsFragment);
                popupWindow.dismiss();
            });
        }

        popupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP, 0, 0);
        View view1 = new View(getApplicationContext());
        view1.setBackgroundResource(R.drawable.background_tint);
        mDrawerLayout.addView(view1);
    }
}