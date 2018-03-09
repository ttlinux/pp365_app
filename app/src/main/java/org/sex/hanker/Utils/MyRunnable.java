package org.sex.hanker.Utils;


public class MyRunnable implements Runnable{

	String temp_tag;
	  int index;
	long createtime;
	 String pageUrl;
	 String requesttag;
	 HttpGet.OnReceiveDataListener listener;
	
	 public MyRunnable(int index)
	 {
		 this.index=index;
	 }
	 
	 public MyRunnable()
	 {
		
	 }
	 
	@Override
	public void run() {
		// TODO Auto-generated method stub
//		LogUtil.log("index--->"+index);	
	}

	public String getTag() {
		return temp_tag;
	}

	public void setTag(String tag) {
		this.temp_tag = tag;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}


	public HttpGet.OnReceiveDataListener getListener() {
		return listener;
	}

	public void setListener(HttpGet.OnReceiveDataListener listener) {
		this.listener = listener;
	}

	public String getRequesttag() {
		return requesttag;
	}

	public void setRequesttag(String requesttag) {
		this.requesttag = requesttag;
	}

	
}
