package edu.scau.buymesth.publish;

import java.util.List;

import cn.finalteam.galleryfinal.model.PhotoInfo;
import edu.scau.buymesth.data.bean.Request;
import rx.Observer;
import rx.SingleSubscriber;

/**
 * Created by Jammy on 2016/8/16.
 */
public interface PublishContract {
    interface  View{
        void onSubmitFinish();
        void onSubmitFail();
        void showLoadingDialog();
        void closeLoadingDialog();
    }

    interface Model{
        void submit(Request request, SingleSubscriber<String> observable, List<PhotoInfo> list);
    }


}
