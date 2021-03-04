package Aeps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vijayjangid.aadharkyc.R;

import java.util.ArrayList;


public class MiniStatementAdapter extends BaseAdapter {
    ArrayList<MiniSModel> arraylist = new ArrayList<MiniSModel>();
    Context c;
    LayoutInflater inflater;
    String from;

    TextView interestTxt;

    public MiniStatementAdapter(Context c, ArrayList<MiniSModel> s1) {

        this.c = c;

        this.arraylist = s1;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arraylist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        TextView date, desc, amount;


        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = null;


        itemView = inflater.inflate(R.layout.ms_item, parent, false);

        date = (TextView) itemView.findViewById(R.id.tv_date);
        desc = (TextView) itemView.findViewById(R.id.tv_description);
        amount = itemView.findViewById(R.id.tv_amount);


        MiniSModel model = arraylist.get(position);

        date.setText(model.getDate());
        desc.setText(model.getDesc());
        amount.setText(model.getAmount());

        if (model.getType().equalsIgnoreCase("Dr")) {
            amount.setTextColor(c.getResources().getColor(R.color.colorDarkRed));
        } else {
            amount.setTextColor(c.getResources().getColor(R.color.green));
        }

        return itemView;
    }


}

