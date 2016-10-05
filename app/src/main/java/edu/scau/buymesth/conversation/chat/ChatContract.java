package edu.scau.buymesth.conversation.chat;

import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.v3.exception.BmobException;
import rx.Observable;

/**
 * Created by ！ on 2016/9/18.
 */
public class ChatContract {
    interface  View{
        void onLoadMoreSuccess(List<BmobIMMessage> list);
        void onError(Throwable throwable, String msg);

        void onRefreshComplete(List<BmobIMMessage> list);
        void onRefreshInterrupt();

        void onLoadMoreComplete(List<BmobIMMessage> list);
        void onLoadMoreInterrupt();

        void onDeleteSuccess(String msg, int position);
        void onMessageSended(int location, BmobIMMessage msg);
        void onMessageSendSuccess(int location, BmobIMMessage msg);
        void onMessageSendFail(int location, BmobIMMessage msg, BmobException e);
    }

    interface Model{
        List<BmobIMMessage> getDatas();
        void setDatas(List<BmobIMMessage> list);
        void resetPage();
        BmobIMConversation getConversation();
        void setConversation(BmobIMConversation mConversation);
        Observable<List<BmobIMMessage>> getRxMessages(boolean fromStart);
    }
}
