package Reports.Ledger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.UserData;
import com.vijayjangid.aadharkyc.activity.AppInProgressActivity;
import com.vijayjangid.aadharkyc.adapter.ReportAdapter2;
import com.vijayjangid.aadharkyc.enums.DatePickerType;
import com.vijayjangid.aadharkyc.enums.ReportTypes;
import com.vijayjangid.aadharkyc.in.RequestHandler;
import com.vijayjangid.aadharkyc.listener.OnDatePicker;
import com.vijayjangid.aadharkyc.listener.PaginationScrollListener;
import com.vijayjangid.aadharkyc.model.Report;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.AppDialogs;
import com.vijayjangid.aadharkyc.util.AppUitls;
import com.vijayjangid.aadharkyc.util.DatePicker;
import com.vijayjangid.aadharkyc.util.MakeToast;
import com.vijayjangid.aadharkyc.util.ReportJsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class ReportFragment extends Fragment implements OnDatePicker {

    public static final String REPORT_TYPE = "report_type";
    private final static String TAG = "ReportFragment";
    UserData userData;
    String start_date = "";
    String end_date = "";
    AlertDialog dialogView;
    LinearLayoutManager linearLayoutManager;
    ImageView ivb_Search, ivb_refreshTransaction;
    private int layout = R.layout.fragment_money_transaction;
    private ReportTypes reportType = ReportTypes.LEDGER_REPORT;
    private HashMap<String, String> searchTypeHashMap = new HashMap<>();
    private HashMap<String, String> orderStatusHashMap = new HashMap<>();
    private HashMap<String, String> orderProductHashMap = new HashMap<>();
    private HashMap<String, String> creditDebitHashMap = new HashMap<>();
    private String strSearchType = "Date";
    private String strStatus = "";
    private String strProduct = "";
    private String page = "";
    private String search_url;
    private RecyclerView recyclerView;
    private ReportAdapter adapter;
    private ReportAdapter2 adapter2;
    private ProgressBar main_progressbar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int total_page = 1;
    private int currentPage = 0;
    private TextView tv_noResult;
    private DatePickerType datePickerType;
    private TextView tv_start_date;
    private TextView tv_end_date;
    private int adapterType = 1;

    public ReportFragment() {
        // constructor
    }

    public static ReportFragment newInstance(ReportTypes reportTypes) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putSerializable(REPORT_TYPE, reportTypes);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_money_transaction, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        userData = new UserData(getContext());

        //search hashMap
        searchTypeHashMap.put("Record ID", "RECORD_ID");
        searchTypeHashMap.put("Txn ID", "TXN_ID");
        searchTypeHashMap.put("Account No", "ACCOUNT_NO");
        searchTypeHashMap.put("Mobile Number", "MOB_NO");
        searchTypeHashMap.put("Date", "DATE");

        creditDebitHashMap.put("CREDIT", "CREDIT");
        creditDebitHashMap.put("DEBIT", "DEBIT");

        HashMap<String, String> statusHashMap = new HashMap<>();
        HashMap<String, String> productHashMap = new HashMap<>();
        //status hashMap
        statusHashMap.put("--Select--", "0");
        statusHashMap.put("Success", "1");
        statusHashMap.put("Failed", "2");
        statusHashMap.put("Pending", "3");
        statusHashMap.put("Refunded", "4");
        statusHashMap.put("Successfully Submitted", "24");
        statusHashMap.put("Refund Success", "21");
        statusHashMap.put("Commission", "22");
        statusHashMap.put("In-Process", "18");

        //product hashmap
        productHashMap.put("--Select--", "0");
        productHashMap.put("DMT1", "4");
        productHashMap.put("A2Z wallet", "5");
        productHashMap.put("AEPS", "10");
        productHashMap.put("Verify", "2");
        productHashMap.put("Recharge/Bill Payment", "1");

        orderProductHashMap = (HashMap<String, String>) AppUitls.sortByComparator(productHashMap, true);
        orderStatusHashMap = (HashMap<String, String>) AppUitls.sortByComparator(statusHashMap, true);
        ivb_Search = view.findViewById(R.id.btn_search);
        ivb_refreshTransaction = view.findViewById(R.id.ivb_refreshTransaction);

        ivb_refreshTransaction.setOnClickListener(view12 -> {

            startAnimation(view12);

            // reload current fragment
            Fragment fragment = ReportFragment.newInstance(ReportTypes.LEDGER_REPORT);
            ;
            FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        });

        if (reportType == ReportTypes.ACCOUNT_STATEMENT || reportType == ReportTypes.NETWORK_RECHARGE)
            adapterType = 2;
        else adapterType = 1;

        if (adapterType == 2)
            adapter2 = new ReportAdapter2(getActivity(), reportType);
        else {
            adapter = new ReportAdapter(getActivity(), reportType);
        }

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (adapterType == 2)
            recyclerView.setAdapter(adapter2);
        else recyclerView.setAdapter(adapter);


        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {

            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                getReports(2);
            }

            @Override
            public int getTotalPageCount() {
                return total_page;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

        });

        getReports(1);

        ivb_Search.setOnClickListener(view1 -> {

            startAnimation(view1);

            start_date = "";
            end_date = "";

            Dialog dialog = AppDialogs.searchReport1(getActivity());
            tv_start_date = dialog.findViewById(R.id.tv_start_date);
            tv_end_date = dialog.findViewById(R.id.tv_end_date);
            Button btn_search = dialog.findViewById(R.id.btn_search);
            ImageButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
            LinearLayout ll_date = dialog.findViewById(R.id.ll_date);

            LinearLayout ll_product = dialog.findViewById(R.id.ll_product);
            LinearLayout ll_status = dialog.findViewById(R.id.ll_status);
            Spinner spn_status = dialog.findViewById(R.id.spn_status);
            Spinner spn_product = dialog.findViewById(R.id.spn_product);

            setupStatus(spn_status);
            setupProduct(spn_product);
            EditText ed_searchInput = dialog.findViewById(R.id.ed_searchType);
            Spinner spn_search = dialog.findViewById(R.id.spn_searchType);
            setupSearchTypeSpn(spn_search, ed_searchInput, ll_date, ll_product, ll_status);

            tv_start_date.setOnClickListener(view2 -> {
                datePickerType = DatePickerType.START_DATE;
                DatePicker.datePicker(getActivity());
            });

            tv_end_date.setOnClickListener(view2 -> {
                datePickerType = DatePickerType.END_DATE;
                DatePicker.datePicker(getActivity());
            });

            btn_cancel.setOnClickListener(view3 -> dialog.dismiss());
            btn_search.setOnClickListener(view4 -> {

                Intent intent = new Intent(getActivity(), ReportSearchActivity.class);
                intent.putExtra(REPORT_TYPE, reportType);
                if (strSearchType.equalsIgnoreCase("Date")) {

                    if (!start_date.isEmpty() && !end_date.isEmpty()) {
                        intent.putExtra("type", strSearchType);
                        intent.putExtra("date_from", start_date);
                        intent.putExtra("date_to", end_date);
                        intent.putExtra("statusOf", strStatus);
                        intent.putExtra("productOf", strProduct);
                        intent.putExtra("search_url", search_url);
                        startActivity(intent);
                        dialog.dismiss();
                    } else MakeToast.show(getActivity(), "Select start and end date");

                } else {

                    if (!ed_searchInput.getText().toString().isEmpty()) {
                        intent.putExtra("type", strSearchType);
                        intent.putExtra("searchInput", ed_searchInput.getText().toString());
                        intent.putExtra("search_url", search_url);
                        startActivity(intent);
                        dialog.dismiss();
                    } else MakeToast.show(getActivity(), "Enter search input ");

                }


            });

            dialog.show();
        });

        tv_noResult = view.findViewById(R.id.tv_noResult);
    }

    @Override
    public void onResume() {
        super.onResume();
        DatePicker.setupOnDatePicker(this);
    }

    private void setupSearchTypeSpn(Spinner spinner, EditText ed_searchInput,
                                    LinearLayout ll_date, LinearLayout ll_product,
                                    LinearLayout ll_status) {
        String[] prepaidStrings = searchTypeHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                R.layout.spinner_layout, prepaidStrings);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(dataAdapter);

        if (reportType == ReportTypes.ACCOUNT_STATEMENT) {
            int position = dataAdapter.getPosition("Date");
            spinner.setSelection(position);
            spinner.setEnabled(false);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSearchType = searchTypeHashMap.get(spinner.getSelectedItem().toString());

                if (spinner.getSelectedItem().toString().equalsIgnoreCase("Date")) {
                    ll_date.setVisibility(View.VISIBLE);
                    ed_searchInput.setVisibility(View.GONE);
                    if (reportType == ReportTypes.LEDGER_REPORT) {
                        ll_status.setVisibility(View.VISIBLE);
                        ll_product.setVisibility(View.VISIBLE);
                    } else if (ReportTypes.USAGE_REPORT == reportType) {
                        ll_status.setVisibility(View.VISIBLE);
                        ll_product.setVisibility(View.GONE);
                    }

                } else {
                    ll_date.setVisibility(View.GONE);
                    ed_searchInput.setVisibility(View.VISIBLE);
                    ll_product.setVisibility(View.GONE);
                    ll_status.setVisibility(View.GONE);
                }


                if (spinner.getSelectedItem().toString().equalsIgnoreCase("Name")) {
                    ed_searchInput.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    ed_searchInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void setupStatus(Spinner spinner) {
        String[] prepaidStrings = orderStatusHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                R.layout.spinner_layout, prepaidStrings);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(dataAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strStatus = orderStatusHashMap.get(spinner.getSelectedItem().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupProduct(Spinner spinner) {
        String[] prepaidStrings = orderProductHashMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                R.layout.spinner_layout, prepaidStrings);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strProduct = orderProductHashMap.get(spinner.getSelectedItem().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getReports(int type) {

        String final_report_url = APIs.LEDGER_REPORT;
        if (reportType == ReportTypes.LEDGER_REPORT)
            final_report_url = APIs.LEDGER_REPORT;
        else if (reportType == ReportTypes.USAGE_REPORT)
            final_report_url = APIs.USAGE_REPORT;
        else if (reportType == ReportTypes.ACCOUNT_STATEMENT)
            final_report_url = APIs.ACCOUNT_STATEMENT_REPORT;
        else if (reportType == ReportTypes.NETWORK_RECHARGE)
            final_report_url = APIs.NETWORK_RECHARGE_D;

        final_report_url = final_report_url
                + "?" + APIs.USER_TAG + "=" + userData.getId()
                + "&" + APIs.TOKEN_TAG + "=" + userData.getToken();

        search_url = final_report_url;

        if (type == 2) {
            final_report_url = final_report_url + "&" + page;
        } else showProgressBar(true, "Please wait...");
        final StringRequest request = new StringRequest(Request.Method.GET,
                final_report_url,
                response -> {
                    try {
                        dialogView.cancel();
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("1")) {

                            int count = jsonObject.getInt("count");
                            if (count > 0) {
                                total_page += 1;
                                page = jsonObject.getString("page");
                                JSONArray jsonArray = jsonObject.getJSONArray("reports");
                                parseData(jsonArray, type);
                            } else {
                                page = "";
                                if (adapterType == 2)
                                    adapter2.removeLoadingFooter();
                                else adapter.removeLoadingFooter();
                                isLoading = false;
                                isLastPage = true;
                                int adapterItemCount = 0;
                                if (adapterType == 2)
                                    adapterItemCount = adapter2.getItemCount();
                                else adapterItemCount = adapter.getItemCount();

                                if (adapterItemCount > 0) {
                                    noTransactionYet(false);
                                } else noTransactionYet(true);
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
                        dialogView.cancel();
                    }
                },
                error -> {
                    dialogView.cancel();
                }) {
        };

        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(240),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void parseData(JSONArray jsonArray, int type) {

        ArrayList<Report> reportList = new ArrayList<>();
        ReportJsonParser parser = new ReportJsonParser(jsonArray);

        try {
            reportList = parser.parseReportData(reportType);
        } catch (JSONException e) {
            MakeToast.show(getActivity(), e.getMessage());
            e.printStackTrace();
            dialogView.cancel();
        }

        if (type == 1) {

            dialogView.cancel();
            if (adapterType == 2)
                adapter2.addAll(reportList);
            else adapter.addAll(reportList);

            if (adapterType == 2) {
                if (currentPage <= total_page) adapter2.addLoadingFooter();
                else isLastPage = true;
            } else {
                if (currentPage <= total_page) adapter.addLoadingFooter();
                else isLastPage = true;
            }

        } else if (type == 2) {

            if (adapterType == 2) {
                adapter2.removeLoadingFooter();
                isLoading = false;
                adapter2.addAll(reportList);

                if (currentPage != total_page) adapter2.addLoadingFooter();
                else isLastPage = true;
            } else {
                adapter.removeLoadingFooter();
                isLoading = false;
                adapter.addAll(reportList);

                if (currentPage != total_page) adapter.addLoadingFooter();
                else isLastPage = true;
            }

        }

        if (adapterType == 2) {
            if (adapter2.getItemCount() > 0) {
                noTransactionYet(false);
            } else {
                noTransactionYet(true);
            }
        } else {
            if (adapter.getItemCount() > 0) {
                noTransactionYet(false);
            } else {
                noTransactionYet(true);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reportType = (ReportTypes) getArguments().getSerializable(REPORT_TYPE);
        }
    }

    @Override
    public void onDatePick(String date) {
        if (datePickerType == DatePickerType.START_DATE) {
            start_date = date;
            tv_start_date.setText(date);
        } else {
            end_date = date;
            tv_end_date.setText(date);
        }

    }

    void showProgressBar(boolean showText, String text) {

        /*to customize the progress bar then go to
         * progressbar_viewxml.xml in layout folder*/

        View view = getLayoutInflater().inflate(R.layout.layout_progressbar, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        CircularProgressIndicator lpi = view.findViewById(R.id.home_progress_bar);
        TextView textView = view.findViewById(R.id.progress_text_tv);
        if (showText) textView.setText(text);
        AlertDialog.Builder alertBldr_loading = new AlertDialog.Builder(getContext())
                .setCancelable(false);
        dialogView = alertBldr_loading.create();
        dialogView.setView(view);
        Window window = dialogView.getWindow();
        if (window != null) window.setBackgroundDrawableResource(R.color.Transparent);
        dialogView.show();
    }

    void noTransactionYet(boolean isTrue) {
        if (isTrue) {
            recyclerView.setVisibility(View.GONE);
            tv_noResult.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tv_noResult.setVisibility(View.GONE);
        }
    }

    public void startAnimation(View view) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(500);

        AnimationSet animation = new AnimationSet(false); //change to false
        //animation.addAnimation(fadeOut);
        animation.addAnimation(fadeIn);
        view.startAnimation(animation);
    }
}
