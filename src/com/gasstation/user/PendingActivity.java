package com.gasstation.user;

import java.util.ArrayList;

import okhttp3.Call;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.TextView;

import com.gasstation.consume.ConsumeInfoActivity;
import com.gasstation.util.Const;
import com.gasstation.util.MyApplication;
import com.guanyin.gasstation.EntranceActivity;
import com.guanyin.gasstation.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

public class PendingActivity extends Activity implements
		android.view.View.OnClickListener {

	private MyApplication app;
	private Intent intent;
	private Context Context = this;
	private String TAG = "PendingActivity";
	private TextView tv_balance;
	private TextView tv_info;
	private TextView tv_date;
	private TextView tv_logout;

	private boolean isFirstGet = true;
	// 设置grid view
	private ArrayList<ReguelInfo> res = new ArrayList<ReguelInfo>();
	private GridView gridView;
	private MyGridViewAdapter adapter;
	private ProgressDialog dialog;

	private Dialog logoutDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pending);

		if (app == null) {

			app = MyApplication.getInstance();
		}

		initView();

		// 联网获取信息
		getRefuelInfo();

		gridView = (GridView) findViewById(R.id.pending_gridview);
		adapter = new MyGridViewAdapter(this, res);
		gridView.setAdapter(adapter);

	}

	private void getRefuelInfo() {
		// if (!isRunning) {
		// return;
		// }
		dialog = ProgressDialog.show(this, null, "正在读取数据，请稍候...");

		final JSONObject jsonObj = new JSONObject();

		try {
			jsonObj.put("id", app.sp.getString("station_id", "0"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OkHttpUtils.post().url(Const.serverurl)
				.addParams("route", Const.apiRouteJournal)
				.addParams("token", app.sp.getString("token", null))
				.addParams("jsonText", jsonObj.toString()).build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call request, Exception e, int arg1) {
						// Const.log(TAG, e.getMessage());
						dialog.dismiss();
						Const.showToast(Context, "联网失败:" + e.getMessage());
					}

					@Override
					public void onResponse(String response, int arg2) {
						Const.log(TAG, response);
						JSONObject jsonObj = null;
						try {
							jsonObj = new JSONObject(response);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						try {
							if (jsonObj.getJSONObject("status")
									.getString("succeed").equals("1")) {
								String totalSum = jsonObj.getJSONObject("data")
										.getString("toltal_sum");
								if (!isFirstGet
										&& totalSum.equals(app.sp.getString(
												"toltal_sum", ""))) {
									dialog.dismiss();
									return;
								}
								Const.log(TAG, "获取到新数据");
								isFirstGet = false;
								Editor editor = app.sp.edit();
								editor.putString("toltal_sum", totalSum);
								editor.apply();
								tv_balance.setText("账户余额：" + totalSum);
								JSONArray result = jsonObj
										.getJSONObject("data").getJSONArray(
												"result");
								res.clear();
								if (result != null && result.length() > 0) {
									for (int i = 0; i < result.length(); i++) {
										ReguelInfo card = new ReguelInfo();
										JSONObject obj = result
												.getJSONObject(i);

										card.journalId = obj
												.getString("journal_id");
										card.memberId = obj
												.getString("member_id");
										card.invoiceCode = obj
												.getString("invoice_code");
										card.invoiceName = obj
												.getString("invoice_name");
										card.stationId = obj
												.getString("station_id");
										card.carNumber = obj
												.getString("car_number");
										card.gunNumber = obj
												.getString("gun_number");
										card.gasType = obj
												.getString("gas_type");
										card.price = obj.getString("price");
										card.totalPrice = obj
												.getString("toltal_price");
										card.oilMass = obj
												.getString("oil_mass");
										card.noticeState = obj
												.getString("state");
										card.printState = obj
												.getString("invoice_print");
										card.insertTime = obj
												.getString("insert_time");

										res.add(card);
									}
								}

								Log.e(TAG, res.toString());
								adapter.notifyDataSetChanged();
								Log.e(TAG, "加载数据完毕");
								dialog.dismiss();
							} else {
								Const.showToast(Context, "读取数据失败:"
										+ jsonObj.getJSONObject("status")
												.getString("error_desc"));
								if (jsonObj.getJSONObject("status")
										.getString("error_code").equals("2002")) {
									intent = new Intent(Context,
											LoginActivity.class);
									startActivity(intent);
								}
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						dialog.dismiss();
					}

				});
	}

	private void initView() {
		tv_balance = (TextView) findViewById(R.id.balance);
		tv_info = (TextView) findViewById(R.id.info);
		tv_date = (TextView) findViewById(R.id.date);
		tv_logout = (TextView) findViewById(R.id.logout);

		// 设置获取账户信息的函数
		tv_info.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_info.setOnClickListener(this);

		tv_logout.setOnClickListener(this);
		// 设置时间
		tv_date.setText("日期：" + Const.Date());

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.info:
			intent = new Intent(Context, ConsumeInfoActivity.class);
			startActivity(intent);
			break;
		case R.id.logout:
			LayoutInflater inflater = LayoutInflater.from(this);
			final View loginView = inflater.inflate(R.layout.logout_dialog,
					null);
			AlertDialog.Builder loginBuilder = new AlertDialog.Builder(this);
			loginBuilder.setTitle("确认注销");
			loginBuilder.setView(loginView);

			loginBuilder.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Editor editor = app.sp.edit();
					editor.remove("token");
					editor.putString("token", "");
					editor.apply();
					intent = new Intent(Context, EntranceActivity.class);
					startActivity(intent);
				}
			});
			loginBuilder.setNegativeButton("取消", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					logoutDialog.dismiss();
				}
			});
			logoutDialog = loginBuilder.create();
			logoutDialog.show();
			break;
		default:
			break;
		}
	}

}
