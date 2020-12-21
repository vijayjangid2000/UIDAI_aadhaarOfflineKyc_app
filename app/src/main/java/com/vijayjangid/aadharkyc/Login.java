package com.vijayjangid.aadharkyc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class Login extends AppCompatActivity {
    TextInputEditText etMobileNumber, etPassword;
    TextInputLayout etMobileNumberLayout, etPasswordLayout;
    TextView tvForgotPass, tvLogin, tvGoSignUp;
    Animation animAlpha;

    // string data used in logging IN
    String mobileNumber, password;

    // for press back again to exit
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        // setting id's
        etMobileNumberLayout = findViewById(R.id.phoneEtL);
        etMobileNumber = findViewById(R.id.phoneEt);
        etPasswordLayout = findViewById(R.id.passwordEtL);
        etPassword = findViewById(R.id.passwordEt);
        tvForgotPass = findViewById(R.id.forgotTv);
        tvLogin = findViewById(R.id.loginTv);
        tvGoSignUp = findViewById(R.id.goSignupTv);

        // setting the alpha animations
        animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_aplha);
        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvForgotPass.setAlpha((float) 0.5);
                forgotPassword();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnected()) login();
            }
        });

        String html = "<p>Don't Have an Account? <strong>" +
                "<span style=\"color: #004063;\">Sign up now</span></strong></p>";
        tvGoSignUp.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
        tvGoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvGoSignUp.setAlpha((float) 0.5);
                startSignUp();
            }
        });
    }

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
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    void startSignUp() {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }

    void forgotPassword() {
        // send mail to change password using firebase service
        Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show();
    }

    void login() {
        mobileNumber = Objects.requireNonNull(etMobileNumber.getText()).toString().trim();
        password = Objects.requireNonNull(etPassword.getText()).toString().trim();

        if (mobileNumber.length() != 10 || password.length() < 6) {
            tvLogin.startAnimation(animAlpha);
            errorCatcherEditText();
            return;
        }

        showProgressBar(true, "Please wait, Logging In");
        Toast.makeText(this, mobileNumber + " " + password, Toast.LENGTH_SHORT).show();
    }

    void errorCatcherEditText() {

        /*This is used to notify user if they have created some mistake
         * But this activates when user try to login with errors*/

        if (mobileNumber.length() != 10) etMobileNumberLayout.setError("Invalid Mobile Number");
        else if (password.length() < 6) etPasswordLayout.setError("Minimum 6 characters");


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

    void showProgressBar(boolean showText, String text) {

        /*to customize the progress bar then go to
         * progressbar_viewxml.xml in layout folder*/

        View view = getLayoutInflater().inflate(R.layout.progressbar_viewxml, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        CircularProgressIndicator lpi = view.findViewById(R.id.home_progress_bar);
        TextView textView = view.findViewById(R.id.progress_text_tv);
        if (showText) textView.setText(text);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(view);
        alert.setCancelable(true);
        alert.setIcon(null);
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvGoSignUp.setAlpha(1);
        tvForgotPass.setAlpha(1);
    }

    // for checking internet connectivity
    public boolean checkConnected() {
        View view = getLayoutInflater().inflate(R.layout.no_internet_alert, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        TextView textView = view.findViewById(R.id.no_internet_tv);
        textView.setText("You are Offline!\nPlease Connect to Internet");

        AlertDialog.Builder ab = new AlertDialog.Builder(Login.this);
        ab.setCancelable(false);
        final AlertDialog dialogViewInternet = ab.create();
        dialogViewInternet.setView(view);

        // this checks if connected or not as a service
        Tovuti.from(this).monitor(new Monitor.ConnectivityListener() {
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast) {
                if (isConnected) {
                    if (dialogViewInternet.isShowing()) dialogViewInternet.cancel();
                    checkInternetAccess();
                } else {
                    if (!dialogViewInternet.isShowing()) dialogViewInternet.show();
                }
            }
        });

        // this checks if connected or not, but instantly
        // so that we can return instantly
        ConnectivityManager cm = (ConnectivityManager)
                Login.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null)
                return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI ||
                        activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE ||
                        activeNetwork.getType() == ConnectivityManager.TYPE_VPN;
        }

        return false;
    }

    void checkInternetAccess() {

        // This will toast a message if we cannot connect to internet in 10 seconds

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://www.geeksforgeeks.org/");
                    URLConnection connection = url.openConnection();
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.connect();
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Login.this, "Cannot Connect To Internet",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        // we can prevent this thread to start if internet does not work
        thread.start();
    }
}