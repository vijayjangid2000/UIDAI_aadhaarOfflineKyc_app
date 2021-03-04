package a2z_wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class RegisterRemitterFragmetA2ZWallet extends Fragment {

    private static final String TAG = "RegisterRemitter";
    private static String ARG_PARAM = "arg_param";
    UserData userData;
    private String strMobileNumber;
    private String strFirstName;
    private String strLastName;
    private String strWalletType = "0";

    private Button btn_next;
    private ProgressBar progressRR;
    private RelativeLayout rl_progress;
    private TextView tv_error;
    private Spinner spn_walletType;
    private EditText ed_first_name;
    private EditText ed_last_name;
    private EditText ed_mobile;

    private OnFragmentInteractionListener mListener;

    public RegisterRemitterFragmetA2ZWallet() {
    }

    public static RegisterRemitterFragmetA2ZWallet newInstance(String strMobileNumber) {
        RegisterRemitterFragmetA2ZWallet fragment = new RegisterRemitterFragmetA2ZWallet();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, strMobileNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            strMobileNumber = getArguments().getString(ARG_PARAM);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_remitter_a2z_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userData = new UserData(getContext());
        ed_first_name = view.findViewById(R.id.ed_first_name);
        ed_last_name = view.findViewById(R.id.ed_last_name);
        ed_mobile = view.findViewById(R.id.ed_mobile);
        ed_mobile.setText(strMobileNumber);
        ed_mobile.setEnabled(false);
        ed_mobile.setAlpha(0.7f);
        btn_next = view.findViewById(R.id.btn_next);
        progressRR = view.findViewById(R.id.progressRR);
        rl_progress = view.findViewById(R.id.rl_progress);
        tv_error = view.findViewById(R.id.tv_error_hint);
        spn_walletType = view.findViewById(R.id.spn_walletType);

        String[] strsWalletType = {"Wallet", "KYC"};

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                R.layout.spinner_layout, strsWalletType);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spn_walletType.setAdapter(dataAdapter);
        spn_walletType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strWalletType = String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
        strMobileNumber = ed_mobile.getText().toString();

        if (!strFirstName.isEmpty()) {
            if (!strLastName.isEmpty()) {
                if (!strMobileNumber.isEmpty()) {
                    isVerify = true;
                } else MakeToast.show(getActivity(), "Mobile Number can't be empty");
            } else MakeToast.show(getActivity(), "Last name can't be empty!");
        } else MakeToast.show(getActivity(), "First name can't be empty!");
        return isVerify;
    }

    private void registerRemitter() {

        progressRR.setVisibility(View.VISIBLE);
        btn_next.setVisibility(View.GONE);
        rl_progress.setVisibility(View.VISIBLE);
        Log.e("url registerRemitter", "=" + APIs.REGISTER_REMITTER_A2ZWallet);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.REGISTER_REMITTER_A2ZWallet,
                response -> {
                    try {


                        progressRR.setVisibility(View.GONE);
                        btn_next.setVisibility(View.VISIBLE);
                        rl_progress.setVisibility(View.GONE);


                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("12")) {
                            MakeToast.show(getActivity(), message);
                            mListener.onFragmentInteraction(1, strMobileNumber);
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
                        } else showConnectionError(message);
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
                params.put("token", userData.getToken());
                params.put("userId", userData.getId());
                params.put("fname", strFirstName);
                params.put("lname", strLastName);
                params.put("mobile", strMobileNumber);
                params.put("wallet", strWalletType);

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
        void onFragmentInteraction(int type, String data);
    }
}
