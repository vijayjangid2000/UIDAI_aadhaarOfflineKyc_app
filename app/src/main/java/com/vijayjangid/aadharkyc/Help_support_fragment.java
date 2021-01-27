package com.vijayjangid.aadharkyc;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Help_support_fragment extends AppCompatActivity implements View.OnClickListener {

    View root;

    ImageView ivb_copyWebsite, ivb_copyMail, ivb_copyNumber, ivb_copyLocation,
            ivb_shareWebsite, ivb_shareMail, ivb_shareNumber, ivb_shareLocation;

    TextView tv_email, tv_emailData, tv_location, tv_locationData,
            tv_website, tv_websiteData, tv_number, tv_numberData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_help_support_fragment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Get Help");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_cross_close2);
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    void idAndListeners() {

        ivb_copyWebsite = root.findViewById(R.id.ivb_copyWebsite);
        ivb_copyMail = root.findViewById(R.id.ivb_copyEmail);
        ivb_copyNumber = root.findViewById(R.id.ivb_copyCall);
        ivb_copyLocation = root.findViewById(R.id.ivb_copyLocation);

        ivb_shareWebsite = root.findViewById(R.id.ivb_websiteShare);
        ivb_shareMail = root.findViewById(R.id.ivb_emailShare);
        ivb_shareNumber = root.findViewById(R.id.ivb_callShare);
        ivb_shareLocation = root.findViewById(R.id.ivb_locationShare);

        tv_email = root.findViewById(R.id.tv_email);
        tv_emailData = root.findViewById(R.id.tv_emailData);
        tv_location = root.findViewById(R.id.tv_location);
        tv_locationData = root.findViewById(R.id.tv_locationData);
        tv_website = root.findViewById(R.id.tv_website);
        tv_websiteData = root.findViewById(R.id.tv_websiteData);
        tv_number = root.findViewById(R.id.tv_call);
        tv_numberData = root.findViewById(R.id.tv_callData);

        ivb_copyWebsite.setOnClickListener(this);
        ivb_copyMail.setOnClickListener(this);
        ivb_copyNumber.setOnClickListener(this);
        ivb_copyLocation.setOnClickListener(this);
        ivb_shareWebsite.setOnClickListener(this);
        ivb_shareMail.setOnClickListener(this);
        ivb_shareNumber.setOnClickListener(this);
        ivb_shareLocation.setOnClickListener(this);
        tv_email.setOnClickListener(this);
        tv_emailData.setOnClickListener(this);
        tv_location.setOnClickListener(this);
        tv_locationData.setOnClickListener(this);
        tv_website.setOnClickListener(this);
        tv_websiteData.setOnClickListener(this);
        tv_number.setOnClickListener(this);
        tv_numberData.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ivb_copyWebsite:
            case R.id.ivb_copyCall:
            case R.id.ivb_copyEmail:
            case R.id.ivb_copyLocation:
                Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                break;

            case R.id.ivb_callShare:
            case R.id.ivb_emailShare:
            case R.id.ivb_locationShare:
            case R.id.ivb_websiteShare:
                Toast.makeText(this, "Open share via intent", Toast.LENGTH_SHORT).show();
                break;

            case R.id.tv_email:
            case R.id.tv_emailData:

            case R.id.tv_location:
            case R.id.tv_locationData:


            case R.id.tv_call:
            case R.id.tv_callData:

            case R.id.tv_website:
            case R.id.tv_websiteData:
                Toast.makeText(this, "Open CustomChromeTab", Toast.LENGTH_SHORT).show();
                break;
        }
    }


}