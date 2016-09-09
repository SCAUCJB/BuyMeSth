package edu.scau.buymesth.discover.detail;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import edu.scau.buymesth.data.bean.Moment;
import edu.scau.buymesth.data.bean.MomentsComment;
import rx.Observable;

/**
 * Created by IamRabbit on 2016/8/24.
 */
public class MomentDetailContract {
    interface  View{
        void onLoadMoreSuccess(List<MomentsComment>list);
        void onError(Throwable throwable,String msg);
        void onRefreshComplete(List<MomentsComment>list);
        void onInitializeLocalDataComplete();
        void onAddOneItem(MomentsComment moment);
        void onItemChanged();
        void onRefreshInterrupt();
        void onLoadMoreInterrupt();
        void onPostCommentSuccess(String msg);
        void initMomentView();
        void setLike(Boolean b,int i);
    }

    interface Model{
        List<MomentsComment> getDatas();
        void setDatas(List<MomentsComment>list);
        void resetPage();
        Observable<List<MomentsComment>> getRxComments(BmobQuery.CachePolicy cachePolicy);
        void updateLikeList();
        void setMoment(Moment moment);
        Moment getMoment();
    }
}
