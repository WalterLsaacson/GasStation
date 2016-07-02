package com.gasstation.consume;

import java.util.ArrayList;

import okhttp3.Call;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.GridView;
import android.widget.TextView;

import com.gasstation.user.ReguelInfo;
import com.gasstation.util.Const;
import com.gasstation.util.MyApplication;
import com.guanyin.gasstation.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

public class ConsumeRecordActivity extends Activity implements OnClickListener {

	// 设置grid view
	private GridView gridView;
	private MyRecordAdapter adapter;
	private ArrayList<ReguelInfo> res;

	private TextView tv_back_record;
	// 联网获取到的数据

	private TextView record_date;
	private TextView car_num;
	private TextView car_gross;
	private TextView gas_money;

	private ProgressDialog dialog;
	private MyApplication app;
	private String TAG = "ConsumeRecordActivity";
	private Context context = ConsumeRecordActivity.this;

	private Intent intent;

	private String date;
	private String oil_mass;
	private String car_sum;
	private String price_mass;
	

	private void initViews() {
		tv_back_record = (TextView) findViewById(R.id.tv_back_record);

		record_date = (TextView) findViewById(R.id.record_date);
		car_num = (TextView) findViewById(R.id.car_num);
		car_gross = (TextView) findViewById(R.id.car_gross);
		gas_money = (TextView) findViewById(R.id.gas_money);

		gridView = (GridView) findViewById(R.id.record_grid);
	}

	private void initDatas() {
		res = new ArrayList<ReguelInfo>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_consume_record);
		if (app == null) {
			app = MyApplication.getInstance();
		}

		initDatas();
		initViews();

		// catch date
		intent = getIntent();
		date = intent.getStringExtra("date");
		oil_mass = intent.getStringExtra("oil_mass");
		price_mass = intent.getStringExtra("price_mass");
		car_sum = intent.getStringExtra("car_sum");

		// TODO 这里的月份需要传递的是6不是06，这就导致10月之后比较难做
		record_date.setText("日期：" + date.substring(0, 4) + "/"
				+ date.substring(5, 7) + "/" + date.substring(8, 10));
		car_num.setText("加油" + car_sum + "辆");
		car_gross.setText("加油总量：" + oil_mass + "升");
		gas_money.setText("加油金额：" + price_mass + "元");

		getMonthConsume();

		tv_back_record.setOnClickListener(this);

	}

	private void getMonthConsume() {
		dialog = ProgressDialog.show(this, null, "正在获取数据，请稍候...");

		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("id", app.sp.getString("station_id", "0"));
			jsonObj.put("date", date.substring(0, 10));

		} catch (org.json.JSONException e) {
		}
		Const.log(TAG, jsonObj.toString());
		OkHttpUtils.post().url(Const.serverurl)
				.addParams("route", Const.apiRouteJournal)
				.addParams("token", app.sp.getString("token", null))
				.addParams("jsonText", jsonObj.toString()).build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call request, Exception e, int arg1) {
						dialog.dismiss();
						Const.log(TAG, "联网失败:" + e.getMessage());
						Const.showToast(context, "联网失败:" + e.getMessage());
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
							// {
							// "status": {
							// "succeed": "1"
							// },
							// "data": {
							// "toltal_sum": "342453.55",
							// "result": [
							// {
							// "journal_id": "106",
							// "member_id": "76",
							// "invoice_code": "12312313aaa",
							// "invoice_name": "西安汤谷信息科技有限公司",
							// "station_id": "2",
							// "car_number": "陕A1BB",
							// "gun_number": "5",
							// "gas_type": "95",
							// "price": "5.00",
							// "toltal_price": "55.00",
							// "oil_mass": "11.00",
							// "invoice_print": "0",
							// "state": "1",
							// "insert_time": "2016-06-03 23:22:11"
							// },
							// ]
							// }
							// }
							if (jsonObj.getJSONObject("status")
									.getString("succeed").equals("1")) {
								String totalSum = jsonObj.getJSONObject("data")
										.getString("toltal_sum");
								Editor editor = app.sp.edit();
								editor.putString("toltal_sum", totalSum);
								editor.apply();

								JSONArray result = jsonObj
										.getJSONObject("data").getJSONArray(
												"result");
								res.clear();
								if (result != null && result.length() > 0) {
									for (int i = 0; i < result.length(); i++) {

										ReguelInfo card = new ReguelInfo();
										JSONObject obj = result
												.getJSONObject(i);
										// 判断状态为语音和打印均通知之后再写入数组，传递给适配器
										if (obj.getString("invoice_print")
												.equals("1")
												&& obj.getString("state")
														.equals("1")) {
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
										} else {
											break;
										}
									}
								}
								adapter = new MyRecordAdapter(context, res);
								gridView.setAdapter(adapter);
								Log.e(TAG, res.toString());
								Log.e(TAG, "加载数据完毕");
								dialog.dismiss();
							} else {
								Const.showToast(context, "获取失败..........."
										+ jsonObj.getJSONObject("status")
												.getString("error_desc"));

							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						dialog.dismiss();

					};

				});

	}

	@Override
	public void onClick(View v) {
		// intent = new Intent(this, ConsumeInfoActivity.class);
		// startActivity(intent);
		finish();

	}

}
