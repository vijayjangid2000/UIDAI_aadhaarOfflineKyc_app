package com.vijayjangid.aadharkyc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.vijayjangid.aadharkyc.login.Login_activity;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;

public class HomePage_activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /* this activity sets navigation drawer
     * and fragment, user cannot see this activity*/

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    int whichFragmentIsVisible = 1;

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment fragment = null;

                    switch (item.getItemId()) {

                        case R.id.nav_home:
                            if (whichFragmentIsVisible == 1) return false;
                            else whichFragmentIsVisible = 1;
                            fragment = new Home_fragment();
                            break;

                        case R.id.nav_wallet_history:
                            if (whichFragmentIsVisible == 2) return false;
                            else whichFragmentIsVisible = 2;
                            fragment = new WalletTransactionFragment();
                            break;

                        case R.id.nav_transaction_history:
                            if (whichFragmentIsVisible == 3) return false;
                            else whichFragmentIsVisible = 3;
                            fragment = new MoneyTransaction();
                            break;

                        case R.id.nav_reward:
                            if (whichFragmentIsVisible == 4) return false;
                            else whichFragmentIsVisible = 4;
                            fragment = new RewardFragment();
                            break;
                    }

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations
                            (R.anim.enter_right_to_left,
                                    R.anim.exit_right_to_left,
                                    R.anim.enter_left_to_right,
                                    R.anim.exit_left_to_right);
                    transaction.replace(R.id.fragment_container, fragment);
                    transaction.commit();

                    if (whichFragmentIsVisible == 1) toolbar.setVisibility(View.VISIBLE);
                    else toolbar.setVisibility(View.GONE);

                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        /* check if user is already log in */
        ifAlreadyLoggedIn();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final LinearLayout content = findViewById(R.id.content);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar
                , R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                content.setTranslationX(slideX);
                float scaleFactor = 6f;
                content.setScaleX(1 - (slideOffset / scaleFactor));
                content.setScaleY(1 - (slideOffset / scaleFactor));
            }

        };
        actionBarDrawerToggle.syncState();

        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.setDrawerElevation(0f);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //ShowIntro("Scan to pay", "Scan any QR code to pay", R.id.ivb_scanner, 1);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setBackgroundColor(Color.parseColor("#EEF9FF"));

        ImageButton ivb_scan = findViewById(R.id.ivb_scanner);
        ivb_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkCameraPermission()) {
                    startActivity(new Intent(HomePage_activity.this, ScanQrCode.class));
                }
            }
        });

        ImageButton ivb_notification = findViewById(R.id.ivb_notification);
        ivb_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage_activity.this,
                        Notification.class));
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                , new Home_fragment()).commit();

    }

    private void ShowIntro(String title, String text, int viewId, final int type) {

        new GuideView.Builder(getContext())
                .setTitle(title)
                .setContentText(text)
                .setTargetView(findViewById(viewId))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(GuideView.DismissType.anywhere) //optional - default dismissible by TargetView
                .setGuideListener(new GuideView.GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        // opening home fragment as default page
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                                , new Home_fragment()).commit();
                    }
                })
                .build()
                .show();
    }

    // LEFT NAVIGATION DRAWER
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_change_password:
                startActivity(new Intent(this, Change_password_fragment.class));
                break;

            case R.id.nav_rate_us:
                startActivity(new Intent(HomePage_activity.this, Rate_activity.class));
                break;
            case R.id.nav_help_support:
                startActivity(new Intent(this, Help_support_fragment.class));
                break;

            case R.id.nav_my_profile:
                startActivity(new Intent(this, MyProfile_fragment.class));
                break;

            case R.id.nav_exit:
                UserData userData = new UserData(this);
                userData.setIsLoggedInAlready(false);
                userData.applyUpdate();
                toast("Logout Successful");
                startActivity(new Intent(getContext(), Login_activity.class));
                finish();
                break;
        }

        return true;
    }

    Context getContext() {
        return HomePage_activity.this;
    }

    boolean checkCameraPermission() {
        /* calling this method checks if location permission is given or not
        if not then ask and if yes then skips */

        if (ContextCompat.checkSelfPermission(HomePage_activity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomePage_activity.this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        }

        return ContextCompat.checkSelfPermission(HomePage_activity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (ContextCompat.checkSelfPermission(HomePage_activity.this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                startActivity(new Intent(HomePage_activity.this, ScanQrCode.class));
        }
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    void ifAlreadyLoggedIn() {
        UserData userData = new UserData(this);
        if (!userData.getIsLoggedInAlready()) {
            // if it is not true then we will send them to login page
            startActivity(new Intent(this, Login_activity.class));
            finish();
        }
    }

    void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}