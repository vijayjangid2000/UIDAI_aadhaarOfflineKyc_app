package com.vijayjangid.aadharkyc;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;

public class z_selOperatorRecharge extends AppCompatActivity {

    // add operator , separated by comma with two spaces
    final String OPERATORS = "  Airtel,  BSNL,  Jio,  MTNL Delhi,  MTNL Mumbai," +
            "  Tata Docomo,  Tata Indicom,  Vodafone Idea";
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_select_operator_recharge);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Select Operator");
        toolbar.setBackgroundColor(getColor(R.color.colorPrimary));
        //toolbar.setNavigationIcon(R.drawable.ic_cross_close);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = findViewById(R.id.listview_operators);
        showSimList();
    }

    // this shows the main list (airtel, jio ...)
    void showSimList() {
        ArrayList<String> operatorList = new ArrayList<String>();
        String[] array = OPERATORS.split(",");
        Collections.addAll(operatorList, array);

        CustomAdapter customAdapter = new CustomAdapter(operatorList);
        listView.setAdapter(customAdapter);
    }

    // this gives the list for the operator,
    // if you change operator name then change it in switch "case" also
    ArrayList<String> getCircleList(String sim) {
        ArrayList<String> circles = new ArrayList<String>();
        String circlesToAdd;
        String[] circles_array;
        sim = sim.trim();

        switch (sim) {
            case "Airtel":
                circlesToAdd = " AP and Telangana, Assam, Bihar, Chennai, Delhi, " +
                        "Gujarat, Haryana, Himachal Pradesh, Jammu and Kashmir, " +
                        "Karnataka, Kerala, Kolkata, Madhya Pradesh, Maharashtra, " +
                        "Mumbai, North East, Orissa, Punjab, Rajasthan, Tamil Nadu, " +
                        "Uttar Pradesh (East), Uttar Pradesh (West), West Bengal";
                circles_array = circlesToAdd.split(",");
                Collections.addAll(circles, circles_array);
                break;
            case "BSNL":
                circlesToAdd = " AP and Telangana, Assam, Bihar, Chennai, " +
                        "Gujarat, Haryana, Himachal Pradesh, Jammu and Kashmir, " +
                        "Karnataka, Kerala, Kolkata, Madhya Pradesh, Maharashtra, " +
                        "North East, Orissa, Punjab, Rajasthan, Tamil Nadu, " +
                        "Uttar Pradesh  (East), Uttar Pradesh (West), West Bengal";
                circles_array = circlesToAdd.split(",");
                Collections.addAll(circles, circles_array);
                break;
            case "Jio":
                circlesToAdd = " AP and Telangana, Assam, Bihar, Delhi, " +
                        "Gujarat, Haryana, Himachal Pradesh, Jammu and Kashmir, " +
                        "Karnataka, Kerala, Kolkata, Madhya Pradesh, Maharashtra, " +
                        "Mumbai, North East, Orissa, Punjab, Rajasthan, Tamil Nadu, " +
                        "Uttar Pradesh  (East), Uttar Pradesh (West), West Bengal";
                circles_array = circlesToAdd.split(",");
                Collections.addAll(circles, circles_array);
                break;
            case "MTNL Delhi":
                circlesToAdd = "Delhi";
                circles_array = circlesToAdd.split(",");
                Collections.addAll(circles, circles_array);
                break;
            case "MTNL Mumbai":
                circlesToAdd = "Mumbai";
                circles_array = circlesToAdd.split(",");
                Collections.addAll(circles, circles_array);
                break;
            case "Tata Docomo":
                circlesToAdd = " AP and Telangana, Bihar, Chennai, Delhi, " +
                        "Gujarat, Haryana, Himachal Pradesh, " +
                        "Karnataka, Kerala, Kolkata, Madhya Pradesh, Maharashtra, " +
                        "Mumbai, Orissa, Punjab, Rajasthan, Tamil Nadu, " +
                        "Uttar Pradesh  (East), Uttar Pradesh (West), West Bengal";
                circles_array = circlesToAdd.split(",");
                Collections.addAll(circles, circles_array);
                break;
            case "Tata Indicom":
                circlesToAdd = " AP and Telangana, Bihar, Chennai, Delhi, " +
                        "Gujarat, Haryana, Himachal Pradesh, " +
                        "Karnataka, Kerala, Kolkata, Madhya Pradesh, Maharashtra, " +
                        "Mumbai, Orissa, Punjab, Rajasthan, Tamil Nadu, " +
                        "Uttar Pradesh  (East), Uttar Pradesh (West), West Bengal";
                circles_array = circlesToAdd.split(",");
                Collections.addAll(circles, circles_array);
                break;
            case "Vodafone Idea":
                circlesToAdd = " AP and Telangana, Bihar, Chennai, Delhi, " +
                        "Gujarat, Haryana, Himachal Pradesh, Jammu and Kashmir" +
                        "Karnataka, Kerala, Kolkata, Madhya Pradesh, Maharashtra, " +
                        "Mumbai, North East, Orissa, Punjab, Rajasthan, Tamil Nadu, " +
                        "Uttar Pradesh  (East), Uttar Pradesh (West), West Bengal";
                circles_array = circlesToAdd.split(",");
                Collections.addAll(circles, circles_array);
                break;
        }
        return circles;
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    // custom adapter for main list here
    public class CustomAdapter implements ListAdapter {

        ArrayList<String> listOperators;

        // constructor for the class here
        public CustomAdapter(ArrayList<String> arrayList) {
            this.listOperators = arrayList;
        }

        // here is the code for sublist
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(z_selOperatorRecharge.this);
                convertView = layoutInflater.inflate(R.layout.listview_operator_alert, null);

                final TextView tv_operator = convertView.findViewById(R.id.operator_textview);
                tv_operator.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_down_arrow, 0, 0, 0);
                final ListView subListView = convertView.findViewById(R.id.subListview_operators);

                tv_operator.setText(listOperators.get(position));

                // getting arrayList for sublist
                final ArrayList<String> arrayList = getCircleList(listOperators.get(position));
                final ArrayAdapter adapter = new ArrayAdapter(z_selOperatorRecharge.this,
                        android.R.layout.simple_list_item_1, arrayList.toArray());

                subListView.setVisibility(View.GONE);
                subListView.setBackgroundColor(Color.parseColor("#EDEBEB"));
                tv_operator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (subListView.getVisibility() == View.GONE) {
                            tv_operator.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cross_close, 0, 0, 0);
                            subListView.setVisibility(View.VISIBLE);
                            subListView.setAdapter(adapter);

                            int numberOfItems = subListView.getAdapter().getCount();

                            // Get total height of all items.
                            int totalItemsHeight = 0;
                            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                                View item = subListView.getAdapter().getView(itemPos, null, subListView);
                                item.measure(0, 0);
                                totalItemsHeight += item.getMeasuredHeight();
                            }

                            // Get total height of all item dividers.
                            int totalDividersHeight = subListView.getDividerHeight() *
                                    (numberOfItems - 1);

                            // Set list height.
                            ViewGroup.LayoutParams params = subListView.getLayoutParams();
                            params.height = totalItemsHeight + totalDividersHeight;
                            subListView.setLayoutParams(params);
                            subListView.requestLayout();

                        } else if (subListView.getVisibility() == View.VISIBLE) {
                            subListView.setVisibility(View.GONE);
                            tv_operator.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_down_arrow, 0, 0, 0);
                        }
                    }
                });

                final int temp = position;
                subListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent();
                        intent.putExtra("MESSAGE", listOperators.get(temp) + " - "
                                + arrayList.get(position));
                        setResult(99, intent);
                        finish();
                    }
                });

            }

            return convertView;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return listOperators.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return listOperators.size();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}