package edu.scau.buymesth.data.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by John on 2016/8/6.
 */

public class Order extends BmobObject {
    Float price;
    Short status;
    String accomplishAt;
    String acceptAt;
    User seller;
    User buyer;
    Request request;
    String expressInc;
    String expressNumber;

}
