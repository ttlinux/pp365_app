package org.sex.hanker.Bean;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/1.
 */
public class PictureEPBean {


    /**
     * id : 26
     * title : Blowjobs
     * contentpath : /PP_picture/blowjob
     * author :
     * smallpic : /blowjobs-5236532/index.jpg
     * createtime : 1514166279067
     * parentid : 2
     * episode : 1
     * picpath : /blowjobs-5236532
     * ispublic : true
     * alt : Blowjobs
     * click : null
     * fimageurl : null
     * images : null
     */

    private int id;
    private String title;
    private String contentpath;
    private String author;
    private String smallpic;
    private long createtime;
    private int parentid;
    private int episode;
    private String picpath;
    private boolean ispublic;
    private String alt;
    private String click;
    private String fimageurl;
    private String images;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentpath() {
        return contentpath;
    }

    public void setContentpath(String contentpath) {
        this.contentpath = contentpath;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSmallpic() {
        return smallpic;
    }

    public void setSmallpic(String smallpic) {
        this.smallpic = smallpic;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public int getEpisode() {
        return episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    public String getPicpath() {
        return picpath;
    }

    public void setPicpath(String picpath) {
        this.picpath = picpath;
    }

    public boolean isIspublic() {
        return ispublic;
    }

    public void setIspublic(boolean ispublic) {
        this.ispublic = ispublic;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public Object getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public Object getFimageurl() {
        return fimageurl;
    }

    public void setFimageurl(String fimageurl) {
        this.fimageurl = fimageurl;
    }

    public Object getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public static PictureEPBean AnalysisData(JSONObject jsonObject)
    {
        PictureEPBean pbean=new PictureEPBean();
        pbean.setAlt(jsonObject.optString("alt",""));
        pbean.setAuthor(jsonObject.optString("author",""));
        pbean.setClick(jsonObject.optString("click",""));
        pbean.setContentpath(jsonObject.optString("contentpath",""));
        pbean.setCreatetime(jsonObject.optLong("createtime",0));
        pbean.setEpisode(jsonObject.optInt("episode",1));
        pbean.setFimageurl(jsonObject.optString("fimageurl",""));
        pbean.setId(jsonObject.optInt("id",1));
        pbean.setImages(jsonObject.optString("images",""));
        pbean.setIspublic(jsonObject.optBoolean("ispublic",true));
        pbean.setParentid(jsonObject.optInt("parentid",1));
        pbean.setPicpath(jsonObject.optString("picpath",""));
        pbean.setSmallpic(jsonObject.optString("smallpic","").replaceAll(" ","%20"));
        pbean.setTitle(jsonObject.optString("title",""));
        return pbean;
    }
}
