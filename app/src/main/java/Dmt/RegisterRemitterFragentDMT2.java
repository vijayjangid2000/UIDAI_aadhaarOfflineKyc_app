package Dmt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.android.volley.AuthFailureError;
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

public class RegisterRemitterFragentDMT2 extends Fragment {

    private static final String ARG_MOBILE = "param1";
    private static final String TAG = "RemitterRegister";
    EditText ed_first_name;
    EditText ed_last_name;
    EditText ed_pincode;
    private int layout = R.layout.fragment_register_remitter;
    private String strMobileNumber;
    private String strFirstName;
    private String strLastName;
    private String strPincode;
    private Button btn_next;
    private ProgressBar progressRR;
    private RelativeLayout rl_progress;
    private TextView tv_error;
    private OnFragmentInteractionListener mListener;

    public RegisterRemitterFragentDMT2() {
        // Required empty public constructor
    }


    public static RegisterRemitterFragentDMT2 newInstance(String mobileNumber) {
        RegisterRemitterFragentDMT2 fragment = new RegisterRemitterFragentDMT2();
        Bundle args = new Bundle();
        args.putString(ARG_MOBILE, mobileNumber);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            strMobileNumber = getArguments().getString(ARG_MOBILE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_register_remitter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ed_first_name = view.findViewById(R.id.ed_first_name);
        ed_last_name = view.findViewById(R.id.ed_last_name);
        ed_pincode = view.findViewById(R.id.ed_pincode);
        btn_next = view.findViewById(R.id.btn_next);
        progressRR = view.findViewById(R.id.progressRR);
        rl_progress = view.findViewById(R.id.rl_progress);
        tv_error = view.findViewById(R.id.tv_error_hint);

        btn_next.setOnClickListener(view1 -> {
            if (InternetConnection.isConnected(getActivity())) {
                if (verifyRemmitterField()) {
                    registerRemitter();
                }
            }
        });

    }

    private boolean verifyRemmitterField() {

        boolean isVerify = false;
        strFirstName = ed_first_name.getText().toString();
        strLastName = ed_last_name.getText().toString();
        strPincode = ed_pincode.getText().toString();

        if (!strFirstName.isEmpty()) {
            if (!strLastName.isEmpty()) {
                if (!strPincode.isEmpty()) {
                    isVerify = true;
                }
            } else MakeToast.show(getActivity(), "Last name can't be empty!");
        } else MakeToast.show(getActivity(), "First name can't be empty!");
        return isVerify;
    }

    private void registerRemitter() {

        progressRR.setVisibility(View.VISIBLE);
        btn_next.setVisibility(View.GONE);
        rl_progress.setVisibility(View.VISIBLE);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.REGISTER_REMITTER_DMT2,
                response -> {
                    try {


                        progressRR.setVisibility(View.GONE);
                        btn_next.setVisibility(View.VISIBLE);
                        rl_progress.setVisibility(View.GONE);


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
                                mListener.onFragmentInteraction(verifyId, 1);
                            }
                        }
                    } catch (JSONException e) {
                        btn_next.setVisibility(View.VISIBLE);
                        progressRR.setVisibility(View.GONE);
                        rl_progress.setVisibility(View.GONE);
                        showConnectionError("Something went wrong!\nTry again");
                    }
                },
                error -> {
                    btn_next.setVisibility(View.VISIBLE);
                    progressRR.setVisibility(View.GONE);
                    rl_progress.setVisibility(View.GONE);
                    showConnectionError("Something went wrong!\nTry again");
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", SharedPref.getInstance(getActivity()).getToken());
                params.put("userId", String.valueOf(SharedPref.getInstance(getActivity()).getId()));
                params.put("fName", strFirstName);
                params.put("lName", strLastName);
                params.put("pinCode", strPincode);
                params.put("mobileNumber", strMobileNumber);
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

    private void showConnectionError(String message) {


        tv_error.setText(message);
        tv_error.setVisibility(View.VISIBLE);


    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String data, int type);
    }
}
