package edu.scau.buymesth.user.address;

import java.util.List;

import edu.scau.buymesth.data.bean.Address;

/**
 * Created by John on 2016/9/27.
 */

public interface Contract {
    interface View{
        void showEmpty();
        void navigate(Address address);
        void showAddresses(List<Address> addresses);

        void showError();
    }
    interface Presenter{
        void subscribe();
        void unsubscribe();
        void loadAddresses();
        void editAddress(Address address);

    }
}
