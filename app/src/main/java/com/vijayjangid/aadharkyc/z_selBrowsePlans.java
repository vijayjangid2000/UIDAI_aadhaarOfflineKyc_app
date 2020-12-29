package com.vijayjangid.aadharkyc;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class z_selBrowsePlans extends AppCompatActivity {

    ListView listView;
    ArrayList<String> textList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_sel_browse_plans);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Select Plans");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = findViewById(R.id.browsePlanListView);
        textList = new ArrayList<>();

        textList.add("apple");
        textList.add("apple");
        textList.add("apple");
        textList.add("apple");
        textList.add("apple");
        textList.add("apple");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setAlpha((float) 0.4);
                finish();
            }
        });

        setListView();
    }

    void setListView() {
        listView.setAdapter(new CustomAdapter(textList));
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public class CustomAdapter implements ListAdapter {


        public CustomAdapter(ArrayList<String> textList) {

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final int pos = position;

            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(z_selBrowsePlans.this);
                convertView = layoutInflater.inflate(R.layout.listview_browse_plans, null);
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
            return textList.size();
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
            return textList.size();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}