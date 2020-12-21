package com.vijayjangid.aadharkyc;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class HomePage_nav extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /* this activity sets navigation drawer
     * and fragment, user cannot see this activity*/

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("IOLab Payment");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar
                , R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // opening home fragment as default page
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                , new Home_fragment()).commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new Home_fragment()).commit();
                break;
            case R.id.nav_pay_reminders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new Pay_remind_fragment()).commit();
                break;
            case R.id.nav_addFund:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new Add_fund_fragment()).commit();
                break;
            case R.id.nav_security:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new Security_fragment()).commit();
                break;
            case R.id.nav_wallet_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new Wallet_history_fragment()).commit();
                break;
            case R.id.nav_transaction_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new Transaction_history_fragment()).commit();
                break;
            case R.id.nav_change_password:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new Change_password_fragment()).commit();
                break;
            case R.id.nav_rate_us:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new Rate_us_fragment()).commit();
                break;
            case R.id.nav_help_support:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new Help_support_fragment()).commit();
                break;
            case R.id.nav_exit:
                startActivity(new Intent(HomePage_nav.this, Login.class));
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}