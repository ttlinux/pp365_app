package org.sex.hanker.Bean;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/2/28.
 */
public class ChatBean {
    int userid;
    String username;
    String userpicpath;
    String message;
    String countryid;
    String videoid;
    long chattime;

    public long getChattime() {
        return chattime;
    }

    public void setChattime(long chattime) {
        this.chattime = chattime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpicpath() {
        return userpicpath;
    }

    public void setUserpicpath(String userpicpath) {
        this.userpicpath = userpicpath;
    }

    public String getCountryid() {
        return countryid;
    }

    public void setCountryid(String countryid) {
        this.countryid = countryid;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public static ChatBean Analysis(JSONObject jsonObject)
    {
        ChatBean chatBean=new ChatBean();
        chatBean.setChattime(jsonObject.optLong("chattime",0));
        chatBean.setMessage(jsonObject.optString("message", ""));
        chatBean.setUserid(jsonObject.optInt("userid", 1));
        chatBean.setUsername(jsonObject.optString("username", ""));
        chatBean.setUserpicpath(jsonObject.optString("userpicpath", ""));
        chatBean.setCountryid(jsonObject.optString("countryid",""));
        chatBean.setVideoid(jsonObject.optString("videoid",""));
        return chatBean;
    }
}
