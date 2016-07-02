package com.gasstation.recharge;

import okhttp3.Call;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gasstation.consume.ConsumeInfoActivity;
import com.gasstation.util.Const;
import com.gasstation.util.MyApplication;
import com.guanyin.gasstation.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

public class RechargeActivity extends Activity implements OnClickListener {

	private ImageView iv_finish;
	private TextView tv_recharge;
	private TextView tv_station_name;

	private EditText et_recharge_card;
	private EditText et_recharge_count;
	private EditText et_recharge_manager;
	private EditText et_recharge_tel;

	private Context context = this;
	private MyApplication app;
	private String TAG = "RechargeActivity";

	// private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_recharge);
		if (app == null) {
			app = MyApplication.getInstance();
		}
		initView();
		Const.log(TAG, app.sp.getString("station_name", ""));
		// tv_station_name.setText(app.sp.getString("station_name", ""));
	}

	private void initView() {
		iv_finish = (ImageView) findViewById(R.id.iv_finish);
		tv_recharge = (TextView) findViewById(R.id.btn_recharge);

		tv_station_name = (TextView) findViewById(R.id.tv_station_name);

		et_recharge_card = (EditText) findViewById(R.id.et_recharge_card);
		et_recharge_count = (EditText) findViewById(R.id.et_recharge_count);
		et_recharge_manager = (EditText) findViewById(R.id.et_recharge_manager);
		et_recharge_tel = (EditText) findViewById(R.id.et_recharge_tel);

		iv_finish.setOnClickListener(this);
		tv_recharge.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_finish:
			finish();
			break;
		case R.id.btn_recharge:
			if (et_recharge_card.getText().toString().equals("")
					|| et_recharge_card.getText().toString().length() < 14) {
				Const.showToast(context, "请输入正确的充值卡号");
				return;
			}
			if (et_recharge_count.getText().toString().equals("")) {
				Const.showToast(context, "请输入充值金额");
				return;
			}
			if (et_recharge_manager.getText().toString().equals("")) {
				Const.showToast(context, "请输入充值人员");
				return;
			}
			// || et_recharge_tel.getText().toString().length() < 11
			if (et_recharge_tel.getText().toString().equals("")) {
				Const.showToast(context, "请输入正确的电话号码");
				return;
			}
			recharge();
			// finish();
			break;

		default:
			break;
		}

	}

	private void recharge() {
		// dialog = new ProgressDialog(context);
		// dialog = ProgressDialog.show(context, null, "正在充值请稍候...");

		final JSONObject jsonObj = new JSONObject();

		try {
			jsonObj.put("id", app.sp.getString("station_id", "0"));
			jsonObj.put("recharge_card", et_recharge_card.getText().toString()
					.trim());
			jsonObj.put("recharge_money", et_recharge_count.getText()
					.toString().trim());
			jsonObj.put("recharge_memeber", et_recharge_manager.getText()
					.toString().trim());
			jsonObj.put("telephone", et_recharge_tel.getText().toString()
					.trim());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OkHttpUtils.post().url(Const.serverurl)
				.addParams("route", Const.apiRouteRecharge)
				.addParams("token", app.sp.getString("token", null))
				.addParams("jsonText", jsonObj.toString()).build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call request, Exception e, int arg1) {
						// dialog.dismiss();
						Const.log(TAG, "联网失败:" + e.getMessage());
						Const.showToast(context, "联网失败:" + e.getMessage());
					}

					@Override
					public void onResponse(String response, int arg2) {

						// Const.log(Tag, response);
						JSONObject jsonObj = null;
						try {
							jsonObj = new JSONObject(response);
						} catch (JSONException e) {
							e.printStackTrace();
						}

						try {
							if (jsonObj.getJSONObject("status")
									.getString("succeed").equals("1")) {
								Const.log(TAG, "充值成功");
								String totalSum = jsonObj.getJSONObject("data")
										.getString("toltal_sum");
								Editor editor = app.sp.edit();
								editor.putString("toltal_sum", totalSum);
								String addup = jsonObj.getJSONObject("data")
										.getString("addup");
								editor.putString("addup", addup);
								String consumeup = jsonObj
										.getJSONObject("data").getString(
												"consumeup");
								editor.putString("consumeup", consumeup);
								editor.apply();
								// dialog.dismiss();
								Const.showToast(context, "充值成功！");
								Intent intent = new Intent(
										RechargeActivity.this,
										ConsumeInfoActivity.class);
								startActivity(intent);
								finish();
							} else {
								Const.showToast(context, "充值失败:"
										+ jsonObj.getJSONObject("status")
												.getString("error_desc"));
								// dialog.dismiss();
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					};

				});
	}

}
