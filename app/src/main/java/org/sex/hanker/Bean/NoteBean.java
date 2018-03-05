package org.sex.hanker.Bean;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/5.
 */
public class NoteBean {

    /**
     * id : 47
     * title : 老婆變別人的炮友
     * contentpath : /pp_note/wife
     * author :
     * createtime : 1514256002932
     * parentid : 2
     * episode : 1
     * notepath : /b45caabc-530b-4f7c-a018-211daae5fea2/1/老婆變別人的炮友.txt
     * ispublic : true
     * content : 我們是一對三十多歲的夫婦，未有小孩，思想開放，想起第一次暴露經過，現在還十分興奮。
     * click : 0
     * alt : 乳房|乳头|交换|人妻|做爱|健身房|偷看|偷窥|儿子|内裤|公司|勃起|口交|叫床|同事|呻吟|奶子|奶头|妻子|妇人|子宫|学生|射精|小穴|少妇|尿道|屁眼|屁股|厕所|性交|性器|性器官|性爱|性感|性慾|爱抚|手淫|打手枪|按摩|按摩师|换伴|换妻|教师|日本|暴露|模特儿|泳衣|派对|浴室|淫叫|淫妇|淫水|淫荡|生殖器|发情|发洩|第一次|精液|丝袜|美国|老公|老婆|老师|肉棒|肛交|肛门|胸罩|胸部|轮姦|迷你裙|邻居
     * notes : null
     */

    private int id;
    private String title;
    private String contentpath;
    private String author;
    private long createtime;
    private int parentid;
    private int episode;
    private String notepath;
    private boolean ispublic;
    private String content;
    private int click;
    private String alt;
    private String notes;

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

    public String getNotepath() {
        return notepath;
    }

    public void setNotepath(String notepath) {
        this.notepath = notepath;
    }

    public boolean isIspublic() {
        return ispublic;
    }

    public void setIspublic(boolean ispublic) {
        this.ispublic = ispublic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getClick() {
        return click;
    }

    public void setClick(int click) {
        this.click = click;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public static NoteBean AnalysisData(JSONObject jsonObject)
    {
        NoteBean noteBean=new NoteBean();
        noteBean.setParentid(jsonObject.optInt("parentid",1));
        noteBean.setId(jsonObject.optInt("id",1));
        noteBean.setContentpath(jsonObject.optString("contentpath",""));
        noteBean.setContent(jsonObject.optString("content",""));
        noteBean.setTitle(jsonObject.optString("title",""));
        noteBean.setAuthor(jsonObject.optString("author",""));
        noteBean.setCreatetime(jsonObject.optLong("createtime",0));
        noteBean.setClick(jsonObject.optInt("click",1));
        noteBean.setParentid(jsonObject.optInt("parentid",1));
        noteBean.setEpisode(jsonObject.optInt("episode",1));
        noteBean.setNotepath(jsonObject.optString("notepath",""));
        noteBean.setIspublic(jsonObject.optBoolean("ispublic",true));
        noteBean.setAlt(jsonObject.optString("alt",""));
        noteBean.setNotes(jsonObject.optString("notes",""));
        return noteBean;
    }
}
