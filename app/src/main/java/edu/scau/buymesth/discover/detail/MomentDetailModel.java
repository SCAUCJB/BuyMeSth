package edu.scau.buymesth.discover.detail;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import edu.scau.Constant;
import edu.scau.buymesth.data.bean.Moment;
import edu.scau.buymesth.data.bean.MomentsComment;
import rx.Observable;

/**
 * Created by IamRabbit on 2016/8/24.
 */
public class MomentDetailModel implements MomentDetailContract.Model{

    List<MomentsComment> momentsCommentList;
    private int loadedPageNum;
    private Moment moment;

    public MomentDetailModel(){
        this.momentsCommentList = new LinkedList<>();
        this.loadedPageNum = 0;
    }

    public MomentDetailModel(Moment moment){
        this.momentsCommentList = new LinkedList<>();
        this.loadedPageNum = 0;
        this.moment = moment;
    }

    @Override
    public List<MomentsComment> getDatas() {
        return momentsCommentList;
    }

    @Override
    public void setDatas(List<MomentsComment> list) {

    }

    @Override
    public void resetPage() {
        loadedPageNum = 0;
    }

    @Override
    public Observable<List<MomentsComment>> getRxComments(BmobQuery.CachePolicy cachePolicy) {
        BmobQuery<MomentsComment> query=new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存两天，可以用来优化下拉刷新而清空了的加载更多
        query.addWhereEqualTo("moment",moment.getObjectId());
        query.order("-createdAt");
        query.include("user");
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (loadedPageNum++));
        return query.findObjectsObservable(MomentsComment.class);
    }

    @Override
    public void updateLikeList() {

    }

    @Override
    public void setMoment(Moment moment) {
        this.moment = moment;
    }

    @Override
    public Moment getMoment() {
        return this.moment;
    }
}
