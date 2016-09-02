package edu.scau.buymesth.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by Jammy on 2016/9/1.
 */
public class ChatFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initConnection();
        return inflater.inflate(R.layout.fragment_chat, null);
    }

    ///连接服务器
    private void initConnection() {
        User user = BmobUser.getCurrentUser(User.class);
        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    Log.i("rjm","connect success");
                } else {
                    Log.e("rjm",e.getErrorCode() + "/" + e.getMessage());
                }
            }
        });

        /////当状态变化，重新连接
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus connectionStatus) {

            }
        });

    }
}
