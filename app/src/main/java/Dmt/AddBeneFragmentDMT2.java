package Dmt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AddBeneFragmentDMT2 extends Fragment {

    private static final String TAG = "AddBeneFragmentDMT2";
    private static final String ARG_MOBILE = "ARG_MOBILE";
    private static final String ARG_REMITER_ID = "ARG_REMITER_ID";
    int layout = R.layout.fragment_add_bene_dmt2;
    private String strMobileNumber;
    private String strRemitterId;
    private HashMap<String, String> hashBankList;
    private OnFragmentInteractionListener mListener;
    private EditText ed_beneName;
    private EditText ed_accountNumber;
    private EditText ed_ifsc_code;
    private AutoCompleteTextView atv_bank_name;
    private Button btn_addBeneficiary;
    private ProgressBar progressRR;
    private RelativeLayout rl_progress;
    private Button btn_verifyAccountNo;
    private TextView tv_error_hint;
    private String strBankName;
    private String strAccountNumber;
    private String strIfscCode;
    private String strBeneName;
    private ProgressBar progressBarAccountNo;

    public AddBeneFragmentDMT2() {
    }

    public static AddBeneFragmentDMT2 newInstance(String param1, String param2) {
        AddBeneFragmentDMT2 fragment = new AddBeneFragmentDMT2();
        Bundle args = new Bundle();
        args.putString(ARG_MOBILE, param1);
        args.putString(ARG_REMITER_ID, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            strMobileNumber = getArguments().getString(ARG_MOBILE);
            strRemitterId = getArguments().getString(ARG_REMITER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_bene_dmt2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);


    }

    private void init(View view) {

        hashBankList = new HashMap<>();
        ed_beneName = view.findViewById(R.id.ed_beneName);
        ed_accountNumber = view.findViewById(R.id.ed_account_number);
        atv_bank_name = view.findViewById(R.id.atv_bank_name);
        ed_ifsc_code = view.findViewById(R.id.ed_ifsce_code);
        btn_addBeneficiary = view.findViewById(R.id.btn_add_bene);
        tv_error_hint = view.findViewById(R.id.tv_error_hint);
        btn_verifyAccountNo = view.findViewById(R.id.btn_verifyAccountNo);
        progressBarAccountNo = view.findViewById(R.id.progress_accountNo);
        progressRR = view.findViewById(R.id.progressRR);
        rl_progress = view.findViewById(R.id.rl_progress);

        getBankList();
        btn_verifyAccountNo.setOnClickListener(view1 -> {

            if (isFieldFilled()) {
                verifyBankAccount();
            }
        });

        btn_addBeneficiary.setOnClickListener(view1 -> {
            if (InternetConnection.isConnected(getActivity())) {
                if (isFieldFilled()) {
                    strBeneName = ed_beneName.getText().toString();
                    if (!strBeneName.isEmpty()) {
                        addBeneficiary();
                    } else MakeToast.show(getActivity(), "Beneficiary name can't be empty!");
                }
            }
        });

    }

    private void addBeneficiary() {
        progressRR.setVisibility(View.VISIBLE);
        rl_progress.setVisibility(View.VISIBLE);
        btn_addBeneficiary.setVisibility(View.GONE);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.ADD_BENEFICIARY_DMT2,
                response -> {
                    try {

                        progressRR.setVisibility(View.GONE);
                        rl_progress.setVisibility(View.GONE);
                        btn_addBeneficiary.setVisibility(View.VISIBLE);
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("Transaction Successful")) {
                            String statusCode = jsonObject.getString("statuscode");
                            if (statusCode.equalsIgnoreCase("TXN")) {
                                mListener.onFragmentInteraction("reset", 3);
                                MakeToast.show(getActivity(), "Beneficiary has Added");
                            }
                        } else if (status.equalsIgnoreCase("200")) {
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
                        }

                    } catch (JSONException e) {
                        progressRR.setVisibility(View.GONE);
                        rl_progress.setVisibility(View.GONE);
                        btn_addBeneficiary.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressRR.setVisibility(View.GONE);
                    rl_progress.setVisibility(View.GONE);
                    btn_addBeneficiary.setVisibility(View.VISIBLE);
                }) {

            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> params = new HashMap<>();
                params.put("token", SharedPref.getInstance(getActivity()).getToken());
                params.put("userId", String.valueOf(SharedPref.getInstance(getActivity()).getId()));
                params.put("senderId", strRemitterId);
                params.put("beneName", ed_beneName.getText().toString());
                params.put("mobile_number", strMobileNumber);
                params.put("accountNumber", strAccountNumber);
                params.put("ifscCode", strIfscCode);
                params.put("bankName", strBankName);

                return params;
            }
        };

        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private boolean isFieldFilled() {
        strBankName = atv_bank_name.getText().toString();
        strAccountNumber = ed_accountNumber.getText().toString();
        strIfscCode = ed_ifsc_code.getText().toString();

        boolean isFilled = false;
        if (!strBankName.isEmpty()) {
            if (!strAccountNumber.isEmpty()) {
                if (!strIfscCode.isEmpty()) {
                    isFilled = true;
                } else MakeToast.show(getActivity(), "IFSC code can't be empty!");
            } else MakeToast.show(getActivity(), "Account number can't be emtpy!");
        } else MakeToast.show(getActivity(), "Bank Name can't be empty!");
        return isFilled;
    }

    private void getBankList() {

        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.GET_BANK_LIST_DMT,
                response -> {
                    try {

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
                            String success = jsonObject.getString("success");
                            if (success.equalsIgnoreCase("1")
                                    && status.equalsIgnoreCase("success")) {
                                hashBankList.clear();
                                JSONObject jsonObject1 = jsonObject.getJSONObject("bankList");
                                Iterator iterator = jsonObject1.keys();
                                while (iterator.hasNext()) {
                                    String key = (String) iterator.next();
                                    hashBankList.put(jsonObject1.getString(key), key);

                                }

                                if (getActivity() != null) {
                                    String[] bankList = hashBankList.keySet().toArray(new String[0]);
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                                            android.R.layout.select_dialog_item, bankList);
                                    atv_bank_name.setThreshold(1);
                                    atv_bank_name.setAdapter(arrayAdapter);
                                    atv_bank_name.setOnItemClickListener((parent, view, position, id) -> {

                                        String value = hashBankList.get(atv_bank_name.getText().toString());
                                        ed_ifsc_code.setText(value);
                                    });

                                }

                            }
                        }
                    } catch (JSONException e) {


                    }
                },
                error -> {


                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", SharedPref.getInstance(getActivity()).getToken());
                params.put("userId", String.valueOf(SharedPref.getInstance(getActivity()).getId()));

                return params;
            }
        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void verifyBankAccount() {

        progressBarAccountNo.setVisibility(View.VISIBLE);
        btn_verifyAccountNo.setVisibility(View.GONE);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.BANK_ACCOUNT_INFO_DMT,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            String beneName = jsonObject.getString("beneName");
                            ed_beneName.setText(beneName);
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
                        } else {
                            MakeToast.show(getActivity(), message);
                        }
                        progressBarAccountNo.setVisibility(View.GONE);
                        btn_verifyAccountNo.setVisibility(View.VISIBLE);

                    } catch (JSONException e) {
                        progressBarAccountNo.setVisibility(View.GONE);
                        btn_verifyAccountNo.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressBarAccountNo.setVisibility(View.GONE);
                    btn_verifyAccountNo.setVisibility(View.VISIBLE);
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", SharedPref.getInstance(getActivity()).getToken());
                params.put("userId", String.valueOf(SharedPref.getInstance(getActivity()).getId()));
                params.put("mobile_number", strMobileNumber);
                params.put("bank_account", strAccountNumber);
                params.put("ifsc", strIfscCode);
                params.put("bankCode", strBankName);
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

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(String data, int type);
    }

}
