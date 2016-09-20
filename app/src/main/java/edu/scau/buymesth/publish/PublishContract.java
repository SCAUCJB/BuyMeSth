package edu.scau.buymesth.publish;

import java.util.List;

import edu.scau.buymesth.data.bean.Request;
import rx.Subscriber;

/**
 * Created by Jammy on 2016/8/16.
 */
public interface PublishContract {
    interface  View{
        void onSubmitFinish();
        void onSubmitFail();
        void showLoadingDialog();
        void closeLoadingDialog();

        void setProgress(Integer progress);
    }

    interface Model{
        void submit( List<String> picHeights,List<String> picWidths,Subscriber<Integer> observable, List<String> list);

        void setRequest(Request request);
    }


}
