package edu.scau.buymesth.property;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import base.util.GlideCircleTransform;
import base.util.ToastUtil;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.data.bean.Wallet;
import edu.scau.buymesth.fragment.EmptyActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ！ on 2016/10/6.
 */

public class MyWalletFragment extends Fragment{

    private User mUser;
    private boolean mQueriedUserData;
    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_my_wallet,container,false);
        mQueriedUserData = false;
        initUserData();
        mRootView.findViewById(R.id.iv_refresh).setOnClickListener(v -> refresh(v));
        return mRootView;
    }

    private void refresh(View view){
        queryUserData();
        queryWallet();
    }

    private void queryUserData() {
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(BmobUser.getCurrentUser().getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                mQueriedUserData = true;
                mUser = user;
                initUserData();
            }
        });
    }

    private void initUserData() {
        String avatar;
        if(mQueriedUserData){
            avatar = mUser.getAvatar();
        }else {
            SharedPreferences settings = getActivity().getSharedPreferences(Constant.SHARE_PREFERENCE_USER_INFO, MODE_PRIVATE);
            avatar = settings.getString(Constant.KEY_AVATAR,"");
        }
        Glide.with(getContext()).load(avatar).crossFade()
                .placeholder(R.mipmap.def_head)
                .transform(new GlideCircleTransform(getContext()))
                .into((ImageView) mRootView.findViewById(R.id.iv_avatar));
    }

    private void queryWallet(){
        BmobQuery<Wallet> query = new BmobQuery<>();
        query.addWhereEqualTo("user",BmobUser.getCurrentUser().getObjectId());
        query.findObjects(new FindListener<Wallet>() {
            @Override
            public void done(List<Wallet> list, BmobException e) {
                if(e != null) ToastUtil.show("查询失败");
                else if(list==null || list.size()==0) ((TextView)mRootView.findViewById(R.id.tv_cash)).setText("0 币");
                else ((TextView)mRootView.findViewById(R.id.tv_cash)).setText(list.get(0).getCash()+" 币");
            }
        });
    }

    private void loginVerification(){
        Bundle arg = new Bundle();
        if(mUser==null) return;
        arg.putSerializable("user",mUser);
//        EmptyActivity.navigateForResult(getActivity(),VerificationFragment.class.getName(),arg,5585,);
    }
}
