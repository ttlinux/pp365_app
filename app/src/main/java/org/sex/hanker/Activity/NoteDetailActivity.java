package org.sex.hanker.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.HttpGet;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.NoteDownloader;
import org.sex.hanker.Utils.NoteMethod;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.View.ScanView;
import org.sex.hanker.View.ScanViewAdapter;
import org.sex.hanker.mybusiness.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.utils.Log;

/**
 * Created by Administrator on 2018/3/7.
 */
public class NoteDetailActivity extends BaseActivity{

    ScanView scanView;
    String URL,Title;
    List<String> contents;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        Initview();
    }

    public void Initview()
    {
        scanView=FindView(R.id.scanview);
        URL=getIntent().getStringExtra(BundleTag.URL);
        Title=getIntent().getStringExtra(BundleTag.Title);
        WindowManager winManager=(WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int height=winManager.getDefaultDisplay().getHeight()-rect.top;
        int width=winManager.getDefaultDisplay().getWidth();
        height=height- ScreenUtils.getDIP2PX(this,15*2+40);
        width=width- ScreenUtils.getDIP2PX(this,15*2);
        int textsize=14;
        //42 54

        int wordheight=ScreenUtils.sp2px(this,textsize)*54/42;
        int verNums=height/wordheight;
        int horNums=width/ScreenUtils.sp2px(this,textsize);
        int Amount=horNums*verNums;
        Amount=Amount-horNums;
        RequestNote(Amount, horNums);
    }

    public void RequestNote(final int Amount,final int HorAmount)
    {
        String contentstr= NoteMethod.getNote(URL, Title, this);
        if (contentstr!=null)
        {
            InitNoteView(Amount,HorAmount,Title,URL);
            return;
        }

        NoteDownloader noteDownloader=new NoteDownloader();
        noteDownloader.Download(this, URL, Title, new NoteDownloader.OnReceiveListener() {
            @Override
            public void OnReceive(final String content) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NoteMethod.SaveFile(content, URL, Title, NoteDetailActivity.this);
                        InitNoteView(Amount,HorAmount,Title,URL);
                    }
                });

            }

            @Override
            public void OnFail(Exception reson) {

            }
        });
    }

    private void InitNoteView(int Amount,int HorAmount,String title,String url)
    {
        File fnote=new File(StorageUtils.getOwnCacheDirectory(
                this, "/PP365/note/").getAbsolutePath()+"/"+title+url.hashCode());
        contents= NoteMethod.getStrList(fnote,Amount,HorAmount);
        ScanViewAdapter adapter = new ScanViewAdapter(NoteDetailActivity.this,Title,contents);
        scanView.setAdapter(adapter);
    }


}
