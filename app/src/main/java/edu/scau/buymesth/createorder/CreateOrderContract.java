package edu.scau.buymesth.createorder;

import java.util.List;

import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by John on 2016/8/30.
 */

public interface CreateOrderContract {
    interface View{
        void setDeliverTime(int year,int month,int day);
        void showDatePickDialog(int year, int month, int day);
        void closeDatePickDialog();

        void setRequestInfo(User buyer, String title, String content, String createdAt);

        void setTagList(List<String> tags);
    }
    interface Model{
        Order getOrder();

        Request getRequest();

        User getBuyer();

        User getSeller();

        int getYear();

        int getMonth();

        int getDay();
        void setYear(int year);
        void setMonth(int month);
        void setDay(int day);
    }
}
