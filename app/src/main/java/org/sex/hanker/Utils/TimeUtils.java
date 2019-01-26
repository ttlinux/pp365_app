package org.sex.hanker.Utils;

import android.os.Handler;
import android.os.Message;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2018/1/30.
 */
public class TimeUtils {

    public static SixtySecondInterface sixtySecondInterface;
    static long m_time;

    public interface SixtySecondInterface
    {
        public void onTik(long time);
    }

    public static void setM_time(long m_time) {
        TimeUtils.m_time = m_time;
    }

    public static void setSixtySecondInterface(SixtySecondInterface sixtySecondInterface) {
        TimeUtils.sixtySecondInterface = sixtySecondInterface;

        if(sixtySecondInterface==null)
        {
            m_time=0;
            handler.removeMessages(1);
        }
        else
        {
            handler.sendEmptyMessageDelayed(1,60*1000);
        }

    }

    private static Handler handler=new Handler(){

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            m_time=m_time-60*1000;
            if(sixtySecondInterface!=null)
            {
                sixtySecondInterface.onTik(m_time);
            }
            sendEmptyMessageDelayed(1, 60 * 1000);
        }

    };


    public static String FormatTime(long time)
    {
        long hour=time/3600/1000;
        long min=(time/1000)%3600/60;//3790
        long sec=(time/1000)%60;
        String hour_str;
        String min_str;
        String sec_str;
        String timestr="%s:%s:%s";

        hour_str=hour>9?(hour+""):("0"+hour);
        min_str=min>9?(min+""):("0"+min);
        sec_str=sec>9?(sec+""):("0"+sec);
//        if(sec_str.length()>2)
//        {
//            sec_str=sec_str.substring(0,2);
//        }
        return String.format(timestr,hour_str,min_str,sec_str);
    }

    public static String FormatTime2(long time)
    {
        StringBuilder sb=new StringBuilder();
        long remaintime=time;
        if(remaintime>24*60*60*1000)
        {
            long day=remaintime/(24*60*60*1000);
            long daytime=day*24*60*60*1000;
            sb.append(day);
            sb.append("天");
            remaintime=remaintime-daytime;
        }
        System.err.println(remaintime);
        if(remaintime>60*60*1000)
        {
            long hour=remaintime/(60*60*1000);
            sb.append(hour);
            sb.append("小时");
            remaintime=remaintime-hour*60*60*1000;
        }
        if(remaintime>60*1000)
        {
            long min=remaintime/(60*1000);
            sb.append(min);
            sb.append("分");
            remaintime=remaintime-min*60*1000;
        }
        if(sb.length()==0)
        {
            sb.append("试用有效期结束");
        }
        return sb.toString();
    }

    public static String LongtoString(long time)
    {
        String front="";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date=new Date(time);
        String dateStr = sdf.format(date);

        Calendar current = Calendar.getInstance();
        current.setTime(new Date());

        Calendar past = Calendar.getInstance();
        past.setTime(date);

        if(current.get(Calendar.MONTH)==past.get(Calendar.MONTH) && current.get(Calendar.YEAR)==past.get(Calendar.YEAR) &&
                current.get(Calendar.DATE)==past.get(Calendar.DATE))
        {
            front="今天";
        }
        else
        {
            if(current.get(Calendar.YEAR)>past.get(Calendar.YEAR))
            {
                front=current.get(Calendar.YEAR)-past.get(Calendar.YEAR)+"年前";
                return front+" "+dateStr;
            }
            if(current.get(Calendar.MONTH)>past.get(Calendar.MONTH))
            {
                front=current.get(Calendar.MONTH)-past.get(Calendar.MONTH)+"月前";
                return front+" "+dateStr;
            }
            if(current.get(Calendar.DATE)>past.get(Calendar.DATE))
            {
                front=current.get(Calendar.DATE)-past.get(Calendar.DATE)+"天前";
                return front+" "+dateStr;
            }
        }

        return front+" "+dateStr;
    }
}
