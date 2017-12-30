package org.sex.hanker.Bean;

import org.json.JSONObject;

public class user {
	
	private String sessionid;
	
    private Integer id;

    private String usename;

    private Integer level;

    private Long createtime;

    private Long vipapplytime;

    private Long viplimittime;

    private String lasttimeip;

    private Boolean handpasswordstate;

    private String userpic;

    private String phone;

    private String personaldata;

    private Integer sexual;

    private Boolean datastate;

    private String weixin;

    private String qq;

    private String location;

    private String tags;

    
    
    public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsename() {
        return usename;
    }

    public void setUsename(String usename) {
        this.usename = usename == null ? null : usename.trim();
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }

    public Long getVipapplytime() {
        return vipapplytime;
    }

    public void setVipapplytime(Long vipapplytime) {
        this.vipapplytime = vipapplytime;
    }

    public Long getViplimittime() {
        return viplimittime;
    }

    public void setViplimittime(Long viplimittime) {
        this.viplimittime = viplimittime;
    }

    public String getLasttimeip() {
        return lasttimeip;
    }

    public void setLasttimeip(String lasttimeip) {
        this.lasttimeip = lasttimeip == null ? null : lasttimeip.trim();
    }

    public Boolean getHandpasswordstate() {
        return handpasswordstate;
    }

    public void setHandpasswordstate(Boolean handpasswordstate) {
        this.handpasswordstate = handpasswordstate;
    }

    public String getUserpic() {
        return userpic;
    }

    public void setUserpic(String userpic) {
        this.userpic = userpic == null ? null : userpic.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getPersonaldata() {
        return personaldata;
    }

    public void setPersonaldata(String personaldata) {
        this.personaldata = personaldata == null ? null : personaldata.trim();
    }

    public Integer getSexual() {
        return sexual;
    }

    public void setSexual(Integer sexual) {
        this.sexual = sexual;
    }

    public Boolean getDatastate() {
        return datastate;
    }

    public void setDatastate(Boolean datastate) {
        this.datastate = datastate;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin == null ? null : weixin.trim();
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq == null ? null : qq.trim();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location == null ? null : location.trim();
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags == null ? null : tags.trim();
    }

    public static  user analysis(JSONObject jsonObject)
    {
        user user=new user();
        user.setCreatetime(jsonObject.optLong("createtime"));
        user.setDatastate(jsonObject.optBoolean("datastate"));
        user.setHandpasswordstate(jsonObject.optBoolean("handpasswordstate"));
        user.setLasttimeip(jsonObject.optString("lasttimeip"));
        user.setLevel(jsonObject.optInt("level"));
        user.setLocation(jsonObject.optString("location"));
        user.setPersonaldata(jsonObject.optString("personaldata"));
        user.setUserpic(jsonObject.optString("userpic"));
        user.setSexual(jsonObject.optInt("sexual"));
        user.setSessionid(jsonObject.optString("sessionid"));
        user.setWeixin(jsonObject.optString("weixin"));
        user.setQq(jsonObject.optString("qq"));
        user.setUsename(jsonObject.optString("usename"));
        user.setTags(jsonObject.optString("tags"));
        return user;

    }
}