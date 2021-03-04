package a2z_wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.UserData;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.InternetConnection;
import com.vijayjangid.aadharkyc.util.MakeToast;
import com.vijayjangid.aadharkyc.util.Security;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class RemitterRegisterOtpFragmentA2ZWallet extends Fragment implements TextWatcher {


    private static final String TAG = "RemitterRegisterOtp";
    private static final String ARG_PARAM1 = "param1";
    UserData userData;
    EditText editText_one;
    EditText editText_two;
    EditText editText_three;
    EditText editText_four;
    Button btn_submit_otp;
    Button btn_resend_otp;
    CountDownTimer ct;
    private OnFragmentInteractionListener mListener;
    private TextView tv_error;
    private TextView tv_time_otp;
    private ProgressBar progressRR;
    private RelativeLayout rl_progress;
    private String strOtp;
    private String strMobileNumber;


    public RemitterRegisterOtpFragmentA2ZWallet() {
        // Required empty public constructor
    }

    public static RemitterRegisterOtpFragmentA2ZWallet newInstance(String strMobile) {
        RemitterRegisterOtpFragmentA2ZWallet fragment = new RemitterRegisterOtpFragmentA2ZWallet();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, strMobile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            strMobileNumber = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_remitter_register_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userData = new UserData(getContext());
        progressRR = view.findViewById(R.id.progressRR);
        rl_progress = view.findViewById(R.id.rl_progress);
        tv_error = view.findViewById(R.id.tv_error_hint);
        tv_time_otp = view.findViewById(R.id.time_otp);
        editText_one = view.findViewById(R.id.editTextone);
        editText_two = view.findViewById(R.id.editTexttwo);
        editText_three = view.findViewById(R.id.editTextthree);
        editText_four = view.findViewById(R.id.editTextfour);

        editText_one.addTextChangedListener(this);
        editText_two.addTextChangedListener(this);
        editText_three.addTextChangedListener(this);
        editText_four.addTextChangedListener(this);

        btn_submit_otp = view.findViewById(R.id.btn_submit_otp);
        btn_submit_otp = view.findViewById(R.id.btn_submit_otp);
        btn_submit_otp.setOnClickListener(view1 -> {
            if (InternetConnection.isConnected(getActivity())) {
                if (validateFields()) {
                    ct.cancel();
                    verifyOtp();
                }
            }
        });
        btn_resend_otp = view.findViewById(R.id.btn_resend_otp);
        btn_resend_otp.setOnClickListener(view1 -> {


            ct.start();
            resendOtpRemitter();
            btn_resend_otp.setEnabled(false);
            btn_resend_otp.setBackgroundColor(getResources().getColor(R.color.black));
        });

        ct = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                tv_time_otp.setText("00:" + millisUntilFinished / 1000);
            }

            public void onFinish() {

                tv_time_otp.setText("00:00");
                btn_resend_otp.setEnabled(true);
                btn_resend_otp.setBackground(getResources().getDrawable(R.drawable.bg_light_blue_0));
            }
        }.start();
    }

    private void resendOtpRemitter() {

        Toast.makeText(getActivity(), "Please wait...", Toast.LENGTH_SHORT).show();
        Security security = new Security(APIs.ENCRYPTED_KEY);
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.RESEND_OTP,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        MakeToast.show(getActivity(), message);
                        /*if(status==1){
                            MakeToast.show(OTPLoginActivity.this,message);
                        }else{
                            Dialog dialog = AppDialogs.transactionStatus(OTPLoginActivity.this,message,1);
                            dialog.show();
                            Button btnOk = dialog.findViewById(R.id.btn_ok);
                            btnOk.setOnClickListener(view->dialog.dismiss());
                            dialog.setOnDismissListener(dialog1 -> {
                                Intent intent = new Intent(OTPLoginActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        }*/

                    } catch (JSONException e) {
                        MakeToast.show(getActivity(), e.getMessage());
                    }
                },
                error -> {

                    MakeToast.show(getActivity(), "error : " + error.getMessage());

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("mobileNumber", security.encrypt(strMobileNumber));
                param.put("type", security.encrypt("REMITTER_RESEND"));
                param.put("userId", userData.getId());
                param.put("token", userData.getToken());
                Log.d("resend", "==" + param.toString());
                return param;
            }
        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void resendOtp() {

        btn_resend_otp.setAlpha(0.7f);
        btn_resend_otp.setText("Requesting...");
        btn_resend_otp.setEnabled(false);
        btn_submit_otp.setEnabled(false);

        String url = APIs.VERIFY_NUMBER_A2ZWallet + "?" + APIs.USER_TAG + "="
                + userData.getId()
                + "&" + APIs.TOKEN_TAG + "=" + userData.getToken()
                + "&mobile_number=" + strMobileNumber;
        final StringRequest request = new StringRequest(Request.Method.GET,
                url,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        MakeToast.show(getActivity(), message);

                    } catch (JSONException e) {
                        showConnectionError("Something went wrong!\nTry again");
                    }
                    btn_resend_otp.setAlpha(1f);
                    btn_resend_otp.setText("Request otp");
                    btn_resend_otp.setEnabled(true);
                    btn_submit_otp.setEnabled(true);
                },
                error -> {

                    showConnectionError("Something went wrong!\nTry again");
                    btn_resend_otp.setAlpha(1f);
                    btn_resend_otp.setText("Request otp");
                    btn_resend_otp.setEnabled(true);
                    btn_submit_otp.setEnabled(true);
                }) {
        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private boolean validateFields() {
        boolean isValid = false;
        String srtOtp1 = editText_one.getText().toString();
        String srtOtp2 = editText_two.getText().toString();
        String srtOtp3 = editText_three.getText().toString();
        String srtOtp4 = editText_four.getText().toString();

        if (!srtOtp1.isEmpty()) {
            if (!srtOtp2.isEmpty()) {
                if (!srtOtp3.isEmpty()) {
                    if (!srtOtp4.isEmpty()) {
                        strOtp = srtOtp1 + srtOtp2 + srtOtp3 + srtOtp4;
                        isValid = true;
                    } else MakeToast.show(getActivity(), "Otp can't be blank!");
                } else MakeToast.show(getActivity(), "Otp can't be blank!");
            } else MakeToast.show(getActivity(), "Otp can't be blank!");
        } else MakeToast.show(getActivity(), "Otp can't be blank!");
        return isValid;
    }

    private void verifyOtp() {

        progressRR.setVisibility(View.VISIBLE);
        btn_submit_otp.setVisibility(View.GONE);
        rl_progress.setVisibility(View.VISIBLE);
        Log.e("url verifyOtp", "=" + APIs.VERIFY_NUMBER_OTP_A2ZWallet);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.VERIFY_NUMBER_OTP_A2ZWallet,
                response -> {
                    try {


                        progressRR.setVisibility(View.GONE);
                        btn_submit_otp.setVisibility(View.VISIBLE);
                        rl_progress.setVisibility(View.GONE);


                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("17")) {

                            mListener.onFragmentInteraction(2, "");

                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        } else showConnectionError("message : " + message);


                    } catch (JSONException e) {
                        btn_submit_otp.setVisibility(View.VISIBLE);
                        progressRR.setVisibility(View.GONE);
                        rl_progress.setVisibility(View.GONE);
                        showConnectionError("Something went wrong!\nTry again");
                    }
                },
                error -> {
                    btn_submit_otp.setVisibility(View.VISIBLE);
                    progressRR.setVisibility(View.GONE);
                    rl_progress.setVisibility(View.GONE);
                    showConnectionError("Something went wrong!\nTry again");
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", userData.getToken());
                params.put("userId", userData.getId());
                params.put("mobile", strMobileNumber);
                params.put("otp", strOtp);
                Log.e("url verifyOtp post", "=" + params.toString());
                return params;
            }
        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() == 1) {
            if (editText_one.length() == 1) {
                editText_two.requestFocus();
            }

            if (editText_two.length() == 1) {
                editText_three.requestFocus();
            }
            if (editText_three.length() == 1) {
                editText_four.requestFocus();
            }

        } else if (editable.length() == 0) {

            if (editText_four.length() == 0) {
                editText_three.requestFocus();
            }
            if (editText_three.length() == 0) {
                editText_two.requestFocus();
            }
            if (editText_two.length() == 0) {
                editText_one.requestFocus();
            }

        }
    }


    private void showConnectionError(String message) {

        tv_error.setText(message);
        tv_error.setVisibility(View.VISIBLE);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int type, String data);
    }
}
