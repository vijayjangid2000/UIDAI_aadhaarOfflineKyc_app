package com.vijayjangid.aadharkyc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Register_activity extends AppCompatActivity {

    final String TAG = "vijay"; // for log

    TextInputEditText etMobileNumber, etPassword, etFullName;
    TextInputLayout etMobileNumberLayout, etPasswordLayout, etFullNameLayout;
    TextInputLayout et_EnterOtp2Layout;  // usage - if user types wrong otp
    TextView tvRegister;
    Animation animAlpha;
    // Alert Dialog
    AlertDialog dialogView;
    // string data used in registration
    String mobileNumber, password, fullName, OTP;
    // firebase authentication
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // setting id's
        etMobileNumber = findViewById(R.id.mobileREt);
        etMobileNumberLayout = findViewById(R.id.mobileREtL);
        etPassword = findViewById(R.id.passwordEtR);
        etPasswordLayout = findViewById(R.id.passwordEtRL);
        tvRegister = findViewById(R.id.registerTv);
        etFullName = findViewById(R.id.fullNameREt);
        etFullNameLayout = findViewById(R.id.fullNameREtL);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Sign Up");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // setting alpha animations
        animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_aplha);


        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvRegister.startAnimation(animAlpha);
                if (checkConnected()) startRegistrationProcess();
            }
        });

    }

    void startRegistrationProcess() {

        /* Here we check few things
         * 1. mobile number is valid or not, locally
         * 2. password length is valid
         * 3. then we creates callbacks for authentication
         * 4. then we ask for the OTP
         * 5. then we show alertBox where user types otp
         * and after that when verify clicked (& succeeds) we move to next Page*/

        mobileNumber = Objects.requireNonNull(etMobileNumber.getText()).toString().trim();
        password = Objects.requireNonNull(etPassword.getText()).toString().trim();
        fullName = Objects.requireNonNull(etFullName.getText()).toString().trim();

        if (mobileNumber.length() != 10 || password.length() < 6 ||
                (fullName.length() < 5 && !fullName.contains(" "))) {
            errorCatcherEditText();
            return;
        }

        showProgressBar(true, "Please wait, Sending OTP");

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Toast.makeText(Register_activity.this, "Registration Successful"
                        , Toast.LENGTH_LONG).show();
                FirebaseAuth.getInstance().getFirebaseAuthSettings()
                        .setAppVerificationDisabledForTesting(true);
                signInWithPhoneAuthCredential(null, credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    Toast.makeText(Register_activity.this, "Invalid Mobile Number",
                            Toast.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(Register_activity.this, "Please try again after 30 Minutes",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Register_activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                dialogView.cancel();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;

                dialogView.cancel();
                showOTPTextBoxAlert();
            }
        };

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // below line will disable captcha verification
        firebaseAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
        // below code sends OTP on user phone number
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber("+91" + mobileNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    void showOTPTextBoxAlert() {
        View view = getLayoutInflater().inflate(R.layout.alertview_otp_dialog, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        final TextView cancelOtp_Tvb, tvb_verifyNow, otp_message_tv;
        final TextInputLayout lt_EnterOtp = view.findViewById(R.id.enterOtpLt);
        et_EnterOtp2Layout = lt_EnterOtp;
        final TextInputEditText et_EnterOtp = view.findViewById(R.id.enterOtpEt);
        final CircularProgressIndicator loader_otpView = view.findViewById(R.id.otp_progress_bar);
        cancelOtp_Tvb = view.findViewById(R.id.cancelOtp_Tvb);
        tvb_verifyNow = view.findViewById(R.id.verifyNow_tvb);
        otp_message_tv = view.findViewById(R.id.otp_message_tv);
        loader_otpView.setVisibility(View.GONE);

        final AlertDialog.Builder alertBldr_otp_box = new AlertDialog.Builder(this);
        alertBldr_otp_box.setCancelable(false).setIcon(null);
        dialogView = alertBldr_otp_box.create();
        dialogView.setView(view);
        dialogView.show();

        otp_message_tv.setText("Message Sent On Mobile Number:\n+91 " + mobileNumber);

        tvb_verifyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvb_verifyNow.startAnimation(animAlpha);

                et_EnterOtp.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (et_EnterOtp.getText().toString().trim().length() < 4
                                || et_EnterOtp.getText() == null) {
                            lt_EnterOtp.setError("Please Enter valid OTP");
                        } else {
                            lt_EnterOtp.setError(null);
                        }
                    }
                });


                if (et_EnterOtp.getText().toString().trim().length() < 4
                        || et_EnterOtp.getText() == null) {
                    lt_EnterOtp.setError("Please Enter valid OTP");
                    return;
                }


                loader_otpView.setVisibility(View.VISIBLE);

                OTP = et_EnterOtp.getText().toString().trim();

                signInWithPhoneAuthCredential(loader_otpView,
                        PhoneAuthProvider.getCredential(mVerificationId, OTP));
            }
        });

        cancelOtp_Tvb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogView.cancel();
            }
        });
    }

    void signInWithPhoneAuthCredential(final CircularProgressIndicator loader_otpView,
                                       PhoneAuthCredential credential) {

        // this is final sign In using credentials
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            dialogView.hide();
                            startActivity(new Intent(Register_activity.this, HomePage_activity.class));
                            finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                et_EnterOtp2Layout.setError("OTP is incorrect, try again");
                                if (loader_otpView != null) loader_otpView.setVisibility(View.GONE);
                                ;
                            }
                        }
                    }
                });
    }

    // this checks mobile, password typed correctly
    // (not connected with authentication at all)
    void errorCatcherEditText() {

        /*This is used to notify user if they have created some mistake
         * But this activates when user try to register with errors*/
        if (fullName.length() < 5 && !fullName.contains(" ")) {
            etFullNameLayout.setError("Please enter valid name");
        } else if (mobileNumber.length() != 10) {
            etMobileNumberLayout.setError("Invalid Mobile Number");
        } else if (password.length() < 6) {
            etPasswordLayout.setError("Minimum 6 characters");
        }


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

        etFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                fullName = Objects.requireNonNull(etFullName.getText()).toString().trim();

                if (fullName.length() < 5 && !fullName.contains(" ")) {
                    etFullNameLayout.setError("Please enter valid name");
                } else {
                    etFullNameLayout.setError(null);
                }
            }
        });

    }

    // show progress bar with message, can't be cancelled
    void showProgressBar(boolean showText, String text) {

        /*to customize the progress bar then go to
         * progressbar_viewxml.xml in layout folder*/

        View view = getLayoutInflater().inflate(R.layout.alertview_progressbar, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        CircularProgressIndicator lpi = view.findViewById(R.id.home_progress_bar);
        TextView textView = view.findViewById(R.id.progress_text_tv);
        if (showText) textView.setText(text);
        AlertDialog.Builder alertBldr_loading = new AlertDialog.Builder(this)
                .setCancelable(false);
        dialogView = alertBldr_loading.create();
        dialogView.setView(view);
        dialogView.show();
    }

    // method - to go to login Page if user is registered already
    void gotoLoginPage() {
        Intent intent = new Intent(Register_activity.this, Login_activity.class);
        startActivity(intent);
        finish();
    }

    // for checking internet connectivity
    public boolean checkConnected() {
        View view = getLayoutInflater().inflate(R.layout.alertview_no_internet, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        TextView textView = view.findViewById(R.id.no_internet_tv);
        textView.setText("You are Offline!\nPlease Connect to Internet");

        AlertDialog.Builder ab = new AlertDialog.Builder(Register_activity.this);
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
                Register_activity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

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
                            Toast.makeText(Register_activity.this, "Cannot Connect To Internet",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        // we can prevent this thread to start if internet does not work
        thread.start();
    }

    @Override
    protected void onStop() {
        Tovuti.from(this).stop();
        super.onStop();
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    // it finishes and can be accessed from login page only, one way
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}