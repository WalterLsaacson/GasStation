package com.gasstation.consume;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gasstation.user.ReguelInfo;
import com.guanyin.gasstation.R;

public class MyRecordAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<ReguelInfo> res;

	public MyRecordAdapter(Context context, ArrayList<ReguelInfo> res) {
		super();
		this.context = context;
		this.res = res;
	}

	@Override
	public int getCount() {
		return res.size();
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
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.consume_record_item, null);
			TextView tv_license = (TextView) convertView
					.findViewById(R.id.record_license);
			TextView tv_time = (TextView) convertView
					.findViewById(R.id.record_time);
			TextView tv_oilgun = (TextView) convertView
					.findViewById(R.id.record_oilgun);
			TextView tv_oilquality = (TextView) convertView
					.findViewById(R.id.record_oilquality);
			TextView tv_price = (TextView) convertView
					.findViewById(R.id.record_price);
			TextView tv_gross = (TextView) convertView
					.findViewById(R.id.record_gross);
			TextView tv_total_price = (TextView) convertView
					.findViewById(R.id.record_totalPrice);
			ViewHolder holder = new ViewHolder();
			holder.tv_license = tv_license;
			holder.tv_gross = tv_gross;
			holder.tv_oilgun = tv_oilgun;
			holder.tv_oilquality = tv_oilquality;
			holder.tv_price = tv_price;
			holder.tv_time = tv_time;
			holder.tv_total_price = tv_total_price;
			convertView.setTag(holder);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();

		TextView tv_license = holder.tv_license;
		tv_license.setText("● " + res.get(position).getCarNumber());

		TextView tv_time = holder.tv_time;
		tv_time.setText(res.get(position).getInsertTime());

		TextView tv_oilgun = holder.tv_oilgun;
		tv_oilgun.setText(res.get(position).getGunNumber());

		TextView tv_oilquality = holder.tv_oilquality;
		tv_oilquality.setText(res.get(position).getGasType());

		TextView tv_price = holder.tv_price;
		tv_price.setText(res.get(position).getPrice());

		TextView tv_gross = holder.tv_gross;
		tv_gross.setText(res.get(position).getOilMass());

		TextView tv_total_price = holder.tv_total_price;
		tv_total_price.setText(res.get(position).getTotalPrice());
		return convertView;
	}

	class ViewHolder {
		TextView tv_license;
		TextView tv_time;
		TextView tv_oilgun;
		TextView tv_oilquality;
		TextView tv_price;
		TextView tv_gross;
		TextView tv_total_price;
		TextView tv_notification;

	}

}
