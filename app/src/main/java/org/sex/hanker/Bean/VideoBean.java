package org.sex.hanker.Bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/12.
 */
public class VideoBean implements Serializable {


    /**
     * id : 158
     * parentid : 103
     * alt : 身材丰满的清纯大奶妹纸性欲真强 一晚要好几次才能尽兴 干得男友的鸡鸡都不举了！
     * phid : 39218
     * timelineurl :
     * timelineimagetype : null
     * timelinecount :
     * videotype : m3u8
     * createtime : 1519889256395
     * ispublic : true
     * rname : null
     * click : 0
     * wouldexpire : false
     * videoDuration :
     * imageUrl : http://video.tan9797.com/upload/2018/02/25/201802251230192453.jpg
     * timestr : null
     * linkUrl : http://www.video77771.com/player/39218-1-0.html
     * videoTitle : 身材丰满的清纯大奶妹纸性欲真强 一晚要好几次才能尽兴 干得男友的鸡鸡都不举了！
     * quality480p : http://cdn1.tan9696.com/2018/02/24/C40FA1/index.m3u8
     */

    private int id;
    private int parentid;
    private String alt;
    private String phid;
    private String timelineurl;
    private int timelineimagetype;
    private String timelinecount;
    private String videotype;
    private long createtime;
    private boolean ispublic;
    private String rname;
    private int click;
    private boolean wouldexpire;
    private String videoDuration;
    private String imageUrl;
    private Object timestr;
    private String linkUrl;
    private String videoTitle;
    private String quality480p;
    private String Countryid;

    public String getCountryid() {
        return Countryid;
    }

    public void setCountryid(String countryid) {
        Countryid = countryid;
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

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getPhid() {
        return phid;
    }

    public void setPhid(String phid) {
        this.phid = phid;
    }

    public String getTimelineurl() {
        return timelineurl;
    }

    public void setTimelineurl(String timelineurl) {
        this.timelineurl = timelineurl;
    }

    public int getTimelineimagetype() {
        return timelineimagetype;
    }

    public void setTimelineimagetype(int timelineimagetype) {
        this.timelineimagetype = timelineimagetype;
    }

    public String getTimelinecount() {
        return timelinecount;
    }

    public void setTimelinecount(String timelinecount) {
        this.timelinecount = timelinecount;
    }

    public String getVideotype() {
        return videotype;
    }

    public void setVideotype(String videotype) {
        this.videotype = videotype;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public boolean isIspublic() {
        return ispublic;
    }

    public void setIspublic(boolean ispublic) {
        this.ispublic = ispublic;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public int getClick() {
        return click;
    }

    public void setClick(int click) {
        this.click = click;
    }

    public boolean isWouldexpire() {
        return wouldexpire;
    }

    public void setWouldexpire(boolean wouldexpire) {
        this.wouldexpire = wouldexpire;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Object getTimestr() {
        return timestr;
    }

    public void setTimestr(Object timestr) {
        this.timestr = timestr;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getQuality480p() {
        return quality480p;
    }

    public void setQuality480p(String quality480p) {
        this.quality480p = quality480p;
    }

    public static VideoBean AnalynsisData(JSONObject jsonObject)
    {
        VideoBean videoBean=new VideoBean();
        videoBean.setId(jsonObject.optInt("id", 1));
        videoBean.setParentid(jsonObject.optInt("parentid", 1));
        videoBean.setAlt(jsonObject.optString("alt", ""));
        videoBean.setPhid(jsonObject.optString("phid", ""));
        videoBean.setTimelineurl(jsonObject.optString("timelineurl", ""));
        videoBean.setTimelineimagetype(jsonObject.optInt("timelineimagetype", -999));
        videoBean.setTimelinecount(jsonObject.optString("timelinecount", ""));
        videoBean.setVideotype(jsonObject.optString("videotype", ""));
        videoBean.setCreatetime(jsonObject.optLong("createtime", 0));
        videoBean.setIspublic(jsonObject.optBoolean("ispublic", true));
        videoBean.setRname(jsonObject.optString("rname", ""));
        videoBean.setClick(jsonObject.optInt("click", 0));
        videoBean.setWouldexpire(jsonObject.optBoolean("wouldexpire", false));
        videoBean.setVideoDuration(jsonObject.optString("videoDuration", ""));
        videoBean.setImageUrl(jsonObject.optString("imageUrl", ""));
        videoBean.setLinkUrl(jsonObject.optString("linkUrl",""));
        videoBean.setVideoTitle(jsonObject.optString("videoTitle",""));
        videoBean.setQuality480p(jsonObject.optString("quality480p",""));
        return videoBean;
    }

    public static VideoBean ConvertBean(BroadcastDataBean bean)
    {
        VideoBean videoBean=new VideoBean();
        videoBean.setPhid(bean.getVIDEO_ID());
        videoBean.setTimelineurl(bean.getTimeLineUrl());
        videoBean.setTimelineimagetype(bean.getTimeLineImageIype());
        videoBean.setTimelinecount(bean.getTimeLineCount() + "");
        videoBean.setVideotype(bean.getSUFFIX());
        videoBean.setImageUrl(bean.getVIDEO_PHOTO());
        videoBean.setVideoTitle(bean.getVIDEO_TITLE());
        videoBean.setQuality480p(bean.getCOLUMN_URL());
        videoBean.setCountryid(bean.getCOUNTRY());
        return videoBean;
    }
}
