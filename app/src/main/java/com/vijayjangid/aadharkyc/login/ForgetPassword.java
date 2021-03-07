package com.vijayjangid.aadharkyc.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.in.SharedPref;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.MakeToast;
import com.vijayjangid.aadharkyc.util.PasswordValidator;
import com.vijayjangid.aadharkyc.util.Security;
import com.vijayjangid.aadharkyc.util.SoftKeyboard;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ForgetPassword extends AppCompatActivity {

    String number, otp, password, confirm_password, token;
    CountDownTimer countDownTimer;
    private TextInputEditText et_Mobile, et_otp, et_password, et_ConfirmPassword;
    private Button btn_submitNumber, btn_resetPassword;
    private ProgressBar progress_number, progress_resetPassword;
    private TextInputLayout etl_Mobile, etl_Password, etl_ConfirmPassword;
    private TextView tv_otpMessage, tv_title, tv_error_hint, tv_resend;
    private LinearLayout ll_mobile, ll_password;
    private RelativeLayout rl_resetPassword;
    private boolean shouldBack = true;
    private ImageView ivb_showPassInfo;
    private View tempView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Reset Password");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });

        initView();

        btn_submitNumber.setOnClickListener(view -> {

            number = et_Mobile.getText().toString();
            if (!number.isEmpty()) {
                verifyNumber();
            } else
                showCustomSnack(et_Mobile, "Invalid Mobile Number", true, true);
        });
        btn_resetPassword.setOnClickListener(view -> {
            if (isValidInput())
                resetPassword();
        });
        et_Mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 10) {
                    SoftKeyboard.hide(ForgetPassword.this);
                    btn_submitNumber.callOnClick();
                }
            }
        });

    }

    private void initView() {

        et_Mobile = findViewById(R.id.et_mobileElec);
        etl_Mobile = findViewById(R.id.etl_mobileElec);
        etl_ConfirmPassword = findViewById(R.id.confirmPasswordEtRL);
        etl_Password = findViewById(R.id.passwordEtRL);

        btn_submitNumber = findViewById(R.id.btn_submitNumber);
        progress_number = findViewById(R.id.progress_submitNumber);
        ll_mobile = findViewById(R.id.ll_mobile);
        ll_password = findViewById(R.id.ll_password);
        tv_otpMessage = findViewById(R.id.tv_otpMessage);
        tv_title = findViewById(R.id.tv_title);
        tv_error_hint = findViewById(R.id.tv_error_hint);
        tv_resend = findViewById(R.id.tv_resend);

        et_otp = findViewById(R.id.et_Otp);
        et_password = findViewById(R.id.passwordEtR);
        et_ConfirmPassword = findViewById(R.id.confirmPasswordEtR);

        progress_resetPassword = findViewById(R.id.progress_resetPassword);
        rl_resetPassword = findViewById(R.id.rl_resetPassword);
        btn_resetPassword = findViewById(R.id.btn_resetPassword);
        ivb_showPassInfo = findViewById(R.id.ivb_showPassInfoR);
        tempView = et_Mobile.getRootView();

        tv_resend.setOnClickListener(view -> {
            // Toast.makeText(ForgetPassword.this, "enable", Toast.LENGTH_SHORT).show();
            tv_resend.setBackgroundColor(getResources().getColor(R.color.black));
            countDownTimer.start();
            resendOtp();
            tv_resend.setEnabled(false);
        });

        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                tv_resend.setText("00:" + millisUntilFinished / 1000);
            }

            public void onFinish() {

                tv_resend.setText("Resend");
                tv_resend.setEnabled(true);
            }
        };
        countDownTimer.start();

        ivb_showPassInfo.setOnClickListener(view -> showAlertPasswordInfo());

    }

    private void verifyNumber() {

        mobileProgress(true);

        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.FORGET_PASSWORD,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {
                            token = jsonObject.getString("token");
                            tv_otpMessage.setText(message);
                            ll_password.setVisibility(View.VISIBLE);
                            ll_mobile.setVisibility(View.GONE);
                            shouldBack = false;

                        } else {

                            tv_error_hint.setVisibility(View.VISIBLE);
                            tv_error_hint.setText(message);
                        }

                    } catch (JSONException e) {
                        e.getStackTrace();
                        tv_error_hint.setVisibility(View.VISIBLE);
                        tv_error_hint.setText("Something went wrong\nplease try letter");
                    }

                    mobileProgress(false);
                },
                error -> {

                    tv_error_hint.setVisibility(View.VISIBLE);
                    tv_error_hint.setText("Something went wrong\nplease try letter");
                    String errorMessage = "Error";

                    if (error instanceof NetworkError)
                        errorMessage = "network error";
                    else if (error instanceof ServerError)
                        errorMessage = "Server error";
                    else if (error instanceof AuthFailureError)
                        errorMessage = "AuthFailure error";
                    else if (error instanceof ParseError)
                        errorMessage = "Parse error";
                    else if (error instanceof TimeoutError)
                        errorMessage = "Timeout  error";

                    showCustomSnack(tempView, errorMessage, true, true);

                    mobileProgress(false);
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("mobile", number);
                return param;
            }

        };

        RequestHandler.getInstance(ForgetPassword.this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void resendOtp() {

        Toast.makeText(ForgetPassword.this, "Please wait...", Toast.LENGTH_SHORT).show();
        Security security = new Security(APIs.ENCRYPTED_KEY);
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.RESEND_OTP,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        MakeToast.show(ForgetPassword.this, message);
                        /*if(status==1){
                            MakeToast.show(OTPLoginActivity.this,message);
                        }else{
                            Dialog dialog = AppDialogs.transactionStatus(OTPLogin_activity.this,message,1);
                            dialog.show();
                            Button btnOk = dialog.findViewById(R.id.btn_ok);
                            btnOk.setOnClickListener(view->dialog.dismiss());
                            dialog.setOnDismissListener(dialog1 -> {
                                Intent intent = new Intent(OTPLogin_activity.this, Login_activity.class);
                                startActivity(intent);
                                finish();
                            });
                        }*/

                    } catch (JSONException e) {
                        MakeToast.show(ForgetPassword.this, e.getMessage());
                    }
                },
                error -> {

                    MakeToast.show(ForgetPassword.this, "error : " + error.getMessage());

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("mobileNumber", security.encrypt(number));
                param.put("type", security.encrypt("FORGET_PASSWORD"));
                param.put("userId", String.valueOf(SharedPref.getInstance(ForgetPassword.this).getId()));
                param.put("token", SharedPref.getInstance(ForgetPassword.this).getToken());
                Log.d("resend", "==" + param.toString());
                return param;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void resetPassword() {

        resetProgress(true);
        Log.e("STORE_PASSWORD", "=" + APIs.STORE_PASSWORD);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.STORE_PASSWORD,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        Log.e("resp store", "=" + jsonObject.toString());
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {
                            Dialog dialog = AppDialogs.transactionStatus(ForgetPassword.this,
                                    message, 1);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {
                                dialog.dismiss();
                                Intent intent = new Intent(ForgetPassword.this, Login_activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            });
                            dialog.setOnCancelListener(dialog1 -> {
                                Intent intent = new Intent(ForgetPassword.this, Login_activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            });


                            dialog.show();
                        } else {

                            tv_error_hint.setVisibility(View.VISIBLE);
                            tv_error_hint.setText(message);
                        }


                    } catch (JSONException e) {
                        e.getStackTrace();
                        tv_error_hint.setVisibility(View.VISIBLE);
                        tv_error_hint.setText("Something went wrong\nplease try letter");
                    }
                    resetProgress(false);
                },
                error -> {

                    tv_error_hint.setVisibility(View.VISIBLE);
                    tv_error_hint.setText("Something went wrong\nplease try again later");
                    String errorMessage = "Error";

                    if (error instanceof NetworkError)
                        errorMessage = "network error";
                    else if (error instanceof ServerError)
                        errorMessage = "Server error";
                    else if (error instanceof AuthFailureError)
                        errorMessage = "AuthFailure error";
                    else if (error instanceof ParseError)
                        errorMessage = "Parse error";
                    else if (error instanceof TimeoutError)
                        errorMessage = "Timeout  error";

                    showCustomSnack(tempView, errorMessage, true, true);

                    resetProgress(false);
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("otp", otp);
                param.put("token", token);
                param.put("password", password);
                param.put("password_confirmation", confirm_password);
                Log.d("password_", "==" + param.toString());
                return param;
            }

        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private boolean isValidInput() {
        boolean isValid = false;

        otp = et_otp.getText().toString();
        password = et_password.getText().toString();
        confirm_password = et_ConfirmPassword.getText().toString();

        if (!otp.isEmpty()) {
            if (!password.isEmpty()) {
                if (PasswordValidator.validate(password)) {
                    if (!confirm_password.isEmpty()) {
                        if (password.equals(confirm_password)) {
                            isValid = true;
                        } else showCustomSnack(tempView, "Password not matching!", true, true);
                    } else showCustomSnack(tempView, "Please confirm password!", true, true);
                } else
                    showCustomSnack(tempView, "Invalid Password", true, true);
            } else showCustomSnack(tempView, "Invalid Mobile Number", true, true);
        } else showCustomSnack(tempView, "Enter correct OTP", true, true);
        return isValid;
    }

    public void showAlertPasswordInfo() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        AlertDialog alert = alertBuilder.create();

        View view = getLayoutInflater().inflate(R.layout.layout_password_info, null);
        alert.setView(view);
        alert.show();
    }

    private void mobileProgress(boolean shouldVisible) {

        if (shouldVisible) {
            et_Mobile.setAlpha(0.4f);
            et_Mobile.setEnabled(false);
            btn_submitNumber.setVisibility(View.GONE);
            progress_number.setVisibility(View.VISIBLE);
            tv_error_hint.setVisibility(View.GONE);

        } else {
            et_Mobile.setAlpha(1f);
            et_Mobile.setEnabled(true);
            btn_submitNumber.setVisibility(View.VISIBLE);
            progress_number.setVisibility(View.GONE);
        }
    }

    private void resetProgress(boolean shouldVisible) {

        if (shouldVisible) {
            et_otp.setAlpha(0.4f);
            et_otp.setEnabled(false);
            et_ConfirmPassword.setAlpha(0.4f);
            et_ConfirmPassword.setEnabled(false);
            et_password.setAlpha(0.4f);
            et_password.setEnabled(false);
            btn_resetPassword.setVisibility(View.GONE);
            progress_resetPassword.setVisibility(View.VISIBLE);
            rl_resetPassword.setVisibility(View.VISIBLE);
            tv_error_hint.setVisibility(View.GONE);

        } else {
            et_otp.setAlpha(1f);
            et_otp.setEnabled(true);
            et_ConfirmPassword.setAlpha(1f);
            et_ConfirmPassword.setEnabled(true);
            et_password.setAlpha(1f);
            et_password.setEnabled(true);
            btn_resetPassword.setVisibility(View.VISIBLE);
            progress_resetPassword.setVisibility(View.GONE);
            rl_resetPassword.setVisibility(View.GONE);
            tv_error_hint.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        if (shouldBack) super.onBackPressed();
        else {
            ll_password.setVisibility(View.GONE);
            ll_mobile.setVisibility(View.VISIBLE);
            shouldBack = true;
        }
    }

    public void showCustomSnack(View view, String message,
                                boolean isIndefinite, boolean isError) {

        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();

        hideKeyboard(this);

        Snackbar snackbar = Snackbar.make(view.getRootView(), message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getColor(R.color.colorPrimary));
        if (isIndefinite) snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        if (isError) snackBarView.setBackgroundColor(getColor(R.color.Red));

        snackBarView.setOnClickListener(view1 -> {
            snackbar.dismiss();
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