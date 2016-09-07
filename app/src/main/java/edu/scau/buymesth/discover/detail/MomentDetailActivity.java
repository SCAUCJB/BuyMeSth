package edu.scau.buymesth.discover.detail;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import base.BaseActivity;
import base.util.GlideCircleTransform;
import butterknife.Bind;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Moment;
import edu.scau.buymesth.data.bean.MomentsComment;
import edu.scau.buymesth.util.ColorChangeHelper;
import edu.scau.buymesth.util.DateFormatHelper;
import edu.scau.buymesth.util.DividerItemDecoration;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import ui.layout.NineGridLayout;

/**
 * Created by IamRabbit on 2016/8/23.
 */
public class MomentDetailActivity extends BaseActivity implements  MomentDetailContract.View{
    @Bind(R.id.rv_moment_comment)
    RecyclerView mRecyclerView;
    @Bind(R.id.store_house_ptr_frame)
    PtrFrameLayout mPtrFrameLayout;

    private MomentDetailPresenter mPresenter;
    private MomentDetailAdapter mMomentDetailAdapter;
    private View momentView;
    private Moment moment;
    private View notLoadingView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_moment_detail;
    }

    @Override
    public void initView() {
        initToolBar();
        moment = (Moment) getIntent().getSerializableExtra(Moment.class.getName());
        //初始化代理人
        mPresenter=new MomentDetailPresenter(this);
        mPresenter.setVM(this,new MomentDetailModel(moment));
        initStoreHouse();
        initMomentView();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration( new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        initAdapter();
        findViewById(R.id.bt_send_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.postComment(((TextView)findViewById(R.id.intput_comment)).getText().toString());
            }
        });
    }

    protected void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initMomentView(){
//        if(mPresenter.mModel.getMoment().getRequest()==null){
            momentView = getLayoutInflater().inflate(R.layout.item_discover_norequest_detail,(ViewGroup) mRecyclerView.getParent(),false);
//        }
//        else{
//            momentView = getLayoutInflater().inflate(R.layout.item_discover_request_detail,(ViewGroup) mRecyclerView.getParent(),false);
//        }
        ((TextView)momentView.findViewById(R.id.tv_name)).setText(moment.getAuthor().getNickname());
        ((TextView)momentView.findViewById(R.id.tv_level)).setText("LV "+moment.getAuthor().getExp()/10);
        ((TextView)momentView.findViewById(R.id.tv_text)).setText(moment.getContent());
        ((TextView)momentView.findViewById(R.id.tv_likes)).setText(""+moment.getLikes());
        ((TextView)momentView.findViewById(R.id.tv_comments)).setText(""+moment.getComments());
        ((TextView)momentView.findViewById(R.id.tv_date)).setText(DateFormatHelper.dateFormat(moment.getCreatedAt()));
        momentView.findViewById(R.id.tv_level).setBackground(ColorChangeHelper.tintDrawable(mContext.getResources().getDrawable(R.drawable.rect_black),
                ColorStateList.valueOf(ColorChangeHelper.IntToColorValue((moment.getAuthor().getExp())))));
        Glide.with(mContext).load(moment.getAuthor().getAvatar())
                .crossFade()
                .placeholder(R.mipmap.def_head)
                .transform(new GlideCircleTransform(mContext))
                .into((ImageView) momentView.findViewById(R.id.iv_avatar));
        if(moment.getImages()!=null&&moment.getImages().size()>0)
            ((NineGridLayout)momentView.findViewById(R.id.nine_grid_layout)).setUrlList(moment.getImages());
    }

    private void initAdapter(){
        mMomentDetailAdapter = new MomentDetailAdapter(mPresenter.mModel.getDatas());
        mMomentDetailAdapter.openLoadAnimation();
        mMomentDetailAdapter.addHeaderView(momentView);
        mRecyclerView.setAdapter(mMomentDetailAdapter);
//        mDiscoverAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
//            Intent intent =new Intent(getActivity(), HomeDetailActivity.class);
//            startActivity(intent);
//        });
//        mMomentDetailAdapter.setOnItemsContentClickListener(new DiscoverAdapter.OnItemsContentClickListener() {
//            @Override
//            public void onItemsContentClick(View v, Moment item, int position) {
//                switch (v.getId()){
//                    case R.id.ly_likes:
//                        mPresenter.AddLike(v,item , position);
//                        break;
//                    case R.id.ly_comments:
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
        mMomentDetailAdapter.setOnLoadMoreListener(() -> mPresenter.LoadMore());
        mMomentDetailAdapter.openLoadMore(Constant.NUMBER_PER_PAGE, true);
    }

    private void initStoreHouse() {
        final StoreHouseHeader header = new StoreHouseHeader(this);
        header.setPadding(0, 80, 0,50);
        header.initWithString("Buy Me Sth");
        header.setTextColor(Color.BLACK);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
//        frame.postDelayed(() -> frame.autoRefresh(false), 0);
        mPtrFrameLayout.postDelayed(()-> mPresenter.Refresh(),0);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return  !mRecyclerView.canScrollVertically(-1);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPresenter.Refresh();
            }
        });
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    public boolean showColorStatusBar() {
        return true;
    }

    @Override
    public int getStatusColorResources() {
        return R.color.colorPrimaryDark;
    }

    @Override
    public void onLoadMoreSuccess(List<MomentsComment>list) {
        if(list.size()>0)
            mMomentDetailAdapter.notifyDataChangedAfterLoadMore(true);
        else{
            mMomentDetailAdapter.notifyDataChangedAfterLoadMore(false);
            if (notLoadingView == null) {
                notLoadingView = this.getLayoutInflater().inflate(R.layout.not_loading, (ViewGroup) mRecyclerView.getParent(), false);
            }
            mMomentDetailAdapter.addFooterView(notLoadingView);
        }
    }

    @Override
    public void onError(Throwable throwable, String msg) {

    }

    @Override
    public void onRefreshComplete(List<MomentsComment> list) {
        mMomentDetailAdapter.notifyDataSetChanged();
        mMomentDetailAdapter.openLoadMore(Constant.NUMBER_PER_PAGE, true);
        mMomentDetailAdapter.removeAllFooterView();
        if(mPtrFrameLayout!=null)
            mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void onInitializeLocalDataComplete() {

    }

    @Override
    public void onAddOneItem(MomentsComment moment) {

    }

    @Override
    public void onItemChanged() {

    }

    @Override
    public void onRefreshInterrupt() {
        if(mPtrFrameLayout!=null)
            mPtrFrameLayout.refreshComplete();
        mMomentDetailAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadMoreInterrupt() {

    }

    @Override
    public void onPostCommentSuccess(String msg) {
        toast(msg);
        ((TextView)findViewById(R.id.intput_comment)).setText(null);
        mPresenter.Refresh();
    }

}
