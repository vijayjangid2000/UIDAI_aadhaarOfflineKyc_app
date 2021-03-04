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
import com.vijayjangid.aadharkyc.UserData;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.listener.OnBeneficiaryAdapterClickListener;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.InternetConnection;
import com.vijayjangid.aadharkyc.util.MakeToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BeneListFragmentA2ZWallet extends Fragment {

    private static final String TAG = "BeneListA2ZWallet";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    UserData userData;
    String strMobileNumber, strRemitterName;
    ArrayList<Beneficiary> beneficiaries;
    LinearLayout ll_beneProgress, ll_noBene;
    RecyclerView recyclerView;
    Button btn_addBene, btn_delete;
    TextView tv_error;
    Thread threadError;
    BeneficiaryAdapter adapter;
    boolean canDelete = true;
    Dialog dialogOtp, dialog;
    EditText ed_otp, search;
    ImageButton btn_dismiss;

    private OnFragmentInteractionListener mListener;

    public BeneListFragmentA2ZWallet() {

    }

    public static BeneListFragmentA2ZWallet
    newInstance(String strMobileNumber, String remitterName) {
        BeneListFragmentA2ZWallet fragment = new BeneListFragmentA2ZWallet();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, strMobileNumber);
        args.putString(ARG_PARAM2, remitterName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userData = new UserData(getContext());

        ll_beneProgress = view.findViewById(R.id.ll_bene_progress);
        ll_noBene = view.findViewById(R.id.ll_no_bene);
        search = view.findViewById(R.id.ed_search);
        recyclerView = view.findViewById(R.id.recyclerView);
        btn_addBene = view.findViewById(R.id.btn_add_bene);
        tv_error = view.findViewById(R.id.tv_error_hint);

        search.setVisibility(View.INVISIBLE);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });
        getBeneficiary();

        btn_addBene.setOnClickListener(view1 -> {
            if (InternetConnection.isConnected(getActivity())) {
                mListener.onFragmentInteraction(3, "AddBeneFragmentDMT2");
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            strMobileNumber = getArguments().getString(ARG_PARAM1);
            strRemitterName = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_bene_list_fragment_a2z_wallet, container, false);
    }

    void filter(String text) {
        if (text.length() > 0 && beneficiaries.size() > 0) {
            ArrayList<Beneficiary> temp = new ArrayList();
            for (Beneficiary d : beneficiaries) {
                //or use .equal(text) with you want equal match
                //use .toLowerCase() for better matches
                if (d.getAccount().toLowerCase().contains(text.toLowerCase()) || d.getName().toLowerCase().contains(text.toLowerCase())) {
                    temp.add(d);
                }
            }
            //update recyclerview
            adapter.updateList(temp);
        }
    }

    private void getBeneficiary() {

        ll_beneProgress.setVisibility(View.VISIBLE);
        String url = APIs.GET_BENElIST_A2ZWallet + "?" + APIs.USER_TAG + "=" + userData.getId()
                + "&" + APIs.TOKEN_TAG + "=" + userData.getToken() + "&mobile_number=" + strMobileNumber;
        Log.e("getBeneficiary", "" + url);
        final StringRequest request = new StringRequest(Request.Method.GET,
                url,
                response -> {
                    ll_beneProgress.setVisibility(View.GONE);
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("22")) {

                            JSONObject jsonObject1 = jsonObject.getJSONObject("message");
                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            listBeneficiary(jsonArray);

                        } else if (status.equalsIgnoreCase("23")) {
                            ll_noBene.setVisibility(View.VISIBLE);
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
                    } catch (Exception e) {
                        showConnectionError("Something went wrong!\nTry again");
                        ll_beneProgress.setVisibility(View.GONE);
                    }
                },
                error -> {
                    showConnectionError("Something went wrong!\nTry again");
                    ll_beneProgress.setVisibility(View.GONE);
                }) {

        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void listBeneficiary(JSONArray jsonArray) {


        beneficiaries = new ArrayList<>();
        beneficiaries.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                Beneficiary beneficiary = new Beneficiary();
                beneficiary.setAccount(jsonObject.getString("account_number"));
                beneficiary.setBank(jsonObject.getString("bank_name"));
                beneficiary.setMobile(jsonObject.getString("customer_number"));
                beneficiary.setIfsc(jsonObject.getString("ifsc"));
                beneficiary.setId(jsonObject.getString("beneId"));
                beneficiary.setName(jsonObject.getString("name"));
                beneficiary.setStatus(jsonObject.getString("status_id"));
                beneficiary.setRemitterName(strRemitterName);
                beneficiary.setIs_verified(jsonObject.getString("is_bank_verified"));

                beneficiaries.add(beneficiary);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        search.setVisibility(View.VISIBLE);
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
                    mListener.onFragmentInteraction(5, beneficiaries.get(position));
                }
            }
        });
    }

    private void deleteBeneficiary(int position) {
        Log.e("deleteBeneficiary", "=" + APIs.DELETE_BENEFICIARY_A2ZWallet);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.DELETE_BENEFICIARY_A2ZWallet,
                response -> {
                    dialog.dismiss();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("37")) {


                            dialogOtp = AppDialogs.dele_bene_otp(getActivity());
                            btn_dismiss = dialogOtp.findViewById(R.id.btn_dismiss);
                            ed_otp = dialogOtp.findViewById(R.id.ed_otp);
                            btn_delete = dialogOtp.findViewById(R.id.btn_delete);
                            btn_dismiss.setOnClickListener(view -> dialogOtp.dismiss());
                            btn_delete.setOnClickListener(view -> {
                                String strOtp = ed_otp.getText().toString();
                                if (!strOtp.isEmpty()) {
                                    dialogViewSetEnable(false);
                                    deleteBeneficiaryotp(strOtp, position);
                                } else MakeToast.show(getActivity(), "Please enter OTP");
                            });
                            dialogOtp.show();
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
                params.put("token", userData.getToken());
                params.put("userId", userData.getId());
                params.put("beneId", beneficiaries.get(position).getId());
                Log.e("deleteBeneficiary", "=" + params.toString());
                return params;
            }
        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

    private void deleteBeneficiaryotp(String strOtp, int position) {

        Log.e("deleteBeneficiary", "=" + APIs.DELETE_BENEFICIARY_OTP_A2ZWallet);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.DELETE_BENEFICIARY_OTP_A2ZWallet,
                response -> {
                    try {
                        Log.e("delete response ", "=" + response.toString());
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("38")) {
                            beneficiaries.remove(position);
                            adapter.notifyItemRemoved(position);
                            if (adapter.getItemCount() > 0) {
                                ll_noBene.setVisibility(View.GONE);
                            } else ll_noBene.setVisibility(View.VISIBLE);
                            MakeToast.show(getActivity(), "Beneficiary deleted!");
                            dialogOtp.dismiss();
                        } else if (status.equalsIgnoreCase("16")) {
                            MakeToast.show(getActivity(), message);
                            dialogViewSetEnable(true);
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
                            showConnectionError(message);
                            dialogViewSetEnable(true);
                            dialogOtp.dismiss();

                        }

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
                params.put("token", userData.getToken());
                params.put("userId", userData.getId());
                params.put("beneId", beneficiaries.get(position).getId());
                params.put("otp", strOtp);
                Log.e("post delete", "=" + params.toString());
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

        threadError = new Thread(() -> {
            try {
                Thread.sleep(4000);
                if (getActivity() != null) {
                    Objects.requireNonNull(getActivity())
                            .runOnUiThread(() -> tv_error.setVisibility(View.GONE));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        threadError.start();

    }

    @Override
    public void onDestroy() {
        if (threadError != null) {
            if (threadError.isAlive()) {
                threadError.interrupt();
                threadError = null;
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

        void onFragmentInteraction(int type, String data);

        void onFragmentInteraction(int type, Beneficiary beneficiary);
    }
}
