package edu.scau.buymesth.adapter;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.CashBook;

/**
 * Created by Jammy on 2016/10/7.
 */
public class CashBookAdapter extends BaseQuickAdapter<CashBook> {

    public CashBookAdapter(List<CashBook> data) {
        super(R.layout.item_cashbook, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CashBook item) {

        helper.setText(R.id.tv_time, item.getUpdatedAt());
            switch (item.getType()) {
                case CashBook.PAY:
                    helper.setText(R.id.tv_status, "订单付款");
                    helper.setText(R.id.tv_money, "-"+String.valueOf(item.getCash())+"￥");
                    break;

                case CashBook.GET:
                    helper.setText(R.id.tv_status, "交易成功");
                    helper.setText(R.id.tv_money, "+"+String.valueOf(item.getCash())+"￥");
                    break;

                case CashBook.CANCLE:
                    helper.setText(R.id.tv_status, "卖家取消订单退款");
                    helper.setText(R.id.tv_money, "+"+String.valueOf(item.getCash())+"￥");
                    break;

                case CashBook.DEPOSIT:
                    helper.setText(R.id.tv_status, "充值");
                    helper.setText(R.id.tv_money, "+"+String.valueOf(item.getCash())+"￥");
                    break;

                case CashBook.WITHDRAW:
                    helper.setText(R.id.tv_status, "提现");
                    helper.setText(R.id.tv_money, "-"+String.valueOf(item.getCash())+"￥");
                    break;

            }
    }
}
