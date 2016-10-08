package edu.scau.buymesth.userinfo.evaluate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import adpater.BaseQuickAdapter;
import base.BaseActivity;
import butterknife.Bind;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.EvaluateListAdapter;
import edu.scau.buymesth.data.bean.Evaluate;
import edu.scau.buymesth.notice.OrderDetailActivity;

import static edu.scau.Constant.NUMBER_PER_PAGE;

/**
 * Created by John on 2016/10/6.
 */

public class EvaluateListActivity extends BaseActivity implements Contract.View {
    @Bind(R.id.rv)
    RecyclerView mRecyclerView;
    @Bind(R.id.tv_hint)
    TextView mHintTv;
    Presenter mPresenter;
    EvaluateListAdapter mAdapter;
    private View notLoadingView;

    public static void navigate(Context context, String id, boolean isBuyer) {
        Intent intent = new Intent(context, EvaluateListActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("isBuyer", isBuyer);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_evaluate_list;
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }


    @Override
    public void initView() {
        mAdapter = new EvaluateListAdapter();
        mAdapter.setOnLoadMoreListener(() -> mPresenter.onLoadMore());
        mAdapter.openLoadMore(NUMBER_PER_PAGE, true);
        mAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
              OrderDetailActivity.navigate((Activity) mContext,mAdapter.getItem(position).getOrderId());
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Model model = new Model(this);
        model.setId(getIntent().getStringExtra("id"));
        model.setIsBuyer(getIntent().getBooleanExtra("isBuyer", false));
        mPresenter = new Presenter(this, model);
        mPresenter.initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    public void setEvaluates(List<Evaluate> evaluates) {
        if (evaluates == null || evaluates.size() == 0) {
            mHintTv.setVisibility(View.VISIBLE);
        } else {
            if (mHintTv.getVisibility() == View.VISIBLE)
                mHintTv.setVisibility(View.GONE);
            mAdapter.setNewData(evaluates);
        }

    }

    public void addFooter() {
        if (notLoadingView == null) {
            notLoadingView = getWindow().getLayoutInflater().inflate(R.layout.not_loading, (ViewGroup) mRecyclerView.getParent(), false);
        }
        mAdapter.addFooterView(notLoadingView);
    }

    public void onLoadMore(List<Evaluate> evaluates) {
        if (evaluates != null)
            mAdapter.notifyDataChangedAfterLoadMore(evaluates, true);
        else {
            mAdapter.notifyDataChangedAfterLoadMore(false);
            addFooter();
        }
    }
}
