package edu.scau.buymesth.user;

import base.BasePresenter;

/**
 * Created by Jammy on 2016/8/31.
 */
public class UserPresenter extends BasePresenter<UserContract.Model,UserContract.View>{
    @Override
    public void onStart() {

    }
    public UserPresenter(UserContract.View v,UserContract.Model m){
        setVM(v,m);
    }
}
