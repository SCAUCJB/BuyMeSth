package edu.scau.buymesth.conversation.list;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import base.BasePresenter;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by ！ on 2016/9/18.
 */
public class ConversationPresenter extends BasePresenter<ConversationContract.Model,ConversationContract.View> {

    Context mContext;
    AtomicBoolean mUpdateStart = new AtomicBoolean(false);

    public ConversationPresenter(Context context){
        this.mContext = context;
    }

    @Override
    public void onStart() {

    }

    public void lightRefresh(){
        mModel.getConversations();
        mView.onRefreshComplete(null);
    }

    public void refresh(){
        mUpdateStart.set(false);
        User user = BmobUser.getCurrentUser(User.class);
        mView.onServerConnectStatusChanges(ConversationContract.View.SERVER_CONNECT_ING,null);
        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if(e==null){
                    if(mUpdateStart.getAndSet(true))return;
                    mModel.getConversations();
                    mView.onServerConnectStatusChanges(ConversationContract.View.SERVER_CONNECT_SUCCEED,null);
                    if(mModel.getDatas()==null){
                        mView.onServerConnectStatusChanges(ConversationContract.View.SERVER_CONNECT_FAILED,"unknown error");
                        return;
                    } else if(mModel.getDatas().size()==0){
                        mView.onUpdatingUserInfo(ConversationContract.View.USER_INFO_UPDATE_COMPLETED,null);
                        return;
                    }else
                        updateUserInfo();
                }else {
                    mView.onServerConnectStatusChanges(ConversationContract.View.SERVER_CONNECT_FAILED,e.toString());
                }
            }
        });
        new Thread(() -> {
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e1) { }
            if(mModel.getConversations()!=null) {
                if(mUpdateStart.getAndSet(true))return;
                mView.onServerConnectStatusChanges(ConversationContract.View.SERVER_CONNECT_SUCCEED, "from !!!!!!!!!!!");
                if (mModel.getDatas() == null) {
                    mView.onServerConnectStatusChanges(ConversationContract.View.SERVER_CONNECT_FAILED, "from !!!!!!!!!!!");
                    return;
                }
                else if (mModel.getDatas().size() == 0) {
                    mView.onUpdatingUserInfo(ConversationContract.View.USER_INFO_UPDATE_COMPLETED, "from !!!!!!!!!!!");
                    return;
                }
                else
                    updateUserInfo();
            }else {
                mView.onServerConnectStatusChanges(ConversationContract.View.SERVER_CONNECT_FAILED,"from !!!!!!!!!!!");
            }
        }).start();
    }

    public void updateUserInfo(){
        List<String> userIds = new ArrayList<>();
        for(BmobIMConversation conversation : mModel.getDatas())
            userIds.add(conversation.getConversationId());
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereContainedIn("objectId",userIds);
        query.setLimit(userIds.size());
        mView.onUpdatingUserInfo(ConversationContract.View.USER_INFO_UPDATEING,null);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e!=null){
                    mView.onUpdatingUserInfo(ConversationContract.View.USER_INFO_UPDATE_FAILED,e.toString());
                    return;
                }
                for(BmobIMConversation conversation : mModel.getDatas()){
                    for(User user : list){
                        if(conversation.getConversationId().equals(user.getObjectId())){
                            BmobIMUserInfo info = new BmobIMUserInfo(conversation.getId(),user.getObjectId(),user.getNickname(),user.getAvatar());
                            //调用updateUserInfo方法更新用户资料
                            BmobIM.getInstance().updateUserInfo(info);
                            conversation.setConversationIcon(user.getAvatar());
                            conversation.setConversationTitle(user.getNickname());
                            conversation.update();
                        }
                    }
                }
                mView.onUpdatingUserInfo(ConversationContract.View.USER_INFO_UPDATE_COMPLETED,null);
            }
        });
    }
}
