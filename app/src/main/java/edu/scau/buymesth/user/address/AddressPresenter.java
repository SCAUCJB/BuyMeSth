package edu.scau.buymesth.user.address;

import java.util.List;

import cn.bmob.v3.BmobUser;
import edu.scau.buymesth.data.bean.Address;
import edu.scau.buymesth.data.bean.User;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by John on 2016/9/27.
 */

public class AddressPresenter implements Contract.Presenter {
    AddressRepository mAddressRepository;
    Contract.View mAddressView;
    Subscription mSubscription;
    public AddressPresenter(Contract.View view, AddressRepository addressRepository){
        mAddressRepository=addressRepository;
        mAddressView=view;
    }

    @Override
    public void subscribe() {
        loadAddresses();
    }

    @Override
    public void unsubscribe() {
        mSubscription.unsubscribe();
    }

    @Override
    public void loadAddresses() {
        try {
            mSubscription = mAddressRepository.getAddresses()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Address>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }

                        @Override
                        public void onNext(List<Address> addresses) {
                            if (addresses == null || addresses.size() == 0) {
                                mAddressView.showEmpty();
                            } else
                                mAddressView.showAddresses(addresses);
                        }
                    });
        }catch (RuntimeException e){
            mSubscription.unsubscribe();
            mAddressView.showError();
        }
    }

    @Override
    public void editAddress(Address address) {
        if(address==null) {
            address=new Address();
            address.setUser(BmobUser.getCurrentUser(User.class));
        }
       mAddressView.navigate(address);
    }
}
