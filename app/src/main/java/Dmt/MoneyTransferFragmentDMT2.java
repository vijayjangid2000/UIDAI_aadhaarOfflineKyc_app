package Dmt;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.vijayjangid.aadharkyc.HomePage_activity;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.adapter.ConfirmDmt1Adapter;
import com.vijayjangid.aadharkyc.adapter.SlipAdapter;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.in.SharedPref;
import com.vijayjangid.aadharkyc.model.ConfirmDm1Transfer;
import com.vijayjangid.aadharkyc.model.PaymentSlipDMT1;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.InternetConnection;
import com.vijayjangid.aadharkyc.util.MakeToast;
import com.vijayjangid.aadharkyc.util.NumberToWordsConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import a2z_wallet.Beneficiary;

public class MoneyTransferFragmentDMT2 extends Fragment {

    private static final String TAG = "MoneyTransferFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int layout = R.layout.fragment_money_transfer_fragment_dmt2;
    private Beneficiary beneficiary;
    private String strMobileNumber;

    private OnFragmentInteractionListener mListener;
    private Button btn_transfer;
    private EditText ed_amount;
    private EditText ed_transactionPin;
    private ProgressBar progressBar;
    private RelativeLayout rl_progress;
    private TextView tv_error_hint;
    private TextView tv_amountInWord;
    private String strAmount;
    private String strChannel = "2";
    private String strTransactionPin;
    private String strResTotalAmount;
    private String strResTxnPin;
    private Dialog dialog_processing;

    public MoneyTransferFragmentDMT2() {
    }

    public static MoneyTransferFragmentDMT2 newInstance(Beneficiary beneficiary, String strMobileNumber) {
        MoneyTransferFragmentDMT2 fragment = new MoneyTransferFragmentDMT2();
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
        return inflater.inflate(R.layout.fragment_money_transfer_fragment_dmt2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_transfer = view.findViewById(R.id.btn_transfer);
        TextView tv_accountNo = view.findViewById(R.id.tv_accountNo);
        TextView tv_bankName = view.findViewById(R.id.tv_bankName);
        TextView tv_beneName = view.findViewById(R.id.tv_beneName);
        Spinner spn_transferType = view.findViewById(R.id.spn_transferType);
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
                    // checkBankDown(beneficiary.getAccount(),beneficiary.getIfsc(),beneficiary.getBank());
                    //checkBankDown();
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
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spn_transferType.getSelectedItem().toString().equalsIgnoreCase("IMPS")) {
                    strChannel = "2";
                } else strChannel = "1";
            }

            @Override
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
        String url = APIs.GET_AGENT_CHARGE_AMT_DMT + "?amount=" + strAmount + "&txnChargeApiName=DMT1&" + APIs.USER_TAG + "="
                + SharedPref.getInstance(getActivity()).getId() + "&" + APIs.TOKEN_TAG + "=" + SharedPref.getInstance(getActivity()).getToken();
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

                            ArrayList<ConfirmDm1Transfer> list = new ArrayList<>();

                            strResTxnPin = jsonObject.getString("txn_pin");
                            strResTotalAmount = jsonObject.getString("totalAmount");

                            JSONObject resultObject = jsonObject.getJSONObject("result");

                            Iterator iterator = resultObject.keys();
                            while (iterator.hasNext()) {
                                String key = (String) iterator.next();

                                JSONObject jsonObject1 = resultObject.getJSONObject(key);
                                ConfirmDm1Transfer confirmDm1Transfer = new ConfirmDm1Transfer();
                                confirmDm1Transfer.setCharge(jsonObject1.getString("charge"));
                                confirmDm1Transfer.setTxnAmount(jsonObject1.getString("txnAmount"));
                                confirmDm1Transfer.setTotal(jsonObject1.getString("total"));
                                list.add(confirmDm1Transfer);
                            }
                            showConfirmDialog(list);


                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
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

    private void showConfirmDialog(ArrayList<ConfirmDm1Transfer> list) {
        Dialog dialog = AppDialogs.transferConformDetailDMT1(getActivity());

        TextView tv_senderMobile = dialog.findViewById(R.id.tv_senderMobile);
        tv_senderMobile.setText(strMobileNumber);
        TextView tv_beneName = dialog.findViewById(R.id.tv_beneName);
        tv_beneName.setText(beneficiary.getName());
        TextView tv_ifsc = dialog.findViewById(R.id.tv_ifsc);
        TextView tv_bank_name = dialog.findViewById(R.id.tv_bank_name);
        tv_ifsc.setText(beneficiary.getIfsc());
        tv_bank_name.setText(beneficiary.getBank());
        TextView tv_totalAmount = dialog.findViewById(R.id.tv_totalAmount);
        tv_totalAmount.setText(strResTotalAmount);
        TextView tv_accountNo = dialog.findViewById(R.id.tv_accountNo);
        tv_accountNo.setText(beneficiary.getAccount());

        EditText ed_confirmAmount = dialog.findViewById(R.id.ed_confirmAmount);

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ConfirmDmt1Adapter adapter = new ConfirmDmt1Adapter(list, getActivity());
        recyclerView.setAdapter(adapter);

        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        Button btn_payAndConfirm = dialog.findViewById(R.id.btn_transfer);

        btn_cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        btn_payAndConfirm.setOnClickListener(view -> {
            if (ed_confirmAmount.getText().toString().equalsIgnoreCase(ed_amount.getText().toString())) {
                if (strTransactionPin.equalsIgnoreCase(strResTxnPin)) {
                    transferMoneyConfirm();
                    dialog.dismiss();
                } else MakeToast.show(getActivity(), "Transaction Pin is incorrect!");
            } else MakeToast.show(getActivity(), "Amount does not matched!");
        });

        dialog.show();

    }

    private void transferMoneyConfirm() {
        progressBar.setVisibility(View.VISIBLE);
        rl_progress.setVisibility(View.VISIBLE);
        btn_transfer.setVisibility(View.GONE);
        showProcessingDialog();

        final StringRequest request = new StringRequest(Request.Method.POST, APIs.MONEY_TRANSACTION_DMT2,
                response -> {
                    try {

                        ArrayList<PaymentSlipDMT1> list = new ArrayList<>();
                        JSONObject jObject = new JSONObject(response);
                        JSONObject menu = jObject.getJSONObject("result");
                        Iterator iter = menu.keys();
                        boolean isSuccess = false;


                        while (iter.hasNext()) {
                            String key = (String) iter.next();
                            String value = menu.getString(key);
                            JSONObject jObject1 = new JSONObject(value);
                            String status = jObject1.getString("status");
                            if (status.equalsIgnoreCase("Failure")) {
                                String message = jObject1.getString("message");
                                showConnectionError(message);
                                break;
                            } else {

                                isSuccess = true;
                                String refNo = jObject1.getString("refNo");
                                String status1 = jObject1.getString("status");
                                if (status1.equalsIgnoreCase("FAILED")
                                        || status1.equalsIgnoreCase("PENDING")) {
                                    refNo = "N/A";
                                }

                                String txnId = jObject1.getString("txnId");
                                String amount = jObject1.getString("amount");
                                String txnTIme = jObject1.getString("txnTIme");

                                list.add(new PaymentSlipDMT1(
                                        txnId, refNo, amount, txnTIme, status1
                                ));
                            }
                        }
                        if (isSuccess) {
                            ed_amount.setText("");
                            ed_transactionPin.setText("");
                            Dialog dialog = AppDialogs.dmt1TransactionSlip(getActivity());
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {
                                dialog.dismiss();

                            });
                            dialog.setOnDismissListener(dialog1 -> {
                                Intent intent = new Intent(getActivity(), HomePage_activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            });

                            RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
                            recyclerView.setHasFixedSize(false);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            SlipAdapter adapter = new SlipAdapter(list, getActivity());
                            recyclerView.setAdapter(adapter);

                            dialog.show();
                        }


                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        rl_progress.setVisibility(View.GONE);
                        btn_transfer.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Volley Error", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Volley Error", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", SharedPref.getInstance(getActivity()).getToken());
                params.put("userId", String.valueOf(SharedPref.getInstance(getActivity()).getId()));
                params.put("senderName", beneficiary.getRemitterName());
                params.put("ifsc", beneficiary.getIfsc());
                params.put("bank_account", beneficiary.getAccount());
                params.put("mobile_number", strMobileNumber);
                params.put("beneficiary_id", beneficiary.getId());
                params.put("amount", strAmount);
                params.put("channel", strChannel);
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

    private void showProcessingDialog() {
        dialog_processing = AppDialogs.processing(getActivity());
        dialog_processing.show();
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(String data, int type);
    }
}
