package edu.scau.buymesth.homedetail;

import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import base.BaseActivity;
import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by John on 2016/8/12.
 *
 */

public class HomeDetailActivity extends BaseActivity {
    @Bind(R.id.fab)
     FloatingActionButton fab;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_home_detail;
    }

    @Override
    public void initView() {
        fab.setOnClickListener(v -> {
            Request request=new Request();
            request.setTitle("帮我请个中国教练");
            request.setContent("很急很关键，十五字十五字十五字十五字");
            request.setLikes(150);
            List<String> urls=new LinkedList<>();
            urls.add("http://www.dota2.com.cn/images/heroes/antimage_vert.jpg");
            request.setUrls(urls);
            request.setComments(30);
            request.setAuthor(BmobUser.getCurrentUser(User.class));
            request.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    Toast.makeText(HomeDetailActivity.this,"save success",Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }
}
