package Dmt;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.in.SharedPref;
import com.vijayjangid.aadharkyc.listener.OnBeneficiaryAdapterClickListener;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.InternetConnection;
import com.vijayjangid.aadharkyc.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import a2z_wallet.Beneficiary;
import a2z_wallet.BeneficiaryAdapter;

public class BeneListFragmentDMT2 extends Fragment {

    private static final String TAG = "BeneListFragmentDMT2";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Dialog dialogOtp;
    EditText ed_otp;
    ImageButton btn_dismiss;
    Button btn_delete;
    Dialog dialog;
    private int layout = R.layout.fragment_bene_list_fragment_dmt2;
    private String strRemitterId;
    private OnFragmentInteractionListener mListener;
    private ArrayList<Beneficiary> beneficiaries;
    private LinearLayout ll_no_bene;
    private RecyclerView recyclerView;
    private Button btn_add_bene;
    private TextView tv_error_hint;
    private Thread th_error_hint;
    private BeneficiaryAdapter adapter;
    private boolean canDelete = true;

    public BeneListFragmentDMT2() {

    }

    public static BeneListFragmentDMT2 newInstance(ArrayList<Beneficiary> beneficiaries, String strRemitterId) {
        BeneListFragmentDMT2 fragment = new BeneListFragmentDMT2();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, beneficiaries);
        args.putString(ARG_PARAM2, strRemitterId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            beneficiaries = getArguments().getParcelableArrayList(ARG_PARAM1);
            strRemitterId = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_bene_list_fragment_dmt2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ll_no_bene = view.findViewById(R.id.ll_no_bene);
        recyclerView = view.findViewById(R.id.recyclerView);
        btn_add_bene = view.findViewById(R.id.btn_add_bene);
        tv_error_hint = view.findViewById(R.id.tv_error_hint);


        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setVisibility(View.VISIBLE);
        adapter = new BeneficiaryAdapter(getActivity(), beneficiaries);
        recyclerView.setAdapter(adapter);
        adapter.setupListener(new OnBeneficiaryAdapterClickListener() {
            @Override
            public void delete(int position) {
                if (InternetConnection.isConnected(getActivity())) {
                    if (canDelete) {
                        canDelete = false;
                        deleteBeneConfirmDialog(position);
                    } else MakeToast.show(getActivity(), "one item is on process! please wait");
                }
            }


            @Override
            public void transfer(int position) {
                if (InternetConnection.isConnected(getActivity())) {
                    mListener.onFragmentInteraction(String.valueOf(position), 5);
                }
            }
        });

        btn_add_bene.setOnClickListener(view1 -> {
            if (InternetConnection.isConnected(getActivity())) {
                mListener.onFragmentInteraction("AddBeneFragmentDMT2", 3);
            }
        });

    }


    private void dialogViewSetEnable(boolean isEnable) {
        if (isEnable) {
            btn_dismiss.setEnabled(true);
            btn_dismiss.setAlpha(1f);
            btn_delete.setEnabled(true);
            btn_delete.setAlpha(1f);
            btn_delete.setText("Delete");
            ed_otp.setEnabled(true);

        } else {
            btn_dismiss.setEnabled(false);
            btn_dismiss.setAlpha(0.7f);
            btn_delete.setEnabled(false);
            btn_delete.setAlpha(0.7f);
            btn_delete.setText("Deleting");
            ed_otp.setEnabled(false);
        }

    }

    private void deleteBeneficiary(int position) {
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.DELETE_BENEFICIARY_DMT2,
                response -> {
                    dialog.dismiss();
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
                            String statuscode = jsonObject.getString("statuscode");
                            if (statuscode.equalsIgnoreCase("TXN") &&
                                    status.equalsIgnoreCase("Transaction Successful")) {
                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                String otp = jsonObject1.getString("otp");

                                if (otp.equalsIgnoreCase("1")) {

                                    dialogOtp = AppDialogs.dele_bene_otp(getActivity());
                                    btn_dismiss = dialogOtp.findViewById(R.id.btn_dismiss);
                                    ed_otp = dialogOtp.findViewById(R.id.ed_otp);
                                    InputFilter[] filterArray = new InputFilter[1];
                                    filterArray[0] = new InputFilter.LengthFilter(6);
                                    ed_otp.setFilters(filterArray);
                                    btn_delete = dialogOtp.findViewById(R.id.btn_delete);

                                    btn_dismiss.setOnClickListener(view -> dialogOtp.dismiss());

                                    btn_delete.setOnClickListener(view -> {
                                        String strOtp = ed_otp.getText().toString();
                                        if (!strOtp.isEmpty()) {
                                            deleteBeneficiaryotp(strOtp, position);

                                        } else MakeToast.show(getActivity(), "Please enter OTP");
                                    });

                                    dialogOtp.show();

                                } else MakeToast.show(getActivity(), "Failed to send otp");

                            }
                        }
                    } catch (JSONException e) {
                        showConnectionError("Something went wrong!\nTry again");
                    }
                    canDelete = true;
                },
                error -> {
                    dialog.dismiss();
                    canDelete = true;
                    showConnectionError("Something went wrong!\nTry again");
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", SharedPref.getInstance(getActivity()).getToken());
                params.put("userId", String.valueOf(SharedPref.getInstance(getActivity()).getId()));
                params.put("remitterId", strRemitterId);
                params.put("beneficiaryId", beneficiaries.get(position).getId());
                return params;
            }
        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void deleteBeneficiaryotp(String strOtp, int position) {

        btn_delete.setAlpha(0.5f);
        btn_delete.setEnabled(false);
        btn_delete.setText("deleting...");
        ed_otp.setAlpha(0.5f);
        ed_otp.setFocusable(false);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.CONFIRM_BENEFICIARY_DELETE_DMT2,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("Transaction Successful")) {
                            String data = jsonObject.getString("data");
                            String statuscode = jsonObject.getString("statuscode");
                            if (statuscode.equalsIgnoreCase("TXN") &&
                                    data.equalsIgnoreCase("Success")) {
                                beneficiaries.remove(position);
                                adapter.notifyItemRemoved(position);
                                if (adapter.getItemCount() > 0) {
                                    ll_no_bene.setVisibility(View.GONE);
                                } else ll_no_bene.setVisibility(View.VISIBLE);
                                MakeToast.show(getActivity(), "Beneficiary deleted!");
                                dialogOtp.dismiss();

                            } else {
                                MakeToast.show(getActivity(), status);
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
                        } else {
                            MakeToast.show(getActivity(), status);
                        }
                        btn_delete.setAlpha(1f);
                        btn_delete.setEnabled(true);
                        btn_delete.setText("DELETE");
                        ed_otp.setAlpha(1f);
                        ed_otp.setFocusable(true);


                    } catch (JSONException e) {
                        showConnectionError("Something went wrong!\nTry again");
                        dialogViewSetEnable(true);
                        dialogOtp.dismiss();
                    }
                },
                error -> {
                    showConnectionError("Something went wrong!\nTry again");
                    dialogViewSetEnable(true);
                    dialogOtp.dismiss();
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();

                params.put("token", SharedPref.getInstance(getActivity()).getToken());
                params.put("userId", String.valueOf(SharedPref.getInstance(getActivity()).getId()));
                params.put("remitterId", strRemitterId);
                params.put("beneficiaryId", beneficiaries.get(position).getId());
                params.put("otp", strOtp);
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
        tv_error_hint.setText(message);
        tv_error_hint.setVisibility(View.VISIBLE);

        th_error_hint = new Thread(() -> {
            try {
                Thread.sleep(4000);
                Objects.requireNonNull(getActivity())
                        .runOnUiThread(() -> tv_error_hint.setVisibility(View.GONE));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        th_error_hint.start();

    }

    @Override
    public void onDestroy() {
        if (th_error_hint != null) {
            if (th_error_hint.isAlive()) {
                th_error_hint.interrupt();
                th_error_hint = null;
            }
        }
        super.onDestroy();
    }

    private void deleteBeneConfirmDialog(int position) {

        dialog = AppDialogs.deleteBene(getActivity());

        ImageButton btn_dismiss = dialog.findViewById(R.id.btn_dismiss);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        Button btn_delete = dialog.findViewById(R.id.btn_delete);
        LinearLayout ll_action = dialog.findViewById(R.id.ll_action);
        TextView tv_processing = dialog.findViewById(R.id.tv_delete_processing);

        btn_cancel.setOnClickListener(view -> {
            dialog.dismiss();
            canDelete = true;
        });
        btn_dismiss.setOnClickListener(view -> {
            dialog.dismiss();
            canDelete = true;
        });
        btn_delete.setOnClickListener(view -> {
            ll_action.setVisibility(View.GONE);
            tv_processing.setVisibility(View.VISIBLE);
            deleteBeneficiary(position);

        });

        dialog.show();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String data, int type);
    }
}
