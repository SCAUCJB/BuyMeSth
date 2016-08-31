package edu.scau.buymesth.createorder;

import java.util.Calendar;

import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by John on 2016/8/30.
 */

public class CreateOrderModel   implements CreateOrderContract.Model {
    private Order order;
    private Request request;
    private User buyer;
    private User seller;
    private Calendar c =  Calendar.getInstance();
    private int year = getC().get(Calendar.YEAR);
    private int month = getC().get(Calendar.MONTH);
    private int day = getC().get(Calendar.DAY_OF_MONTH);

    @Override
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
        setBuyer(request.getAuthor());
    }

    @Override
    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    @Override
    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public Calendar getC() {
        return c;
    }

    public void setC(Calendar c) {
        this.c = c;
    }

    @Override
    public int getYear() {
        return year;
    }
    @Override
    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public int getMonth() {
        return month;
    }
    @Override
    public void setMonth(int month) {
        this.month = month;
    }

    @Override
    public int getDay() {
        return day;
    }


    @Override
    public void setDay(int day) {
        this.day = day;
    }
}
