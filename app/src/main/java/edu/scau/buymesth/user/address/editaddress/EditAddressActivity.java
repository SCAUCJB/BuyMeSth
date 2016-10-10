package edu.scau.buymesth.user.address.editaddress;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import base.BaseActivity;
import base.util.ToastUtil;
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
    private Bundle mLocationData;

    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;

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
        else {
            mIsNew = false;
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
            } catch (RuntimeException e) {
                toast("网络中断");
            }
        });

        mLocation.setOnClickListener(v ->
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    toast("我们需要访问位置信息");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            243);
                    //        Snackbar.make(welcomeImage,"我们需要访问存储来读写缓存",Snackbar.LENGTH_LONG).show();
                } else {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            243);
                }

            } else
                getLocation();
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 243) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.help);
                builder.setMessage(R.string.string_help_text);

                // 拒绝, 退出应用
                builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        toast("不能获取位置信息");
                    }
                });

                builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

                builder.setCancelable(false);

                builder.show();
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
    private void startLocation() {
        ToastUtil.show("开始定位");
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，
        // 启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，
        // setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private void getLocation() {

        AMapLocationListener mLocationListener = aMapLocation -> {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    ToastUtil.show("定位成功");
                    mLocationData = new Bundle();

                    mLocationData.putString("Province", aMapLocation.getProvince());//省信息
                    mLocationData.putString("City", aMapLocation.getCity());//城市信息
                    mLocationData.putString("District", aMapLocation.getDistrict());//城区信息
                    mLocationClient.stopLocation();
                    updateInfo();
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    ToastUtil.show("定位失败");
                }
            }
        };
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setWifiActiveScan(false);
        startLocation();
    }

    private void updateInfo() {
        runOnUiThread(() -> {
            mRegion.setText(mLocationData.getString("Province") +
                    mLocationData.getString("City") +
                    mLocationData.getString("District"));
        });
    }


    private void submit() throws RuntimeException {
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
        if (mSubscription != null)
            mSubscription.unsubscribe();
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    public int getStatusColorResources() {
        return R.color.colorPrimaryDark;
    }
}
