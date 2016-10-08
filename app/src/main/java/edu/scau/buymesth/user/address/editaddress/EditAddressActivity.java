package edu.scau.buymesth.user.address.editaddress;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import base.BaseActivity;
import butterknife.Bind;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Address;
import edu.scau.buymesth.fragment.EmptyActivity;
import edu.scau.buymesth.location.LocationFragment;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by John on 2016/9/29.
 */

public class EditAddressActivity extends BaseActivity {
    @Bind(R.id.btn_complete)
    Button mCompleteBtn;
    @Bind(R.id.et_recipient)
    EditText mRecipient;
    @Bind(R.id.phone)
    EditText mPhone;
    @Bind(R.id.region)
    EditText mRegion;
    @Bind(R.id.specific)
    EditText mSpecific;
    @Bind(R.id.iv_location)
    ImageView mLocation;

    private Address mAddress = null;
    private boolean mIsNew;
    private Subscription mSubscription;

    public static void navigate(Context context, Address address) {
        Intent intent = new Intent(context, EditAddressActivity.class);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_address;
    }

    @Override
    public void initView() {
        mAddress = (Address) getIntent().getSerializableExtra("address");
        if (mAddress.getRecipient() == null) mIsNew = true;
        else{
            mIsNew=false;
            mRecipient.setText(mAddress.getRecipient());
            mPhone.setText(mAddress.getPhone());
            mRegion.setText(mAddress.getRegion());
            mSpecific.setText(mAddress.getSpecific());
        }

    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }


    @Override
    protected void setListener() {
        mCompleteBtn.setOnClickListener(v -> {
            if (!checkValidate()) return;
            try {
                submit();
            }catch (RuntimeException e){
                toast("网络中断");
            }
        });

        mLocation.setOnClickListener(v -> EmptyActivity.navigate(EditAddressActivity.this, LocationFragment.class.getName(),null,"定位"));
    }

    private void getLocation() {
        //通过经纬度获取省市区
   //     BmobGeoPoint bmobGeoPoint=new BmobGeoPoint()

    }

    private void submit() throws RuntimeException{
        mAddress.setRecipient(mRecipient.getText().toString());
        mAddress.setPhone(mPhone.getText().toString());
        mAddress.setRegion(mRegion.getText().toString());
        mAddress.setSpecific(mSpecific.getText().toString());
        if (mIsNew) {
            showLoadingDialog();
            mSubscription = mAddress.saveObservable().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable throwable) {
                            toast("保存失败");
                            closeLoadingDialog();
                        }

                        @Override
                        public void onNext(String s) {
                            closeLoadingDialog();
                            finish();
                        }
                    });
        } else {
            showLoadingDialog();
            mAddress.updateObservable().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Void>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable throwable) {
                    toast("保存失败");
                    closeLoadingDialog();
                }

                @Override
                public void onNext(Void aVoid) {
                    closeLoadingDialog();
                    finish();
                }
            });

        }

    }


    private boolean checkValidate() {

        if (TextUtils.isEmpty(mRecipient.getText())) {
            toast("请填写收货人");
            return false;
        }
        if (TextUtils.isEmpty(mPhone.getText())) {
            toast("请填写联系电话");
            return false;
        }
        if (TextUtils.isEmpty(mRegion.getText())) {
            toast("请填写省市区");
            return false;
        }
        if (TextUtils.isEmpty(mSpecific.getText())) {
            toast("请填写详细地址");
            return false;
        }
        if (mPhone.getText().toString().length() != 11) {
            toast("手机号码填写错误");
            return false;
        }
        return true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mSubscription!=null)
        mSubscription.unsubscribe();
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }
}
