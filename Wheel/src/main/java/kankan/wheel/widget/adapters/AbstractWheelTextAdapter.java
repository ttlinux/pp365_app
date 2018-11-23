package kankan.wheel.widget.adapters;

/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;

/**
 * Abstract wheel adapter provides common functionality for adapters.
 */
public abstract class AbstractWheelTextAdapter extends AbstractWheelAdapter implements OnWheelChangedListener {

	/** Text view resource. Used as a default view for adapter. */
	public static final int TEXT_VIEW_ITEM_RESOURCE = -1;

	/** No resource constant. */
	protected static final int NO_RESOURCE = 0;

	/** Default text color */
	public static final int DEFAULT_TEXT_COLOR = 0xFF0288ce;

	/** Default text color */
	public static final int LABEL_COLOR = 0xFF700070;
	
	public static final int CONFIG_TEXT_COLOR = 0xFF6699ff;

	/** Default text size */
	public   int DEFAULT_TEXT_SIZE = 13;

	// Text settings
	private int textColor = DEFAULT_TEXT_COLOR;
	private int textSize = DEFAULT_TEXT_SIZE;
	
	private String textType = "";
	
	private int SelectColor=0xFF0288ce;

	private int UNSelectColor=0x440288ce;
	// Current context
	protected Context context;
	// Layout inflater
	protected LayoutInflater inflater;

	// Items resources
	protected int itemResourceId;
	protected int itemTextResourceId;

	// Empty items resources
	protected int emptyItemResourceId;

	private int initValue=-1;

	private SparseArray<TextView> textViewSparseArray=new SparseArray<>();

	//设置粗体
	boolean isbold=false;
	
	public void setDEFAULT_TEXT_SIZE_TYPE_SP(int size)
	{
		textSize=size;
	}

	public void settextBold(boolean isbold)
	{
		this.isbold=isbold;
	}

	public String getTextType() {
		return textType;
	}

	public void setTextType(String textType) {
		this.textType = textType;
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the current context
	 */
	protected AbstractWheelTextAdapter(Context context) {
		this(context, TEXT_VIEW_ITEM_RESOURCE);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the current context
	 * @param itemResource
	 *            the resource ID for a layout file containing a TextView to use
	 *            when instantiating items views
	 */
	protected AbstractWheelTextAdapter(Context context, int itemResource) {
		this(context, itemResource, NO_RESOURCE);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the current context
	 * @param itemResource
	 *            the resource ID for a layout file containing a TextView to use
	 *            when instantiating items views
	 * @param itemTextResource
	 *            the resource ID for a text view in the item layout
	 */
	protected AbstractWheelTextAdapter(Context context, int itemResource,
			int itemTextResource) {
		this.context = context;
		itemResourceId = itemResource;
		itemTextResourceId = itemTextResource;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * Gets text color
	 * 
	 * @return the text color
	 */
	public int getTextColor() {
		return textColor;
	}

	/**
	 * Sets text color
	 * 
	 * @param textColor
	 *            the text color to set
	 */
	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	/**
	 * Gets text size
	 * 
	 * @return the text size
	 */
	public int getTextSize() {
		return textSize;
	}

	/**
	 * Sets text size
	 * 
	 * @param textSize
	 *            the text size to set
	 */
	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	/**
	 * Gets resource Id for items views
	 * 
	 * @return the item resource Id
	 */
	public int getItemResource() {
		return itemResourceId;
	}

	/**
	 * Sets resource Id for items views
	 * 
	 * @param itemResourceId
	 *            the resource Id to set
	 */
	public void setItemResource(int itemResourceId) {
		this.itemResourceId = itemResourceId;
	}

	/**
	 * Gets resource Id for text view in item layout
	 * 
	 * @return the item text resource Id
	 */
	public int getItemTextResource() {
		return itemTextResourceId;
	}

	/**
	 * Sets resource Id for text view in item layout
	 * 
	 * @param itemTextResourceId
	 *            the item text resource Id to set
	 */
	public void setItemTextResource(int itemTextResourceId) {
		this.itemTextResourceId = itemTextResourceId;
	}

	/**
	 * Gets resource Id for empty items views
	 * 
	 * @return the empty item resource Id
	 */
	public int getEmptyItemResource() {
		return emptyItemResourceId;
	}

	/**
	 * Sets resource Id for empty items views
	 * 
	 * @param emptyItemResourceId
	 *            the empty item resource Id to set
	 */
	public void setEmptyItemResource(int emptyItemResourceId) {
		this.emptyItemResourceId = emptyItemResourceId;
	}

	/**
	 * Returns text for specified item
	 * 
	 * @param index
	 *            the item index
	 * @return the text of specified items
	 */
	protected abstract CharSequence getItemText(int index);


	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		Log.e("cccc111", oldValue + " " + newValue);
		if(textViewSparseArray.get(newValue)!=null)
		textViewSparseArray.get(newValue).setTextColor(SelectColor);

		int item_1=newValue-1<0?getItemsCount()-1:newValue-1;
		if(textViewSparseArray.get(item_1)!=null)
		textViewSparseArray.get(item_1).setTextColor(UNSelectColor);

		int item_2=newValue-2<0?getItemsCount()-2:newValue-2;
		if(textViewSparseArray.get(item_2)!=null)
		textViewSparseArray.get(item_2).setTextColor(UNSelectColor);

		int item1=newValue+1>=getItemsCount()?0:newValue+1;
		if(textViewSparseArray.get(item1)!=null)
		textViewSparseArray.get(item1).setTextColor(UNSelectColor);

		int item2=newValue+2>=getItemsCount()?0:newValue+2;
		if(textViewSparseArray.get(item2)!=null)
		textViewSparseArray.get(item2).setTextColor(UNSelectColor);
	}

	public void InitTextColor(int newValue)
	{
		if(getItemsCount()<=newValue)return;
		initValue=newValue;

	}

	public View getItem(int index, View convertView, ViewGroup parent) {
		Log.e("cccc222",index+" ");
		if (index >= 0 && index < getItemsCount()) {
			if (convertView == null) {
				convertView = getView(itemResourceId, parent);
			}
			TextView textView = getTextView(convertView, itemTextResourceId);
//			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
			textView.setPadding(0, getDIP2PX(context, 10), 0, getDIP2PX(context, 10));
			if (textView != null) {
				CharSequence text = getItemText(index);
				if (text == null) {
					text = "";
				}
				textView.setText(text);

				if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
					Log.e("getItem111","getItem111");
					configureTextView(textView);
				}
			}
			if(initValue>-1 && textViewSparseArray.size()<getItemsCount())
			{
				if(index==initValue)
					textView.setTextColor(SelectColor);
				else
					textView.setTextColor(UNSelectColor);
			}
			else
			{
				initValue=-1;
			}
			textViewSparseArray.put(index,(TextView)convertView);

			return convertView;
		}
		return null;
	}

	@Override
	public View getEmptyItem(View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = getView(emptyItemResourceId, parent);
		}
		if (emptyItemResourceId == TEXT_VIEW_ITEM_RESOURCE
				&& convertView instanceof TextView) {
			Log.e("getItem222","getItem222");
			configureTextView((TextView) convertView);
		}

		return convertView;
	}

	/**
	 * Configures text view. Is called for the TEXT_VIEW_ITEM_RESOURCE views.
	 * 
	 * @param view
	 *            the text view to be configured
	 */
	protected void configureTextView(TextView view) {
//		view.setTextColor(textColor);
		view.setGravity(Gravity.CENTER);
		view.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
		view.setLines(1);
		view.setText(view.getText() + textType);
		if(isbold)
		view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
	}

	/**
	 * Loads a text view from view
	 * 
	 * @param view
	 *            the text view or layout containing it
	 * @param textResource
	 *            the text resource Id in layout
	 * @return the loaded text view
	 */
	private TextView getTextView(View view, int textResource) {
		TextView text = null;
		try {
			if (textResource == NO_RESOURCE && view instanceof TextView) {
				text = (TextView) view;
			} else if (textResource != NO_RESOURCE) {
				text = (TextView) view.findViewById(textResource);
			}
		} catch (ClassCastException e) {
			Log.e("AbstractWheelAdapter",
					"You must supply a resource ID for a TextView");
			throw new IllegalStateException(
					"AbstractWheelAdapter requires the resource ID to be a TextView",
					e);
		}

		return text;
	}

	/**
	 * Loads view from resources
	 * 
	 * @param resource
	 *            the resource Id
	 * @return the loaded view or null if resource is not set
	 */
	private View getView(int resource, ViewGroup parent) {
		switch (resource) {
		case NO_RESOURCE:
			return null;
		case TEXT_VIEW_ITEM_RESOURCE:
			return new TextView(context);
		default:
			return inflater.inflate(resource, parent, false);
		}
	}

	public  int getDIP2PX(Context context,float i) {
		float scale = context.getResources().getDisplayMetrics().density;
		int size = (int) (i * scale + 0.5f);
		return size;

	}
}