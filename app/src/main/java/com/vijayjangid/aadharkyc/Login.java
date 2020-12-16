package com.vijayjangid.aadharkyc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class Login extends AppCompatActivity {

    TextInputEditText etMobileNumber, etPassword;
    TextInputLayout etMobileNumberLayout, etPasswordLayout;
    TextView tvForgotPass, tvLogin, tvGoSignUp;

    // string data used in logging IN
    String mobileNumber, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // setting id's
        etMobileNumberLayout = findViewById(R.id.phoneEtL);
        etMobileNumber = findViewById(R.id.phoneEt);
        etPasswordLayout = findViewById(R.id.passwordEtL);
        etPassword = findViewById(R.id.passwordEt);
        tvForgotPass = findViewById(R.id.forgotTv);
        tvLogin = findViewById(R.id.loginTv);
        tvGoSignUp = findViewById(R.id.goSignupTv);

        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        tvGoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignUp();
            }
        });


    }

    void startSignUp() {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
        finish();
    }

    void forgotPassword() {
        // send mail to change password using firebase service
        Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show();
    }

    void login() {
        mobileNumber = Objects.requireNonNull(etMobileNumber.getText()).toString().trim();
        password = Objects.requireNonNull(etPassword.getText()).toString().trim();

        if (mobileNumber.length() != 10 || password.length() < 6) {
            errorCatcherEditText();
            return;
        }

        Toast.makeText(this, mobileNumber + " " + password, Toast.LENGTH_SHORT).show();
    }

    void errorCatcherEditText() {

        /*This is used to notify user if they have created some mistake
         * But this activates when user try to login with errors*/

        if (mobileNumber.length() != 10) etMobileNumberLayout.setError("Invalid Mobile Number");
        if (password.length() < 6) etPasswordLayout.setError("Minimum 6 characters");


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