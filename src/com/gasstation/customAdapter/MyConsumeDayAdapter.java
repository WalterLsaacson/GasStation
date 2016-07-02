package com.gasstation.customAdapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gasstation.data.ConsumeInfo;
import com.guanyin.gasstation.R;

public class MyConsumeDayAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<ConsumeInfo> cons;

	private boolean isNull;

	

	public MyConsumeDayAdapter(Context context, ArrayList<ConsumeInfo> cons,
			boolean isNull) {
		super();
		this.context = context;
		this.cons = cons;
		this.isNull = isNull;
		
	}

	@Override
	public int getCount() {
		return cons.size();
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

		if (isNull) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				// 加载消费记录
				convertView = LayoutInflater.from(context).inflate(
						R.layout.day_item, null);
				holder.tv_date_num = (TextView) convertView
						.findViewById(R.id.item_carnum);
				holder.tv_oil_gross = (TextView) convertView
						.findViewById(R.id.tv_oil_gross);
				holder.tv_total_price = (TextView) convertView
						.findViewById(R.id.tv_total_price);
				holder.tv_update_time = (TextView) convertView
						.findViewById(R.id.item_date);
				convertView.setTag(holder);
			}
			TextView tv_date_num = ((ViewHolder) convertView.getTag()).tv_date_num;
			tv_date_num.setText(cons.get(position).getCar_sum());
			TextView tv_oil_gross = ((ViewHolder) convertView.getTag()).tv_oil_gross;
			tv_oil_gross.setText(cons.get(position).getOil_mass());
			TextView tv_total_price = ((ViewHolder) convertView.getTag()).tv_total_price;
			tv_total_price.setText(cons.get(position).getPrice_mass());
			TextView tv_update_time = ((ViewHolder) convertView.getTag()).tv_update_time;
			tv_update_time.setText(cons.get(position).getUpdate_time());
			//当月的消费记录为空时
			if (isNull) {
				convertView.setSelected(true);
			}

			return convertView;

		} else {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.no_record_item, null);
			return convertView;
		}

	}

	class ViewHolder {
		TextView tv_date_num;
		TextView tv_oil_gross;
		TextView tv_total_price;
		TextView tv_update_time;
	}

}
