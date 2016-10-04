package edu.scau.buymesth.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import base.RxManager;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.BmobWrapper;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.ValueEventListener;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.Request;
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

    Messenger messenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            client = msg.replyTo;
            switch (msg.what) {
                case Constant.START_SERVICE:
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
                            }catch (JSONException e) {
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
                                    Log.v("DATA_CHANGE JSON解析错误","err");
                                }
                            }

                            /////这里要判断自己是买家还是卖家，存不同的数据库,还是更新....
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
                                                db.insert(SQLiteHelper.DATABASE_TABLE, values);

                                                Message message = new Message();
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("order",order);
                                                message.setData(bundle);
                                                try {
                                                    client.send(message);
                                                    Notification.Builder builder = new Notification.Builder(RequestService.this);
                                                    Intent intent = new Intent(Intent.ACTION_VIEW,null);
                                                    PendingIntent pendingIntent = PendingIntent.getActivity(RequestService.this,0,intent,0);
                                                    builder.setSmallIcon(R.mipmap.ic_launcher);
                                                    builder.setContentIntent(pendingIntent);
                                                    builder.setAutoCancel(true);
                                                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
                                                    builder.setContentTitle("你的订单有变化了");
//                                                    builder.setContentText()
                                                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                    notificationManager.notify(1,builder.build());
                                                } catch (RemoteException e1) {
                                                    Log.v("出错啦！！！", "出错啦！！！！！");
                                                }
                                            }
                                        }
                                    });
                            }
                        }
                    });
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
    }
}