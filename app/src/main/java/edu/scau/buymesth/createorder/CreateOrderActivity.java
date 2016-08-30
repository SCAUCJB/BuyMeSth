package edu.scau.buymesth.createorder;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import base.BaseActivity;
import butterknife.Bind;
import edu.scau.buymesth.R;

/**
 * Created by John on 2016/8/29.
 */

public class CreateOrderActivity extends BaseActivity {
    @Bind(R.id.tv_deliver_time)
    public TextView tvDeliverTime;
    Calendar c =  Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);
    public void showDatePickDialog()
    {

        DatePickerDialogFragment dialog = new DatePickerDialogFragment();
        Bundle bundle=new Bundle();
        bundle.putInt("year",year);
        bundle.putInt("month",month);
        bundle.putInt("day",day);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "DatePicker");

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_order;
    }

    @Override
    public void initView() {
        tvDeliverTime.setText(year+"年"+month+"月"+day+"日");
    }
    protected  int getToolBarId(){
        return  R.id.toolbar;
    }

    @Override
    protected void setListener() {
       tvDeliverTime.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               showDatePickDialog();
           }
       });
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }
}
