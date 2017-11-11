package org.sex.hanker.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.sex.hanker.mybusiness.R;

/**
 * Created by Administrator on 2017/11/9.
 */
public class fragment_note_Adapter extends BaseAdapter{

    Context context;
    public fragment_note_Adapter(Context context)
    {
        this.context=context;
    }

    @Override
    public int getCount() {
        return 15;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return View.inflate(context, R.layout.model2_vertical_note,null);
    }
}
