package edu.scau.buymesth.homedetail;

import base.BasePresenter;
import edu.scau.buymesth.data.bean.Request;

/**
 * Created by John on 2016/8/23.
 */

public class RequestDetailPresenter extends BasePresenter<RequestDetailContract.Model, RequestDetailContract.View> {
    @Override
    public void onStart() {

    }

    public void initUserInfo() {
        Request request = mModel.getRequest();
        mView.setAuthorAvatar(request.getAuthor().getAvatar());
        mView.setAuthorExp(request.getAuthor().getExp());
        mView.setAuthorName(request.getAuthor().getNickname());
        mView.setAuthorOnClicked();
        mView.setOnAcceptClicked();
    }

    public void initCommentBar() {
        mView.setCommentBtn(mModel.getCommentBtnStr());
        mView.setUserAvatar();
    }

    public void initContent() {
        Request request=mModel.getRequest();
        mView.setTitle(request.getTitle());
        mView.setContent(request.getContent());
        mView.setLikes(request.getLikes());
        mView.setTime(request.getCreatedAt());

        if(request.getUrls()!=null){
            mView.setUpViewPager(request.getUrls());
        }else {
            mView.hideViewPager();
        }
    }
    public void setComment(){

    }
}
