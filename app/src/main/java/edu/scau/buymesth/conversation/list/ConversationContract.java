package edu.scau.buymesth.conversation.list;

import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import edu.scau.buymesth.data.bean.User;
import rx.Observable;

/**
 * Created by ！ on 2016/9/18.
 */
public class ConversationContract {
    interface  View{
        int SERVER_CONNECT_SUCCEED = 1;
        int SERVER_CONNECT_ING = 0;
        int SERVER_CONNECT_FAILED = 2;

        int USER_INFO_UPDATE_COMPLETED = 1;
        int USER_INFO_UPDATEING = 0;
        int USER_INFO_UPDATE_FAILED = 2;

        void onLoadMoreSuccess(List<BmobIMConversation> list);
        void onError(Throwable throwable, String msg);
        void onRefreshComplete(List<BmobIMConversation> list);
        void onRefreshInterrupt();
        void onDeleteSuccess(String msg, int position);

        void onServerConnectStatusChanges(int status, String msg);
        void onUpdatingUserInfo(int status, String msg);
    }

    interface Model{
        List<BmobIMConversation> getDatas();
        void setDatas(List<String> list);
        void resetPage();
        Observable<List<String>> getAllUserIdRx();
        Observable<List<User>> getUsersRx();
        List<BmobIMConversation> getConversations();
    }
}
