package Electricity;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vijayjangid.aadharkyc.R;

import MobRecharge.OperatorModel;

public class SheetBill extends BottomSheetDialogFragment {

    View view;
    OperatorModel operatorModel;
    ConfirmInterface confirmInterface;

    public SheetBill(OperatorModel operatorModel,
                     ConfirmInterface confirmInterface) {

        this.operatorModel = operatorModel;
        this.confirmInterface = confirmInterface;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_confirm_payment,
                container, false);

        TextView tv_amount = view.findViewById(R.id.tv_amountConfirm);
        Button btn_confirm = view.findViewById(R.id.btn_ConfirmPayment);
        TextView tv_costDetail = view.findViewById(R.id.tv_detailConfirm);

        // setting the cost here
        tv_amount.setText("Rs. " + operatorModel.getBillCost());

        // setting deails using the methods below
        if (operatorModel.isPostpaid()) tv_costDetail.setText(getPostPaidDetails(operatorModel));
        else tv_costDetail.setText(getPrePaidDetails(operatorModel));

        btn_confirm.setOnClickListener(view -> {
            confirmInterface.makeRechargeInterface();
        });

        return view;
    }

    Spanned getPostPaidDetails(OperatorModel model) {

        String firstColor = "<font color=#4fa5d5>";
        String secondColor = "</font> <font color=#222020>";
        String lastCloseColor = "</font>";
        String breakLine = "<br><br>";

        StringBuilder stringBuilder = new StringBuilder("");

        String customerName = firstColor + "Customer Name: " + secondColor
                + model.getCustomerName() + lastCloseColor + breakLine;

        stringBuilder.append(customerName);

        String mobile = firstColor + "Mobile Number: " + secondColor
                + model.getMobileNumber() + lastCloseColor + breakLine;

        stringBuilder.append(mobile);

        String operatorName = firstColor + "Operator: " + secondColor
                + model.getProviderName() + lastCloseColor + breakLine;

        stringBuilder.append(operatorName);

        String dueDate = firstColor + "Due Date: " + secondColor +
                model.getDueDate() + lastCloseColor + breakLine;

        stringBuilder.append(dueDate);

        String amount = firstColor + "Amount: " + secondColor +
                model.getBillCost() + lastCloseColor;

        stringBuilder.append(amount);

        return Html.fromHtml(stringBuilder.toString());
    }

    Spanned getPrePaidDetails(OperatorModel model) {

        String firstColor = "<font color=#4fa5d5>";
        String secondColor = "</font> <font color=#222020>";
        String lastCloseColor = "</font>";

        StringBuilder stringBuilder = new StringBuilder("");

        String line1 = firstColor + "Mobile Number: " + secondColor
                + model.getMobileNumber() + lastCloseColor + "<br><br>";

        stringBuilder.append(line1);

        String line2 = firstColor + "Operator: " + secondColor
                + model.getProviderName() + lastCloseColor;

        stringBuilder.append(line2);

        return Html.fromHtml(stringBuilder.toString());

    }

    public interface ConfirmInterface {
        void makeRechargeInterface();
    }

}