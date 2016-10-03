package edu.scau.buymesth.user.address;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import base.BaseActivity;
import base.util.SpaceItemDecoration;
import butterknife.Bind;
import edu.scau.buymesth.R;
import edu.scau.buymesth.adapter.AddressAdapter;
import edu.scau.buymesth.data.bean.Address;
import edu.scau.buymesth.user.address.editaddress.EditAddressActivity;

/**
 * Created by John on 2016/9/27.
 */

public class AddressActivity extends BaseActivity implements Contract.View {
    @Bind(R.id.btn_add)
    Button mAddBtn;
    @Bind(R.id.empty_view)
    ViewGroup mEmptyView;
    @Bind(R.id.tv_retry)
    TextView mRetryTv;
    @Bind(R.id.btn_go_add)
    Button mGoAddBtn;
    @Bind(R.id.rv_content)
    RecyclerView mRecyclerView;
    AddressPresenter mPresenter;
    private AddressAdapter mAddressAdapter;
    private boolean mIsForResult;

    public static void navigate(Activity activity){
    Intent intent=new Intent(activity,AddressActivity.class);
    activity.startActivity(intent);
}
    public static final String RESULT_ADDRESS="address";
    public static final int REQUEST_PICK_ADDRESS =126;
    public static void navigateForResult(Activity activity){
        Intent intent=new Intent(activity,AddressActivity.class);
        intent.putExtra("isForResult",true);
        activity.startActivityForResult(intent, REQUEST_PICK_ADDRESS);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_address;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new AddressPresenter(this, new AddressRepository(mContext));
    }

    @Override
    public void initView() {
        mIsForResult=getIntent().getBooleanExtra("isForResult",false);
        initRecyclerView();
    }

    @Override
    protected void setListener() {
        mGoAddBtn.setOnClickListener(v -> {
            //jump to edit
            mPresenter.editAddress(null);
        });
        mAddBtn.setOnClickListener(v -> {
            //jump to edit
            mPresenter.editAddress(null);
        });
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
    protected int getToolBarId() {
        return R.id.toolbar;
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    public void showEmpty() {
        mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void navigate(Address address) {
        EditAddressActivity.navigate(this,address);
    }

    @Override
    public void showAddresses(List<Address> addresses) {
        if(mRetryTv.getVisibility()==View.VISIBLE)
            mRetryTv.setVisibility(View.GONE);
        mAddressAdapter.setNewData(addresses);
    }

    @Override
    public void showError() {
        mRetryTv.setVisibility(View.VISIBLE);
        mRetryTv.setOnClickListener(v-> mPresenter.loadAddresses());
    }
private void initRecyclerView(){
    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    mRecyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_4)));
    mAddressAdapter=new AddressAdapter(R.layout.item_adress,null);
    mRecyclerView.setAdapter(mAddressAdapter);
    if(mIsForResult)
    mAddressAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
        Intent data=new Intent();
        data.putExtra(RESULT_ADDRESS,mAddressAdapter.getData().get(position));
        setResult(REQUEST_PICK_ADDRESS,data);
        finish();
    });
}

}
