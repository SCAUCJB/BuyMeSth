package edu.scau.buymesth.homedetail;

import edu.scau.buymesth.data.bean.Request;

/**
 * Created by John on 2016/8/23.
 */

public class RequestDetailModel implements RequestDetailContract.Model {
    private Request request;

    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    public String getCommentBtnStr() {
        return "要勾搭，先评论";
    }
}
