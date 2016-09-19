package edu.scau.buymesth.chat;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import base.RxManager;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.BmobWrapper;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.ValueEventListener;
import edu.scau.Constant;
import edu.scau.buymesth.data.bean.Order;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by Jammy on 2016/9/6.
 */
public class RequestService extends Service {
    private Message mMessage;
    Messenger messenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mMessage = msg;
//            mMessage=new Message();
//            mMessage.copyFrom(msg);

            switch (msg.what) {
                case Constant.START_SERVICE:
                    BmobRealTimeData rtd = new BmobRealTimeData();
                    try {
                        Bmob.getApplicationContext();
                    } catch (RuntimeException e) {
                        Bmob.initialize(getApplicationContext(), Constant.BMOB_APP_ID);
                    }
//                    Log.v("replyTo", String.valueOf(msg.replyTo==null)+"1");
                    rtd.start(new ValueEventListener() {
                        @Override
                        public void onConnectCompleted(Exception e) {
                            Log.d("bmob", "连接成功:" + rtd.isConnected());
                            if (rtd.isConnected()) {
                                rtd.subTableUpdate("Order");
                            }
                            Log.v("replyto:", String.valueOf(mMessage.replyTo==null));
                        }

                        @Override
                        public void onDataChange(JSONObject data) {
                            Log.d("bmob", "(" + data.optString("action") + ")" + "数据：" + data);
                            //User user = BmobUser.getCurrentUser(User.class);
                            ////得到数据，判断是否是自己的，如果是则更新Rv
                            try {
                                Log.v("data是：", data.getString("data"));
                                JSONObject jsonObject = new JSONObject(data.getString("data"));
                                BmobQuery<Order> bmobQuery = new BmobQuery<Order>();
                                bmobQuery.getObject((String) jsonObject.get("objectId"), new QueryListener<Order>() {
                                    @Override
                                    public void done(Order order, BmobException e) {
                                        if (order != null) {

                                            Message message = new Message();
                                            message.obj = order;
                                            try {
                                                Log.v("send", "send");
                                                Log.v("replyTo", String.valueOf(mMessage.replyTo == null) + "123");
                                                mMessage.replyTo.send(message);
                                            } catch (RemoteException e1) {
                                                e1.printStackTrace();
                                                Log.v("出错啦！！！", "出错啦！！！！！");
                                            }

                                        }
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
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
    }
}