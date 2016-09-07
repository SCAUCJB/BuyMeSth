package edu.scau.buymesth.createorder;

import java.util.List;

import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;
import rx.Observable;

/**
 * Created by John on 2016/8/30.
 */

public interface CreateOrderContract {
    interface View {
        void setDeliverTime(int year, int month, int day);

        void showDatePickDialog(int year, int month, int day);

        void closeDatePickDialog();

        void setPrice(String price);

        void setRequestInfo(User buyer, String title, String content);

        void setTagList(List<String> tags);

        void initPickerView();

        String getTip();

        String getPrice();

        void showMsg(String msg);

        void showLoadingDialog();

        void closeLoadingDialog();

        User getSeller();

        void exit();
    }

    interface Model {
        Order getOrder();

        Request getRequest();

        User getBuyer();

        User getSeller();

        void setSeller(User seller);

        int getYear();

        int getMonth();

        int getDay();

        void setYear(int year);

        void setMonth(int month);

        void setDay(int day);

        void setPriceType(String text);

        void setTipType(String text);

        void setTip(Float tip);

        void setPrice(Float price);

        Observable<String> submit();
    }
}
