package edu.scau.buymesth.home;

import java.util.List;

import base.BasePresenter;
import edu.scau.buymesth.data.bean.Request;
import rx.Observable;

/**
 * Created by John on 2016/8/5.
 */

public interface HomeContract {
    interface  View{

    }
    interface Presenter {
        void loadData();

    }
    interface Model{
        interface GetRequestListener{
            void onSuccess(List<Request>list);
        }
        void getRequests(GetRequestListener listener);
        Observable<List<Request>> getRxRequests();
    }
}
