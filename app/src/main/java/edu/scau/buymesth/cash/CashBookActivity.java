package edu.scau.buymesth.cash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import base.util.SpaceItemDecoration;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.CashBookAdapter;
import edu.scau.buymesth.data.bean.CashBook;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by Jammy on 2016/10/7.
 */
public class CashBookActivity extends BaseActivity {
    User user;
    @Bind(R.id.rv)
    RecyclerView rv;
    CashBookAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cashbook;
    }

    @Override
    public void initView() {
        user = (User) getIntent().getSerializableExtra("user");
        adapter = new CashBookAdapter(new ArrayList<>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        rv.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_6)));
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);
        rv.setAdapter(adapter);


        BmobQuery<CashBook> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("user", user.getObjectId());
        BmobQuery<CashBook> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("toUser", user.getObjectId());
        List<BmobQuery<CashBook>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);
        BmobQuery<CashBook> query = new BmobQuery<>();
        query.or(queries);
        query.include("user,toUser,toOrder");
        query.order("-updatedAt");
        query.findObjects(new FindListener<CashBook>() {
            @Override
            public void done(List<CashBook> list, BmobException e) {
                adapter.setNewData(list);
            }
        });
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    public static void navigate(Activity activity, User user) {
        Intent intent = new Intent(activity, CashBookActivity.class);
        intent.putExtra("user", user);
        activity.startActivity(intent);
    }

}
