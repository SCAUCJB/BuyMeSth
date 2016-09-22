package edu.scau.buymesth.user.moment;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import edu.scau.buymesth.data.bean.Moment;
import edu.scau.buymesth.data.bean.MomentsLike;
import rx.Observable;

/**
 * Created by IamRabbit on 2016/8/10.
 */
public class MomentContract {
    interface  View{
        void onLoadMoreSuccess(List<Moment> list);
        void onError(Throwable throwable, String msg);
        void onRefreshComplete(List<Moment> list);
        void onInitializeLocalDataComplete();
        void onAddOneItem(Moment moment);
        void onItemChanged();
        void onRefreshInterrupt();
    }

    interface Model{
        List<MomentsLike> getLikesList();
        List<Moment> getDatas();
        void setDatas(List<Moment> list);
        void resetPage();
        Observable<List<Moment>> getRxMoments(BmobQuery.CachePolicy cachePolicy);
        void updateLikeList();
    }
}
