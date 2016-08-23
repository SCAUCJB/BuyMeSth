package edu.scau.buymesth.homedetail;

import java.util.List;

import edu.scau.buymesth.data.bean.Request;

/**
 * Created by John on 2016/8/23.
 */

public interface RequestDetailContract {
     interface View{

         void setAuthorAvatar(String avatar);

         void setAuthorExp(Integer exp);

         void setAuthorName(String nickname);

         void setAuthorOnClicked();

         void setOnAcceptClicked();

         void setCommentBtn(String commentBtnStr);

         void setUserAvatar();

         void setTitle(String title);

         void setContent(String content);

         void setLikes(Integer likes);

         void setTime(String createdAt);

         void setUpViewPager(List<String> urls);

         void hideViewPager();
     }
    interface Model{
        void setRequest(Request request);
        Request getRequest();
        String getCommentBtnStr();
    }
}
