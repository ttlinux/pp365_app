package org.sex.hanker.View;

import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.sex.hanker.mybusiness.R;

public class ScanViewAdapter extends PageAdapter
{
	Context context;
	AssetManager am;
	String title;
	List<String> titlestr;

	public ScanViewAdapter(Context context,String title,List<String> titlestr)
	{
		this.context = context;
		am = context.getAssets();
		this.title=title;
		this.titlestr=titlestr;
	}

	public void addContent(View view, int position)
	{
		TextView content = (TextView) view.findViewById(R.id.content);
		TextView tv = (TextView) view.findViewById(R.id.index);
		TextView titlev=(TextView)view.findViewById(R.id.title);
		titlev.setText(title);
		if ((position - 1) < 0 || (position - 1) >= getCount())
			return;
		content.setText(titlestr.get(position-1));
		tv.setText(position+"/"+titlestr.size());
	}

	public int getCount()
	{
		return titlestr.size();
	}

	public View getView()
	{
		View view = LayoutInflater.from(context).inflate(R.layout.page_layout,
				null);
		return view;
	}
}
