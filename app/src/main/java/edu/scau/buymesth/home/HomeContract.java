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
        void onLoadMoreSuccess(List<Request>list);
        void showError(String msg);
        void onRefreshComplete(List<Request>list);
        void onRefreshFail();
        void setAdapter(List<Request>list);
    }

    interface Model{

        List<Request> getDatas();
        void setDatas(List<Request>list);
        void resetPage();
        Observable<List<Request>> getRxRequests(int policy);
        Observable<List<Request>> getSomeonesRxRequests(int policy,String userId);
    }
}
