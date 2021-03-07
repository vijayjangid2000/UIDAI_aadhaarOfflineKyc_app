package com.vijayjangid.aadharkyc.login;

import android.app.Activity;
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
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.vijayjangid.aadharkyc.HomePage_activity;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.UserData;
import com.vijayjangid.aadharkyc.in.RequestHandler;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.Manifest.permission.READ_PHONE_NUMBERS;

public class Register_activity extends AppCompatActivity {
    String mobileNumber, password, fullName,
            OTP, email, confirmPassword; // firebase authentication

    private int layout = R.layout.activity_register;

    TextInputEditText etMobileNumber, etPassword, etFullName, etEmail, etConfirmPassword;
    TextInputLayout etMobileNumberLayout, etPasswordLayout, etFullNameLayout, etEmailLayout, etConfirmPasswordLayout;

    TextInputLayout et_EnterOtp2Layout;  // usage - if user types wrong otp
    TextInputEditText et_EnterOtp2; // usage - for getting OTP automatically

    TextView tvb_verifyNow2; // usage textViewButton for automatic registration
    TextView tvRegister, tv_errorStatus;

    Animation animAlpha; // Alert Dialog
    AlertDialog dialogView; // String data used in registration

    View tempViewForSnackBar;
    ImageView ivb_showPassInfo;

    private String mVerificationId;

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
        etFullName = findViewById(R.id.fullNameREt);
        etFullNameLayout = findViewById(R.id.fullNameREtL);
        etEmail = findViewById(R.id.emailREt);
        etEmailLayout = findViewById(R.id.emailREtL);
        etConfirmPassword = findViewById(R.id.confirmPasswordEtR);
        etConfirmPasswordLayout = findViewById(R.id.confirmPasswordEtRL);
        tvRegister = findViewById(R.id.registerTv);
        tv_errorStatus = findViewById(R.id.tv_errorStatus);
        tempViewForSnackBar = tv_errorStatus.getRootView();
        ivb_showPassInfo = findViewById(R.id.ivb_showPassInfoR);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Sign Up");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // setting alpha animations
        animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_aplha);

        tvRegister.setOnClickListener(v -> {
            tvRegister.startAnimation(animAlpha);
            // if (checkConnected()) startSendingOtp(); (asks for OTP)
            if (checkConnected()) sendServerRequest();
        });

        ivb_showPassInfo.setOnClickListener(view -> showAlertPasswordInfo());

        setMobileNumberAutomatically();
    }

    void startSendingOtp() {

        /* Checks if input data is valid
         * 1. Send OTP, until it is correct
         * 2. If correct send the data to server
         * 3. Show errors in the textView */

        if (isUserInputInvalid()) return;

        showProgressBar(true, "Please wait, Sending OTP");

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                checkOtpCorrect(null, credential);
                showProgressBar(false, "");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    showTextError(2);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    showTextError(1);
                } else {
                    showTextError(3);
                }

                dialogView.dismiss();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;

                dialogView.dismiss();
                showOtpAlertDialog();
            }
        };

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // below line will disable captcha verification
        // firebaseAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
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

    void showOtpAlertDialog() {
        View view = getLayoutInflater().inflate(R.layout.layout_otp_dialog, null);
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

        tvb_verifyNow.setOnClickListener(v -> {

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

            checkOtpCorrect(loader_otpView,
                    PhoneAuthProvider.getCredential(mVerificationId, OTP));
        });

        cancelOtp_Tvb.setOnClickListener(v -> dialogView.dismiss());
    }

    void checkOtpCorrect(final CircularProgressIndicator loader_otpView,
                         PhoneAuthCredential credential) {

        String wrongOtpError = "Incorrect OTP, try again";

        // this is final sign In using credentials
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        dialogView.dismiss();
                        sendServerRequest();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            et_EnterOtp2Layout.setError(wrongOtpError);
                            if (loader_otpView != null) loader_otpView.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public void sendServerRequest() {

        if (isUserInputInvalid()) return;
        else showProgressBar(true, "Please wait");

        RequestQueue queue = Volley.newRequestQueue(this);

        String baseUrl = "https://prod.excelonestopsolution.com/";
        String registrationUrl = "mobileapp/api/registration";

        StringRequest request = new StringRequest(Request.Method.POST, baseUrl + registrationUrl,
                response -> {
                    try {
                        onServerResponse(response);
                    } catch (Exception error) {
                        showCustomSnack(etEmail.getRootView(), "Error: " + error,
                                true);
                    }
                },
                error -> {
                    dialogView.cancel();

                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        toast("Error: No Internet Connection");
                    } else if (error instanceof AuthFailureError) {
                        toast("Error: AuthFailureError");
                    } else if (error instanceof ServerError) {
                        toast("Error: ServerError");
                    } else if (error instanceof NetworkError) {
                        toast("Error: NetworkError");
                    } else if (error instanceof ParseError) {
                        toast("Error: ParseError");
                    } else {
                        toast("Error: " + error.getMessage());
                    }

                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", fullName);
                params.put("mobile", mobileNumber);
                params.put("email", email);
                params.put("password", password);
                params.put("confirm_password", password);
                return params;
            }
        };

        queue.add(request);

        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void onServerResponse(String response) throws Exception {

        JSONObject jsonObject = new JSONObject(response);
        int status = jsonObject.getInt("status");
        String message = jsonObject.getString("message");

        if (status == 1) {
            onSuccessfullyRegistered();
        } else if (status == 8001 && message.contains("The mobile has already been taken")) {
            showCustomSnack(tempViewForSnackBar, "Mobile number already registered, Use different number", true);
        } else if (status == 8001 && message.contains("email has already been taken")) {
            showCustomSnack(tempViewForSnackBar, "Email already registered, Use different email", true);
        } else {
            toast("Error: " + message);
        }

        dialogView.hide();
    }

    void onSuccessfullyRegistered() {
        saveUserDataLocally();
        Toast.makeText(Register_activity.this, "Registration Successful"
                , Toast.LENGTH_LONG).show();
        startActivity(new Intent(Register_activity.this, HomePage_activity.class));
        finish();
    }

    void saveUserDataLocally() {
        UserData userData = new UserData(this);
        userData.eraseData();
        userData.setMobile(mobileNumber);
        userData.setPassword(password);
        userData.setEmail(email);
        userData.setName(fullName);
        userData.setIsLoggedInAlready(true);
        userData.applyUpdate();
    }

    void showTextError(int errorCode) {

        String errorMessage = null;

        switch (errorCode) {
            case 1:
                errorMessage = "Too many OTP requests, wait for few hours or change number";
                break;
            case 2:
                errorMessage = "Invalid Mobile Number, Please try again";
                break;
            case 3:
                errorMessage = "Verification failed, please try again";
                break;
        }

        toast(errorMessage);
    }

    boolean isUserInputInvalid() {

        fullName = Objects.requireNonNull(etFullName.getText()).toString().trim();
        email = Objects.requireNonNull(etEmail.getText()).toString().trim();
        mobileNumber = Objects.requireNonNull(etMobileNumber.getText()).toString().trim();
        password = Objects.requireNonNull(etPassword.getText()).toString().trim();
        confirmPassword = Objects.requireNonNull(etConfirmPassword.getText()).toString().trim();

        if (!isValidName(fullName) || !isValid(email) ||
                !isValidMobile(mobileNumber) || !isValidPassword(password)
                || !confirmPassword.equals(password)) {
            textWatcherEditText();
            return true;
        }

        return false;
    }

    /* this checks mobile, password typed correctly, (not connected with authentication at all) */
    void textWatcherEditText() {

        String wrongPass = "Enter valid password";
        String wrongName = "Please enter valid name";
        String wrongEmail = "Enter valid E-mail";
        String wrongConfirmPa = "Passwords must match";
        String wrongMobile = "Invalid Mobile Number";

        /*This is used to notify user if they have created some mistake
         * But this activates when user try to register with errors*/
        if (!isValidName(fullName)) {
            etFullNameLayout.setError(wrongName);
        } else if (!isValidMobile(mobileNumber)) {
            etMobileNumberLayout.setError(wrongMobile);
        } else if (!isValidPassword(password)) {
            etPasswordLayout.setError(wrongPass);
        } else if (!isValid(email)) {
            etEmailLayout.setError(wrongEmail);
        } else if (!password.equals(confirmPassword)) {
            etConfirmPasswordLayout.setError(wrongConfirmPa);
        }

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

                if (!isValidName(fullName))
                    etFullNameLayout.setError(wrongName);
                else
                    etFullNameLayout.setError(null);

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
                if (!isValid(email)) {
                    etEmailLayout.setError(wrongEmail);
                } else etEmailLayout.setError(null);
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

                if (!isValidMobile(mobileNumber))
                    etMobileNumberLayout.setError(wrongMobile);
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
                if (!isValidPassword(password)) etPasswordLayout.setError(wrongPass);
                else etPasswordLayout.setError(null);
            }
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                confirmPassword = editable.toString();
                if (!confirmPassword.equals(password)) {
                    etConfirmPasswordLayout.setError(wrongConfirmPa);
                } else {
                    etConfirmPasswordLayout.setError(null);
                }
            }
        });
    }

    public boolean isValidPassword(String password) {

        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#&$*])"
                + "(?=\\S+$).{8,15}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the password is empty
        // return false
        if (password == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.
        Matcher m = p.matcher(password);

        // Return if the password
        // matched the ReGex
        return m.matches();
    }

    public boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public boolean isValidName(String name) {
        if (name.length() < 5 || name.length() > 50 || !name.contains(" ")) {
            return false;
        } else return true;
    }

    public boolean isValidMobile(String mobile) {
        if (mobile == null) return false;
        if (mobile.length() == 0) return false;

        char first = mobile.charAt(0);
        if ((first == '7' || first == '8' ||
                first == '9' || first == '6') && mobile.length() == 10) {
            return true;
        } else return false;
    }

    // show progress bar with message, can't be cancelled
    void showProgressBar(boolean showText, String text) {

        /*to customize the progress bar then go to
         * progressbar_viewxml.xml in layout folder*/

        View view = getLayoutInflater().inflate(R.layout.layout_progressbar, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        CircularProgressIndicator lpi = view.findViewById(R.id.home_progress_bar);
        TextView textView = view.findViewById(R.id.progress_text_tv);
        if (showText) textView.setText(text);
        AlertDialog.Builder alertBldr_loading = new AlertDialog.Builder(this)
                .setCancelable(false);
        dialogView = alertBldr_loading.create();
        dialogView.setView(view);
        Window window = dialogView.getWindow();
        if (window != null) window.setBackgroundDrawableResource(R.color.Transparent);
        dialogView.show();
    }

    // for checking internet connectivity
    public boolean checkConnected() {
        View view = getLayoutInflater().inflate(R.layout.layout_no_internet, null);
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

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    // It finishes and can be accessed from login page only, one way
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setMobileNumberAutomatically() {

        if (ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) ==
                PackageManager.PERMISSION_GRANTED) {

            try {
                TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                String mPhoneNumber = tMgr.getLine1Number();
                etMobileNumber.setText(mPhoneNumber.substring(2, 12));
            } catch (Exception ignored) {
            }

        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        requestPermissions(new String[]{READ_PHONE_NUMBERS}, 100);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 100) {
            if (ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) ==
                    PackageManager.PERMISSION_GRANTED) {

                try {
                    TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                    String mPhoneNumber = tMgr.getLine1Number();
                    etMobileNumber.setText(mPhoneNumber.substring(2, 12));
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Tovuti.from(this).stop();
    }

    public void showAlertPasswordInfo() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        AlertDialog alert = alertBuilder.create();

        View view = getLayoutInflater().inflate(R.layout.layout_password_info, null);
        alert.setView(view);
        alert.show();
    }

    void toast(String message) {
        showCustomSnack(tempViewForSnackBar, message, false);
    }

    public void showCustomSnack(View view, String message, boolean isIndefinite) {

        hideKeyboard(this);

        Snackbar snackbar = Snackbar.make(view.getRootView(), message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getColor(R.color.colorPrimary));
        if (isIndefinite) {
            snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
            snackBarView.setBackgroundColor(getColor(R.color.Red));
        }
        snackBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }

}