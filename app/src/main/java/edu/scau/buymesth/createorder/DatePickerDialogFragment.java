package edu.scau.buymesth.createorder;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import edu.scau.buymesth.R;

/**
 * Created by John on 2016/8/29.
 */

public class DatePickerDialogFragment extends DialogFragment {
    private int mYear;
    private int mMonth;
    private int mDay;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mYear=getArguments().getInt("year");
        mMonth=getArguments().getInt("month");
        mDay=getArguments().getInt("day");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_date_pick_dialog, null);
        DatePicker datePicker= (DatePicker) view.findViewById(R.id.date_picker);
        datePicker.init(mYear, mMonth, mDay, (view1, year, monthOfYear, dayOfMonth) -> {
            mYear=year;
            mMonth=monthOfYear;
            mDay=dayOfMonth;
        });
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("确定",
                        (dialog, id) -> {
                            ((CreateOrderActivity)getActivity()).tvDeliverTime.setText(mYear+"年"+(mMonth+1)+"月"+mDay+"日");
                            ((CreateOrderModel)((CreateOrderActivity)getActivity()).mPresenter.mModel).setYear(mYear);
                            ((CreateOrderModel)((CreateOrderActivity)getActivity()).mPresenter.mModel).setMonth(mMonth);
                            ((CreateOrderModel)((CreateOrderActivity)getActivity()).mPresenter.mModel).setDay(mDay);
                        }).setNegativeButton("取消", null);
        return builder.create();
    }
}
