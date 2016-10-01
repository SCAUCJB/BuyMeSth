package edu.scau.buymesth.chat;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Build;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.QueryObservable;
import com.squareup.sqlbrite.SqlBrite;

import java.util.LinkedList;
import java.util.List;

import base.util.SpaceItemDecoration;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.chat.detail.BuyOrderDetailActivity;
import edu.scau.buymesth.chat.detail.BuyerAcceptActivity;
import edu.scau.buymesth.chat.detail.SellerAcceptActivity;
import edu.scau.buymesth.chat.detail.SellerOrderDetailActivity;
import edu.scau.buymesth.data.bean.Order;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by Jammy on 2016/9/1.
 */
public class ChatFragment extends Fragment {
    private BriteDatabase db;
    Gson gson;

    ChatAdapter adapter;
    RecyclerView rv;
    List<Order> list = new LinkedList<>();
    Messenger mService;
    Messenger mMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            list.add((Order) msg.getData().get("order"));
            adapter.notifyDataSetChanged();
            Log.v("数据更新了", "111");
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

    @TargetApi(Build.VERSION_CODES.DONUT)
    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.RESPOND_VIA_MESSAGE");
        intent.setPackage(getActivity().getPackageName());
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR_MR1)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adapter = new ChatAdapter(list);
        gson = new Gson();
        SqlBrite sqlBrite = SqlBrite.create();
        SQLiteHelper dbHelper = new SQLiteHelper(getContext());
        db = sqlBrite.wrapDatabaseHelper(dbHelper, Schedulers.io());
        View view = inflater.inflate(R.layout.fragment_chat, null);
        rv = (RecyclerView) view.findViewById(R.id.chat_rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_6)));
        adapter.setOnRecyclerViewItemLongClickListener((view1, position) -> {
            Order order = (Order) adapter.getItem(position);
            new AlertDialog.Builder(getContext()).setTitle("是否删除").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ////TODO:删除要更改，改成根据primary来删除
                    db.delete(SQLiteHelper.DATABASE_TABLE, "objectId=?", order.getObjectId());
                    adapter.remove(position);
                }
            }).create().show();

            return true;
        });
        adapter.setOnRecyclerViewItemClickListener((view1, position) -> {
            Order order = (Order) adapter.getItem(position);
            Intent intent;
            Bundle bundle;
            switch(order.getItemType()){
                case Constant.BUYER_STATUS_CREATE:
                    Toast.makeText(getContext(),"买家创建",Toast.LENGTH_LONG).show();
                    intent =new Intent(getActivity(),BuyOrderDetailActivity.class);
                    bundle = new Bundle();
                    bundle.putSerializable("order",order);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;

                case Constant.SELLER_STATUS_CREATE:
                    Toast.makeText(getContext(),"卖家创建",Toast.LENGTH_LONG).show();
                    intent =new Intent(getActivity(), SellerOrderDetailActivity.class);
                    bundle = new Bundle();
                    bundle.putSerializable("order",order);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case Constant.BUYER_STATUS_ACCEPT:
                    intent =new Intent(getActivity(), BuyerAcceptActivity.class);
                    bundle = new Bundle();
                    bundle.putSerializable("order",order);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;

                case Constant.SELLER_STATUS_ACCEPT:
                    intent =new Intent(getActivity(), SellerAcceptActivity.class);
                    bundle = new Bundle();
                    bundle.putSerializable("order",order);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
            }
        });
        rv.setAdapter(adapter);
        initFromDataBase();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(serviceConnection);
    }

    Subscription subscription;

    public void initFromDataBase() {
        list = new LinkedList<>();
        QueryObservable query = db.createQuery(SQLiteHelper.DATABASE_TABLE, "select * from " + SQLiteHelper.DATABASE_TABLE);
        subscription = query.subscribe(new Subscriber<SqlBrite.Query>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(SqlBrite.Query query) {
                ////TODO:使用同一个DataBase创建的话可以在插入和删除时进行监听
                Cursor cursor = query.run();
                while (cursor.moveToNext()) {
                    Order order = gson.fromJson(cursor.getString(cursor.getColumnIndex("orderJson")), Order.class);
                    list.add(order);
                }
                //这里的list用临时的列表来存储就不用取消订阅了
                cursor.close();
                adapter.setNewData(list);
                this.unsubscribe();
            }
        });

    }
}
