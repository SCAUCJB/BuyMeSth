package edu.scau.buymesth.request.requestdetail;

import android.app.Activity;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import edu.scau.buymesth.data.bean.Comment;
import edu.scau.buymesth.data.bean.Request;
import rx.Observable;

/**
 * Created by John on 2016/8/23.
 */

public interface RequestDetailContract {
    interface View {

        void setAuthorAvatar(String avatar);

        void setAuthorExp(Integer exp);

        void setAuthorName(String nickname);

        void setAuthorOnClicked();

        void setOnFollowClicked();

        void setOnCollectClicked();

        void setCommentBtn(String commentBtnStr);

        void setUserAvatar();

        void setTitle(String title);

        void setContent(String content);


        void setTime(String createdAt);


        void hideViewPager();

        void setComment(List<Comment> commentList);

        void setTagList(List<String> tags);

        void setPrice(String price);

        void setUpViewPager(List<String> picHeights, List<String> picWidths, List<String> urls);

        void setFollow(boolean b);

        void setCollect(boolean b);

        void toast(String msg);

        void showDialog();

        void closeDialog();

        Activity getContext();

        void exit();
    }

    interface Model {
        void setRequest(Request request);

        Request getRequest();

        String getCommentBtnStr();

        Observable<List<Comment>> getRxComment(String objId, BmobQuery.CachePolicy policy);

        List<Comment> getCommentList();

        void setCommentList(List<Comment> comments);
    }
}
