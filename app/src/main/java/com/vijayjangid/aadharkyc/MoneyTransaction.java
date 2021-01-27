package com.vijayjangid.aadharkyc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class MoneyTransaction extends Fragment {

    View root;
    RecyclerView recyclerView;
    ImageView ivb_searchTransaction, ivb_refreshList;
    SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_money_transaction, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.rv_moneyTransaction);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<String> arrayList = new ArrayList();
        arrayList.add("");
        arrayList.add("");
        arrayList.add("");
        arrayList.add("");
        arrayList.add("");
        arrayList.add("");
        recyclerView.setAdapter(new RecyclerViewAdapter2(arrayList));

        ivb_searchTransaction = root.findViewById(R.id.ivb_searchTransaction);
        ivb_refreshList = root.findViewById(R.id.ivb_refreshTransaction);
        searchView = root.findViewById(R.id.searchview_moneytransa);

        ivb_searchTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchView.getVisibility() == View.VISIBLE) searchView.setVisibility(View.GONE);
                else {
                    searchView.setVisibility(View.VISIBLE);
                    searchView.requestFocus();
                }
            }
        });

        showSearchAlert();

        ivb_refreshList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Refreshing..", Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    void showAlert() {
        SheetClass sheetClass = new SheetClass();
        sheetClass.show(getActivity().getSupportFragmentManager(), "exampleBottomSheet");
    }

    void showSearchAlert() {
        searchView.setIconified(false);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.setVisibility(View.GONE);
                return true;
            }
        });

        searchView.setVisibility(View.GONE);
        searchView.clearFocus();

    }

    public static class SheetClass extends BottomSheetDialogFragment {

        SheetClass sheetClass;
        View view;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.layout_transaction_summary, container, false);

            final ImageView ivb_close, ivb_share, ivb_copy;
            TextView tvb_complain;
            RecyclerView rv_summary;

            ivb_close = view.findViewById(R.id.ivb_ClosetranSumLayt);
            ivb_share = view.findViewById(R.id.ivb_SharetranSumLayt);
            ivb_copy = view.findViewById(R.id.ivb_CopytranSumLayt);
            tvb_complain = view.findViewById(R.id.tvb_ComplaintranSumLayt0);
            rv_summary = view.findViewById(R.id.rv_summary);

            ivb_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sheetClass = SheetClass.this;
                    sheetClass.dismiss();
                }
            });

            ivb_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "OILab Complain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Here will be the data in a particular format");
                    startActivity(Intent.createChooser(shareIntent, "Share Link via "));
                }
            });

            ivb_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Transaction details copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            });

            tvb_complain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Complain page will open", Toast.LENGTH_SHORT).show();
                }
            });

            rv_summary.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_summary.setAdapter(new MyListAdapter());

            return view;
        }
    }

    public static class MyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.listview_transaction_data, parent, false);
            return new ViewHolder(listItem);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 20;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                TextView tvRow1 = itemView.findViewById(R.id.tv_row1);
                TextView tvRow2 = itemView.findViewById(R.id.tv_row2);
            }

        }
    }

    class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerViewAdapter2.ViewHolder> {

        private ArrayList<String> listData;

        // RecyclerView recyclerView;
        public RecyclerViewAdapter2(ArrayList<String> listData) {
            this.listData = listData;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.listview_transaction_history, parent, false);
            ViewHolder viewHolder = new ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return listData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlert();
                    }
                });
            }

        }

    }

}