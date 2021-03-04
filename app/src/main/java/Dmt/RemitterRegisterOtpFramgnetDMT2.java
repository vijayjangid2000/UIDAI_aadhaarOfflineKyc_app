package Dmt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.in.SharedPref;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.InternetConnection;
import com.vijayjangid.aadharkyc.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class RemitterRegisterOtpFramgnetDMT2 extends Fragment implements TextWatcher {

    private static final String TAG = "RemiterRegisterOtp";
    private static final String ARG_MOBILE = "mobile";
    private static final String ARG_VERIFY_ID = "verifyId";
    EditText editText_one;
    EditText editText_two;
    EditText editText_three;
    EditText editText_four;
    EditText editText_five;
    EditText editText_six;
    Button btn_submit_otp;
    TextView btn_resend_otp;
    private int layout = R.layout.fragment_remitter_verify_fragment;
    private String strMobileNumber;
    private String strVerifId;
    private String strOtp;
    private TextView tv_error;
    private ProgressBar progressRR;
    private RelativeLayout rl_progress;
    private OnFragmentInteractionListener mListener;

    public RemitterRegisterOtpFramgnetDMT2() {
    }


    public static RemitterRegisterOtpFramgnetDMT2 newInstance(String mobileNumber, String verifyId) {
        RemitterRegisterOtpFramgnetDMT2 fragment = new RemitterRegisterOtpFramgnetDMT2();
        Bundle args = new Bundle();
        args.putString(ARG_MOBILE, mobileNumber);
        args.putString(ARG_VERIFY_ID, verifyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            strMobileNumber = getArguments().getString(ARG_MOBILE);
            strVerifId = getArguments().getString(ARG_VERIFY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_remitter_verify_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        progressRR = view.findViewById(R.id.progressRR);
        rl_progress = view.findViewById(R.id.rl_progress);
        tv_error = view.findViewById(R.id.tv_error_hint);
        editText_one = view.findViewById(R.id.editTextone);
        editText_two = view.findViewById(R.id.editTexttwo);
        editText_three = view.findViewById(R.id.editTextthree);
        editText_four = view.findViewById(R.id.editTextfour);
        editText_five = view.findViewById(R.id.editTextfive);
        editText_six = view.findViewById(R.id.editTextSix);

        editText_one.addTextChangedListener(this);
        editText_two.addTextChangedListener(this);
        editText_three.addTextChangedListener(this);
        editText_four.addTextChangedListener(this);
        editText_five.addTextChangedListener(this);
        editText_six.addTextChangedListener(this);

        btn_submit_otp = view.findViewById(R.id.btn_submit_otp);
        btn_resend_otp = view.findViewById(R.id.btn_resend_otp);
        btn_submit_otp = view.findViewById(R.id.btn_submit_otp);
        btn_submit_otp.setOnClickListener(view1 -> {
            if (InternetConnection.isConnected(getActivity())) {
                tv_error.setVisibility(View.GONE);
                if (validateFields())
                    verifyOtp();
            }
        });
        btn_resend_otp.setOnClickListener(view1 -> {
            resendOtp();
        });
    }

    private void resendOtp() {

        btn_resend_otp.setAlpha(0.7f);
        btn_resend_otp.setText("Requesting...");
        btn_resend_otp.setEnabled(false);
        btn_submit_otp.setEnabled(false);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.REMITTER_VERIFICATION_RESEND_OTP,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String statuscode = jsonObject.getString("statuscode");
                        String status = jsonObject.getString("status");
                        if (statuscode.equalsIgnoreCase("TXN")) {

                        }
                        MakeToast.show(getActivity(), status);

                    } catch (JSONException e) {
                        showConnectionError("Something went wrong! Try again");
                    }
                    btn_resend_otp.setAlpha(1f);
                    btn_resend_otp.setText("Request otp");
                    btn_resend_otp.setEnabled(true);
                    btn_submit_otp.setEnabled(true);
                },
                error -> {

                    showConnectionError("Something went wrong! Try again");
                    btn_resend_otp.setAlpha(1f);
                    btn_resend_otp.setText("Request otp");
                    btn_resend_otp.setEnabled(true);
                    btn_submit_otp.setEnabled(true);
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", SharedPref.getInstance(getActivity()).getToken());
                params.put("userId", String.valueOf(SharedPref.getInstance(getActivity()).getId()));
                params.put("mobile_number", strMobileNumber);
                return params;
            }
        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }

    private void verifyOtp() {
        progressRR.setVisibility(View.VISIBLE);
        btn_submit_otp.setVisibility(View.GONE);
        rl_progress.setVisibility(View.VISIBLE);

        btn_resend_otp.setEnabled(false);

        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.REMITTER_VERIFICATION_DMT2,
                response -> {
                    try {


                        progressRR.setVisibility(View.GONE);
                        btn_submit_otp.setVisibility(View.VISIBLE);
                        rl_progress.setVisibility(View.GONE);
                        btn_resend_otp.setEnabled(true);


                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        if (status.equalsIgnoreCase("200")) {
                            String message = jsonObject.getString("message");
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            String message = jsonObject.getString("message");
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        } else {
                            String statuscode = jsonObject.getString("statuscode");
                            if (statuscode.equalsIgnoreCase("TXN")
                                    && status.equalsIgnoreCase("OTP sent successfully")) {

                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                JSONObject jsonObject2 = jsonObject1.getJSONObject("remitter");
                                String verifyId = jsonObject2.getString("id");
                                int isVerified = jsonObject2.getInt("is_verified");

                                if (isVerified == 1) {
                                    mListener.onFragmentInteraction(verifyId, 2);
                                } else {
                                    MakeToast.show(getActivity(), "Verification failed !");
                                }

                            } else if (statuscode.equalsIgnoreCase("ERR")) {
                                MakeToast.show(getActivity(), status);
                            }
                        }

                    } catch (JSONException e) {
                        btn_submit_otp.setVisibility(View.VISIBLE);
                        progressRR.setVisibility(View.GONE);
                        rl_progress.setVisibility(View.GONE);
                        showConnectionError("Something went wrong! Try again");
                    }
                },
                error -> {
                    btn_submit_otp.setVisibility(View.VISIBLE);
                    btn_resend_otp.setEnabled(true);
                    progressRR.setVisibility(View.GONE);
                    rl_progress.setVisibility(View.GONE);
                    showConnectionError("Something went wrong! Try again");
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", SharedPref.getInstance(getActivity()).getToken());
                params.put("userId", String.valueOf(SharedPref.getInstance(getActivity()).getId()));
                params.put("mobileNumber", strMobileNumber);
                params.put("remitterVerifyId", strVerifId);
                params.put("remitterOTP", strOtp);
                return params;
            }
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
        String srtOtp5 = editText_five.getText().toString();
        String srtOtp6 = editText_six.getText().toString();

        if (!srtOtp1.isEmpty()) {
            if (!srtOtp2.isEmpty()) {
                if (!srtOtp3.isEmpty()) {
                    if (!srtOtp4.isEmpty()) {
                        if (!srtOtp5.isEmpty()) {
                            if (!srtOtp6.isEmpty()) {
                                strOtp = srtOtp1 + srtOtp2 + srtOtp3 + srtOtp4 + srtOtp5 + srtOtp6;
                                isValid = true;
                            } else MakeToast.show(getActivity(), "Otp can't be blank!");
                        } else MakeToast.show(getActivity(), "Otp can't be blank!");
                    } else MakeToast.show(getActivity(), "Otp can't be blank!");
                } else MakeToast.show(getActivity(), "Otp can't be blank!");
            } else MakeToast.show(getActivity(), "Otp can't be blank!");
        } else MakeToast.show(getActivity(), "Otp can't be blank!");
        return isValid;
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
            if (editText_four.length() == 1) {
                editText_five.requestFocus();
            }
            if (editText_five.length() == 1) {
                editText_six.requestFocus();
            }
        } else if (editable.length() == 0) {
            if (editText_six.length() == 0) {
                editText_five.requestFocus();
            }
            if (editText_five.length() == 0) {
                editText_four.requestFocus();
            }
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String data, int type);
    }

}
