package edu.scau.buymesth.request;

import java.util.List;

import cn.bmob.v3.datatype.BmobQueryResult;
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
        Observable<List<Request>> getSomeonesRxRequests(int policy,List<String> userIds);
        Observable<List<Request>> getFuzzySearchRxRequests(int policy,String key);
        Observable<BmobQueryResult<Request>> getFollowedRxRequests(int policy, String key);
    }
}
