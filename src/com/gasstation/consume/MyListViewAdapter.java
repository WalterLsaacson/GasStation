package com.gasstation.consume;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.guanyin.gasstation.R;

public class MyListViewAdapter extends BaseAdapter {

	private ArrayList<String> date;
	private Context Context;
	private String mold;

	private int curYear;
	private int curMonth;

	private View view;

	public MyListViewAdapter(ArrayList<String> years, Context context,
			String mold, int curYear, int curMonth) {
		super();
		this.date = years;
		this.Context = context;
		this.mold = mold;

		this.curYear = curYear;
		this.curMonth = curMonth;
	}

	@Override
	public int getCount() {
		return date.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		view = LayoutInflater.from(Context).inflate(R.layout.list_item, null);

		switch (mold) {
		case ("year"):
			if (convertView == null) {
				convertView = LayoutInflater.from(Context).inflate(
						R.layout.list_item, null);
				holder = new ViewHolder();
				holder.tv_date = (TextView) convertView
						.findViewById(R.id.tv_year);
				convertView.setTag(holder);
			}
			// if (position == curYear) {
			// view.setBackgroundResource(R.color.year_item_selected);
			// } else {
			// view.setBackgroundResource(R.color.year_item_unselected);
			// }
			TextView tv_year = ((ViewHolder) convertView.getTag()).tv_date;
			tv_year.setText(date.get(position) + "年");

			break;
		case ("month"):
			if (convertView == null) {
				convertView = LayoutInflater.from(Context).inflate(
						R.layout.list_item, null);
				holder = new ViewHolder();
				holder.tv_date = (TextView) convertView
						.findViewById(R.id.tv_year);
				convertView.setTag(holder);
			}
			// if (curMonth == position) {
			// convertView.setBackgroundResource(R.color.month_item_selected);
			// } else {
			// convertView
			// .setBackgroundResource(R.color.month_item_unselected);
			// }
			TextView tv_month = ((ViewHolder) convertView.getTag()).tv_date;
			tv_month.setText(date.get(position) + "月");

			break;

		}
		return convertView;
	}

	class ViewHolder {
		TextView tv_date;
	}

}
