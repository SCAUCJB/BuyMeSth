package edu.scau.buymesth.request.requestcomment;

import edu.scau.buymesth.data.bean.Request;

/**
 * Created by John on 2016/9/7.
 */

public interface CommentContract {
    interface View{

    }
    interface Model{
        void setRequest(Request request);
        Request getRequest();
    }
}
