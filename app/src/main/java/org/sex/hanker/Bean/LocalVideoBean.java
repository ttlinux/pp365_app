package org.sex.hanker.Bean;

import org.sex.hanker.ProxyURL.IOUtil;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.VideoDownload.RequestM3U8Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/7/31.
 */
public class LocalVideoBean implements Serializable{

    private String COLUMN_URL,VIDEO_ID,COUNTRY,SUFFIX,LocalPath,VIDEO_TITLE,VIDEO_PHOTO,TimeLineUrl;
    private int ID,STATUS,TimeLineImageIype,TimeLineCount;
    private int Persent;

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

    public String getVIDEO_PHOTO() {
        return VIDEO_PHOTO;
    }

    public void setVIDEO_PHOTO(String VIDEO_PHOTO) {
        this.VIDEO_PHOTO = VIDEO_PHOTO;
    }

    private ArrayList<M3U8_ITEM> m3U8_items;

    public ArrayList<M3U8_ITEM> getM3U8_items() {
        return m3U8_items;
    }

    public String getVIDEO_TITLE() {
        return VIDEO_TITLE;
    }

    public void setVIDEO_TITLE(String VIDEO_TITLE) {
        this.VIDEO_TITLE = VIDEO_TITLE;
    }

    public void setM3U8_items(ArrayList<M3U8_ITEM> m3U8_items) {
        this.m3U8_items = m3U8_items;
    }

    public int getPersent() {
        return Persent;
    }

    public void setPersent(int persent) {
        Persent = persent;
    }

    public String getCOLUMN_URL() {
        return COLUMN_URL;
    }

    public void setCOLUMN_URL(String COLUMN_URL) {
        this.COLUMN_URL = COLUMN_URL;
    }

    public String getVIDEO_ID() {
        return VIDEO_ID;
    }

    public void setVIDEO_ID(String VIDEO_ID) {
        this.VIDEO_ID = VIDEO_ID;
    }

    public String getCOUNTRY() {
        return COUNTRY;
    }

    public void setCOUNTRY(String COUNTRY) {
        this.COUNTRY = COUNTRY;
    }

    public String getSUFFIX() {
        return SUFFIX;
    }

    public void setSUFFIX(String SUFFIX) {
        this.SUFFIX = SUFFIX;
    }

    public String getLocalPath() {
        return LocalPath;
    }

    public void setLocalPath(String localPath) {
        LocalPath = localPath;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(int STATUS) {
        this.STATUS = STATUS;
    }

    public static class M3U8_ITEM
    {
        private int ID,STATUS,FILE_INDEX,Parent_ID;
        String M3U8_URL,TS_URL,LocalPath,SUFFIX;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getSUFFIX() {
            return SUFFIX;
        }

        public void setSUFFIX(String SUFFIX) {
            this.SUFFIX = SUFFIX;
        }

        public int getFILE_INDEX() {
            return FILE_INDEX;
        }

        public void setFILE_INDEX(int FILE_INDEX) {
            this.FILE_INDEX = FILE_INDEX;
        }

        public String getLocalPath() {
            return LocalPath;
        }

        public void setLocalPath(String localPath) {
            LocalPath = localPath;
        }

        public String getM3U8_URL() {
            return M3U8_URL;
        }

        public void setM3U8_URL(String m3U8_URL) {
            M3U8_URL = m3U8_URL;
        }

        public int getParent_ID() {
            return Parent_ID;
        }

        public void setParent_ID(int parent_ID) {
            Parent_ID = parent_ID;
        }

        public int getSTATUS() {
            return STATUS;
        }

        public void setSTATUS(int STATUS) {
            this.STATUS = STATUS;
        }

        public String getTS_URL() {
            return TS_URL;
        }

        public void setTS_URL(String TS_URL) {
            this.TS_URL = TS_URL;
        }

        public static ArrayList<M3U8_ITEM> swip(ArrayList<RequestM3U8Data.M3U8URLbean> beans,int parentid,String Localrootpath)
        {
            ArrayList<M3U8_ITEM> m3u8items=new ArrayList<>();
            int index=0;
            for (int i = 0; i < beans.size(); i++) {
                RequestM3U8Data.M3U8URLbean mbean=beans.get(i);
                for (int j = 0; j <mbean.getUrls().size() ; j++) {
                    String url=mbean.getUrls().get(j);
                    M3U8_ITEM mi=new M3U8_ITEM();
                    mi.setSUFFIX(IOUtil.getSuffixName(url));
                    mi.setTS_URL(url);
                    mi.setSTATUS(0);
                    mi.setParent_ID(parentid);
                    mi.setM3U8_URL(mbean.getMainM3u8Url());
                    mi.setFILE_INDEX(index++);
                    mi.setLocalPath(Localrootpath + "/" + System.nanoTime() + "." + mi.getSUFFIX() + "." + BundleTag.Dsuffix);
                    m3u8items.add(mi);
                }
            }
            return m3u8items;
        }
    }
}
