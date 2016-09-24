package edu.scau.buymesth.user.request;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import adpater.animation.ScaleInAnimation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.RequestListAdapter;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.request.requestdetail.RequestDetailActivity;
import edu.scau.buymesth.util.DividerItemDecoration;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static cn.bmob.v3.BmobQuery.CachePolicy.CACHE_ONLY;
import static cn.bmob.v3.BmobQuery.CachePolicy.NETWORK_ELSE_CACHE;

/**
 * Created by John on 2016/9/21.
 */

public class RequestFragment extends Fragment {
    public RecyclerView mRecyclerView;
    private RequestListAdapter mRequestListAdapter;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();
    private TextView mHintTv;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, container, false);
          mHintTv = (TextView) view.findViewById(R.id.tv_hint);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv);
        //没滑到顶部之前不允许嵌套滑动
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(mParent==null) return;
                if(mRecyclerView.canScrollVertically(-1))
                {
                    mParent.setNestedScrollingEnabled(false);
                }
                else
                    mParent.setNestedScrollingEnabled(true);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
          initAdapter();
        //   mPresenter = new UserPresenter();
        return view;
    }
    NestedScrollView mParent;
    public void disallowIntercept(NestedScrollView parent){
        mParent=parent;
    }
    private void initAdapter() {

        mRequestListAdapter = new RequestListAdapter();
        mRequestListAdapter.openLoadAnimation(new ScaleInAnimation());

        //         mPresenter.initAdapter();
        mRequestListAdapter.setOnRecyclerViewItemClickListener(
                (view, position) -> RequestDetailActivity.navigate(getActivity(), mRequestListAdapter.getData().get(position))
        );

        mRecyclerView.setAdapter(mRequestListAdapter);
        Subscription subscription = getSomeonesRxRequests(CACHE_ONLY, BmobUser.getCurrentUser().getObjectId()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Request>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onNext(List<Request> requests) {
                        if(requests==null||requests.size()==0){
                            mHintTv.setVisibility(View.VISIBLE);
                        }
                        mRequestListAdapter.setNewData(requests);
                    }
                });
        mSubscriptions.add(subscription);
        //       mRequestListAdapter.setOnLoadMoreListener(() -> mPresenter.onLoadMore(filter, filterKey));
        //      mRequestListAdapter.openLoadMore(Constant.NUMBER_PER_PAGE, true);
    }

    private int pageNum = 0;

    public Observable<List<Request>> getSomeonesRxRequests(BmobQuery.CachePolicy policy, String userId) {
        BmobQuery<Request> query = new BmobQuery<>();
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));//此表示缓存一天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.include("user");
        query.addWhereEqualTo("user", userId);
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (pageNum++));
        if (policy == CACHE_ONLY && query.hasCachedResult(Request.class))
            query.setCachePolicy(CACHE_ONLY);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        else
            query.setCachePolicy(NETWORK_ELSE_CACHE);//先从缓存再从网络

        return query.findObjectsObservable(Request.class);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSubscriptions.isUnsubscribed())
            mSubscriptions.unsubscribe();
    }
}
