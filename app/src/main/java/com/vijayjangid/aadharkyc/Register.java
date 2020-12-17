package com.vijayjangid.aadharkyc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    TextInputEditText etMobileNumber, etEmail, etPassword;
    TextInputLayout etMobileNumberLayout, etEmailLayout, etPasswordLayout;
    TextView tvRegister, tvGoLogIn;
    Animation animAlpha;

    // string data used in registration
    String mobileNumber, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // setting id's
        etMobileNumber = findViewById(R.id.mobileREt);
        etMobileNumberLayout = findViewById(R.id.mobileREtL);
        etEmail = findViewById(R.id.emailEt);
        etEmailLayout = findViewById(R.id.emailEtL);
        etPassword = findViewById(R.id.passwordEtR);
        etPasswordLayout = findViewById(R.id.passwordEtRL);
        tvGoLogIn = findViewById(R.id.goLoginTv);
        tvRegister = findViewById(R.id.registerTv);

        // setting alpha animations
        animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_aplha);

        tvGoLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvGoLogIn.setAlpha((float) 0.5);
                gotoLogin();
            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvRegister.startAnimation(animAlpha);
                registerNow();
            }
        });
    }

    void gotoLogin() {
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
        finish();
    }

    void registerNow() {
        mobileNumber = Objects.requireNonNull(etMobileNumber.getText()).toString().trim();
        email = Objects.requireNonNull(etEmail.getText()).toString().trim().toLowerCase();
        password = Objects.requireNonNull(etPassword.getText()).toString().trim();

        if (mobileNumber.length() != 10 ||
                invalidEmail(email) || password.length() < 6) {
            errorCatcherEditText();
            return;
        }

        Toast.makeText(this, mobileNumber + " " + email, Toast.LENGTH_SHORT).show();
    }

    void errorCatcherEditText() {

        /*This is used to notify user if they have created some mistake
         * But this activates when user try to register with errors*/

        if (mobileNumber.length() != 10) etMobileNumberLayout.setError("Invalid Mobile Number");
        if (invalidEmail(email)) etEmailLayout.setError("Invalid Email Address");
        if (password.length() < 6) etPasswordLayout.setError("Minimum 6 characters");

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                password = Objects.requireNonNull(etPassword.getText()).toString().trim();
                if (password.length() < 6) etPasswordLayout.setError("Minimum 6 characters");
                else etPasswordLayout.setError(null);
            }
        });

        etMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mobileNumber = Objects.requireNonNull(etMobileNumber.getText()).toString().trim();

                if (mobileNumber.length() != 10)
                    etMobileNumberLayout.setError("Invalid Mobile Number");
                else etMobileNumberLayout.setError(null);
            }
        });


        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                email = Objects.requireNonNull(etEmail.getText()).toString().trim();
                if (invalidEmail(email)) etEmailLayout.setError("Invalid Email Address");
                else etEmailLayout.setError(null);
            }
        });
    }

    public boolean invalidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return true;
        return !pat.matcher(email).matches();
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}