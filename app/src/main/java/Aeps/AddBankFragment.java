package Aeps;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.vijayjangid.aadharkyc.model.BankDetail;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.InternetConnection;
import com.vijayjangid.aadharkyc.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddBankFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddBankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddBankFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private EditText ed_beneName;
    private EditText ed_accountNumber;
    private EditText ed_ifsc_code;
    private EditText atv_bank_name;
    private EditText atv_branch_name;
    private Button btn_addBank;
    private ProgressBar progressRR;
    private RelativeLayout rl_progress;

    private TextView tv_error_hint;

    private String strBankName;
    private String strAccountNumber;
    private String strIfscCode;
    private String strBeneName;
    private String strBranchName;

    private boolean edit;
    private BankDetail bankDetail;

    public AddBankFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AddBankFragment newInstance() {
        AddBankFragment fragment = new AddBankFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        edit = bundle.getBoolean("edit");
        if (edit) {
            bankDetail = (BankDetail) bundle.getSerializable("obj");
        }
        Log.e("bundle", "" + bundle.getBoolean("edit"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_bank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }

    private void init(View view) {


        ed_beneName = view.findViewById(R.id.ed_beneName);
        ed_accountNumber = view.findViewById(R.id.ed_account_number);
        atv_bank_name = view.findViewById(R.id.atv_bank_name);
        atv_branch_name = view.findViewById(R.id.atv_branch_name);
        ed_ifsc_code = view.findViewById(R.id.ed_ifsce_code);
        btn_addBank = view.findViewById(R.id.btn_add_bene);
        tv_error_hint = view.findViewById(R.id.tv_error_hint);

        progressRR = view.findViewById(R.id.progressRR);
        rl_progress = view.findViewById(R.id.rl_progress);

        if (edit) {
            btn_addBank.setText("Update");
            ed_beneName.setText("" + bankDetail.getBeneName());
            ed_accountNumber.setText("" + bankDetail.getAccountNumber());
            atv_bank_name.setText("" + bankDetail.getBankName());
            atv_branch_name.setText("" + bankDetail.getBranchName());
            ed_ifsc_code.setText("" + bankDetail.getIfsc());
        }


        btn_addBank.setOnClickListener(view1 -> {
            if (InternetConnection.isConnected(getActivity())) {
                if (isFieldFilled()) {
                    strBeneName = ed_beneName.getText().toString();
                    if (!strBeneName.isEmpty()) {

                        addBank();
                    } else MakeToast.show(getActivity(), "Account Holder name can't be empty!");
                }
            }
        });

    }

    private void addBank() {
        progressRR.setVisibility(View.VISIBLE);
        rl_progress.setVisibility(View.VISIBLE);
        btn_addBank.setVisibility(View.GONE);

        String url = "";

        if (edit)
            url = APIs.UPDATE_BANK_DETAILS;
        else
            url = APIs.ADD_BANK_DETAILS;

        Log.e("addBank" + edit, "" + url);
        final StringRequest request = new StringRequest(Request.Method.POST,
                url,
                response -> {
                    try {

                        progressRR.setVisibility(View.GONE);
                        rl_progress.setVisibility(View.GONE);
                        btn_addBank.setVisibility(View.VISIBLE);
                        Log.e("response post", "=" + response.toString());
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {
                            Dialog dialog = AppDialogs.dialogMessage(getActivity(), message, 0);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {
                                dialog.dismiss();
                                mListener.onFragmentInteraction();
                            });
                            dialog.show();
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
                        progressRR.setVisibility(View.GONE);
                        rl_progress.setVisibility(View.GONE);
                        btn_addBank.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressRR.setVisibility(View.GONE);
                    rl_progress.setVisibility(View.GONE);
                    btn_addBank.setVisibility(View.VISIBLE);
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", SharedPref.getInstance(getActivity()).getToken());

                params.put("name", ed_beneName.getText().toString());
                params.put("ifsc", strIfscCode.toUpperCase());
                params.put("bank_name", strBankName);
                params.put("branch_name", strBranchName);
                params.put("account_number", strAccountNumber);
                if (edit) {
                    params.put("user_id", String.valueOf(SharedPref.getInstance(getActivity()).getId()));
                    params.put("id", bankDetail.getId());
                } else
                    params.put("userId", String.valueOf(SharedPref.getInstance(getActivity()).getId()));
                Log.e("addBank post", "=" + params.toString());
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


    private boolean isFieldFilled() {
        strBankName = atv_bank_name.getText().toString();
        strBranchName = atv_branch_name.getText().toString();
        strAccountNumber = ed_accountNumber.getText().toString();
        strIfscCode = ed_ifsc_code.getText().toString();

        boolean isFilled = false;
        if (!strBankName.isEmpty()) {
            if (!strBranchName.isEmpty()) {
                if (!strAccountNumber.isEmpty()) {
                    if (!strIfscCode.isEmpty()) {
                        isFilled = true;
                    } else MakeToast.show(getActivity(), "IFSC code can't be empty!");
                } else MakeToast.show(getActivity(), "Account number can't be emtpy!");
            } else MakeToast.show(getActivity(), "Branch Name can't be empty!");
        } else MakeToast.show(getActivity(), "Bank Name can't be empty!");
        return isFilled;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}
