package edu.scau.buymesth.user.mark;

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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import adpater.animation.ScaleInAnimation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.MarkListAdapter;
import edu.scau.buymesth.data.bean.Collect;
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

import static cn.bmob.v3.BmobQuery.CachePolicy.CACHE_ONLY;
import static cn.bmob.v3.BmobQuery.CachePolicy.NETWORK_ELSE_CACHE;
import static cn.bmob.v3.BmobQuery.CachePolicy.NETWORK_ONLY;
import static edu.scau.buymesth.util.NetworkHelper.isOpenNetwork;

/**
 * Created by John on 2016/10/2.
 */

public class MarkRequestFragment extends Fragment {
    public RecyclerView mRecyclerView;
    private MarkListAdapter mMarkListAdapter;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();
    private TextView mHintTv;
    private String mId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null)
            pageNum = 0;
        View view = inflater.inflate(R.layout.fragment_mark_request, container, false);
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

        mMarkListAdapter = new MarkListAdapter();
        mMarkListAdapter.openLoadAnimation(new ScaleInAnimation());


        mMarkListAdapter.setOnRecyclerViewItemClickListener(
                (view, position) -> RequestDetailActivity.navigate(getActivity(), mMarkListAdapter.getData().get(position))
        );
        mRecyclerView.setAdapter(mMarkListAdapter);
        BmobQuery.CachePolicy policy=NetworkHelper.isOpenNetwork(getContext())?NETWORK_ONLY:CACHE_ONLY;
        Subscription subscription = getCollects(policy, mId).subscribeOn(Schedulers.io()).map(collects -> {
            List<Request> requests = new ArrayList<>(collects.size());
            for (Collect c : collects)
              if(c.getRequest().getMaxPrice()!=null)//如果这是一张已经被删除了的请求，那么价格一定返回空
                requests.add(c.getRequest());

            return requests;
        }) .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Request>>() {
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
                        }else if(requests.size() >0&&mHintTv.getVisibility()==View.VISIBLE)
                            mHintTv.setVisibility(View.GONE);
                        mMarkListAdapter.setNewData(requests);
                    }
                });
        mSubscriptions.add(subscription);
        mMarkListAdapter.setOnLoadMoreListener(() -> onLoadMore(mId));
        mMarkListAdapter.openLoadMore(Constant.NUMBER_PER_PAGE, true);
    }

    private int pageNum = 0;


    void onLoadMore(String userId) {
        BmobQuery.CachePolicy policy = isOpenNetwork(getContext()) ? BmobQuery.CachePolicy.NETWORK_ONLY : BmobQuery.CachePolicy.CACHE_ONLY;
        mSubscriptions.add(getCollects(policy, userId).subscribeOn(Schedulers.io())
                .map(collects -> {
                    List<Request> requests = new ArrayList<>(collects.size());
                    for (Collect c : collects)
                        requests.add(c.getRequest());
                    return requests;
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Request>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        showMsg("获取收藏列表出现了问题");
                    }

                    @Override
                    public void onNext(List<Request> requests) {
                        mMarkListAdapter.notifyDataChangedAfterLoadMore(requests, true);
                    }
                }));
    }

    public Observable<List<Collect>> getCollects(BmobQuery.CachePolicy policy, String userId) {
        BmobQuery<Collect> query = new BmobQuery<>();
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));//此表示缓存一天，可以用来优化下拉刷新而清空了的加载更多
        query.order("-createdAt");
        query.include("request");
        query.addWhereEqualTo("user", userId);
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (pageNum++));
        if (policy == CACHE_ONLY && query.hasCachedResult(Collect.class))
            query.setCachePolicy(CACHE_ONLY);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        else
            query.setCachePolicy(NETWORK_ELSE_CACHE);//先从缓存再从网络
        return query.findObjectsObservable(Collect.class);
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