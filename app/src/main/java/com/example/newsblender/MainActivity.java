package com.example.newsblender;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.newsblender.classes.ItemViewModel;
import com.example.newsblender.classes.TelegramNews;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    /* Constants for type of news */
    private static final int ALL_NEWS = 0;
    private static final int VINNUTSIA = 1;
    private static final int VOLYN = 2;
    private static final int DNIPROPETROVSK = 3;
    private static final int TRANSCARPATHIAN = 4;
    private static final int ZAPORIZHZHIA = 5;
    private static final int IVANO_FRANKIVSK = 6;
    private static final int KYIV = 7;
    private static final int KIROVOHRAD = 8;
    private static final int LVIV = 9;
    private static final int MYKOLAYIV = 10;
    private static final int ODESA = 11;
    private static final int RIVNE = 12;
    private static final int TERNOPIL = 13;
    private static final int KHARKIV = 14;
    private static final int KHERSON = 15;
    private static final int KHMELNYTSKY = 16;
    private static final int CHERKASY = 17;
    private static final int CHERNIHIV = 18;
    private static final int CHERNIVTSI = 19;
    private static final int ZHYTOMYR = 20;
    private static final int POLTAVA = 21;
    private static final int LUHANSK = 22;
    private static final int DONETSK = 23;
    private static final int SUMY = 24;

    /* Variables */
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.setNewsNavigationTypeValue(LVIV);
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
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_news).setOpenableLayout(mDrawerLayout).build();
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavigationView, mNavController);
        navigationInit();
        mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
        mToolbar.setNavigationOnClickListener(view -> mDrawerLayout.open());

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
//            TODO: Optimize it
            switch (menuItem.getItemId()) {
                case R.id.nav_news:
                    viewModel.setNewsNavigationTypeValue(ALL_NEWS);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_vinnutsia:
                    viewModel.setNewsNavigationTypeValue(VINNUTSIA);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_volyn:
                    viewModel.setNewsNavigationTypeValue(VOLYN);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_dnipropetrovsk:
                    viewModel.setNewsNavigationTypeValue(DNIPROPETROVSK);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_transcarpathian:
                    viewModel.setNewsNavigationTypeValue(TRANSCARPATHIAN);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_zaporizhzhia:
                    viewModel.setNewsNavigationTypeValue(ZAPORIZHZHIA);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_ivano_frankivsk:
                    viewModel.setNewsNavigationTypeValue(IVANO_FRANKIVSK);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_kyiv:
                    viewModel.setNewsNavigationTypeValue(KYIV);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_kirovohrad:
                    viewModel.setNewsNavigationTypeValue(KIROVOHRAD);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_lviv:
                    viewModel.setNewsNavigationTypeValue(LVIV);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_mykolayiv:
                    viewModel.setNewsNavigationTypeValue(MYKOLAYIV);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_odesa:
                    viewModel.setNewsNavigationTypeValue(ODESA);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_rivne:
                    viewModel.setNewsNavigationTypeValue(RIVNE);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_ternopil:
                    viewModel.setNewsNavigationTypeValue(TERNOPIL);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_kharkiv:
                    viewModel.setNewsNavigationTypeValue(KHARKIV);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_kherson:
                    viewModel.setNewsNavigationTypeValue(KHERSON);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_khmelnytsky:
                    viewModel.setNewsNavigationTypeValue(KHMELNYTSKY);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_cherkasy:
                    viewModel.setNewsNavigationTypeValue(CHERKASY);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_chernihiv:
                    viewModel.setNewsNavigationTypeValue(CHERNIHIV);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_chernivtsi:
                    viewModel.setNewsNavigationTypeValue(CHERNIVTSI);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_zhytomyr:
                    viewModel.setNewsNavigationTypeValue(ZHYTOMYR);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_poltava:
                    viewModel.setNewsNavigationTypeValue(POLTAVA);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_luhansk:
                    viewModel.setNewsNavigationTypeValue(LUHANSK);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_donetsk:
                    viewModel.setNewsNavigationTypeValue(DONETSK);
                    mNavController.navigate(R.id.newsFragment);
                    mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
                    break;
                case R.id.nav_sumy:
                    viewModel.setNewsNavigationTypeValue(SUMY);
                    mNavController.navigate(R.id.newsFragment);
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