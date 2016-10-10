package edu.scau.buymesth.conversation.list;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import base.util.SpaceItemDecoration;
import base.util.ToastUtil;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.conversation.chat.ChatFragment;
import edu.scau.buymesth.conversation.userlist.UserListFragment;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.fragment.EmptyActivity;
import edu.scau.buymesth.notice.NoticeFragment;
import edu.scau.buymesth.util.DateFormatHelper;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

/**
 * Created by ！ on 2016/9/18.
 */
public class ConversationFragment extends Fragment implements ConversationContract.View ,MessageListHandler {
    private RecyclerView mRecyclerView;
    private ConversationAdapter mConversationAdapter;
    private ConversationPresenter mPresenter;
    private PtrFrameLayout mPtrFrameLayout;

    private View mLoadingView;
    private View mHeaderView;
    private View mHeaderView2;
    private View mEmptyView;
    //requestService
    private ServiceConnection serviceConnection;
    private Messenger mMessenger;
    private Messenger mService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation,container,false);
        mRecyclerView= (RecyclerView) view.findViewById(R.id.rv_discover_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(2));
        //初始化代理人
        mPresenter=new ConversationPresenter(getActivity().getBaseContext());
        mPresenter.setVM(this,new ConversationModel());
        mHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.item_conversation,(ViewGroup) mRecyclerView.getParent(), false);
        mHeaderView2 = LayoutInflater.from(getContext()).inflate(R.layout.item_conversation,(ViewGroup) mRecyclerView.getParent(), false);
        mEmptyView = LayoutInflater.from(getContext()).inflate(R.layout.empty_view_add_conversation,(ViewGroup) mRecyclerView.getParent(), false);
        mEmptyView.setOnClickListener(v -> EmptyActivity.navigate(getActivity(), UserListFragment.class.getName(), null, 101));
        mHeaderView2.setOnClickListener(v -> EmptyActivity.navigate(getActivity(),UserListFragment.class.getName(),null,"我的关注"));
        mHeaderView.setOnClickListener(v -> {
            mPresenter.lightRefresh();
            ///连接成功后发送信息让他进行服务器连接判断是否有新的信息,service用于判断类型
            Message msg = Message.obtain(null, Constant.MARK_READ);
            msg.replyTo = mMessenger;/////设置回调
            try {
                if(mService!=null) mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            ((TextView)mHeaderView.findViewById(R.id.unread_mark)).setVisibility(View.INVISIBLE);
            EmptyActivity.navigate(getActivity(), NoticeFragment.class.getName(),null);
        });
        ((TextView)mHeaderView.findViewById(R.id.tv_name)).setText("系统消息");
        ((TextView)mHeaderView.findViewById(R.id.tv_new_msg)).setText(" ");
        ((TextView)mHeaderView.findViewById(R.id.tv_date)).setText(" ");
        ((ImageView)mHeaderView.findViewById(R.id.iv_avatar)).setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));

        ((TextView)mHeaderView2.findViewById(R.id.tv_name)).setText("添加对话");
        ((TextView)mHeaderView2.findViewById(R.id.tv_new_msg)).setText("来开始一个新的聊天吧");
        ((TextView)mHeaderView2.findViewById(R.id.tv_date)).setText(" ");
        ((ImageView)mHeaderView2.findViewById(R.id.iv_avatar)).setImageDrawable(getResources().getDrawable(R.drawable.ic_person_green));
        initAdapter();
        initStoreHouse(view);
        mLoadingView = view.findViewById(R.id.ly_loading_tips);
        bindRequestService();
        return view;
    }

    private void bindRequestService(){
        mMessenger = new Messenger(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Order order = (Order) msg.getData().get("order");
                int unreadCount = msg.getData().getInt("unread");
                if(order==null)return;
                ((TextView)mHeaderView.findViewById(R.id.tv_name)).setText("系统消息");
                ((TextView)mHeaderView.findViewById(R.id.tv_new_msg)).setText("订单有变化");
                ((TextView)mHeaderView.findViewById(R.id.tv_date)).setText(DateFormatHelper.dateFormat(order.getUpdatedAt()));
                ((TextView)mHeaderView.findViewById(R.id.unread_mark)).setText(unreadCount==0?" ":unreadCount+"");
                if(unreadCount>0) mHeaderView.findViewById(R.id.unread_mark).setVisibility(View.VISIBLE);
                else mHeaderView.findViewById(R.id.unread_mark).setVisibility(View.INVISIBLE);
                Log.v("数据更新了", "111");
                super.handleMessage(msg);
            }
        });

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.v("ConversationFragment", "连接成功");
                mService = new Messenger(service);
                ///连接成功后发送信息让他进行服务器连接判断是否有新的信息,service用于判断类型
                Message msg = Message.obtain(null, Constant.START_SERVICE);
                msg.replyTo = mMessenger;/////设置回调
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.v("NoticeFragment", "断开连接");
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.RESPOND_VIA_MESSAGE");
        intent.setPackage(getActivity().getPackageName());
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initAdapter(){
        mConversationAdapter = new ConversationAdapter(getActivity(),mPresenter.mModel.getDatas());
        mConversationAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
            if(mPtrFrameLayout.isRefreshing())return;
            Bundle arg = new Bundle();
            arg.putSerializable(BmobIMConversation.class.getName(),mConversationAdapter.getItem(position));
            EmptyActivity.navigate(getActivity(), ChatFragment.class.getName(),arg,"关注列表");
        });
        mConversationAdapter.setEmptyView(true,mEmptyView);
        mConversationAdapter.setOnRecyclerViewItemLongClickListener((view, position) -> {
            SpannableStringBuilder redStr=new SpannableStringBuilder("删除");
            redStr.setSpan(new ForegroundColorSpan(Color.RED), 0, redStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
            new AlertDialog.Builder(getActivity()).setTitle("删除会话？")
                    .setPositiveButton(redStr, (dialog, which) -> {
                        BmobIMConversation tempItem = mConversationAdapter.getItem(position);
                        mConversationAdapter.remove(position);
                        tempItem.delete();
                    })
                    .setNegativeButton("取消",null)
                    .show();
            return false;
        });
        mConversationAdapter.addHeaderView(mHeaderView);
        mConversationAdapter.addHeaderView(mHeaderView2);
//        mConversationAdapter.openLoadAnimation();
        mRecyclerView.setAdapter(mConversationAdapter);
//        mConversationAdapter.setOnLoadMoreListener(() -> mPresenter.loadMore());
//        mConversationAdapter.openLoadMore(Constant.NUMBER_PER_PAGE, true);
    }

    private void initStoreHouse(View view) {
        mPtrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.store_house_ptr_frame);
        final StoreHouseHeader header = new StoreHouseHeader(getActivity());
        header.setPadding(0, 80, 0,50);
        header.initWithString("Buy Me Sth");
        header.setTextColor(Color.BLACK);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.post(() -> mPresenter.refresh());
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return  !mRecyclerView.canScrollVertically(-1);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                mPresenter.refresh();
                mLoadingView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onLoadMoreSuccess(List<BmobIMConversation> list) {

    }

    @Override
    public void onError(Throwable throwable, String msg) {
//        mLoadingView.setVisibility(View.VISIBLE);
//        ((TextView)mLoadingView.findViewById(R.id.tv_tips)).setText(msg+","+throwable==null?" ":throwable.toString());
    }

    @Override
    public void onRefreshComplete(List<BmobIMConversation> list) {
        getActivity().runOnUiThread(() -> {
//            mLoadingView.setVisibility(View.INVISIBLE);
            mConversationAdapter.notifyDataSetChanged();
            mPtrFrameLayout.refreshComplete();
        });
    }

    @Override
    public void onRefreshInterrupt() {

    }

    @Override
    public void onDeleteSuccess(String msg, int position) {

    }

    @Override
    public void onServerConnectStatusChanges(int status,String msg) {
        getActivity().runOnUiThread(() -> {
//            ToastUtil.show("onServerConnectStatusChanges "+status+" "+msg);
            switch (status){
                case 0:
                    mLoadingView.setVisibility(View.VISIBLE);
                    ((TextView)mLoadingView.findViewById(R.id.tv_tips)).setText("连接服务器中");
                    mLoadingView.setBackgroundColor(getResources().getColor(R.color.black));
                    break;
                case 1:
                    ((TextView)mLoadingView.findViewById(R.id.tv_tips)).setText("已连接服务器");
                    mLoadingView.setBackgroundColor(getResources().getColor(R.color.green));
                    mConversationAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    mLoadingView.setVisibility(View.VISIBLE);
                    ((TextView)mLoadingView.findViewById(R.id.tv_tips)).setText("连接服务器失败");
                    mLoadingView.setBackgroundColor(getResources().getColor(R.color.red));
                    mPtrFrameLayout.refreshComplete();
                    break;
            }
        });
    }

    @Override
    public void onUpdatingUserInfo(int status,String msg) {
        getActivity().runOnUiThread(() -> {
//            ToastUtil.show("onUpdatingUserInfo "+status+" "+msg);
            switch (status){
                case 0:
                    mLoadingView.setVisibility(View.VISIBLE);
                    ((TextView)mLoadingView.findViewById(R.id.tv_tips)).setText("更新用户信息中");
                    mLoadingView.setBackgroundColor(getResources().getColor(R.color.black));
                    break;
                case 1:
                    mLoadingView.postDelayed(() -> mLoadingView.setVisibility(View.GONE),1500);
                    ((TextView)mLoadingView.findViewById(R.id.tv_tips)).setText("已更新用户信息");
                    mLoadingView.setBackgroundColor(getResources().getColor(R.color.green));
                    mConversationAdapter.notifyDataSetChanged();
                    mPtrFrameLayout.refreshComplete();
                    break;
                case 2:
                    mLoadingView.setVisibility(View.VISIBLE);
                    mLoadingView.postDelayed(() -> mLoadingView.setVisibility(View.GONE),1500);
                    ((TextView)mLoadingView.findViewById(R.id.tv_tips)).setText("更新用户信息失败");
                    mLoadingView.setBackgroundColor(getResources().getColor(R.color.red));
                    mConversationAdapter.notifyDataSetChanged();
                    mPtrFrameLayout.refreshComplete();
                    break;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        BmobIM.getInstance().addMessageListHandler(this);
        mPresenter.lightRefresh();
        ///连接成功后发送信息让他进行服务器连接判断是否有新的信息,service用于判断类型
        if(mService==null)return;
        Message msg = Message.obtain(null, Constant.GET_UNREAD);
        msg.replyTo = mMessenger;/////设置回调
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BmobIM.getInstance().removeMessageListHandler(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Message msg = Message.obtain(null, Constant.END_BIND);
        msg.replyTo = null;/////设置回调
        try {
            if(mService!=null) mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        boolean refresh = true;
        for(MessageEvent event:list){
//            if(event.getMessage().getId()==-1){
//                ((TextView)mHeaderView.findViewById(R.id.tv_name)).setText("系统消息");
//                ((TextView)mHeaderView.findViewById(R.id.tv_new_msg)).setText(event.getMessage().getContent());
//                ((TextView)mHeaderView.findViewById(R.id.tv_date)).setText(" ");
//                ((TextView)mHeaderView.findViewById(R.id.unread_mark)).setText(" ");
//                ((TextView)mHeaderView.findViewById(R.id.unread_mark)).setVisibility(View.VISIBLE);
//            }
            for(BmobIMConversation cc:mPresenter.mModel.getDatas()){
                if(event.getConversation().getConversationId().equals(cc.getConversationId())){
                    refresh = false;
                    break;
                }
            }
        }
        if(refresh) mPresenter.refresh();
        else {
            mPresenter.lightRefresh();
        }
    }
}
