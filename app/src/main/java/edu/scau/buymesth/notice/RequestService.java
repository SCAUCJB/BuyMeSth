package edu.scau.buymesth.notice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Notificate;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.User;
import rx.schedulers.Schedulers;

/**
 * Created by Jammy on 2016/9/6.
 */
public class RequestService extends Service {
    Messenger client;

    User user;
    Gson gson;
    private BriteDatabase db;
    int unreadCount = 0;
    private Order mOrder;

    Messenger messenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            client = msg.replyTo;
            switch (msg.what) {
                case Constant.START_SERVICE:
                    break;
                case Constant.GET_UNREAD:
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt("unread", unreadCount);
                    if (mOrder != null)
                        bundle.putSerializable("order", mOrder);
                    message.setData(bundle);
                    break;
                case Constant.MARK_READ:
                    unreadCount = 0;
                    break;
            }
            super.handleMessage(msg);
        }
    });

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v("RJM", "服务启动了");
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gson = new Gson();
        user = BmobUser.getCurrentUser(User.class);
        SqlBrite sqlBrite = SqlBrite.create();
        SQLiteHelper dbHelper = new SQLiteHelper(this);
        db = sqlBrite.wrapDatabaseHelper(dbHelper, Schedulers.io());
        //start service
        BmobRealTimeData rtd = new BmobRealTimeData();
        try {
            Bmob.getApplicationContext();
        } catch (RuntimeException e) {
            Bmob.initialize(getApplicationContext(), Constant.BMOB_APP_ID);
        }
        rtd.start(new ValueEventListener() {
            @Override
            public void onConnectCompleted(Exception e) {
                Log.d("bmob", "连接成功:" + rtd.isConnected());
                if (rtd.isConnected()) {
                    rtd.subTableUpdate("Order");
                }
            }

            @Override
            public void onDataChange(JSONObject data) {
                /////action是delete的时候   直接跳出
                Log.d("bmob", "(" + data.optString("action") + ")" + "数据：" + data);
                ////得到数据，判断是否是自己的，如果是则更新Rv
                JSONObject jsonObject = null;
                String objectId = null;
                try {
                    Log.v("data是：", data.getString("data"));
                    jsonObject = new JSONObject(data.getString("data"));
                    objectId = jsonObject.getString("objectId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String buyerId = null;
                String sellerId = null;

                try {
                    buyerId = jsonObject.getJSONObject("buyer").getString("objectId");
                    sellerId = jsonObject.getJSONObject("seller").getString("objectId");
                } catch (JSONException e1) {
//                                e1.printStackTrace();
                    try {
                        buyerId = jsonObject.getString("buyer");
                        sellerId = jsonObject.getString("seller");
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                        Log.v("DATA_CHANGE JSON解析错误", "err");
                    }
                }

                if (buyerId.equals(user.getObjectId()) || sellerId.equals(user.getObjectId())) {
                    BmobQuery<Order> bmobQuery = new BmobQuery<>();
                    bmobQuery.include("buyer,request,seller");
                    bmobQuery.addWhereEqualTo("objectId", objectId);
                    bmobQuery.findObjects(new FindListener<Order>() {
                        @Override
                        public void done(List<Order> list, BmobException e) {
                            Order order = list.get(0);
                            if (order != null) {
                                ContentValues values = new ContentValues();
                                String orderJson = gson.toJson(order);
                                values.put("orderJson", orderJson);
                                values.put("objectId", order.getObjectId());
                                values.put("status", order.getStatus());
                                values.put("updateTime", order.getUpdatedAt());
                                db.insert(SQLiteHelper.DATABASE_TABLE, values);

                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("order", order);
                                mOrder = order;
                                message.setData(bundle);
                                unreadCount++;
                                try {
                                    if (client != null)
                                        client.send(message);
                                    Notification.Builder builder = new Notification.Builder(RequestService.this);
                                    Intent intent = new Intent(RequestService.this, OrderDetailActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("order", order);
                                    ////TODO:这里加一个随机数的生成
                                    int time = new Random().nextInt(65535);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(RequestService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    builder.setSmallIcon(R.mipmap.ic_launcher);
                                    builder.setContentIntent(pendingIntent);
                                    builder.setAutoCancel(true);
                                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                                    builder.setContentTitle("你的订单有变化了");
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    notificationManager.notify(time, builder.build());

                                    if(( order.getBuyer().getObjectId().equals(user.getObjectId())&&order.getStatus()==Order.STATUS_ACCEPTED)
                                            ||(order.getBuyer().getObjectId().equals(user.getObjectId())&&order.getStatus()==Order.STATUS_REJECTED)
                                            ||(order.getSeller().getObjectId().equals(user.getObjectId())&&order.getStatus()==Order.STATUS_SELLER_REJECT)
                                            ||(order.getSeller().getObjectId().equals(user.getObjectId())&&order.getStatus()==Order.STATUS_DELIVERING)
                                            ||(order.getBuyer().getObjectId().equals(user.getObjectId())&&order.getStatus()==Order.STATUS_FINISH)
                                            ){

                                    }else{
                                        BmobQuery<Notificate> query1 = new BmobQuery<>();
                                        query1.addWhereEqualTo("user",user.getObjectId());
                                        BmobQuery<Notificate> query2 = new BmobQuery<>();
                                        query2.addWhereEqualTo("order",order.getObjectId());
                                        List<BmobQuery<Notificate>> queries = new ArrayList<>();
                                        queries.add(query1);
                                        queries.add(query2);
                                        BmobQuery<Notificate> query = new BmobQuery<>();
                                        query.or(queries);
                                        query.order("-updatedAt");
                                        query.findObjects(new FindListener<Notificate>() {
                                            @Override
                                            public void done(List<Notificate> list, BmobException e) {
                                                if(list.size()>0){
                                                    Notificate notificate =new Notificate();
                                                    notificate.setObjectId(list.get(0).getObjectId());
                                                    notificate.delete(new UpdateListener() {
                                                        @Override
                                                        public void done(BmobException e) {
                                                            if(e==null){
                                                                Log.i("bmob","成功");
                                                            }else{
                                                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } catch (RemoteException e1) {
                                    Log.v("出错啦！！！", "出错啦！！！！！");
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}