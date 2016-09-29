package edu.scau.buymesth.user.address.editaddress;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import base.BaseActivity;
import butterknife.Bind;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Address;
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

    public static void navigate(Activity activity, Address address) {
        Intent intent = new Intent(activity, EditAddressActivity.class);
        intent.putExtra("address", address);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_address;
    }

    @Override
    public void initView() {
        mAddress = (Address) getIntent().getSerializableExtra("address");
        if (mAddress.getRecipient() == null) mIsNew = true;
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }


    @Override
    protected void setListener() {
        mCompleteBtn.setOnClickListener(v -> {
            if (!checkValidate()) return;
            submit();
        });

        mLocation.setOnClickListener(v -> {
              getLocation();
        });
    }

    private void getLocation() {
   //     BmobGeoPoint bmobGeoPoint=new BmobGeoPoint()
    }

    private void submit() {
        mAddress.setRecipient(mRecipient.getText().toString());
        mAddress.setPhone(mPhone.getText().toString());
        mAddress.setRegion(mRegion.getText().toString());
        mAddress.setSpecific(mRegion.getText().toString());
        if (mIsNew) {
            showLoadingDialog();
            mSubscription = mAddress.saveObservable().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
                        @Override
                        public void onCompleted() {
                            closeLoadingDialog();
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            toast("保存失败");
                            closeLoadingDialog();
                        }

                        @Override
                        public void onNext(String s) {

                            closeLoadingDialog();
                        }
                    });
        } else {
            showLoadingDialog();
            mSubscription = mAddress.saveObservable().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
                        @Override
                        public void onCompleted() {
                            closeLoadingDialog();
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            toast("保存失败");
                            closeLoadingDialog();
                        }

                        @Override
                        public void onNext(String s) {

                            closeLoadingDialog();
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
        mSubscription.unsubscribe();
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }
}
