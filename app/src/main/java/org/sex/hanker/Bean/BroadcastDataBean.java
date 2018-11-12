package org.sex.hanker.Bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/9/28.
 */
public class BroadcastDataBean implements Serializable {

    private String COLUMN_URL, VIDEO_ID, COUNTRY, SUFFIX, LocalPath, VIDEO_TITLE, VIDEO_PHOTO, TimeLineUrl,FileLength;
    private int ID, STATUS, TimeLineImageIype, TimeLineCount;
    private int Persent;

    private long speed,contentlength,currentlength;
    private int downloadepisode,episodeAmount;

    public String getFileLength() {
        return FileLength;
    }

    public void setFileLength(String fileLength) {
        FileLength = fileLength;
    }

    public long getContentlength() {
        return contentlength;
    }

    public void setContentlength(long contentlength) {
        this.contentlength = contentlength;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public int getEpisodeAmount() {
        return episodeAmount;
    }

    public void setEpisodeAmount(int episodeAmount) {
        this.episodeAmount = episodeAmount;
    }

    public int getDownloadepisode() {
        return downloadepisode;
    }

    public void setDownloadepisode(int downloadepisode) {
        this.downloadepisode = downloadepisode;
    }

    public long getCurrentlength() {
        return currentlength;
    }

    public void setCurrentlength(long currentlength) {
        this.currentlength = currentlength;
    }

    public String getCOLUMN_URL() {
        return COLUMN_URL;
    }

    public void setCOLUMN_URL(String COLUMN_URL) {
        this.COLUMN_URL = COLUMN_URL;
    }

    public String getCOUNTRY() {
        return COUNTRY;
    }

    public void setCOUNTRY(String COUNTRY) {
        this.COUNTRY = COUNTRY;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLocalPath() {
        return LocalPath;
    }

    public void setLocalPath(String localPath) {
        LocalPath = localPath;
    }

    public int getPersent() {
        return Persent;
    }

    public void setPersent(int persent) {
        Persent = persent;
    }

    public int getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(int STATUS) {
        this.STATUS = STATUS;
    }

    public String getSUFFIX() {
        return SUFFIX;
    }

    public void setSUFFIX(String SUFFIX) {
        this.SUFFIX = SUFFIX;
    }

    public int getTimeLineCount() {
        return TimeLineCount;
    }

    public void setTimeLineCount(int timeLineCount) {
        TimeLineCount = timeLineCount;
    }

    public int getTimeLineImageIype() {
        return TimeLineImageIype;
    }

    public void setTimeLineImageIype(int timeLineImageIype) {
        TimeLineImageIype = timeLineImageIype;
    }

    public String getTimeLineUrl() {
        return TimeLineUrl;
    }

    public void setTimeLineUrl(String timeLineUrl) {
        TimeLineUrl = timeLineUrl;
    }

    public String getVIDEO_ID() {
        return VIDEO_ID;
    }

    public void setVIDEO_ID(String VIDEO_ID) {
        this.VIDEO_ID = VIDEO_ID;
    }

    public String getVIDEO_PHOTO() {
        return VIDEO_PHOTO;
    }

    public void setVIDEO_PHOTO(String VIDEO_PHOTO) {
        this.VIDEO_PHOTO = VIDEO_PHOTO;
    }

    public String getVIDEO_TITLE() {
        return VIDEO_TITLE;
    }

    public void setVIDEO_TITLE(String VIDEO_TITLE) {
        this.VIDEO_TITLE = VIDEO_TITLE;
    }

    public static BroadcastDataBean ConverData(LocalVideoBean localVideoBean) {
        BroadcastDataBean broadcastDataBean = new BroadcastDataBean();
        broadcastDataBean.setID(localVideoBean.getID());
        broadcastDataBean.setSTATUS(localVideoBean.getSTATUS());
        broadcastDataBean.setPersent(localVideoBean.getPersent());
        broadcastDataBean.setCOLUMN_URL(localVideoBean.getCOLUMN_URL());
        broadcastDataBean.setCOUNTRY(localVideoBean.getCOUNTRY());
        broadcastDataBean.setLocalPath(localVideoBean.getLocalPath());
        broadcastDataBean.setSUFFIX(localVideoBean.getSUFFIX());
        broadcastDataBean.setTimeLineCount(localVideoBean.getTimeLineCount());
        broadcastDataBean.setTimeLineImageIype(localVideoBean.getTimeLineImageIype());
        broadcastDataBean.setTimeLineUrl(localVideoBean.getTimeLineUrl());
        broadcastDataBean.setVIDEO_ID(localVideoBean.getVIDEO_ID());
        broadcastDataBean.setVIDEO_PHOTO(localVideoBean.getVIDEO_PHOTO());
        broadcastDataBean.setVIDEO_TITLE(localVideoBean.getVIDEO_TITLE());
        return broadcastDataBean;
    }
}
