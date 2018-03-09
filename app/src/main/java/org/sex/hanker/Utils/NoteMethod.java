package org.sex.hanker.Utils;

import android.content.Context;

import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/8.
 */
public class NoteMethod {

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param file
     *            文件
     * @param count
     *            指定长度
     * @return
     */
    public static ArrayList<String> getStrList(File file, int CutCount,int HorAmount) {
        LogTools.e("Wlenght",CutCount+"   "+HorAmount);
        ArrayList<String> strs=new ArrayList<>();
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(
                    new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            int l=0;
            StringBuilder sb=new StringBuilder();

            while ((line = br.readLine()) != null) {
                if(line.length()==0)continue;
                int ex=line.length()+HorAmount*2-(line.length()%HorAmount);
                if(l+ex>CutCount)
                {
                    int needWords=CutCount-l;
                    boolean notenough=needWords>line.length();
                    sb.append(line.substring(0, notenough?line.length():needWords));
                    strs.add(sb.toString());
                    sb.delete(0, sb.length());
                    if(notenough)
                    {
                        l=0;
                        continue;
                    }

                    int usewords=needWords;
                    while(line.length()-usewords>CutCount)
                    {
                        sb.append(line.substring(usewords, CutCount+usewords));
                        strs.add(sb.toString());
                        sb.delete(0, sb.length());
                        usewords=usewords+CutCount;
                    }
                    sb.append(line.substring(usewords, line.length()));
                    sb.append("\n\n");
                    l=0;
                    l=line.length()-usewords;
                    int spacenum=HorAmount-((line.length()-usewords)%HorAmount);
                    l=spacenum+HorAmount+l;
                }
                else
                {
                    l=l+line.length();
                    sb.append(line);
                    sb.append("\n\n");
                    l=l+HorAmount*2-(line.length()%HorAmount);

                }



            }
            if(l<=CutCount && sb.toString().length()>0)
            {
                strs.add(sb.toString());
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // 建立一个输入流对象reader
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return strs;
    }


    public static void SaveFile(String Content,String Url,String title,Context context)
    {
        File file= StorageUtils.getOwnCacheDirectory(
                context, "/PP365/note/");
        if(file==null || !file.exists())
        {
            file.mkdirs();
        }
        File fnote=new File(file.getAbsolutePath()+"/"+title+Url.hashCode());
        LogTools.e("fnote",fnote.getAbsolutePath());
        if(fnote!=null && fnote.exists())
        {
            return;
        }
        Writer fwriter = null;
        try {
            fwriter = new OutputStreamWriter(new FileOutputStream(fnote.getAbsolutePath(), false),"UTF-8");
            fwriter.write(Content);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String  getNote(String url ,String title,Context context)
    {
        File fnote=new File(StorageUtils.getOwnCacheDirectory(
                context, "/PP365/note/").getAbsolutePath()+"/"+title+url.hashCode());
        byte[] filecontent =null;
        if(fnote!=null && fnote.exists())
        {
            try {
                filecontent=new byte[Long.valueOf(fnote.length()).intValue()];
                FileInputStream inputStream=new FileInputStream(fnote);
                inputStream.read(filecontent);
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                return new String(filecontent,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
