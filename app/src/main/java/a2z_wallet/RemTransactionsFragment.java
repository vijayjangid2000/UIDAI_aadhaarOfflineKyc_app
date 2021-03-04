package a2z_wallet;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import com.vijayjangid.aadharkyc.adapter.RemTransAdapter;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.model.Report;
import com.vijayjangid.aadharkyc.util.APIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemTransactionsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    UserData userData;
    private int layout = R.layout.fragment_rem_transactions;
    private String strMobileNumber;
    private ArrayList<Report> transactions;
    private RemTransAdapter adapter;

    private LinearLayout ll_bene_progress, ll_no_bene;
    private RecyclerView recyclerView;

    public RemTransactionsFragment() {
        // Required empty public constructor
    }

    public static RemTransactionsFragment newInstance(String strMobileNumber) {
        RemTransactionsFragment fragment = new RemTransactionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, strMobileNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userData = new UserData(getContext());
        ll_bene_progress = view.findViewById(R.id.ll_bene_progress);
        ll_no_bene = view.findViewById(R.id.ll_no_bene);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getTransactions();
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
        return inflater.inflate(R.layout.fragment_rem_transactions, container, false);
    }


    private void getTransactions() {
        ll_bene_progress.setVisibility(View.VISIBLE);
        String url = APIs.GET_REM_TRANSACTIONS + "?" + APIs.USER_TAG + "=" +
                userData.getId()
                + "&" + APIs.TOKEN_TAG + "=" + userData.getToken() + "&mobile_number=" + strMobileNumber;
        Log.e("getTransactions", "" + url);
        final StringRequest request = new StringRequest(Request.Method.GET,
                url,
                response -> {
                    ll_bene_progress.setVisibility(View.GONE);
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {


                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            listTrans(jsonArray);

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
                        } else { //showConnectionError(message);
                        }
                    } catch (Exception e) {
                        //showConnectionError("Something went wrong!\nTry again");
                        ll_bene_progress.setVisibility(View.GONE);
                    }
                },
                error -> {
                    ///showConnectionError("Something went wrong!\nTry again");
                    ll_bene_progress.setVisibility(View.GONE);
                }) {

        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void listTrans(JSONArray jsonArray) {


        transactions = new ArrayList<>();
        transactions.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                Report report = new Report();
                report.setBeneAccount(jsonObject.getString("accountNumber"));
                report.setBankName(jsonObject.getString("bankName"));
                report.setIfscCode(jsonObject.getString("ifscCode"));
                report.setName(jsonObject.getString("beneName"));
                report.setStatus(jsonObject.getString("status"));


                transactions.add(report);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        recyclerView.setVisibility(View.VISIBLE);
        adapter = new RemTransAdapter(getActivity(), transactions);
        recyclerView.setAdapter(adapter);

    }
    /*private void showConnectionError(String message) {
        tv_error_hint.setText(message);
        tv_error_hint.setVisibility(View.VISIBLE);

        th_error_hint = new Thread(() -> {
            try {
                Thread.sleep(4000);
                if(getActivity()!=null){
                    Objects.requireNonNull(getActivity())
                            .runOnUiThread(() -> tv_error_hint.setVisibility(View.GONE));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        th_error_hint.start();

    }
*/
}
