package com.vijayjangid.aadharkyc;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Notification extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayout ll_noNotification;
    TextView tvb_notificationSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Notifications");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_cross_close2);

        recyclerView = findViewById(R.id.recyclerViewNotification);
        tvb_notificationSetting = findViewById(R.id.tvb_notificationSetting);
        ll_noNotification = findViewById(R.id.ll_noNotification);

        tvb_notificationSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(Notification.this, "Opens notification setting activity",
                        Toast.LENGTH_SHORT).show();

            }
        });

        newFeature();
    }

    void showZeroItemsNotification(boolean zeroItems) {
        if (!zeroItems) {
            ll_noNotification.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            ll_noNotification.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    void newFeature() {

        final ArrayList<RecyclerEntity> arrayList = new ArrayList<>();
        RecyclerEntity entity = new RecyclerEntity();
        arrayList.add(entity);
        arrayList.add(entity);
        arrayList.add(entity);
        arrayList.add(entity);

        final RecyclerViewAdapter adapter =
                new RecyclerViewAdapter(Notification.this, arrayList);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        Toast.makeText(Notification.this, "on Move", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        Toast.makeText(Notification.this, "Message Deleted", Toast.LENGTH_SHORT).show();
                        //Remove swiped item from list and notify the RecyclerView
                        int position = viewHolder.getAdapterPosition();
                        arrayList.remove(position);
                        adapter.notifyDataSetChanged();

                        showZeroItemsNotification(arrayList.size() == 0);
                    }
                };


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        showZeroItemsNotification(arrayList.size() == 0);
    }

    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<RecyclerEntity> list;
        private LayoutInflater mInflater;

        RecyclerViewAdapter(Context context, List<RecyclerEntity> data) {
            this.mInflater = LayoutInflater.from(context);
            this.list = data;
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.listview_notification, parent, false);
            return new RecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        /*this comes again and again*/

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ViewHolder(View itemView) {
                super(itemView);
                /*here will be id and on click listeners*/
            }
        }

    }

    public class RecyclerEntity {
        private String title;
        private boolean showMenu = false;
        private int image;

        public RecyclerEntity() {
        }

        public RecyclerEntity(String title, int image, boolean showMenu) {
            this.title = title;
            this.showMenu = showMenu;
            this.image = image;
        }

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isShowMenu() {
            return showMenu;
        }

        public void setShowMenu(boolean showMenu) {
            this.showMenu = showMenu;
        }
    }

}