package edu.scau.buymesth.chat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import base.util.SpaceItemDecoration;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.ChatAdapter;
import edu.scau.buymesth.data.bean.Order;

/**
 * Created by Jammy on 2016/9/1.
 */
public class ChatFragment extends Fragment {
    ChatAdapter adapter;
    RecyclerView rv;
    List<Order> list = new ArrayList<>();
    Messenger mService;
    Messenger mMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            /////处理service发回的信息
            Log.v("返回结果", msg.toString());
            Order order = (Order) msg.obj;
            list.add(order);
            adapter.notifyDataSetChanged();
            super.handleMessage(msg);
        }
    });


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v("ChatFragment", "连接成功");
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
            Log.v("ChatFragment", "断开连接");
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.RESPOND_VIA_MESSAGE");
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();
        View view = inflater.inflate(R.layout.fragment_chat, null);
        rv = (RecyclerView) view.findViewById(R.id.chat_rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_6)));
        rv.setAdapter(adapter);
        return view;
    }

    private void init() {
        adapter = new ChatAdapter(R.layout.chat_order, list);
        adapter.setActivity(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(serviceConnection);
    }
}
