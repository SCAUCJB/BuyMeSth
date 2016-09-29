package edu.scau.buymesth.user.address;

import java.util.ArrayList;
import java.util.List;

import edu.scau.buymesth.data.bean.Address;
import rx.Observable;

/**
 * Created by John on 2016/9/27.
 */

public class AddressRepository {
    public Observable<List<Address>> getAddresses() {
        List<Address> addresses=new ArrayList<>();
        return  Observable.just(addresses );
    }
}
