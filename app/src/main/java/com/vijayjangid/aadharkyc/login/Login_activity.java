package com.vijayjangid.aadharkyc.login;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
import com.vijayjangid.aadharkyc.HomePage_activity;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.UserData;
import com.vijayjangid.aadharkyc.in.RequestHandler;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Login_activity extends AppCompatActivity {

    // for press back again to exit
    boolean doubleBackToExitPressedOnce = false;
    private String TAG = "LoginActivity: tiger -> \n"; // tag for log
    private TextInputEditText et_MobileNumber, et_Password;
    private TextInputLayout et_MobileNumberLayout, et_PasswordLayout;
    private TextView tvb_ForgotPass, tvb_Login, tvb_GoSignUp;
    private Animation animAlpha;
    // string data used in logging IN
    private String mobileNumber, password, hardwareSerialNumber,
            deviceImei, deviceName, mobileEncrypted, passwordEncrypted;
    private TextInputLayout et_EnterOtp2Layout;  // usage - if user types wrong otp
    private TextInputEditText et_EnterOtp2; // usage - for getting OTP automatically
    private TextView tvb_verifyNow2; // usage textViewButton for automatic registration
    private AlertDialog dialogView; // usage - to hide alertView globally
    private ImageView ivb_showPassInfo;
    private View zSnack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        // setting id's
        et_MobileNumberLayout = findViewById(R.id.phoneEtL);
        et_MobileNumber = findViewById(R.id.phoneEt);
        et_PasswordLayout = findViewById(R.id.passwordEtL);
        et_Password = findViewById(R.id.passwordEt);
        tvb_ForgotPass = findViewById(R.id.forgotTv);
        tvb_Login = findViewById(R.id.loginTv);
        tvb_GoSignUp = findViewById(R.id.goSignupTv);
        ivb_showPassInfo = findViewById(R.id.ivb_showPassInfo);

        // setting the alpha animations
        animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_aplha);
        tvb_ForgotPass.setOnClickListener(v -> {
            tvb_ForgotPass.setAlpha((float) 0.5);
            forgotPassword();
        });

        tvb_Login.setOnClickListener(v -> {
            if (checkConnected()) startLogin();
            else
                showCustomSnack(zSnack, "No Internet, Check connection and try again.", true, true);
        });

        String html = "<p>Don't Have an Account? <strong>" +
                "<span style=\"color: #004063;\">Sign up now</span></strong></p>";
        tvb_GoSignUp.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
        tvb_GoSignUp.setOnClickListener(v -> {
            tvb_GoSignUp.setAlpha((float) 0.5);
            openSignUp();
        });

        tvb_ForgotPass.setOnClickListener(view -> showCustomSnack(zSnack, "Forgot Password Clicked", false, false));

        ivb_showPassInfo.setOnClickListener(view -> showAlertPasswordInfo());

        zSnack = tvb_ForgotPass.getRootView();

        et_MobileNumber.setText("8875673907");
        et_Password.setText("A2z@9876");
    }

    /* flow of login is below*/

    void startLogin() {
        et_MobileNumber.clearFocus();
        et_Password.clearFocus();

        mobileNumber = Objects.requireNonNull(et_MobileNumber.getText()).toString().trim();
        password = Objects.requireNonNull(et_Password.getText()).toString().trim();
        getDeviceInformation();

        if (!isValidMobile(mobileNumber) || password.length() < 6) {
            tvb_Login.startAnimation(animAlpha);
            errorCatcherEditText();
        } else {
            sendLoginRequest();
        }
    }

    void sendLoginRequest() {

        getEncryptedDetails();

        showProgressBar(true, "Please wait, Logging In");

        RequestQueue queue = Volley.newRequestQueue(this);

        String baseUrl = "https://prod.excelonestopsolution.com/";
        String registrationUrl = "mobileapp/api/agentLogin";

        StringRequest request = new StringRequest(Request.Method.POST, baseUrl + registrationUrl,
                response -> {
                    try {
                        onServerResponse(response);
                    } catch (Exception e) {
                        showCustomSnack(zSnack, "Error: " + e.getMessage(), true, true);
                    }
                },
                error -> {
                    dialogView.dismiss();
                    String temp = "Error";
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        temp = "Error: No Internet Connection";
                    } else if (error instanceof AuthFailureError) {
                        temp = "Error: AuthFailureError";
                    } else if (error instanceof ServerError) {
                        temp = "Error: ServerError";
                    } else if (error instanceof NetworkError) {
                        temp = "Error: NetworkError";
                    } else if (error instanceof ParseError) {
                        temp = "Error: ParseError";
                    }

                    showCustomSnack(zSnack, temp, true, true);

                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("case", "FIRST");
                params.put("mobileNumber", mobileEncrypted);
                params.put("password", passwordEncrypted);
                params.put("imei", deviceImei);
                params.put("deviceName", deviceName);
                params.put("hardwareSerialNumber", hardwareSerialNumber);

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

    void onServerResponse(String response) throws JSONException {
        dialogView.dismiss();

        JSONObject object = new JSONObject(response);
        Log.d(TAG, "onServerResponse: \n\n" + response + "\n\n");
        int status = object.getInt("status");

        String tempError = "Error";

        if (status == 1) {
            try {
                onLoginSuccessful(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (status == 4 || status == 700) {
            showCustomSnack(zSnack, "OTP sent to " + mobileNumber + ".", true, false);
            showOtpAlertDialog();
        } else if (status == 108) {
            tempError = "Due to three unsuccessful attempts, " +
                    "Your account is locked for 24 Hours. " +
                    "Please Login with OTP.";
            showCustomSnack(zSnack, tempError, true, true);
        } else {
            tempError = "Error: " + object.getString("message");
            showCustomSnack(zSnack, tempError, true, true);
        }
    }

    void onLoginSuccessful(String response) throws Exception {
        // success
        JSONObject object = new JSONObject(response);
        Log.e("login resp", "=" + object.toString());

        UserData userData = new UserData(this);
        userData.eraseData(); // this will set all values to "";

        userData.setStatus(String.valueOf(object.getInt("status")));
        userData.setChangePin(String.valueOf(0));//object.getInt("changePin")
        userData.setId(String.valueOf(object.getInt("id")));
        userData.setToken(object.getString("token"));
        userData.setName(object.getString("name"));
        userData.setMobile(object.getString("mobile"));
        userData.setPassword(password);
        userData.setEmail(object.getString("email"));
        userData.setUserBalance(object.getString("user_balance"));
        userData.setOtpNumber(String.valueOf(object.optInt("otp_number")));
        userData.setProfilePicture(object.getString("profile_picture"));
        userData.setRoleId(String.valueOf(object.getInt("role_id")));
        userData.setMessage(object.getString("message"));
        userData.setShopName(object.getString("shop_name"));
        userData.setAddress(object.getString("address"));
        userData.setShop_address(object.getString("shop_address"));
        userData.setJoiningDate(object.getString("joining_date"));
        userData.setLastUpdate(object.getString("last_update"));
        userData.setPanCardPicture(object.getString("pancard_img"));
        userData.setAadhaarFrontPic(object.getString("adhaar_img"));
        userData.setAadharBackPic(object.getString("adhaar_back_img"));
        userData.setStateId(object.getString("state_id"));
        userData.setPinCode(object.getString("pincode"));
        userData.setAadhaarCardNo(object.getString("adhaar"));
        userData.setIsLoggedInAlready(true);
        userData.setKycStatus(String.valueOf(1));
        userData.setPopup(object.getString("popup"));
        userData.setShowPopup(false);
        userData.applyUpdate();

        //sharedPref.setFather(object.getString("father_name"));
        //sharedPref.setDob(object.getString("dob"));
        //sharedPref.setGender(object.getString("gender"));
        //sharedPref.setCity(object.getString("city"));
        /* sharedPref.setPan(object.getString("pan"));
        if(!object.getString("kyc_status").equalsIgnoreCase("null"))
        sharedPref.setKYC_STATUS(Integer.parseInt(object.getString("kyc_status")));
        else sharedPref.setKYC_STATUS(0);*/

        int role_id = Integer.parseInt(userData.getRoleId());

        if (role_id == 5 || role_id == 4) {
            startActivity(new Intent(this, HomePage_activity.class));
            Toast.makeText(this, "Login Successful, Welcome Back", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            showCustomSnack(tvb_ForgotPass.getRootView(), "Sorry, This app is only available for retailers and distributors"
                    , true, true);
        }

    }

    /* useful methods below */

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

            String OTP = et_EnterOtp.getText().toString().trim();

            checkOtpCorrect(OTP);
        });

        cancelOtp_Tvb.setOnClickListener(v -> dialogView.dismiss());
    }

    void checkOtpCorrect(String Otp) {

        String wrongOtpError = "Incorrect OTP, try again";

        if (true) {
            et_EnterOtp2Layout.setError(wrongOtpError);
        } else {

        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        showCustomSnack(zSnack, "Press BACK again to exit", false, false);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    void forgotPassword() {
        // send mail to change password using firebase service
        showCustomSnack(zSnack, "Forgot password clicked", false, false);
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

    void errorCatcherEditText() {

        /*This is used to notify user if they have created some mistake
         * But this activates when user try to login with errors*/

        if (!isValidMobile(mobileNumber))
            et_MobileNumberLayout.setError("Invalid Mobile Number");
        else if (password.length() < 6) et_PasswordLayout.setError("Minimum 6 characters");


        et_MobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mobileNumber = Objects.requireNonNull(et_MobileNumber.getText()).toString().trim();

                if (!isValidMobile(mobileNumber))
                    et_MobileNumberLayout.setError("Invalid Mobile Number");
                else et_MobileNumberLayout.setError(null);
            }
        });
        et_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                password = Objects.requireNonNull(et_Password.getText()).toString().trim();
                if (password.length() < 6) et_PasswordLayout.setError("Minimum 6 characters");
                else et_PasswordLayout.setError(null);
            }
        });
    }

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

    @Override
    protected void onResume() {
        super.onResume();
        tvb_GoSignUp.setAlpha(1);
        tvb_ForgotPass.setAlpha(1);
    }

    // for checking internet connectivity
    public boolean checkConnected() {
        View view = getLayoutInflater().inflate(R.layout.layout_no_internet, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        TextView textView = view.findViewById(R.id.no_internet_tv);
        textView.setText("You are Offline!\nPlease Connect to Internet");

        AlertDialog.Builder ab = new AlertDialog.Builder(Login_activity.this);
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
                Login_activity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

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
                            showCustomSnack(zSnack, "Cannot Connect To Internet", false, true);
                        }
                    });
                }
            }
        });

        // we can prevent this thread to start if internet does not work
        thread.start();
    }

    void getDeviceInformation() {
        // information saved in the global string at top

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
        } else {
            TelephonyManager telephonyManager = (TelephonyManager)
                    getSystemService(Context.TELEPHONY_SERVICE);
            deviceImei = telephonyManager.getDeviceSoftwareVersion();
            hardwareSerialNumber = Secure.ANDROID_ID;
            deviceName = Build.MODEL;

            if (deviceImei == null) deviceImei = "";
            if (deviceName == null) deviceName = "";
        }

    }

    void getEncryptedDetails() {

        String key = "OPENUSERIDPASSWO";

        mobileEncrypted = encrypt(mobileNumber, key);
        passwordEncrypted = encrypt(password, key);
    }

    void openSignUp() {
        Intent intent = new Intent(Login_activity.this, Register_activity.class);
        startActivity(intent);
    }

    private String encrypt(String value, String key) {

        byte[] crypted = null;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(value.getBytes());
        } catch (Exception e) {
            e.getStackTrace();
        }

        return new String(Base64.encodeBase64(crypted));
    }

    private String decrypt(String encrypted, String key, String initVector) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    // may be useful later, try to use it with a message
    public void showCustomToast(Context context, String message) {

        View view = getLayoutInflater().inflate(R.layout.layout_custom_toast, null);
        TextView textView = view.findViewById(R.id.tv_toast_message);
        textView.setText(message);

        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();

    }

    public void showAlertPasswordInfo() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        AlertDialog alert = alertBuilder.create();

        View view = getLayoutInflater().inflate(R.layout.layout_password_info, null);
        alert.setView(view);
        alert.show();
    }

    public void showCustomSnack(View view, String message,
                                boolean isIndefinite, boolean isError) {

        hideKeyboard(this);

        Snackbar snackbar = Snackbar.make(view.getRootView(), message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getColor(R.color.colorPrimary));
        if (isIndefinite) snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        if (isError) snackBarView.setBackgroundColor(getColor(R.color.Red));

        snackBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager)
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view,
        // so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one,
        // just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }

}
