package com.vijayjangid.aadharkyc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MyProfile_fragment extends AppCompatActivity {

    TextView tv_userName, tv_userMobile, tv_userUpi, tv_userEmail;
    ImageView iv_userProfileImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_my_profile_fragment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_cross_close2);

        idAndListeners();
    }

    void idAndListeners() {
        tv_userEmail = findViewById(R.id.tv_emailProfile);
        tv_userMobile = findViewById(R.id.tv_mobileProfile);
        tv_userName = findViewById(R.id.tv_nameProfile);
        iv_userProfileImage = findViewById(R.id.iv_profile);
        restoreData();
    }

    void restoreData() {
        UserData userData = new UserData(this);
        tv_userName.setText(userData.getName());
        tv_userMobile.setText(userData.getMobile());
        tv_userEmail.setText(userData.getEmail());

        byte[] decodedString = Base64.decode(userData.getPhotoByteCode(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        iv_userProfileImage.setImageBitmap(bitmap);
    }


    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}