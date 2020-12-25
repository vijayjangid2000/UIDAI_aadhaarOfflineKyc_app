package com.vijayjangid.aadharkyc;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;

public class z_selBoardElectricity extends AppCompatActivity {

    final String BOARDS = "Ajmer Vidyut Vitran Nigam Ltd. (AVVNL)," +
            "Bharatpur Electricity Services Ltd. (BESL)," +
            "Bikaner Electricity Supply Limited," +
            "Jaipur Vidyut Vitran Nigam Ltd. (JVVNL)," +
            "Jodhpur Vidyut Vitran Nigam Ltd. (JDVVNL)," +
            "Kota Electricity Distribution Ltd. (KEDL)," +
            "TP Ajmer Distribution Ltd. (TPADL)";
    ListView listV_boards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_sel_board_electricity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Select Board");
        toolbar.setBackgroundColor(getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listV_boards = findViewById(R.id.board_listview);
        showSimList();
    }

    void showSimList() {
        // setting boards name
        ArrayList<String> boardsList = new ArrayList<String>();
        String[] array = BOARDS.split(",");
        Collections.addAll(boardsList, array);

        //setting images for boards
        ArrayList<Drawable> imageList = new ArrayList<>();
        imageList.add(ContextCompat.getDrawable(z_selBoardElectricity.this, R.drawable.z_ajmervvnl));
        imageList.add(ContextCompat.getDrawable(z_selBoardElectricity.this, R.drawable.z_cesc));
        imageList.add(ContextCompat.getDrawable(z_selBoardElectricity.this, R.drawable.z_cesc));
        imageList.add(ContextCompat.getDrawable(z_selBoardElectricity.this, R.drawable.z_jaivvn));
        imageList.add(ContextCompat.getDrawable(z_selBoardElectricity.this, R.drawable.z_jodvvnl));
        imageList.add(ContextCompat.getDrawable(z_selBoardElectricity.this, R.drawable.z_cesc));
        imageList.add(ContextCompat.getDrawable(z_selBoardElectricity.this, R.drawable.z_tradpl));

        CustomAdapter customAdapter = new CustomAdapter(boardsList, imageList);
        listV_boards.setAdapter(customAdapter);
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public class CustomAdapter implements ListAdapter {

        ArrayList<String> text_list;
        ArrayList<Drawable> imageAddressList;

        public CustomAdapter(ArrayList<String> text, ArrayList<Drawable> imageAddress) {
            this.text_list = text;
            this.imageAddressList = imageAddress;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final int pos = position;

            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(z_selBoardElectricity.this);
                convertView = layoutInflater.inflate(R.layout.listview_board_alert, null);

                TextView textView = convertView.findViewById(R.id.board_name_tv);
                ImageView imageView = convertView.findViewById(R.id.board_imageview);

                textView.setText(text_list.get(position));
                imageView.setImageDrawable(imageAddressList.get(position));

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("MESSAGE", text_list.get(pos));
                        setResult(98, intent);
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
            return text_list.size();
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
            return text_list.size();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

}