package edu.scau.buymesth.conversation.list;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import edu.scau.Constant;
import edu.scau.buymesth.data.bean.Follow;
import edu.scau.buymesth.data.bean.User;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by ！ on 2016/9/18.
 */
public class ConversationModel implements ConversationContract.Model{
    List<String> mUserIds;
    List<User> mUsers;
    List<BmobIMConversation> mConversations;
    private int loadedPageNum;

    public ConversationModel() {
        this.mUserIds = new ArrayList<>();
        this.mUsers = new ArrayList<>();
        this.mConversations = new ArrayList<>();
        this.loadedPageNum = 0;
    }

    @Override
    public List<BmobIMConversation> getDatas() {
        return mConversations;
    }

    @Override
    public void setDatas(List<String> list) {

    }

    @Override
    public void resetPage() {

    }

    public Observable<List<Follow>> getFollowRx(){
        BmobQuery<Follow> query=new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存两天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.addWhereEqualTo("fromUser", BmobUser.getCurrentUser().getObjectId());
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (loadedPageNum++));
        return query.findObjectsObservable(Follow.class);
    }

    @Override
    public Observable<List<String>> getAllUserIdRx(){
        mUserIds.clear();
        List<BmobIMConversation> conversations = BmobIM.getInstance().loadAllConversation();
        for(BmobIMConversation conversation:conversations){
            mUserIds.add(String.valueOf(conversation.getId()));
        }
        return Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                BmobQuery<Follow> query=new BmobQuery<>();
                query.setCachePolicy(BmobQuery.CachePolicy.CACHE_THEN_NETWORK);
                query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存两天，可以用来优化下拉刷新而清空了的加载更多
                query.order("-createdAt");
                query.addWhereEqualTo("fromUser", BmobUser.getCurrentUser().getObjectId());
                query.setLimit(Constant.NUMBER_PER_PAGE);
                query.setSkip(Constant.NUMBER_PER_PAGE * (loadedPageNum++));
                query.findObjects(new FindListener<Follow>() {
                    @Override
                    public void done(List<Follow> list, BmobException e) {
                        if(list!=null&&list.size()>0){
                            for(Follow follow:list){
                                boolean add = true;
                                for(String id:mUserIds){
                                    if (follow.getToUser().getObjectId().equals(id))add = false;
                                }
                                if(add)
                                    mUserIds.add(follow.getToUser().getObjectId());
                            }
                        }
                        subscriber.onNext(mUserIds);
                        subscriber.onCompleted();
                    }
                });
//                getFollowRx().flatMap(follows -> Observable.from(follows))
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Observer<Follow>() {
//                            @Override
//                            public void onCompleted() {
//                                subscriber.onNext(mUserIds);
//                                subscriber.onCompleted();
//                            }
//
//                            @Override
//                            public void onError(Throwable throwable) {
//                                subscriber.onError(throwable);
//                            }
//
//                            @Override
//                            public void onNext(Follow follow) {
//                                for(String id:mUserIds){
//                                    if (follow.getToUser().getObjectId().equals(id))return;
//                                }
//                                mUserIds.add(follow.getToUser().getObjectId());
//                            }
//                        });
            }
        });
    }

    @Override
    public Observable<List<User>> getUsersRx(){
        BmobQuery<User> query=new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存两天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.addWhereContainedIn("objectId",mUserIds);
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (loadedPageNum++));
        return query.findObjectsObservable(User.class);
    }

    @Override
    public List<BmobIMConversation> getConversations() {
        List<BmobIMConversation> conversations = BmobIM.getInstance().loadAllConversation();
        if(conversations==null){
            return null;
        }else if(conversations.size()>0){
            mConversations.clear();
            mConversations.addAll(conversations);
        }
        return mConversations;
    }
}
