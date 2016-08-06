package edu.scau.buymesth.data;

import java.util.LinkedList;
import java.util.List;

import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by John on 2016/8/4.
 */

public class DataServer {
    private DataServer() {
    }

    /**
     *
     * @param  size 请求的数量
     * @return
     */
    public static List<Request>getRequests(int size){
        List<Request> list=new LinkedList<>();
        for(int i=0;i<size;i++){
            Request request=new Request();
            User user=new User();
            user.setNickname("Miracle");
            user.setAvatar("http://p2.ifengimg.com/fck/2016_28/26e28bccda30aaa_w350_h495.png");
            request.setTitle("帮我请个中国教练");
            request.setContent("很急很关键，十五字十五字十五字十五字");
            request.setLikes(150);
            List<String> urls=new LinkedList<>();
            urls.add("http://www.dota2.com.cn/images/heroes/antimage_vert.jpg");
            request.setUrls(urls);
            request.setComments(30);
            request.setAuthor(user);
            list.add(request);
        }
        return list;
    }
}
