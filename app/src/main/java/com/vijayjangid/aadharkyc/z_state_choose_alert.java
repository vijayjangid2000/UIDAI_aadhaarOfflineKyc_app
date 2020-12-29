package com.vijayjangid.aadharkyc;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class z_state_choose_alert extends AppCompatActivity {

    final int RESULT_CODE_STATE = 97;

    final String intentListText = "TEXT_LIST";
    final String toolbarName = "TOOLBAR_NAME";

    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_state_choose_alert);

        Intent intent = getIntent();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(intent.getStringExtra(toolbarName));
        toolbar.setBackgroundColor(getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final ArrayList<String> textList = intent.getStringArrayListExtra(intentListText);
        listview = findViewById(R.id.state_listview);
        showSimList(textList);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setAlpha((float) 0.4);
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", textList.get(position));
                setResult(RESULT_CODE_STATE, intent);
                finish();
            }
        });

        final ArrayList<String> searchTextList = new ArrayList<>();
        final SearchView searchView = findViewById(R.id.searchview_list2);
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
                        searchView.setBackground(ContextCompat.getDrawable(z_state_choose_alert.this,
                                R.drawable.rounded_shape_3));
                    }
                    showSimList(searchTextList);
                }

                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                showSimList(textList);
                searchView.clearFocus();
                return false;
            }
        });

    }

    void showSimList(ArrayList<String> textList) {
        // ________setting_boards_name_________
        // from the global ArrayList
        final ArrayAdapter adapter = new ArrayAdapter(z_state_choose_alert.this,
                android.R.layout.simple_list_item_1, textList);
        listview.setAdapter(adapter);
        //setting images for boards
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}