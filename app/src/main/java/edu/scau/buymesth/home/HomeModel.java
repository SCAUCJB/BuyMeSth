package edu.scau.buymesth.home;

import android.content.Context;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import edu.scau.Constant;
import edu.scau.buymesth.data.bean.Request;
import rx.Observable;

/**
 * Created by John on 2016/8/5.
 */

public class HomeModel implements HomeContract.Model {
    private final Context mContext;
    private int pageNum=0;
    public HomeModel(Context context){
        mContext=context;
    }

    @Override
    public void getRequests(GetRequestListener listener) {
        BmobQuery<Request> query=new BmobQuery<>();
        query.order("-createAt");
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (pageNum++));
        query.findObjects(new FindListener<Request>() {
            @Override
            public void done(List<Request> list, BmobException e) {
                if(list.isEmpty()){
                    --pageNum;
                    return;
                }
               if(listener!=null)
                   listener.onSuccess(list);
            }
        });

    }

    @Override
    public Observable<List<Request>> getRxRequests() {
        BmobQuery<Request> query=new BmobQuery<>();
        query.order("-createAt");
        query.setLimit(Constant.NUMBER_PER_PAGE);
        query.setSkip(Constant.NUMBER_PER_PAGE * (pageNum++));
        return query.findObjectsObservable(Request.class);
    }
}
