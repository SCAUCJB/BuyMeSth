package edu.scau.buymesth.user.address;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import base.BaseActivity;
import butterknife.Bind;
import edu.scau.buymesth.R;
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
    @Bind(R.id.btn_go_add)
    Button mGoAddBtn;
    AddressPresenter mPresenter;
public static void navigate(Activity activity){
    Intent intent=new Intent(activity,AddressActivity.class);
    activity.startActivity(intent);
}
    @Override
    protected int getLayoutId() {
        return R.layout.activity_address;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new AddressPresenter(this, new AddressRepository());
    }

    @Override
    public void initView() {

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

    }


}
