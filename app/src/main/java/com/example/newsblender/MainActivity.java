package com.example.newsblender;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.example.newsblender.classes.AddTelegramChannel;
import com.example.newsblender.classes.ItemViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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

    private static final String[] telegram_news = {"https://allnews", "https://t.me/vinnytskaODA", "https://t.me/volynskaODA", "https://t.me/dnipropetrovskaODA",
            "https://t.me/zakarpatskaODA", "https://t.me/starukhofficial", "https://t.me/onyshchuksvitlana",
            "https://t.me/kyivoda", "https://t.me/chornamary", "https://t.me/kozytskyy_maksym_official",
            "https://t.me/mykolaivskaODA", "https://t.me/odeskaODA", "https://t.me/vitalykoval8",
            "https://t.me/ternopilskaODA", "https://t.me/synegubov", "https://t.me/khersonskaODA",
            "https://t.me/khmelnytskaODA", "https://t.me/cherkaskaODA", "https://t.me/chernigivskaODA",
            "https://t.me/chernivetskaODA", "https://t.me/zhytomyrskaODA", "https://t.me/DMYTROLUNIN",
            "https://t.me/luhanskaVTSA", "https://t.me/pavlokyrylenko_donoda", "https://t.me/Zhyvytskyy"
    };


    /* Variables */
    private FirebaseAuth fAuth;
    private FirebaseStorage fStorage;
    private StorageReference mStorageReference;
    private GoogleSignInClient mSignInClient;
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
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.setNewsNavigationTypeValue(ALL_NEWS);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        /* Initialize preferences */
        initPreferences();

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
        mToolbar.setNavigationOnClickListener(view -> {
            if (viewModel.getNewsNavigationTypeValue() != ALL_NEWS || !Objects.requireNonNull(mNavController.getCurrentDestination()).getDisplayName().contains("newsFragment")) {
                viewModel.setNewsNavigationTypeValue(ALL_NEWS);
                mNavController.navigate(R.id.newsFragment);
                mNavigationView.setCheckedItem(mNavigationView.getMenu().getItem(1));
                mNavigationView.setCheckedItem(mNavigationView.getMenu().getItem(0));
                mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
            } else
                mDrawerLayout.open();
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        /* Initialize image view */
        popUpButtonImageView = findViewById(R.id.popUpButtonImageView);
        mStorageReference = fStorage.getReference();
        mStorageReference.child("images/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this).load(uri).into(popUpButtonImageView);
        });
        mNavController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if (navDestination.getDisplayName().contains("newsFragment") && viewModel.getNewsNavigationTypeValue() == ALL_NEWS)
                mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
            else mToolbar.setNavigationIcon(R.drawable.ic_arrow);


        });
    }

    @Override
    public void onBackPressed() {
        if (viewModel.getNewsNavigationTypeValue() != ALL_NEWS || !Objects.requireNonNull(mNavController.getCurrentDestination()).getDisplayName().contains("newsFragment")) {
            viewModel.setNewsNavigationTypeValue(ALL_NEWS);
            mNavController.navigate(R.id.newsFragment);
            mNavigationView.setCheckedItem(mNavigationView.getMenu().getItem(1));
            mNavigationView.setCheckedItem(mNavigationView.getMenu().getItem(0));
            mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
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
            View constraint = popupView.findViewById(R.id.popUpWindowConstraintLayout);
            constraint.setBackgroundResource(R.drawable.rectangle_gradient_incognito);
            userName.setText(R.string.incognito);
            userName.setTextColor(Color.WHITE);
            Button mButtonSignOut = popupView.findViewById(R.id.buttonSignOutAnonymous);
            mButtonSignOut.setOnClickListener(item -> {
                fAuth.signOut();
                Intent login_activity = new Intent(this, LoginActivity.class);
                startActivity(login_activity);
            });
        } else {
            View constraint = popupView.findViewById(R.id.popUpWindowConstraintLayout);
            constraint.setBackgroundResource(R.drawable.rectangle_gradient);
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

            /* Generating popup for news resources */
            mTextViewNewsResources.setOnClickListener(item -> {

                /* Dismiss parent popup */
                popupWindow.dismiss();

                // inflate the layout of the popup window
                LayoutInflater inflaterChild = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupViewChild = inflaterChild.inflate(R.layout.pop_up_news_resources, null);
                /* Access to database and resources */
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference newsResources = FirebaseFirestore.getInstance().collection("news_resources");

                ConstraintLayout constraintLayout = popupViewChild.findViewById(R.id.scrollView2).findViewById(R.id.popUpNewsResourcesConstraintLayout);
                newsResources.whereEqualTo("Uid", fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document :
                            task.getResult()) {
                        AppCompatCheckBox chkBx = findCheckBoxInLayoutByText(constraintLayout, document.getString("channel_name"));
                        if (chkBx != null) {
                            chkBx.setChecked(document.getBoolean("selected"));
                        } else {
                            AppCompatCheckBox appCompatCheckBox = new AppCompatCheckBox(this);
                            appCompatCheckBox.setButtonDrawable(R.drawable.checkbox);
                            appCompatCheckBox.setText(document.getString("channel_name"));
                            appCompatCheckBox.setChecked(document.getBoolean("selected"));
                            appCompatCheckBox.setId(View.generateViewId());
                            appCompatCheckBox.setPadding(12, 0, 0, 0);
                            constraintLayout.addView(appCompatCheckBox);
                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(constraintLayout);
                            constraintSet.connect(appCompatCheckBox.getId(), ConstraintSet.TOP, constraintLayout.getChildAt(constraintLayout.getChildCount() - 2).getId(), ConstraintSet.BOTTOM);
                            constraintSet.connect(appCompatCheckBox.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
                            constraintSet.applyTo(constraintLayout);
                        }
                    }
                });

                // create the popup window
                final PopupWindow popupWindowChild = new PopupWindow(popupViewChild, width, height, true);
                /* Generate popup by click on textView */
                TextView popUpNewsResourcesAddTextView = popupViewChild.findViewById(R.id.popUpNewsResourcesAddTextView);
                popUpNewsResourcesAddTextView.setOnClickListener(textView -> {
                    popupWindowChild.dismiss();
                    LayoutInflater inflaterAddResources = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupViewAddResources = inflaterAddResources.inflate(R.layout.pop_up_add_news_resource, null);

                    // create the popup window
                    final PopupWindow popupWindowAddResources = new PopupWindow(popupViewAddResources, width, height, true);

                    /* Open parent popup when dismiss current */
                    popupWindowAddResources.setOnDismissListener(() -> popupWindowChild.showAtLocation(item, Gravity.TOP | Gravity.CENTER, 0, 135));

                    /* Open the popup */
                    popupWindowAddResources.showAtLocation(textView, Gravity.CENTER, 0, 0);

                    /* Set actions to buttons */
                    ImageButton closeBtn = popupViewAddResources.findViewById(R.id.popUpAddNewsResourceCloseButton);
                    closeBtn.setOnClickListener(closeBtnView -> popupWindowAddResources.dismiss());

                    AppCompatButton addBtn = popupViewAddResources.findViewById(R.id.popUpAddNewsResourceAddButton);
                    addBtn.setOnClickListener(addBtnView -> {
                        EditText editText = popupViewAddResources.findViewById(R.id.popUpAddNewsResourceEditText);
                        String channelLink = editText.getText().toString();
                        if (channelLink.length() > 15)
                            try {
                                new AddTelegramChannel(channelLink, fAuth, popupWindowAddResources, popupWindowChild).execute();
                            } catch (Exception e) {
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        else
                            Toast.makeText(this, "Must be at least 16 symbols!\nTry again!", Toast.LENGTH_SHORT).show();
                    });

                    /* Add tint on background */
                    View view1 = new View(getApplicationContext());
                    view1.setBackgroundResource(R.drawable.background_tint);
                    mDrawerLayout.addView(view1);
                });

                /* If last view element is tint view - delete it */
                popupWindowChild.setOnDismissListener(() -> {
                    if (mDrawerLayout.getChildAt(mDrawerLayout.getChildCount() - 1).getClass().equals(View.class))
                        mDrawerLayout.removeViewAt(mDrawerLayout.getChildCount() - 1);
                });

                /* Open the popup */
                popupWindowChild.showAtLocation(item, Gravity.TOP | Gravity.CENTER, 0, 135);


                TextView cancelTextView = popupViewChild.findViewById(R.id.cancelButton);
                cancelTextView.setOnClickListener(item1 -> popupWindowChild.dismiss());

                TextView okTextView = popupViewChild.findViewById(R.id.okButton);
                okTextView.setOnClickListener(item1 -> {
                    try {
                        for (int i = 0; i < constraintLayout.getChildCount(); ++i) {
                            if (constraintLayout.getChildAt(i) instanceof AppCompatCheckBox) {
                                AppCompatCheckBox appCompatCheckBox = (AppCompatCheckBox) constraintLayout.getChildAt(i);

                                boolean flag = (i + 1 < telegram_news.length);
                                int finalI = i;
                                newsResources.whereEqualTo("Uid", fUser.getUid())
                                        .whereEqualTo("channel_name", appCompatCheckBox.getText()).get().addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                if (task.getResult().size() == 0 && flag) {
                                                    Map<String, Object> newsResource = new HashMap<>();
                                                    newsResource.put("channel_name", appCompatCheckBox.getText());
                                                    newsResource.put("selected", appCompatCheckBox.isChecked());
                                                    newsResource.put("channel_link", telegram_news[finalI]);
                                                    newsResource.put("Uid", Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
                                                    newsResource.put("date_added", Timestamp.now());
                                                    db.collection("news_resources").document().set(newsResource);
                                                } else
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        DocumentReference documentReference = document.getReference();
                                                        documentReference.update("selected", appCompatCheckBox.isChecked());
                                                    }
                                            }
                                        });
                            }
                        }
                        recreate();
                        popupWindowChild.dismiss();
                    } catch (Exception e) {
                        Toast.makeText(this, "ERROR!", Toast.LENGTH_SHORT).show();
                    }
                });

                /* Add tint on background */
                View view1 = new View(getApplicationContext());
                view1.setBackgroundResource(R.drawable.background_tint);
                mDrawerLayout.addView(view1);

            });

            /* Set actions to another buttons */
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

        popupWindow.showAtLocation(view, Gravity.END | Gravity.TOP, 0, 0);
        View view1 = new View(getApplicationContext());
        view1.setBackgroundResource(R.drawable.background_tint);
        mDrawerLayout.addView(view1);
    }

    /* Method for searching checkbox in constraint layout by text */
    protected AppCompatCheckBox findCheckBoxInLayoutByText(ConstraintLayout constraintLayout, String text) {
        for (int i = 0; i < constraintLayout.getChildCount(); ++i) {
            if (constraintLayout.getChildAt(i) instanceof AppCompatCheckBox) {
                if (((AppCompatCheckBox) constraintLayout.getChildAt(i)).getText().equals(text))
                    return (AppCompatCheckBox) constraintLayout.getChildAt(i);
            }
        }
        return null;
    }

    /* Method for initialize base settings */
    private void initPreferences() {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        String[] fontSizeValues = getResources().getStringArray(R.array.font_size_values);
        String[] languageValues = getResources().getStringArray(R.array.language);
        int font_size_id = 0;

        /* Set the font size*/
        if (prefs.getString("font_size", fontSizeValues[1]).equals(fontSizeValues[0])) {
            if (prefs.getString("font", "Inter").equals("Inter"))
                setTheme(R.style.Theme_NewsBlender_Small_Inter);
            else setTheme(R.style.Theme_NewsBlender_Small_Ubuntu);
        } else if (prefs.getString("font_size", fontSizeValues[1]).equals(fontSizeValues[1])) {
            if (prefs.getString("font", "Inter").equals("Inter"))
                setTheme(R.style.Theme_NewsBlender_Normal_Inter);
            else setTheme(R.style.Theme_NewsBlender_Normal_Ubuntu);
            font_size_id = 1;
        } else {
            if (prefs.getString("font", "Inter").equals("Inter"))
                setTheme(R.style.Theme_NewsBlender_Large_Inter);
            else setTheme(R.style.Theme_NewsBlender_Large_Ubuntu);
            font_size_id = 2;
        }

        /* Set the locale, must be in the end */
        if (prefs.getString("language", languageValues[0]).equals(languageValues[0])) {
            changeLocale("uk");
        } else if (prefs.getString("language", languageValues[0]).equals(languageValues[1])) {
            changeLocale("ru");
        } else {
            changeLocale("en");
        }


        prefsEditor.putString("font_size", getResources().getStringArray(R.array.font_size_values)[font_size_id]);
        prefsEditor.putString("theme", getResources().getStringArray(R.array.theme_values)[(AppCompatDelegate.getDefaultNightMode() - 1) == -101 ? 0 : AppCompatDelegate.getDefaultNightMode() - 1]);

        prefsEditor.apply();
    }

    private void changeLocale(String language) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language));
        res.updateConfiguration(conf, dm);
    }
}