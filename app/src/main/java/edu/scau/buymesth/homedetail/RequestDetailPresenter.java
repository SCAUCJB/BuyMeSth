package edu.scau.buymesth.homedetail;

import java.util.List;

import base.BasePresenter;
import edu.scau.buymesth.data.bean.Comment;
import edu.scau.buymesth.data.bean.Request;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    public void initComment(){
        mModel.getRxComment(mModel.getRequest().getObjectId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Comment>>() {
            @Override
            public void onCompleted() {
                mView.setComment(mModel.getCommentList());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(List<Comment> comments) {
                mModel.setCommentList(comments);
            }
        });
    }
}