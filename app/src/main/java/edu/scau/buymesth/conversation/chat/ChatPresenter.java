package edu.scau.buymesth.conversation.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import base.BasePresenter;
import base.util.ToastUtil;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ！ on 2016/9/18.
 */
public class ChatPresenter extends BasePresenter<ChatContract.Model,ChatContract.View> {

    Context mContext;

    public ChatPresenter(Context context){
        this.mContext = context;
    }

    @Override
    public void onStart() {

    }

    public void refresh(){
        mModel.getRxMessages(true).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BmobIMMessage>>() {
                    @Override
                    public void onCompleted() {
                        mView.onRefreshComplete(mModel.getDatas());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mView.onError(throwable,"load error");
                    }

                    @Override
                    public void onNext(List<BmobIMMessage> bmobIMMessages) {
                        if(bmobIMMessages.size()>0){
                            int i = 0;
                            for(;i<mModel.getDatas().size();i++){
                                if(mModel.getDatas().get(i).getId()==bmobIMMessages.get(0).getId())break;
                            }
                            bmobIMMessages = bmobIMMessages.subList(mModel.getDatas().size() - i , bmobIMMessages.size());
                        }
                        mModel.getDatas().addAll(bmobIMMessages);
                    }
                });
    }

    public void loadMore(){
        mModel.getRxMessages(false).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BmobIMMessage>>() {
                    @Override
                    public void onCompleted() {
                        mView.onLoadMoreComplete(mModel.getDatas());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mView.onError(throwable,"load error");
                    }

                    @Override
                    public void onNext(List<BmobIMMessage> bmobIMMessages) {
                        mModel.getDatas().addAll(0,bmobIMMessages);
                    }
                });
    }

    public void sendTextMessage(String text) throws JSONException {
        //make a jsonObject
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",0);
        jsonObject.put("content",text);
        jsonObject.put("size","12sp");//
        BmobIMTextMessage msg =new BmobIMTextMessage();
        msg.setContent(jsonObject.toString());
        mModel.getConversation().sendMessage(msg, new MessageSendListener() {
            int location = -1;
            @Override
            public void onStart(BmobIMMessage msg) {
                super.onStart(msg);
                msg.setSendStatus(1);
                location = mModel.getDatas().size();
                mModel.getDatas().add(msg);
                mView.onMessageSended(location,msg);
            }

            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if(e==null){
                    mModel.getDatas().get(location).setSendStatus(2);
                    mView.onMessageSendSuccess(location,msg);
                }
                else if(location!=-1){
                    mModel.getDatas().get(location).setSendStatus(3);
                    mView.onMessageSendFail(location,msg,e);
                } else {
                    //error
                }
            }
        });
    }

    public void resendMessage(BmobIMMessage msg) {
        mModel.getConversation().resendMessage(msg, new MessageSendListener() {
            int location = -1;
            @Override
            public void onStart(BmobIMMessage msg) {
                super.onStart(msg);
//                scrollToBottom();
                msg.setSendStatus(1);
                location = mModel.getDatas().size()-1;
                mView.onMessageSended(location,msg);
//                adapter.notifyDataSetChanged();
            }

            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if(e==null){
                    mModel.getDatas().get(location).setSendStatus(2);
                    mView.onMessageSendSuccess(location,msg);
                }
                else if(location!=-1){
                    mModel.getDatas().get(location).setSendStatus(3);
                    mView.onMessageSendFail(location,msg,e);
                } else {
                    //network error
                }
            }
        });
    }

    public void sendImageMessage(String imageUrl) throws JSONException {
        //decode image and get size
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(imageUrl, options);
        //make a json object
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",1);
        jsonObject.put("content",imageUrl);
        jsonObject.put("width",options.outWidth);
        jsonObject.put("height",options.outHeight);
        Log.i("image size: ",options.outWidth +"  "+ options.outHeight);
        //upload file
        BmobFile bmobFile = new BmobFile(new File(imageUrl));
        bmobFile.uploadblock(new UploadFileListener() {
            BmobIMTextMessage msg;
            int location = -1;
            @Override
            public void onStart() {
                //make the message
                msg =new BmobIMTextMessage();
                msg.setSendStatus(1);
                msg.setContent(jsonObject.toString());
                msg.setFromId(BmobUser.getCurrentUser().getObjectId());
                msg.setToId(mModel.getConversation().getConversationId());
                //tell the view
                location = mModel.getDatas().size();
                mModel.getDatas().add(msg);
                mView.onMessageSended(location,msg);
            }

            @Override
            public void done(BmobException e) {
                if(e==null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    jsonObject.remove("content");
                    try {
                        jsonObject.put("content",bmobFile.getFileUrl());
                    } catch (JSONException e1) { }
                    msg.setContent(jsonObject.toString());
                    mModel.getConversation().sendMessage(msg, new MessageSendListener() {
                        @Override
                        public void done(BmobIMMessage msg, BmobException e) {
                            if(e==null){
                                mModel.getDatas().get(location).setSendStatus(2);
                                mView.onMessageSendSuccess(location,msg);
                            }
                            else if(location!=-1){
                                mModel.getDatas().get(location).setSendStatus(3);
                                mView.onMessageSendFail(location,msg,e);
                            } else {
                                //error
                            }
                        }
                    });
                }else{
                    ToastUtil.show("上传文件失败：" + e.getMessage());
                }
            }
        });
    }

    public void sendFileMessage(String fileUrl) throws JSONException {
        //decode image and get size
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //make a json object
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",3);
        jsonObject.put("content",fileUrl);
        jsonObject.put("filesize",new File(fileUrl).length());
        jsonObject.put("filename",new File(fileUrl).getName());
        //upload file
        BmobFile bmobFile = new BmobFile(new File(fileUrl));
        bmobFile.uploadblock(new UploadFileListener() {
            BmobIMTextMessage msg;
            int location = -1;
            @Override
            public void onStart() {
                //make the message
                msg =new BmobIMTextMessage();
                msg.setSendStatus(1);
                msg.setContent(jsonObject.toString());
                msg.setFromId(BmobUser.getCurrentUser().getObjectId());
                msg.setToId(mModel.getConversation().getConversationId());
                //tell the view
                location = mModel.getDatas().size();
                mModel.getDatas().add(msg);
                mView.onMessageSended(location,msg);
            }

            @Override
            public void done(BmobException e) {
                if(e==null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    jsonObject.remove("content");
                    try {
                        jsonObject.put("content",bmobFile.getFileUrl());
                    } catch (JSONException e1) { }
                    msg.setContent(jsonObject.toString());
                    mModel.getConversation().sendMessage(msg, new MessageSendListener() {
                        @Override
                        public void done(BmobIMMessage msg, BmobException e) {
                            if(e==null){
                                mModel.getDatas().get(location).setSendStatus(2);
                                mView.onMessageSendSuccess(location,msg);
                            }
                            else if(location!=-1){
                                mModel.getDatas().get(location).setSendStatus(3);
                                mView.onMessageSendFail(location,msg,e);
                            } else {
                                //error
                            }
                        }
                    });
                }else{
                    ToastUtil.show("上传文件失败：" + e.getMessage());
                }
            }
        });
    }

    public void sendRequestMessage(String id) throws JSONException {
        //make a jsonObject
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",2);
        jsonObject.put("content",id);
        BmobIMTextMessage msg =new BmobIMTextMessage();
        msg.setContent(jsonObject.toString());
        mModel.getConversation().sendMessage(msg, new MessageSendListener() {
            int location = -1;
            @Override
            public void onStart(BmobIMMessage msg) {
                super.onStart(msg);
                msg.setSendStatus(1);
                location = mModel.getDatas().size();
                mModel.getDatas().add(msg);
                mView.onMessageSended(location,msg);
            }

            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if(e==null){
                    mModel.getDatas().get(location).setSendStatus(2);
                    mView.onMessageSendSuccess(location,msg);
                }
                else if(location!=-1){
                    mModel.getDatas().get(location).setSendStatus(3);
                    mView.onMessageSendFail(location,msg,e);
                } else {
                    //error
                }
            }
        });
    }
}
