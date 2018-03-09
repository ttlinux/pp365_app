package org.sex.hanker.Utils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpGet {
	/**
	 * 该方法可以很好的解决中文乱码问题，同样采用java自带的HttpURLConnection类，方便
	 */
	static ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors
			.newScheduledThreadPool(5);

//	static ScheduledThreadPoolExecutor timer = (ScheduledThreadPoolExecutor) Executors
//			.newScheduledThreadPool(1);

	static int Threadcount = 0;
	static int Threadtag = 0;
	// static HashMap<Integer, Myfutrue> runables=new HashMap<>();
	static boolean isinit = false;
	static int timeout = 20000;

	public static void request(final String m_pageUrl, final String m_tag,
			final OnReceiveDataListener m_listener) {
		// Proxy proxy = new Proxy(Proxy.Type.HTTP, new
		// InetSocketAddress("127.0.0.1", 8580)); //设置代理服务器


		MyRunnable runable = new MyRunnable(Threadtag) {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				if(m_pageUrl.toLowerCase().startsWith("https"))
					ProcessHttpSConnect(m_pageUrl, null,m_tag, m_listener);
				else
					ProcessHttpconnect(m_pageUrl,null, m_tag, m_listener);
			}
		};

		// runable.setIndex(Threadtag);

		executor.submit(runable);
		// f.setCreatetime(-1);
		// f.setIndex(Threadtag);
		// f.setListener(m_listener);
		// f.setPageUrl(m_pageUrl);
		// f.setRequesttag(m_tag);
		// f.setFuture(executor.submit(runable));
		// Threadtag++;

		// Watcher();//监视器
	}
	public static void requestWithUnicode(final String m_pageUrl,final String Unicode, final String m_tag,
			final OnReceiveDataListener m_listener) {
		
		MyRunnable runable = new MyRunnable(Threadtag) {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				if(m_pageUrl.toLowerCase().startsWith("https"))
					ProcessHttpSConnect(m_pageUrl,Unicode,m_tag, m_listener);
				else
					ProcessHttpconnect(m_pageUrl,Unicode,m_tag, m_listener);
			}
		};
		
		executor.submit(runable);
	}
	
	

	public static boolean isCompelete() {
		return executor.getActiveCount() == 0;
	}

	// private static boolean Watcher()
	// {
	// if(isinit==false)
	// {
	//
	// timer.scheduleAtFixedRate(new MyRunnable() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// LogTools.e("HttpGet","心跳在战争中");
	// Iterator urer= runables.values().iterator();
	// StringBuilder sb=new StringBuilder();
	// ArrayList<Integer> tags=new ArrayList<Integer>();
	// while (urer.hasNext()) {
	// Myfutrue myrunable=(Myfutrue)urer.next();
	// long createtime=myrunable.getCreatetime();
	// if(createtime<0)continue;
	// long time=System.currentTimeMillis()- createtime;
	// sb.append(time);
	// sb.append("-");
	// if( time>timeout)
	// {
	// tags.add(myrunable.getIndex());
	// }
	// }
	// if(tags.size()>0)
	// {
	// LogTools.e("HttpGet","超时线程数"+tags.size());
	// for (int i = 0; i < tags.size(); i++) {
	// Myfutrue myrunable=runables.get(tags.get(i));
	// if(myrunable==null)continue;
	// String pageUrl=myrunable.getPageUrl();
	// String requesttag=myrunable.getRequesttag();
	// OnReceiveDataListener listener=myrunable.getListener();
	// myrunable.getFuture().cancel(true);
	// runables.remove(myrunable.getIndex());
	// request(pageUrl,requesttag,listener);
	// }
	// }
	// // LogTools.e("HttpGet",sb.toString());
	// }
	// }, 0, 1, TimeUnit.SECONDS);
	// isinit=true;
	// }
	// return isinit;
	// }

	public static void ProcessHttpSConnect(final String pageUrl,final String Unicode,
			final String tag, final OnReceiveDataListener listener) {
		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
					new java.security.SecureRandom());

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		InputStream in = null;
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String MULTIPART_FROM_DATA = "text/html";
		try {
			URL url = new URL(pageUrl);
			System.out.println("Https请求的地址" + url.toString());
			// HttpURLConnection conn = (HttpURLConnection)
			// url.openConnection(proxy);

			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);
//			conn.setFollowRedirects(true);
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			conn.setRequestMethod("POST");
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestProperty("Cookie", "dsdsd");
			conn.setRequestProperty("Referer", pageUrl);
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
			// conn.addRequestProperty("connection", "keep-alive");
			if(Unicode!=null)
				conn.addRequestProperty("Content-Type", "text/html; charset="+Unicode);
			else
				conn.addRequestProperty("Content-Type", "text/html; charset=UTF-8");
			in = conn.getInputStream();
			if(Unicode!=null)
				br = new BufferedReader(new InputStreamReader(in, Unicode));
			else
				br = new BufferedReader(new InputStreamReader(in, "utf-8"));
			String line = null;
			if (!br.ready()) {
				LogTools.e("HttpGet","BufferedReader准备中");
			}
			while (!br.ready()) {

			}
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\r\n");
			}
			br.close();
			in.close();
			Threadcount = executor.getActiveCount();
			if (listener != null)
				listener.OnSuccess(sb.toString(), tag, Threadcount,
						executor.getTaskCount());
		} catch (MalformedURLException e) {
			LogTools.e("HttpGet","目标地址 "+pageUrl);
			LogTools.e("HttpGet","url格式不规范:" + e.toString());
			if (br != null) {
				try {
					br.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (listener != null)
				listener.OnFail(e, tag, Threadcount, executor.getTaskCount());
		} catch (IOException e) {
			LogTools.e("HttpGet","IO操作错误：" + e.toString());
			if (br != null) {
				try {
					br.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (listener != null)
				listener.OnFail(e, tag, Threadcount, executor.getTaskCount());
		}

	}

	public static void ProcessHttpconnect(final String pageUrl,final String Unicode,
			final String tag, final OnReceiveDataListener listener) {
		final StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		InputStream in = null;
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String MULTIPART_FROM_DATA = "text/html";
		try {
			URL url = new URL(pageUrl);
			System.out.println("请求的地址" + url.toString());
			// HttpURLConnection conn = (HttpURLConnection)
			// url.openConnection(proxy);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestProperty("Cookie", System.currentTimeMillis()+"");
			conn.setRequestProperty("Referer", pageUrl);
			conn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; CIBA)");
			// conn.addRequestProperty("connection", "keep-alive");
			if(Unicode!=null)
				conn.addRequestProperty("Content-Type", "text/html; charset="+Unicode);
			else
				conn.addRequestProperty("Content-Type", "text/html; charset=UTF-8");
		
			in = conn.getInputStream();
			if(Unicode!=null)
				br = new BufferedReader(new InputStreamReader(in, Unicode));
			else
				br = new BufferedReader(new InputStreamReader(in, "utf-8"));
			String line = null;
			if (!br.ready()) {
				LogTools.e("HttpGet","BufferedReader准备中");
			}
			while (!br.ready()) {

			}
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\r\n");
			}
			Threadcount = executor.getActiveCount();
		} catch (MalformedURLException e) {
			LogTools.e("HttpGet","目标地址 "+pageUrl);
			LogTools.e("HttpGet","url格式不规范:" + e.toString());
			if (br != null) {
				try {
					br.close();
					if (in != null) 
					in.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (listener != null)
				listener.OnFail(e, tag, Threadcount, executor.getTaskCount());
		} catch (IOException e) {
			LogTools.e("HttpGet","IO操作错误：" + e.toString());
			if (br != null) {
				try {
					br.close();
					if (in != null) 
						in.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (listener != null)
				listener.OnFail(e, tag, Threadcount, executor.getTaskCount());
		}
		finally
		{
			try {
				if (br != null)
					br.close();
				if (in != null) 
					in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (listener != null)
			{
					listener.OnSuccess(sb.toString(), tag, Threadcount,
						executor.getTaskCount());
			}
		}
	}

	// public synchronized static void ProcessHttpClient(final String pageUrl,
	// final String tag, final OnReceiveDataListener listener) {
	// // 定义输入输出流
	// InputStream input = null;
	// // 得到 post 方法
	// GetMethod getMethod = new GetMethod(pageUrl);
	// // 执行，返回状态码
	// HttpClient httpClient = new HttpClient();
	//
	// // 针对状态码进行处理
	// // 简单起见，只处理返回值为 200 的状态码
	//
	// try {
	// int statusCode = httpClient.executeMethod(getMethod);
	// if (statusCode == HttpStatus.SC_OK) {
	// input = getMethod.getResponseBodyAsStream();
	//
	// }
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	public static void Stop() {
		executor.shutdownNow();
	}

	public interface OnReceiveDataListener {

		public void OnSuccess(String data, String tag, int Threadcount,
							  long TaskCount);

		public void OnFail(Exception ex, String tag, int Threadcount,
						   long TaskCount);
	}

	private static class TrustAnyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}
}
