package com.vijayjangid.aadharkyc;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.vijayjangid.aadharkyc.login.Login_activity;

import java.util.ArrayList;
import java.util.List;


public class SplashScreen extends AppCompatActivity {

    final int LOCK_REQUEST_CODE = 1, SECURITY_SETTING_REQUEST_CODE = 2;
    ViewPager screenPager;
    SplashViewPagerAdapter splashViewPagerAdapter;
    TabLayout tabLayout;
    int position = 0;
    Button btnGetStarted;
    TextView btnNext;
    Animation btnAnim;
    TextView tvSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(this, HomePage_activity.class));
        //authenticateApp();
        checkLocation();

        // make the activity on full screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // when this activity is about to be launch we need to check if its openened before or not

       /* if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(), SplashScreen.class);
            startActivity(mainActivity);
            finish();
        }*/

        setContentView(R.layout.activity_splash_screen);

        // hide the action bar

        // initialize views
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabLayout = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);

        // fill list screen

        final List<ScreenItemObject> mList = new ArrayList<>();
        mList.add(new ScreenItemObject("Secure",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",
                R.drawable.temporary1));
        mList.add(new ScreenItemObject("Bill Payments",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",
                R.drawable.temporary2));
        mList.add(new ScreenItemObject("DTH Recharge",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",
                R.drawable.temporary3));
        mList.add(new ScreenItemObject("Mobile Recharge",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",
                R.drawable.temporary4));
        mList.add(new ScreenItemObject("Send Money to anyone",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",
                R.drawable.temporary5));

        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        splashViewPagerAdapter = new SplashViewPagerAdapter(this, mList);
        screenPager.setAdapter(splashViewPagerAdapter);

        // setup tabLayout with viewpager
        tabLayout.setupWithViewPager(screenPager);

        // next button click Listener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = screenPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }

                if (position == mList.size() - 1) { // when we rech to the last screen

                    // TODO : show the GETSTARTED Button and hide the indicator and the next button
                    loadLastScreen();
                }

            }
        });

        // tabLayout add change listener


        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size() - 1) {
                    loadLastScreen();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        // Get Started button click listener

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open main activity

                Intent mainActivity = new Intent(SplashScreen.this, Login_activity.class);
                startActivity(mainActivity);
                // also we need to save a boolean value to storage so next time when the user run the app
                // we could know that he is already checked the intro screen activity
                // i'm going to use shared preferences to that process
                finish();


            }
        });

        // skip button click listener

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(mList.size());
            }
        });


    }

    /* Screen Item object creating class*/

    // show the getStarted Button and hide the indicator and the next button
    private void loadLastScreen() {

        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabLayout.setVisibility(View.INVISIBLE);
        // TODO : ADD an animation the getstarted button
        // setup animation
        btnGetStarted.setAnimation(btnAnim);
    }

    /* Adapter for viewPager class */

    private void authenticateApp() {

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        Intent i = keyguardManager.createConfirmDeviceCredentialIntent("Unlock", "Confirm your screen lock");

        try {
            startActivityForResult(i, LOCK_REQUEST_CODE);
        } catch (Exception e) {

            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            try {
                startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
            } catch (Exception ignored) {
            }

        }
    }

    // For authentication purposes

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case LOCK_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    // passed
                } else {
                    // failed
                }
                break;

            case SECURITY_SETTING_REQUEST_CODE:
                // When user is enabled Security settings then we don't get any kind of RESULT_OK
                // So we need to check whether device has enabled screen lock or not
                if (isDeviceSecure()) {
                    // If screen lock enabled show toast and start intent to authenticate user
                    authenticateApp();
                } else {
                    // If screen lock is not enabled just update text
                    // textView.setText(getResources().getString(R.string.security_device_cancelled));
                }

                break;
        }
    }

    private boolean isDeviceSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        return keyguardManager.isKeyguardSecure();
    }

    void checkLocation() {
        /* calling this method checks if location permission is given or not
        if not then ask and if yes then skips */

        if (ContextCompat.checkSelfPermission(SplashScreen.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(SplashScreen.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(SplashScreen.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(SplashScreen.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    // location permission here

    public static class SplashViewPagerAdapter extends PagerAdapter {

        Context mContext;
        List<ScreenItemObject> mListScreen;

        public SplashViewPagerAdapter(Context mContext, List<ScreenItemObject> mListScreen) {
            this.mContext = mContext;
            this.mListScreen = mListScreen;
        }


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layoutScreen = inflater.inflate(R.layout.layout_splash, null);

            ImageView imgSlide = layoutScreen.findViewById(R.id.intro_img);
            TextView title = layoutScreen.findViewById(R.id.intro_title);
            TextView description = layoutScreen.findViewById(R.id.intro_description);

            String title2 = mListScreen.get(position).getTitle();
            String description2 = mListScreen.get(position).getDescription();
            title.setText(title2);
            description.setText(description2);
            imgSlide.setImageResource(mListScreen.get(position).getScreenImage());

            container.addView(layoutScreen);

            return layoutScreen;
        }

        @Override
        public int getCount() {
            return mListScreen.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

            container.removeView((View) object);

        }
    }

    public class ScreenItemObject {

        String title, description;
        int screenImage;

        public ScreenItemObject(String title, String description, int screenImg) {
            this.title = title;
            this.description = description;
            this.screenImage = screenImg;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getScreenImage() {
            return screenImage;
        }

        public void setScreenImage(int screenImage) {
            this.screenImage = screenImage;
        }
    }
}