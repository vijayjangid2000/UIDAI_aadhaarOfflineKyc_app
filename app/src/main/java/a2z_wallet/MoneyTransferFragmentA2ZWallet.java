package a2z_wallet;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.vijayjangid.aadharkyc.HomePage_activity;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.UserData;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.InternetConnection;
import com.vijayjangid.aadharkyc.util.MakeToast;
import com.vijayjangid.aadharkyc.util.NumberToWordsConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class MoneyTransferFragmentA2ZWallet extends Fragment {

    private static final String TAG = "MoneyTransferFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    UserData userData;
    private Beneficiary beneficiary;
    private String strMobileNumber;

    private OnFragmentInteractionListener mListener;
    private TextView tv_beneName;
    private TextView tv_bankName;
    private TextView tv_accountNo;
    private Button btn_transfer;
    private Spinner spn_transferType;
    private EditText ed_amount;
    private EditText ed_transactionPin;
    private ProgressBar progressBar;
    private RelativeLayout rl_progress;
    private TextView tv_error_hint;
    private TextView tv_amountInWord;
    private String strAmount;
    private String strType = "2";
    private String strTransactionPin;
    private String strResTotalAmount;
    private String strResTxnPin;
    //if result ->1
    private String strResTxnAmount;
    private String strResCharge;
    private String strResTotal;
    private Dialog dialog_processing;

    public MoneyTransferFragmentA2ZWallet() {
        // Required empty public constructor
    }

    public static MoneyTransferFragmentA2ZWallet newInstance(Beneficiary beneficiary, String strMobileNumber) {
        MoneyTransferFragmentA2ZWallet fragment = new MoneyTransferFragmentA2ZWallet();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, beneficiary);
        args.putString(ARG_PARAM2, strMobileNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            beneficiary = getArguments().getParcelable(ARG_PARAM1);
            strMobileNumber = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_money_transfer_fragment_a2z_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userData = new UserData(getContext());
        btn_transfer = view.findViewById(R.id.btn_transfer);

        tv_accountNo = view.findViewById(R.id.tv_accountNo);
        tv_bankName = view.findViewById(R.id.tv_bankName);
        tv_beneName = view.findViewById(R.id.tv_beneName);
        spn_transferType = view.findViewById(R.id.spn_transferType);
        ed_amount = view.findViewById(R.id.ed_amount);
        ed_transactionPin = view.findViewById(R.id.ed_transactionPin);
        progressBar = view.findViewById(R.id.progressBar);
        rl_progress = view.findViewById(R.id.rl_progress);
        tv_error_hint = view.findViewById(R.id.tv_incorrect);
        tv_amountInWord = view.findViewById(R.id.tv_amountInWord);

        tv_accountNo.setText(beneficiary.getAccount());
        tv_bankName.setText(beneficiary.getBank());
        tv_beneName.setText(beneficiary.getName());


        btn_transfer.setOnClickListener(view1 -> {
            if (InternetConnection.isConnected(getActivity())) {
                if (validateField()) {
                    tv_error_hint.setVisibility(View.GONE);
                    transferMoney();
                }
            }
        });


        String[] transferTypes = {"IMPS", "NEFT"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                R.layout.spinner_layout, transferTypes);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spn_transferType.setAdapter(dataAdapter);

        spn_transferType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    strType = "2";
                else
                    strType = "1";
            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ed_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!ed_amount.getText().toString().isEmpty()) {
                    String amountInWord = NumberToWordsConverter.convert(Integer.parseInt(ed_amount.getText().toString()));
                    tv_amountInWord.setText(amountInWord + " Rs only/-");
                } else {
                    tv_amountInWord.setText("Enter amount");
                }


            }
        });
    }

    private boolean validateField() {
        boolean isValid = false;
        strAmount = ed_amount.getText().toString();
        strTransactionPin = ed_transactionPin.getText().toString();

        if (!strAmount.isEmpty()) {
            if (!strTransactionPin.isEmpty()) {
                isValid = true;
            } else MakeToast.show(getActivity(), "Enter Transaction Pin!");

        } else MakeToast.show(getActivity(), "Enter Transaction Amount!");

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

    private void transferMoney() {
        progressBar.setVisibility(View.VISIBLE);
        rl_progress.setVisibility(View.VISIBLE);
        btn_transfer.setVisibility(View.GONE);
        String url = APIs.GET_AGENT_CHARGE_AMT_A2ZWallet + "?amount=" + strAmount + "&txnChargeApiName=TRAMO&" + APIs.USER_TAG + "="
                + userData.getId() + "&" + APIs.TOKEN_TAG + "=" + userData.getToken();
        final StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {

                        progressBar.setVisibility(View.GONE);
                        rl_progress.setVisibility(View.GONE);
                        btn_transfer.setVisibility(View.VISIBLE);
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {

                            strResTxnPin = jsonObject.getString("txn_pin");
                            strResTotalAmount = jsonObject.getString("totalAmount");

                            JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("1");

                            strResTxnAmount = jsonObject2.getString("txnAmount");
                            strResTotal = jsonObject2.getString("total");
                            strResCharge = jsonObject2.getString("charge");


                            showConfirmDialog();


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
                        progressBar.setVisibility(View.GONE);
                        rl_progress.setVisibility(View.GONE);
                        btn_transfer.setVisibility(View.VISIBLE);
                        showConnectionError("Something went wrong!\nTry again");
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    rl_progress.setVisibility(View.GONE);
                    btn_transfer.setVisibility(View.VISIBLE);
                    showConnectionError("Something went wrong!\nTry again");
                }) {

        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void showConfirmDialog() {
        Dialog dialog = AppDialogs.transferConformDetail(getActivity());

        TextView tv_senderMobile = dialog.findViewById(R.id.tv_senderMobile);
        tv_senderMobile.setText(strMobileNumber);
        TextView tv_beneName = dialog.findViewById(R.id.tv_beneName);
        tv_beneName.setText(beneficiary.getName());
        TextView tv_ifsc = dialog.findViewById(R.id.tv_ifsc);
        tv_ifsc.setText(beneficiary.getIfsc());
        TextView tv_accountNo = dialog.findViewById(R.id.tv_accountNo);
        tv_accountNo.setText(beneficiary.getAccount());
        TextView tv_bank_name = dialog.findViewById(R.id.tv_bank_name);
        tv_bank_name.setText(beneficiary.getBank());

        EditText ed_confirmAmount = dialog.findViewById(R.id.ed_confirmAmount);

        TextView tv_transferAmount = dialog.findViewById(R.id.tv_transferAmount);
        tv_transferAmount.setText(strResTxnAmount);
        TextView tv_txnCharge = dialog.findViewById(R.id.tv_txnCharge);
        tv_txnCharge.setText(strResCharge);
        TextView tv_totalAmount = dialog.findViewById(R.id.tv_totalAmount);
        tv_totalAmount.setText(strResTotalAmount);

        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        Button btn_payAndConfirm = dialog.findViewById(R.id.btn_transfer);

        btn_cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        btn_payAndConfirm.setOnClickListener(view -> {

            if (ed_confirmAmount.getText().toString().equalsIgnoreCase(strResTxnAmount)) {
                if (strTransactionPin.equalsIgnoreCase(strResTxnPin)) {

                    Log.e("TRANSFER_MONEY", APIs.TRANSFER_MONEY_A2ZWallet);
                    transferMoneyConfirm();
                    dialog.dismiss();
                } else MakeToast.show(getActivity(), "Transaction Pin is incorrect!");
            } else MakeToast.show(getActivity(), "Amount does not matched!");

        });

        dialog.show();

    }

    private void showProcessingDialog() {
        dialog_processing = AppDialogs.processing(getActivity());
        dialog_processing.show();
    }

    private void transferMoneyConfirm() {
        progressBar.setVisibility(View.VISIBLE);
        rl_progress.setVisibility(View.VISIBLE);
        btn_transfer.setVisibility(View.GONE);
        showProcessingDialog();

        final StringRequest request = new StringRequest(Request.Method.POST, APIs.TRANSFER_MONEY_A2ZWallet,
                response -> {
                    try {


                        Log.e("response transfer", "sd " + response.toString());
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");


                        if (status.equalsIgnoreCase("200")) {
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
                            ed_amount.setText("");
                            ed_transactionPin.setText("");
                            Dialog dialog = AppDialogs.transactionStatus(getActivity(), message, Integer.parseInt(status));
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {
                                dialog.dismiss();
                            });

                            dialog.setOnDismissListener(dialog1 -> {
                                Intent intent = new Intent(getActivity(), HomePage_activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            });
                            dialog.show();
                        }


                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        rl_progress.setVisibility(View.GONE);
                        btn_transfer.setVisibility(View.VISIBLE);
                        //AppDialogs.volleyErrorDialog(getActivity(), 1);
                        Toast.makeText(getContext(), "Volley Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                    rl_progress.setVisibility(View.GONE);
                    btn_transfer.setVisibility(View.VISIBLE);
                    dialog_processing.dismiss();
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    rl_progress.setVisibility(View.GONE);
                    btn_transfer.setVisibility(View.VISIBLE);
                    dialog_processing.dismiss();
                    //AppDialogs.volleyErrorDialog(getActivity(), 0);
                    Toast.makeText(getContext(), "Error -> " + error.getMessage(), Toast.LENGTH_SHORT).show();

                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", userData.getToken());
                params.put("userId", userData.getId());
                params.put("beneName", beneficiary.getName());
                params.put("ifsc", beneficiary.getIfsc());
                params.put("bank_account", beneficiary.getAccount());
                params.put("mobile_number", strMobileNumber);
                params.put("beneficiary_id", beneficiary.getId());
                params.put("amount", strAmount);
                params.put("senderName", beneficiary.getRemitterName());
                params.put("channel", "" + strType);
                Log.e("params", params.toString());
                return params;
            }
        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void showConnectionError(String message) {
        tv_error_hint.setText(message);
        tv_error_hint.setVisibility(View.VISIBLE);
    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(int type, String data);
    }

}
