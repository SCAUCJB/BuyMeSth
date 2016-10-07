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
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

import adpater.animation.SlideInBottomAnimation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.RequestListAdapter;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;
import edu.scau.buymesth.request.requestdetail.RequestDetailActivity;
import edu.scau.buymesth.util.DividerItemDecoration;
import edu.scau.buymesth.util.NetworkHelper;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static cn.bmob.v3.BmobQuery.CachePolicy.CACHE_ELSE_NETWORK;
import static cn.bmob.v3.BmobQuery.CachePolicy.CACHE_ONLY;
import static cn.bmob.v3.BmobQuery.CachePolicy.NETWORK_ELSE_CACHE;
import static cn.bmob.v3.BmobQuery.CachePolicy.NETWORK_ONLY;
import static edu.scau.Constant.NUMBER_PER_PAGE;

/**
 * Created by John on 2016/9/21.
 */

public class RequestFragment extends Fragment {
    public RecyclerView mRecyclerView;
    private RequestListAdapter mRequestListAdapter;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();
    private TextView mHintTv;
    private String mId;
    private View notLoadingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null)
            pageNum = 0;
        View view = inflater.inflate(R.layout.fragment_request, container, false);
        User user = (User) getActivity().getIntent().getSerializableExtra("user");
        mId = user != null ? user.getObjectId() : BmobUser.getCurrentUser().getObjectId();
        mHintTv = (TextView) view.findViewById(R.id.tv_hint);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv);
        //没滑到顶部之前不允许嵌套滑动
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mParent == null) return;
                if (mRecyclerView.canScrollVertically(-1)) {
                    mParent.setNestedScrollingEnabled(false);
                } else
                    mParent.setNestedScrollingEnabled(true);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        initAdapter();
        return view;
    }

    NestedScrollView mParent;

    public void disallowIntercept(NestedScrollView parent) {
        mParent = parent;
    }

    private void initAdapter() {

        mRequestListAdapter = new RequestListAdapter();
        mRequestListAdapter.openLoadAnimation(new SlideInBottomAnimation());

        mRequestListAdapter.setOnRecyclerViewItemClickListener(
                (view, position) -> RequestDetailActivity.navigate(getActivity(), mRequestListAdapter.getData().get(position))
        );

        mRecyclerView.setAdapter(mRequestListAdapter);
        Subscription subscription = getSomeonesRxRequests(CACHE_ELSE_NETWORK, mId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Request>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onNext(List<Request> requests) {

                        if (requests == null || requests.size() == 0) {
                            mHintTv.setVisibility(View.VISIBLE);
                        }else if(requests.size()>0&&mHintTv.getVisibility()==View.VISIBLE)
                            mHintTv.setVisibility(View.GONE);
                        mRequestListAdapter.setNewData(requests);
                    }
                });
        mSubscriptions.add(subscription);
        mRequestListAdapter.setOnLoadMoreListener(() -> onLoadMore(mId));
        mRequestListAdapter.openLoadMore(NUMBER_PER_PAGE, true);
    }

    private int pageNum = 0;

    @Override
    public void onResume() {
        super.onResume();
         refresh();
    }

    public void refresh() {
        pageNum=0;
        Subscription subscription = getSomeonesRxRequests(NETWORK_ONLY, mId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Request>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onNext(List<Request> requests) {

                        if (requests == null || requests.size() == 0) {
                            mHintTv.setVisibility(View.VISIBLE);
                        }else if(requests.size()>0&&mHintTv.getVisibility()==View.VISIBLE)
                            mHintTv.setVisibility(View.GONE);
                        mRequestListAdapter.setNewData(requests);
                        if(requests!=null&&requests.size()>0&&requests.size()<NUMBER_PER_PAGE){
                            if (notLoadingView == null) {
                                notLoadingView = getActivity().getWindow().getLayoutInflater().inflate(R.layout.not_loading, (ViewGroup) mRecyclerView.getParent(), false);
                            }
                            mRequestListAdapter.addFooterView(notLoadingView);
                        }
                    }
                });
        mSubscriptions.add(subscription);
    }

    public Observable<List<Request>> getSomeonesRxRequests(BmobQuery.CachePolicy policy, String userId) {
        BmobQuery<Request> query = new BmobQuery<>();
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));//此表示缓存一天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.include("user");
        query.addWhereEqualTo("user", userId);
        query.setLimit(NUMBER_PER_PAGE);
        query.setSkip(NUMBER_PER_PAGE * (pageNum++));
        if (policy == CACHE_ONLY && query.hasCachedResult(Request.class))
            query.setCachePolicy(CACHE_ONLY);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        else
            query.setCachePolicy(NETWORK_ELSE_CACHE);// 从网络

        return query.findObjectsObservable(Request.class);
    }

    void onLoadMore(String userId) {
        BmobQuery.CachePolicy policy = NetworkHelper.isOpenNetwork(getContext()) ? BmobQuery.CachePolicy.NETWORK_ONLY : BmobQuery.CachePolicy.CACHE_ONLY;
        mSubscriptions.add(getSomeonesRxRequests(policy, userId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Request>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        showMsg("获取请求列表出现了问题");
                        mRequestListAdapter.notifyDataChangedAfterLoadMore(false);
                    }

                    @Override
                    public void onNext(List<Request> requests) {
                        if(requests!=null)
                        mRequestListAdapter.notifyDataChangedAfterLoadMore(requests, true);
                        else {
                            mRequestListAdapter.notifyDataChangedAfterLoadMore(false);
                            if (notLoadingView == null) {
                                notLoadingView = getActivity().getWindow().getLayoutInflater().inflate(R.layout.not_loading, (ViewGroup) mRecyclerView.getParent(), false);
                            }
                            mRequestListAdapter.addFooterView(notLoadingView);
                        }
                    }
                }));
    }

    public void showMsg(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSubscriptions.clear();
    }
}
