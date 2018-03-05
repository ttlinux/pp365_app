package org.sex.hanker.Bean;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/5.
 */
public class NoteMenuBean {

    int id,parentid;
    String title,createtime,picpath,contentpath;

    public String getContentpath() {
        return contentpath;
    }

    public void setContentpath(String contentpath) {
        this.contentpath = contentpath;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public String getPicpath() {
        return picpath;
    }

    public void setPicpath(String picpath) {
        this.picpath = picpath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static NoteMenuBean Analysis(JSONObject jsonObject)
    {
        NoteMenuBean noteMenuBean =new NoteMenuBean();
        noteMenuBean.setTitle(jsonObject.optString("title"));
        noteMenuBean.setContentpath(jsonObject.optString("contentpath"));
        noteMenuBean.setCreatetime(jsonObject.optString("createtime"));
        noteMenuBean.setPicpath(jsonObject.optString("picpath"));
        noteMenuBean.setId(jsonObject.optInt("id", 1));
        noteMenuBean.setParentid(jsonObject.optInt("parentid",2));
        return noteMenuBean;
    }
}
