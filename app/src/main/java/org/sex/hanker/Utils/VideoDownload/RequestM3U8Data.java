package org.sex.hanker.Utils.VideoDownload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;








import com.google.gson.Gson;
import com.iheartradio.m3u8.Encoding;
import com.iheartradio.m3u8.Format;
import com.iheartradio.m3u8.ParseException;
import com.iheartradio.m3u8.PlaylistException;
import com.iheartradio.m3u8.PlaylistParser;
import com.iheartradio.m3u8.data.Playlist;
import com.iheartradio.m3u8.data.PlaylistData;
import com.iheartradio.m3u8.data.TrackData;

public class RequestM3U8Data {

	private final static int TimeOut = 20000;
	private static final String testurl="http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
	private static final String testurl2="https://cdn.812zy.com/20180722/zQglH9cP/index.m3u8";
	 int count=0;
	int Error_Test=0;
	String ErrorMessage;
	private  ArrayList<M3U8URLbean> m3u8bean=new ArrayList<>();
	private  String Downloadurl="";
	private static RequestM3U8Data requestM3U8Data;
	public static RequestM3U8Data getInstance()
	{
		if(requestM3U8Data==null)
		{
			requestM3U8Data=new RequestM3U8Data();
		}
		return requestM3U8Data;
	}

	public ArrayList<M3U8URLbean> Excute(String Downloadurl)
	{
		this.Downloadurl=Downloadurl;
		boolean status=false;
		while (!status && Error_Test<3)
		{
			status=Handler(Downloadurl);
			Error_Test++;
		}
		return m3u8bean;
	}


	private   boolean Handler(final String url) {
		Playlist playlist=null;
		if(url.startsWith("https"))
		{
			playlist=SyncGetM3U8_HTTPS(url);
		}
		else
		{
			playlist=SyncGetM3U8_HTTP(url);
		}
		if(playlist!=null)
		{
			if(playlist.hasMasterPlaylist())
			{
				 List<PlaylistData> plays=playlist.getMasterPlaylist().getPlaylists();
				for (int i = 0; i < plays.size(); i++) {
					String uri=plays.get(i).getUri();
					if(uri!=null && uri.length()>0)
					{
						String rurl=AnalysisURL(url,uri);
						if(rurl!=null && rurl.length()>0)
						{
							Handler(rurl);
						}
					}
				}
			}
			else
			{
				if(playlist.hasMediaPlaylist())
				{
					M3U8URLbean bean=new M3U8URLbean();
					bean.setIndex(count++);
					bean.setMainM3u8Url(url);
					ArrayList<String> urls=new ArrayList<>();
					for (int i = 0; i < playlist.getMediaPlaylist().getTracks().size(); i++) {
						TrackData trackdata=playlist.getMediaPlaylist().getTracks().get(i);
						urls.add(AnalysisURL(url,trackdata.getUri()));
					}
					bean.setUrls(urls);
					m3u8bean.add(bean);
				}
			}
			return true;
		}
		else
		{
			m3u8bean.clear();
			//错误处理
			return false;
		}
	}
	
	private static String  AnalysisURL(String originalurl,String masterurl)
	{
		String tempurl=masterurl;
		if(!originalurl.startsWith("http"))
		{
			return null;
		}
		if(masterurl.startsWith("/"))
		{
			int end=originalurl.indexOf("/", originalurl.indexOf("//")+2);
			String header=originalurl.substring(0,end);
			tempurl=header+masterurl;
		}
		else
		{
			if(masterurl.startsWith("http"))
			{
				return tempurl;
			}
			else
			{
				String header=originalurl.substring(0,originalurl.lastIndexOf("/")+1);
				tempurl=header+masterurl;
			}
		}
		return tempurl;
	}

	private  Playlist SyncGetM3U8_HTTP(String Url) {
		Print("请求地址"+Url);
		InputStream in = null;
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String MULTIPART_FROM_DATA = "text/html";
		try {
			URL url = new URL(Url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TimeOut);
			conn.setReadTimeout(TimeOut);
			conn.setRequestMethod("GET");
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestProperty("Cookie", System.currentTimeMillis() + "");
			conn.setRequestProperty("Referer", Url);
			conn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; CIBA)");
			// conn.addRequestProperty("connection", "keep-alive");
			conn.addRequestProperty("Content-Type", "text/html; charset=UTF-8");


			switch (conn.getResponseCode())
			{
				case 502:
					ErrorMessage="该地址的文件已失效";
					break;
				default:
					in = conn.getInputStream();
					PlaylistParser parser = new PlaylistParser(in, Format.EXT_M3U,
							Encoding.UTF_8);
					return parser.parse();
			}


		} catch (MalformedURLException e) {
			Print("目标地址 " + Url);
			Print("url格式不规范:" + e.toString());
		} catch (IOException e) {
			Print("IO操作错误：" + e.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PlaylistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeInputstream(in);
		}
		return null;
	}

	private  Playlist SyncGetM3U8_HTTPS(String Url) {
		Print("请求地址"+Url);
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

		InputStream in = null;
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String MULTIPART_FROM_DATA = "text/html";
		try {
			URL url = new URL(Url);
			System.out.println("Https请求的地址" + url.toString());

			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setConnectTimeout(TimeOut);
			conn.setReadTimeout(TimeOut);
			// conn.setFollowRedirects(true);
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			conn.setRequestMethod("GET");
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestProperty("Cookie", "dsdsd");
			conn.setRequestProperty("Referer", Url);
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:61.0) Gecko/20100101 Firefox/61.0");
			// conn.addRequestProperty("connection", "keep-alive");
			conn.addRequestProperty("Content-Type", "text/html; charset=UTF-8");
			switch (conn.getResponseCode())
			{
				case 502:
					ErrorMessage="该地址的文件已失效";
					break;
				default:
					in = conn.getInputStream();
					PlaylistParser parser = new PlaylistParser(in, Format.EXT_M3U,
							Encoding.UTF_8);
					return parser.parse();
			}
			
		} catch (MalformedURLException e) {
			Print("目标地址 " + Url);
			Print("url格式不规范:" + e.toString());
		} catch (IOException e) {
			Print("IO操作错误：" + e.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PlaylistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			closeInputstream(in);
		}
		return null;

	}

	private static void Print(String text) {
		System.out.println(text);
	}
	
	private static void closeInputstream(InputStream in)
	{
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

	public interface OnAnalysisDataListener {
		public void OnAnalysisData();
	}

	public static class M3U8URLbean
	{
		String mainM3u8Url;
		int index;
		ArrayList<String> urls;
		public String getMainM3u8Url() {
			return mainM3u8Url;
		}
		public void setMainM3u8Url(String mainM3u8Url) {
			this.mainM3u8Url = mainM3u8Url;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public ArrayList<String> getUrls() {
			return urls;
		}
		public void setUrls(ArrayList<String> urls) {
			this.urls = urls;
		}
		
	}
}
