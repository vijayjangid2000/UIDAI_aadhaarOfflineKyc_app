package Reports.Ledger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vijayjangid.aadharkyc.R;

import java.util.ArrayList;

public class MoneyTransaction extends Fragment {

    ImageView ivb_searchTransaction, ivb_refreshList;

    View root;
    RecyclerView recyclerView;
    SearchView searchView;
    private int layout = R.layout.fragment_money_transaction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_money_transaction, container, false);

        //recyclerView = root.findViewById(R.id.rv_moneyTransaction);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("");
        arrayList.add("");
        arrayList.add("");
        arrayList.add("");
        arrayList.add("");
        arrayList.add("");
        recyclerView.setAdapter(new RecyclerViewAdapter2(arrayList));

        //ivb_searchTransaction = root.findViewById(R.id.ivb_searchTransaction);
        ivb_refreshList = root.findViewById(R.id.ivb_refreshTransaction);
        //searchView = root.findViewById(R.id.searchview_moneytransa);

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

    /*void showAlert() {
        SheetClassReport sheetClass = new SheetClassReport();
        sheetClass.show(getActivity().getSupportFragmentManager(), "exampleBottomSheet");
    }*/

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
                        //showAlert();
                    }
                });
            }

        }

    }

}