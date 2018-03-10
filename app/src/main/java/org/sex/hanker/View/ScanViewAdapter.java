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

public class ScanViewAdapter extends PageAdapter {
	Context context;
	AssetManager am;
	String title;
	List<String> titlestr;
	OnJumpPageListener onJumpPageListener;

	public void setOnJumpPageListener(OnJumpPageListener onJumpPageListener) {
		this.onJumpPageListener = onJumpPageListener;
	}

	public ScanViewAdapter(Context context, String title, List<String> titlestr) {
		this.context = context;
		am = context.getAssets();
		this.title = title;
		this.titlestr = titlestr;
	}

	public void addContent(View view, int position) {
		TextView content = (TextView) view.findViewById(R.id.content);
		TextView tv = (TextView) view.findViewById(R.id.index);
		TextView titlev = (TextView) view.findViewById(R.id.title);
		TextView backtoindex = (TextView) view.findViewById(R.id.backtoindex);
		TextView jumptoend = (TextView) view.findViewById(R.id.jumptoend);
		if (position == 1) backtoindex.setVisibility(View.GONE);
		if (position == getCount()) jumptoend.setVisibility(View.GONE);
		backtoindex.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onJumpPageListener!=null)onJumpPageListener.Onjump(1);
			}
		});
		jumptoend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onJumpPageListener!=null)onJumpPageListener.Onjump(getCount());
			}
		});
		titlev.setText(title);
		if ((position - 1) < 0 || (position - 1) >= getCount())
			return;
		content.setText(titlestr.get(position - 1));
		tv.setText(position + "/" + titlestr.size());
	}

	public int getCount() {
		return titlestr.size();
	}

	public View getView() {
		View view = LayoutInflater.from(context).inflate(R.layout.page_layout,
				null);
		return view;
	}

	public interface OnJumpPageListener
	{
		public void Onjump(int page);
	}
}
