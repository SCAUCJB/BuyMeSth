package edu.scau.buymesth.discover.list;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import edu.scau.Constant;
import edu.scau.buymesth.data.bean.Moment;
import edu.scau.buymesth.data.bean.MomentsLike;
import edu.scau.buymesth.data.bean.User;
import rx.Observable;

/**
 * Created by IamRabbit on 2016/8/10.
 */
public class DiscoverModel implements DiscoverContract.Model{

    private List<Moment> momentList;
    private int loadedPageNum;
    private List<MomentsLike> likeList;

    public DiscoverModel(){
        momentList = new LinkedList<>();
        loadedPageNum = 0;
    }

    public void updateLikeList(){
        likeList = new ArrayList<>();
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<MomentsLike> query = new BmobQuery<MomentsLike>();
        query.addWhereEqualTo("liker", user.getObjectId());
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存两天，可以用来优化下拉刷新而清空了的加载更多
        //执行查询方法
        query.findObjects(new FindListener<MomentsLike>() {
            @Override
            public void done(List<MomentsLike> object, BmobException e) {
                if(e==null){
                    likeList = object;
                }
            }
        });
    }

    @Override
    public List<MomentsLike> getLikesList() {
        return likeList;
    }

    @Override
    public List<Moment> getDatas() {
        return this.momentList;
    }

    @Override
    public void setDatas(List<Moment> list) {
        this.momentList = list;
    }

    @Override
    public void resetPage() {
        loadedPageNum = 0;
    }

    public Observable<List<Moment>> getRxMoments(BmobQuery.CachePolicy cachePolicy) {
        BmobQuery<Moment> query=new BmobQuery<>();
        query.setCachePolicy(cachePolicy);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存两天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.include("author,request");
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (loadedPageNum++));
        return query.findObjectsObservable(Moment.class);
    }
}
