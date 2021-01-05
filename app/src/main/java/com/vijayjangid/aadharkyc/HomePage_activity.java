package com.vijayjangid.aadharkyc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class HomePage_activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /* this activity sets navigation drawer
     * and fragment, user cannot see this activity*/

    DrawerLayout drawer;

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment fragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            fragment = new Home_fragment();
                            break;
                        case R.id.nav_wallet_history:
                            fragment = new Wallet_history_fragment();
                            break;
                        case R.id.nav_transaction_history:
                            fragment = new Transaction_history_fragment();
                            break;
                        case R.id.nav_pay_reminders:
                            fragment = new Pay_remind_fragment();
                            break;
                        case R.id.nav_help_support:
                            fragment = new Help_support_fragment();
                            break;
                    }

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                            R.anim.enter_left_to_right, R.anim.exit_left_to_right);
                    transaction.replace(R.id.fragment_container, fragment);
                    transaction.commit();

                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = new Home_fragment();
                break;
            case R.id.nav_addFund:
                fragment = new Add_fund_fragment();
                break;
            case R.id.nav_security:
                fragment = new Security_fragment();
                break;
            case R.id.nav_change_password:
                fragment = new Change_password_fragment();
                break;
            case R.id.nav_rate_us:
                fragment = new Rate_us_fragment();
                break;
            case R.id.nav_exit:
                startActivity(new Intent(getContext(), Login_activity.class));
                finish();
                break;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    Context getContext() {
        return HomePage_activity.this;
    }


}