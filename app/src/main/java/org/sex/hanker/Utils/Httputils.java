package org.sex.hanker.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/3/29.
 */
public class Httputils {

    private static MyAsyncHttpClient client;
    private static final int timeout=5000;
    public static boolean isShow = false;//
    public static String AndroidApkPath;
    public static String AndroidUpdate="";
    public static String BaseUrl="http://192.168.1.108:8080/pp365/";//http://www.xokong.com
    public static String WSBaseurl="ws://www.xokong.com/pp365/websocket?";//ws://www.xokong.com/pp365/websocket?
    public static String ImgBaseUrl="http://www.xokong.com/pp365";
    public static String Home="/interface/Home";
    //小说
    public static String notemenu="/interface/notemenu";
    public static String note="/interface/note";
    public static String noteSearch="/interface/noteSearch";
    public static String notedetail="/interface/notedetail";

    //视频
    public static String Videomenu="/interface/Videomenu";
    public static String VideoDetail="/interface/VideoDetail";
    public static String Video="/interface/Video";
    public static String VideoSearch="/interface/VideoSearch";
    //图片
    public static String picturemenu="/interface/picturemenu";
    public static String picture="/interface/picture";
    public static String picturedetail="/interface/picturedetail";
    //注册登陆
    public static String login="/userlogin";
    public static String register="/register";

    //聊天记录
    public static String MessageRecord="/interface/MessageRecord";

    public interface BalanceListener
    {
        public void OnRecevicedata(String balance);
        public void Onfail(String str);
    }

    /**更新配置请求页面*/
    public static String httpurlAndroidUpdate(){
//        if(isShow){
//            AndroidUpdate="http://up.djr158.com/App/config.json";
//        }
//        else{
//            AndroidUpdate = "http://update.gdgccj.com/app/config.json";
//        }
        AndroidUpdate = "commons/getMobileVersion";
        return AndroidUpdate;
    }

    //配置apk下载地址
    public static String GetApkDownloadPath()
    {
//        if(isShow)
//        {
//            AndroidApkPath="http://jqd-download.oss-cn-hangzhou.aliyuncs.com/install/robot_zz.apk";
//        }
//        else
//        {
//            AndroidApkPath="http://jqd-download.oss-cn-hangzhou.aliyuncs.com/install/robot_gc.apk";
//        }

        return AndroidApkPath;
    }
    public static  void Post(String url,RequestParams requestParams,MyJsonHttpResponseHandler jsonHttpResponseHandler)
    {
        client=new MyAsyncHttpClient(jsonHttpResponseHandler.getContext());
        client.setTimeout(timeout);
        client.post(url,requestParams,jsonHttpResponseHandler);
    }
    public static  void Post(String url,RequestParams requestParams,JsonHttpResponseHandler jsonHttpResponseHandler)
    {
        LogTools.e("URLPost",  url + new Gson().toJson(requestParams));
        AsyncHttpClient client=new AsyncHttpClient();
        client.setTimeout(timeout);
        client.post(url,requestParams,jsonHttpResponseHandler);
    }
    public static String[] convertStrToArray(String str){
        String regEx = "[`~!@#$%^&*()\\-+={}':;,\\[ \\].<>/?￥%…（）_+|【】‘；：”“’。，、？\\= \\+ ] ";
//        LogTools.e("Stringcode",str);
        String aaa=str.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\\"", "").replaceAll("\\+", ",").replaceAll("=", ",");
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(aaa);
        String aa=m.replaceAll( ",").trim();
//        LogTools.e("Stringcode",aa);
        String[] strArray = null;
        strArray = aa.split(",");
//        LogTools.e("Stringcodeee", strArray.toString());
        //拆分字符为"," ,然后把结果交给数组strArray
        return strArray;
    }



    public static boolean useLoop(int[] arr, int targetValue) {
        for (int s : arr) {
            if (s==targetValue) {
                return true;
            }
        }
        return false;
    }

    public static void Get(String url,RequestParams requestParams,MyJsonHttpResponseHandler jsonHttpResponseHandler)
    {
        client=new MyAsyncHttpClient(jsonHttpResponseHandler.getContext());
        client.setTimeout(timeout);
        LogTools.e("url", url);
        client.get(url, requestParams, jsonHttpResponseHandler);
    }

    public static void Get(String url,RequestParams requestParams,JsonHttpResponseHandler jsonHttpResponseHandler)
    {
        AsyncHttpClient client=new AsyncHttpClient();
        client.setTimeout(timeout);
        LogTools.e("url", url+ new Gson().toJson(requestParams));
        client.get(url, requestParams, jsonHttpResponseHandler);
    }

    public static void PostWithBaseUrl(String url,RequestParams requestParams,MyJsonHttpResponseHandler jsonHttpResponseHandler) {

        client=new MyAsyncHttpClient(jsonHttpResponseHandler.getContext());
        client.setTimeout(timeout);
        LogTools.e("URL", BaseUrl + url  + new Gson().toJson(requestParams));
        client.post(BaseUrl + url, requestParams, jsonHttpResponseHandler);
    }
    public static void PostWithBaseUrl(String url,RequestParams requestParams,MyJsonHttpResponseHandler2 jsonHttpResponseHandler) {
        LogTools.e("URL", BaseUrl + url  + new Gson().toJson(requestParams));
        client=new MyAsyncHttpClient(jsonHttpResponseHandler.getContext());
        client.setTimeout(timeout);
        client.post(BaseUrl + url, requestParams, jsonHttpResponseHandler);
    }
//    public static void PostWithBaseUrld(String url,RequestParams requestParams,MyJsonHttpResponseHandler jsonHttpResponseHandler) {
//        client=new MyAsyncHttpClient(jsonHttpResponseHandler.getContext());
//        client.setTimeout(timeout);
//        LogTools.e("getURL", "http://192.168.0.238:8083/Inter/" + url + new Gson().toJson(requestParams));
//        client.post("http://192.168.0.238:8083/Inter/" + url, requestParams, jsonHttpResponseHandler);
//    }
//    public static void GetWithBaseUrld(String url,RequestParams requestParams,MyJsonHttpResponseHandler jsonHttpResponseHandler)
//    {
//        client=new MyAsyncHttpClient(jsonHttpResponseHandler.getContext());
//        client.setTimeout(timeout);
//        LogTools.e("getURL", "http://192.168.0.238:8083/Inter/" + url);
//        client.get("http://192.168.0.238:8083/Inter/" + url, requestParams, jsonHttpResponseHandler);
//    }
public static void GETWithoutBASEURL(String url, RequestParams params,
                                     MyJsonHttpResponseHandler responseHandler) {
    AsyncHttpClient client1 = new AsyncHttpClient();
    client1.setTimeout(3000);
    client1.get(url, params, responseHandler);
    LogTools.e("getAbsoluteUrl(url)",""+url+params);
//		saveFile(""+BASE+url+params);
}

    public static void getWithoutBASEurl(String url, RequestParams params,
                                         MyJsonHttpResponseHandler responseHandler) {
        AsyncHttpClient client1 = new AsyncHttpClient();
        client1.setTimeout(3000);
        client1.get(url, params, responseHandler);
        LogTools.e("getAbsoluteUrl(url)", "" + url + params);
//		saveFile(""+BASE+url+params);
    }


    public static void GetWithBaseUrl(String url,RequestParams requestParams,MyJsonHttpResponseHandler jsonHttpResponseHandler)
    {
        client=new MyAsyncHttpClient(jsonHttpResponseHandler.getContext());
        client.setTimeout(timeout);
        client.get(BaseUrl + url, requestParams, jsonHttpResponseHandler);
    }

    public static void PutWithBaseUrl(String url,RequestParams requestParams,MyJsonHttpResponseHandler jsonHttpResponseHandler)
    {
        client=new MyAsyncHttpClient(jsonHttpResponseHandler.getContext());
        client.setTimeout(timeout);
        client.put(BaseUrl + url, requestParams, jsonHttpResponseHandler);
    }
    public static void DeleteWithBaseUrl(Context context,String url,MyJsonHttpResponseHandler jsonHttpResponseHandler)
    {
        client=new MyAsyncHttpClient(jsonHttpResponseHandler.getContext());
        client.setTimeout(timeout);
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");

        client.delete(context, url, jsonHttpResponseHandler);
//        client.delete(BaseUrl + url, jsonHttpResponseHandler);
    }


//    /**
//     * Delete
//     * @param url 发送请求的URL
//     * @return 服务器响应字符串
//     * @throws Exception
//     */
//    public static String deleteRequest(final String url,final int id)
//            throws Exception
//    {
//
//        FutureTask<String> task = new FutureTask<String>(
//                new Callable<String>()
//                {
//                    @Override
//                    public String call() throws Exception
//                    {
//                        HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
//// json 处理
//                        httpDelete.setHeader("Content-Type", "application/x-www-form-urlencoded;");//or addHeader();
//                        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
//
//                        NameValuePair pair1 = new BasicNameValuePair("userName", "duanaifei");
//                        NameValuePair pair2 = new BasicNameValuePair("id", id+"");
//
//                        pairs.add(pair1);
//                        pairs.add(pair2);
//                        httpDelete.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
//
//                        httpDelete.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
//
//                        httpDelete.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
//                        // 发送GET请求
//                        httpClient= new DefaultHttpClient();
//                        HttpResponse httpResponse = httpClient.execute(httpDelete);
//                        // 如果服务器成功地返回响应
//                        LogTools.e("codeccccc", httpResponse.getStatusLine()
//                                .getStatusCode()+ " " + httpResponse.getEntity().getContentType() + " " + httpResponse.getStatusLine().getReasonPhrase());
//
//                        LogTools.e("success", convertStreamToString(httpResponse.getEntity().getContent()));
//
//                        if (httpResponse.getStatusLine()
//                                .getStatusCode() == 204)
//                        {
//                            // 获取服务器响应字符串
//                            LogTools.e("success","success");
//                            return "success";
//                        }
//                        return null;
//                    }
//                });
//        new Thread(task).start();
//        return task.get();
//    }

//    public static class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
//
//        public static final String METHOD_NAME = "DELETE";
//        public String getMethod() { return METHOD_NAME; }
//        public HttpDeleteWithBody(final String uri) {
//            super();
//            setURI(URI.create(uri));
//        }
//        public HttpDeleteWithBody(final URI uri) {
//            super();
//            setURI(uri);
//        }
//        public HttpDeleteWithBody() { super(); }
//
//    }


    public static String Limit2(double data){
        DecimalFormat df = new DecimalFormat("#0.00");
        return  df.format(data);
    }

    public static String convertStreamToString(InputStream is) {
        /*
          * To convert the InputStream to String we use the BufferedReader.readLine()
          * method. We iterate until the BufferedReader return null which means
          * there's no more data to read. Each line will appended to a StringBuilder
          * and returned as String.
          */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
    /***年月日 时分秒***/
    public static String getStringtime() {
        SimpleDateFormat df  =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();
        long diff = 0;
        String d1=df.format(date);
        try {
            diff = df.parse(d1).getTime() ;
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return d1;

    }
    /***年月日时分秒***/
    @SuppressLint("SimpleDateFormat")
    public static String getStringToDate(long  diff) {
        String day=null;
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        String diffDaystring="天";
        String diffHourstirng="小时";
        String diffMinutestring="分";
        String diffSecondstring="秒";

        String diffdays=String.valueOf(diffDays);
        String diffhours=String.valueOf(diffHours);
        String diffminutes=String.valueOf(diffMinutes);
        String diffseconds=String.valueOf(diffSeconds);
        if(diffDays<1){
            diffDaystring="";
            diffdays="";
        }
         if(diffHours<1){
             if(diffDays<1){
             diffHourstirng="";
             diffhours="";
             }
        }
         if(diffMinutes<1){
             if(diffDays<1&&diffHours<1) {
                 diffMinutestring = "";
                 diffminutes = "";
             }
        }
            day = diffdays + diffDaystring + diffhours +diffHourstirng + diffminutes +diffMinutestring + diffseconds +diffSecondstring;
        return day;
    }
    /***年月日***/
    public static String getStringtimeday() {
        SimpleDateFormat df  =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date=new Date();
        long diff = 0;
        String d1=df.format(date);
        try {
            diff = df.parse(d1).getTime() ;
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return d1;

    }

    /***年月日***/
    public static String getStringtimeymd() {
        SimpleDateFormat df  =  new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();
        long diff = 0;
        String d1=df.format(date);
        try {
            diff = df.parse(d1).getTime() ;
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return d1;

    }
    /***年月日时分秒***/
    @SuppressLint("SimpleDateFormat")
    public static String getStringToDate(long  endtime,long startime,int reshtime) {
        SimpleDateFormat  dateformat3 =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       String timed= dateformat3.format(new Date(Long.valueOf(endtime + "000")));
        String stime= dateformat3.format(new Date(Long.valueOf(startime + "000")));
        return getStringtime(stime,timed, reshtime);
    }

    @SuppressLint("SimpleDateFormat")
    /***时间差***/
    public static String getStringtime(String  stime,String endtime,int reshtime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String day=null;
        Date d1 = null;
        Date d2 = null;
        Date d3= null;
        try {
            d1 = format.parse(stime);
            d2 = format.parse(endtime);
//            Calendar nowTime = Calendar.getInstance();
//            nowTime.add(Calendar.SECOND, reshtime);
//
//            String three_days_ago = format.format(nowTime.getTime());
//            d3=format.parse(three_days_ago);
            //毫秒ms
            long diff = d2.getTime() - d1.getTime()/*- d3.getTime()*/;

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            day=diffDays+"天"+diffHours+"时"+diffMinutes+"分"+diffSeconds;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return day;

    }
    /**
     * 根据日期获得星期
     * @param date
     * @return
     */
    public static String getWeekOfDate() {

        SimpleDateFormat df  =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date=new Date();
        long diff = 0;
        String d1=df.format(date);
        String[] weekDaysName = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysCode[intWeek];
    }
    /**
     * 获取某日期往前多少天的日期
     * @CreateTime
     * @Author PSY
     * @param nowDate
     * @param beforeNum
     * @return
     */
    public static String getBeforeDate(Integer beforeNum){
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        calendar1.add(Calendar.DATE, -beforeNum);
        String three_days_ago = sdf1.format(calendar1.getTime());
        return (three_days_ago);//得到前beforeNum天的时间
    }
    /**
     * 获取某日期往前多少小时的时间
     * @CreateTime
     * @Author PSY
     * @param nowDate
     * @param beforeNum
     * @return
     */
    public static Date getBeforeHour(Date nowDate, Integer beforeNum){
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(nowDate);//把当前时间赋给日历
        calendar.add(Calendar.HOUR_OF_DAY, -beforeNum);  //设置为前beforeNum小时
        return calendar.getTime();   //得到前beforeNum小时的时间
    }

    /**
     * 判断是否是Integer类型
     * @author daichangfu
     * @param str
     * @return
     */
    public static boolean isNumber(String str){
        if(str!=null&&!"".equals(str.trim())){
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher isNum = pattern.matcher(str);
            Long number = 0l;
            if(isNum.matches()){
                number=Long.parseLong(str);
            }else{
                return false;
            }
            if(number>2147483647){
                return false;
            }
        }else{
            return false;
        }
        return true;
    }
    /*/保留2位小数***/
    public static String setjiage(String data){
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(new BigDecimal(Double.valueOf(data.toString())));
    }


}
