package edu.scau.buymesth.conversation.chat;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.v3.exception.BmobException;
import edu.scau.Constant;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by ！ on 2016/9/18.
 */
public class ChatModel implements ChatContract.Model{
    private BmobIMConversation mConversation;
    List<BmobIMMessage> mMessageList;

    public ChatModel(BmobIMConversation conversation) {
        this.mMessageList = new ArrayList<>();
        this.mConversation = BmobIMConversation.obtain(BmobIMClient.getInstance(),conversation);
    }

    @Override
    public List<BmobIMMessage> getDatas() {
        return mMessageList;
    }

    @Override
    public void setDatas(List<BmobIMMessage> list) {

    }

    @Override
    public void resetPage() {

    }

    @Override
    public Observable<List<BmobIMMessage>> getRxMessages(boolean fromStart) {
        return Observable.create(new Observable.OnSubscribe<List<BmobIMMessage>>() {
            @Override
            public void call(Subscriber<? super List<BmobIMMessage>> subscriber) {
                mConversation.queryMessages((mMessageList.size()>0&&!fromStart)?mMessageList.get(0):null,
                        Constant.NUMBER_PER_PAGE, new MessagesQueryListener() {
                    @Override
                    public void done(List<BmobIMMessage> list, BmobException e) {
                        if (e == null) {
                            subscriber.onNext(list);
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(e);
                        }
                    }
                });
            }
        });
    }

    public BmobIMConversation getConversation() {
        return mConversation;
    }

    public void setConversation(BmobIMConversation mConversation) {
        this.mConversation = mConversation;
    }
}
