package edu.scau.buymesth.userinfo.evaluate;

import java.util.List;

import edu.scau.buymesth.data.bean.Evaluate;

/**
 * Created by John on 2016/10/7.
 */

public interface Contract {
interface View{

    void setEvaluates(List<Evaluate> evaluates);
    void onLoadMore(List<Evaluate>evaluates);
}
}
