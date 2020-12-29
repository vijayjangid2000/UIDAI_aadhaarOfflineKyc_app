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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class z_selectImageText extends AppCompatActivity {

    final String intentListText = "TEXT_LIST";
    final String intentListImage = "IMAGE_LINK_LIST";
    final String toolbarName = "TOOLBAR_NAME";

    GridView listV_boards;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_sel_board_electricity);

        Intent intent = getIntent();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(intent.getStringExtra(toolbarName));
        toolbar.setBackgroundColor(getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final ArrayList<String> textList;
        textList = intent.getStringArrayListExtra(intentListText);

        final ArrayList<String> imageList;
        imageList = intent.getStringArrayListExtra(intentListImage);

        listV_boards = findViewById(R.id.board_listview);
        listV_boards.setNumColumns(2);
        showSimList(textList, imageList);

        final ArrayList<String> searchTextList = new ArrayList<>();
        searchView = findViewById(R.id.searchview_list);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchTextList.clear();
                for (int i = 0; i < textList.size(); i++) {
                    if (textList.get(i).toLowerCase().trim().
                            contains(newText.toLowerCase().trim())) {
                        searchTextList.add(textList.get(i));
                    }

                    if (searchTextList.size() != 0) {
                        searchView.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        searchView.setBackground(ContextCompat.getDrawable(z_selectImageText.this,
                                R.drawable.rounded_shape_3));
                    }
                    showSimList(searchTextList, imageList);
                }

                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                showSimList(textList, imageList);
                searchView.clearFocus();
                return false;
            }
        });

        listV_boards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setAlpha((float) 0.4);
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", textList.get(position));
                setResult(98, intent);
                finish();
            }
        });


    }

    void showSimList(ArrayList<String> textList, ArrayList<String> imageLinks) {
        // ________setting_boards_name_________
        // from the global ArrayList

        //setting images for boards
        CustomAdapter customAdapter = new CustomAdapter(textList, imageLinks);
        listV_boards.setAdapter(customAdapter);
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public class CustomAdapter implements ListAdapter {

        ArrayList<String> textList;
        ArrayList<String> imageAddressList;

        public CustomAdapter(ArrayList<String> textList, ArrayList<String> imageAddress) {
            this.textList = textList;
            this.imageAddressList = imageAddress;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final int pos = position;

            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(z_selectImageText.this);
                convertView = layoutInflater.inflate(R.layout.listview_board_alert, null);

                TextView textView = convertView.findViewById(R.id.board_name_tv);
                ImageView imageView = convertView.findViewById(R.id.board_imageview);

                if (textList.get(position) != null)
                    textView.setText(textList.get(position));

                /*if(imageAddressList.get(position) != null)
                    imageView.setImageDrawable(imageAddressList.get(position));*/
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