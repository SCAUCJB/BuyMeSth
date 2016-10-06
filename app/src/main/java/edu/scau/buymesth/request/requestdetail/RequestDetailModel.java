package edu.scau.buymesth.request.requestdetail;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import edu.scau.buymesth.data.bean.Comment;
import edu.scau.buymesth.data.bean.Request;
import rx.Observable;

/**
 * Created by John on 2016/8/23.
 */

public class RequestDetailModel implements RequestDetailContract.Model {
    Request request = null;
    List<Comment> comments = null;


    public void setRequest(Request request) {
        this.request = request;
    }

    public RequestDetailModel( ) {


    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    public String getCommentBtnStr() {
        return "要勾搭，先评论";
    }



    @Override
    public Observable<List<Comment>> getRxComment(String objId,BmobQuery.CachePolicy policy) {
        BmobQuery<Comment> query = new BmobQuery<>();
        Request request = new Request();
        request.setObjectId(objId);
        query.addWhereEqualTo("request", new BmobPointer(request));
        query.order("-createdAt");
        query.setLimit(4);
        query.include("author");
        if(query.hasCachedResult(Comment.class)&&policy== BmobQuery.CachePolicy.CACHE_ONLY)
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);
        else{
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        }
        return query.findObjectsObservable(Comment.class);
    }

    @Override
    public List<Comment> getCommentList() {
        return comments;
    }

    @Override
    public void setCommentList(List<Comment> comments) {
        this.comments = comments;
    }
}
