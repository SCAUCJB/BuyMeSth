package edu.scau.buymesth.data.bean;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;

/**
 * Created by ！ on 2016/9/27.
 */

public class Conversation extends BmobIMConversation{

    private BmobIMConversation conversation;
    BmobIMMessage lastMsg = null;

    public Conversation(BmobIMConversation bmobIMConversation) {
        super(bmobIMConversation.getId(),
                bmobIMConversation.getConversationId(),
                bmobIMConversation.getConversationType(),
                bmobIMConversation.getConversationTitle(),
                bmobIMConversation.getConversationIcon(),
                bmobIMConversation.getUnreadCount(),
                bmobIMConversation.getUpdateTime(),
                bmobIMConversation.getIsTop(),
                bmobIMConversation.getDraft());
        conversation = bmobIMConversation;
        List<BmobIMMessage> msgs =conversation.getMessages();
        if(msgs!=null && msgs.size()>0){
            lastMsg =msgs.get(0);
        }
    }

    public void readAllMessages() {
        conversation.updateLocalCache();
    }

    public Object getAvatar() {
        return conversation.getConversationIcon();
    }

    public String getLastMessageTime() {
        if(lastMsg!=null) {
            return String.valueOf(lastMsg.getCreateTime());
        }else{
            return null;
        }
    }

    public int getUnreadCount(){
        return (int) BmobIM.getInstance().getUnReadCount(conversation.getConversationId());
    }

    public String getLastMessageContent() {
        List<BmobIMMessage> msgs =conversation.getMessages();
        if(msgs!=null && msgs.size()>0){
            lastMsg =msgs.get(0);
        }
        if(lastMsg!=null){
            String content =lastMsg.getContent();
            if(lastMsg.getMsgType().equals(BmobIMMessageType.TEXT.getType())){
                return content;
            }else if(lastMsg.getMsgType().equals(BmobIMMessageType.IMAGE.getType())){
                return "[图片]";
            }else if(lastMsg.getMsgType().equals(BmobIMMessageType.VOICE.getType())){
                return "[语音]";
            }else if(lastMsg.getMsgType().equals(BmobIMMessageType.LOCATION.getType())){
                return"[位置]";
            }else if(lastMsg.getMsgType().equals(BmobIMMessageType.VIDEO.getType())){
                return "[视频]";
            }else{//开发者自定义的消息类型，需要自行处理
                return "[未知]";
            }
        }else{//防止消息错乱
            return "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        if (!((Conversation) o).getConversationId().equals(that.getConversationId())) return false;
        return getConversationType() == that.getConversationType();
    }

}
