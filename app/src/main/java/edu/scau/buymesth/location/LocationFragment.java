package edu.scau.buymesth.location;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import base.util.SpaceItemDecoration;
import base.util.ToastUtil;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.fragment.EmptyActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * Created by ！ on 2016/10/7.
 */

public class LocationFragment extends Fragment implements EmptyActivity.ButtonOnToolbar{
    private RecyclerView mRecyclerView;

    private EditText mCountry;
    private EditText mProvince;
    private EditText mCity;
    private EditText mDetail;
    private TextView mLocation;
    private String locationProvider;

    private Bundle mLocationData;

    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    private AMapLocationListener mLocationListener = null;
    private final static String AMAP_WEB_API_KEY = "bba3ad4b510032c675b2f0f0f54ea8fb";
    private JSONObject mSurroundingInfo;
    private List<String> mSurroundingData;
    private SurroundingAdapter mSurroundingAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location,container,false);
        mRecyclerView= (RecyclerView) view.findViewById(R.id.rv_location_detail_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(2));
        mCountry = (EditText) view.findViewById(R.id.tv_country);
        mProvince = (EditText) view.findViewById(R.id.tv_province);
        mCity = (EditText) view.findViewById(R.id.tv_city);
        mDetail = (EditText) view.findViewById(R.id.tv_detail);
        mLocation = (TextView) view.findViewById(R.id.tv_location_loading);
        mLocation.setOnClickListener(v -> {
            startLocation();
        });
        initAdapter();
        initLocation();
        startLocation();
        mDetail.setText(sHA1(getContext()));
        return view;
    }

    public String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        initLocation();
    }

    private void initAdapter() {
        mSurroundingData = new ArrayList<>();
        mSurroundingAdapter = new SurroundingAdapter(mSurroundingData);
        mRecyclerView.setAdapter(mSurroundingAdapter);
        mSurroundingAdapter.setOnRecyclerViewItemClickListener((view, position) ->{
            mDetail.setText(mSurroundingAdapter.getItem(position).split("\n")[0]);
//            mLocationData.remove("Address");
//            mLocationData.putString("Address",mSurroundingAdapter.getItem(position).split("\n")[0]);
        });
    }

    private void initLocation(){
        mLocationListener = aMapLocation -> {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    ToastUtil.show("定位成功");
                    mLocationData = new Bundle();
                    mLocationData.putInt("LocationType",aMapLocation.getLocationType());//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    mLocationData.putDouble("Latitude",aMapLocation.getLatitude());//获取纬度
                    mLocationData.putDouble("Longitude",aMapLocation.getLongitude());//获取经度
                    mLocationData.putString("Address",aMapLocation.getAddress());//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    mLocationData.putString("Country",aMapLocation.getCountry());//国家信息
                    mLocationData.putString("Province",aMapLocation.getProvince());//省信息
                    mLocationData.putString("City",aMapLocation.getCity());//城市信息
                    mLocationData.putString("District",aMapLocation.getDistrict());//城区信息
                    mLocationData.putString("Street",aMapLocation.getStreet());//街道信息
                    mLocationData.putString("StreetNum",aMapLocation.getStreetNum());//街道门牌号信息
                    mLocationClient.stopLocation();
                    updateInfo();
                    getSurrounding();
                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    ToastUtil.show("AmapError"+"location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
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
    }

    private void updateInfo() {
        getActivity().runOnUiThread(() -> {
            mCountry.setText(mLocationData.getString("Country"));
            mProvince.setText(mLocationData.getString("Province"));
            mCity.setText(mLocationData.getString("City"));
            mDetail.setText(mLocationData.getString("Address"));
        });
    }

    private void getSurrounding(){
        Double longitude = mLocationData.getDouble("Longitude");//获取经度
        Double latitude = mLocationData.getDouble("Latitude");//获取纬度
        String url = "http://restapi.amap.com/v3/geocode/regeo?key=" +
                AMAP_WEB_API_KEY +
                "&location=" +
                longitude +
                "," +
                latitude +
                "&poitype=&radius=1000&extensions=all&batch=false&roadlevel=1";
        OkHttpClient okHttpClient = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                ToastUtil.show(e==null?"未知错误":e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                ToastUtil.show(response==null?"未知错误":response.body().string());
//                System.out.println("!!!!!!!!!!!!!!!!!!!!"+response.body().string());
                try {
                    mSurroundingInfo = new JSONObject(response.body().string());
                    changeAdapterData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void changeAdapterData() throws JSONException {
        JSONObject regeocode = mSurroundingInfo.getJSONObject("regeocode");
        JSONArray pois = regeocode.getJSONArray("pois");
        mSurroundingData.clear();
        for(int i =0;i<pois.length();i++){
            JSONObject poi = (JSONObject) pois.get(i);
            String item = poi.getString("name")+"\n";
            item += poi.getString("address");
            mSurroundingData.add(item);
        }
        mRecyclerView.post(() -> mSurroundingAdapter.notifyDataSetChanged());
    }

    private void startLocation(){
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

    @Override
    public void onDestroyView() {
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
        super.onDestroyView();
    }

    @Override
    public void onToolbarButtonClick() {
        Intent i = new Intent();
        mLocationData.remove("Country");
        mLocationData.remove("Province");
        mLocationData.remove("City");
        mLocationData.remove("Address");
        mLocationData.putString("Country",mCountry.getText().toString());
        mLocationData.putString("Province",mProvince.getText().toString());
        mLocationData.putString("City",mCity.getText().toString());
        mLocationData.putString("Address",mDetail.getText().toString());
        i.putExtra("data",mLocationData);
        getActivity().setResult(Constant.LOCATION_SELECT_RESULT_CODE,i);
        getActivity().finish();
    }

    @Override
    public String toolbarsButtonText() {
        return "选择地址";
    }

    private class SurroundingAdapter extends BaseQuickAdapter<String>{

        public SurroundingAdapter(List<String> data) {
            super(R.layout.item_surrounding, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            String[] info = item.split("\n");
            helper.setText(R.id.tv_location_name,info[0]);
            helper.setText(R.id.tv_location_address,info[1]);
        }
    }
}
