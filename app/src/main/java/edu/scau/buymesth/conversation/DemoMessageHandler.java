package edu.scau.buymesth.conversation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.newim.notification.BmobNotificationManager;
import edu.scau.buymesth.R;
import edu.scau.buymesth.main.TabActivity;

/**
 * Created by Jammy on 2016/9/1.
 */
public class DemoMessageHandler extends BmobIMMessageHandler {
    Context context;
    Bitmap largetIcon;
    public DemoMessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onMessageReceive(final MessageEvent event) {
//        //当接收到服务器发来的消息时，此方法被调用
        if(largetIcon==null)
            largetIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
//        if (BmobNotificationManager.getInstance(context).isShowNotification()) {
            Intent pendingIntent = new Intent(context, TabActivity.class);
            pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            BmobIMMessage message = event.getMessage();
            String content = "unknown message";
            try {
                JSONObject jsonMsg = new JSONObject(message.getContent());
                switch (jsonMsg.getInt("type")){
                    case 0 : content = jsonMsg.getString("content"); break;
                    case 1 : content = "[image]"; break;
                    case 2 : content = "[request]"; break;
                    case 3 : content = "[file]"; break;
                    default: break;
                }
            } catch (JSONException e) { }
//            BmobNotificationManager.getInstance(context).showNotification(largetIcon,
//                    event.getConversation().getConversationTitle(),
//                    content,content,pendingIntent);
            event.getMessage().setContent(content);
            BmobNotificationManager.getInstance(context).showNotification(event,pendingIntent);
//        }
    }

    @Override
    public void onOfflineReceive(final OfflineMessageEvent event) {
        if(largetIcon==null)
            largetIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        Map<String,List<MessageEvent>> map =event.getEventMap();
        int size = 0;
        //挨个检测下离线消息所属的用户的信息是否需要更新
        for (Map.Entry<String, List<MessageEvent>> entry : map.entrySet()) {
            List<MessageEvent> list =entry.getValue();
            size += list.size();
        }
        Intent pendingIntent = new Intent(context, TabActivity.class);
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        BmobNotificationManager.getInstance(context).showNotification(largetIcon,
                "离线消息",
                map.size()+"个用户给你发了"+size+"条消息",map.size()+"个用户给你发了"+size+"条消息",pendingIntent);
    }
}