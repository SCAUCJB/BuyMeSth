package edu.scau.buymesth.user.address;

import android.content.Context;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import edu.scau.buymesth.data.bean.Address;
import edu.scau.buymesth.util.NetworkHelper;
import rx.Observable;

/**
 * Created by John on 2016/9/27.
 */

public class AddressRepository {
    private final Context mContext;

    public AddressRepository(Context context){
        mContext=context;
    }
    public Observable<List<Address>> getAddresses() {
        BmobQuery<Address> query=new BmobQuery<>();
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
        query.order("-createdAt");
        query.addWhereEqualTo("user", BmobUser.getCurrentUser().getObjectId());
        if(NetworkHelper.isOpenNetwork(mContext)){
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        }else{
            if(query.hasCachedResult(Address.class))
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);
            else return null;
        }
        return  query.findObjectsObservable(Address.class);
    }
}
