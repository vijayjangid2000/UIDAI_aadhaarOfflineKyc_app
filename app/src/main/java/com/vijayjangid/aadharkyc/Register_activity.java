package com.vijayjangid.aadharkyc;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class Register_activity extends AppCompatActivity {

    private static final int REQ_USER_CONSENT = 200;

    TextInputEditText etMobileNumber, etPassword, etFullName, etEmail;
    TextInputLayout etMobileNumberLayout, etPasswordLayout, etFullNameLayout, etEmailLayout;
    TextInputLayout et_EnterOtp2Layout;  // usage - if user types wrong otp
    TextInputEditText et_EnterOtp2; // usage - for getting OTP automatically
    TextView tvb_verifyNow2; // usage textViewButton for automatic registration
    TextView tvRegister;
    Animation animAlpha;
    // Alert Dialog
    AlertDialog dialogView;
    // string data used in registration
    String mobileNumber, password, fullName, OTP, email;
    // firebase authentication
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    SmsBroadcastReceiver smsBroadcastReceiver;
    boolean isInformationValid;

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
        etEmail = findViewById(R.id.emailREt);
        etEmailLayout = findViewById(R.id.emailREtL);

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
                //if (checkConnected()) startRegistrationProcess();
                sendRegistrationRequest(null);
            }
        });

        etFullName.setText("Vijay Jangid");
        etMobileNumber.setText("8952962167");
        etEmail.setText("jangid84529vijay@gmail.com");
        etPassword.setText("Vijay84529@");
        setMobileNumberAutomatically();
    }

    void startRegistrationProcess() {

        /* Here we check few things
         * 1. mobile number is valid or not, locally
         * 2. password length is valid
         * 3. then we creates callbacks for authentication
         * 4. then we ask for the OTP
         * 5. then we show alertBox where user types otp
         * and after that when verify clicked (& succeeds) we move to next Page*/

        if (isAllDataInvalid()) return;

        showProgressBar(true, "Please wait, Sending OTP");

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                sendRegistrationRequest(credential);
                showProgressBar(false, "");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    Toast.makeText(Register_activity.this, "Invalid Mobile Number",
                            Toast.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(Register_activity.this, "Too many OTP requests, wait for few hours or change number",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Register_activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
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
        //firebaseAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
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
        et_EnterOtp2 = et_EnterOtp;
        final CircularProgressIndicator loader_otpView = view.findViewById(R.id.otp_progress_bar);
        cancelOtp_Tvb = view.findViewById(R.id.cancelOtp_Tvb);
        tvb_verifyNow = view.findViewById(R.id.verifyNow_tvb);
        tvb_verifyNow2 = tvb_verifyNow;
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
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        dialogView.hide();
                        startActivity(new Intent(Register_activity.this, HomePage_activity.class));
                        finish();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            et_EnterOtp2Layout.setError("OTP is incorrect, try again");
                            if (loader_otpView != null) loader_otpView.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public void sendRegistrationRequest(PhoneAuthCredential credential) {

        if (isAllDataInvalid()) return;

        RequestQueue queue = Volley.newRequestQueue(this);

        String baseUrl = "https://prod.excelonestopsolution.com/";
        String registrationUrl = "mobileapp/api/registration";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, baseUrl + registrationUrl, null,
                (Response.Listener<JSONObject>) response -> {
                    if (response.toString().contains("Registration Successful")) {
                        Toast.makeText(Register_activity.this, "Registration Successful"
                                , Toast.LENGTH_LONG).show();
                        //signInWithPhoneAuthCredential(null, credential);
                    } else {
                        Toast.makeText(this, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                    }

                    TextView textview = findViewById(R.id.textView3);
                    textview.setText(response.toString());
                },
                error -> {

                    TextView textview = findViewById(R.id.textView3);
                    textview.setText(error.getMessage());

                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", fullName);
                params.put("mobile", mobileNumber);
                params.put("password", password);
                params.put("confirm_password", password);
                params.put("email", email);

                return params;
            }
        };

        queue.add(jsonObjReq);
    }

    /* NOT EDITABLE METHODS BELOW */

    boolean isAllDataInvalid() {

        mobileNumber = Objects.requireNonNull(etMobileNumber.getText()).toString().trim();
        password = Objects.requireNonNull(etPassword.getText()).toString().trim();
        fullName = Objects.requireNonNull(etFullName.getText()).toString().trim();
        email = Objects.requireNonNull(etFullName.getText()).toString().trim();

        if (mobileNumber.length() != 10 || password.length() < 8 || password.length() > 15 ||
                fullName.length() < 5 || fullName.length() > 50) {
            errorCatcherEditText();
            return true;
        }

        return false;
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
        } else if (password.length() < 8) {
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
                if (password.length() < 8) etPasswordLayout.setError("Minimum 8 characters");
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

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                email = editable.toString();
                if (email.length() == 0 || !email.contains("@") || !email.contains(".")) {
                    etEmailLayout.setError("Please enter valid E-mail");
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
        super.onStop();
        Tovuti.from(this).stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsBroadcastReceiver);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                //That gives all message to us.
                // We need to get the code from inside with regex
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                // HERE IS THE MESSAGE WE WILL GET
                try {
                    getOtpFromMessage(message);
                } catch (Exception ignored) {
                }
            }
        }

    }

    private void getOtpFromMessage(String message) {
        // This will match any 6 digit number in the message
        Pattern pattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            et_EnterOtp2.setText(matcher.group(0));
            tvb_verifyNow2.performClick();
        }
    }

    private void setMobileNumberAutomatically() {

        if (ActivityCompat.checkSelfPermission(this, READ_SMS) ==
                PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) ==
                        PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) ==
                        PackageManager.PERMISSION_GRANTED) {

            TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();
            etMobileNumber.setText(mPhoneNumber.substring(2, 12));

        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, 100);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 100:
                TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                String mPhoneNumber = tMgr.getLine1Number();
                etMobileNumber.setText(mPhoneNumber.substring(2, 12));
                break;
        }
    }

}