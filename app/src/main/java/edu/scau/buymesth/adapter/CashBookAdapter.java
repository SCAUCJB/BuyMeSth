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
        helper.setText(R.id.tv_money, String.valueOf(item.getCash())+"￥");
        helper.setText(R.id.tv_time, item.getUpdatedAt());
            switch (item.getType()) {
                case CashBook.BUYER_PAY:
                    helper.setText(R.id.tv_status, "订单付款");
                    break;

                case CashBook.SELLER_GET:
                    helper.setText(R.id.tv_status, "交易成功");
                    break;

                case CashBook.SELLER_CANCLE:
                    helper.setText(R.id.tv_status, "取消订单退款");
                    break;

                case CashBook.DEPOSIT:
                    helper.setText(R.id.tv_status, "充值");
                    break;

                case CashBook.WITHDRAW:
                    helper.setText(R.id.tv_status, "提现");
                    break;

            }
    }
}
