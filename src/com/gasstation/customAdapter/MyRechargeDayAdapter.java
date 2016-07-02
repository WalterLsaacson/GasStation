package com.gasstation.customAdapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gasstation.data.RechargeInfo;
import com.guanyin.gasstation.R;

public class MyRechargeDayAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<RechargeInfo> res;
	private boolean hasData;

	public MyRechargeDayAdapter(Context context, ArrayList<RechargeInfo> res,
			boolean hasData) {
		super();
		this.context = context;
		this.res = res;
		this.hasData = hasData;
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

		if (hasData) {
			ViewHolder holder = new ViewHolder();

			if (convertView == null) {
				// 加载充值记录
				convertView = LayoutInflater.from(context).inflate(
						R.layout.recharge_record_item, null);
				holder.tv_recharge_member = (TextView) convertView
						.findViewById(R.id.tv_recharge_member);
				holder.tv_recharge_money = (TextView) convertView
						.findViewById(R.id.tv_recharge_money);
				holder.tv_recharge_tell = (TextView) convertView
						.findViewById(R.id.tv_recharge_tell);
				convertView.setTag(holder);
			}
			TextView tv_recharge_member = ((ViewHolder) convertView.getTag()).tv_recharge_member;
			tv_recharge_member.setText(res.get(position).getRecharge_memeber());
			TextView tv_recharge_money = ((ViewHolder) convertView.getTag()).tv_recharge_money;
			tv_recharge_money.setText(res.get(position).getRecharge_money());
			TextView tv_recharge_tell = ((ViewHolder) convertView.getTag()).tv_recharge_tell;
			tv_recharge_tell.setText(res.get(position).getTelephone());

			return convertView;
		} else {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.recharge_record_item, null);
			TextView tv_null = (TextView) convertView.findViewById(R.id.isnull);
			TextView tv_set_null = (TextView) convertView
					.findViewById(R.id.setnull);
			tv_null.setText("当月没有充值记录！");
			tv_set_null.setText("");

			return convertView;
		}

	}

	class ViewHolder {
		TextView tv_recharge_money;
		TextView tv_recharge_member;
		TextView tv_recharge_tell;
	}

}
