package com.gasstation.user;

import okhttp3.Call;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.gasstation.application.MyApplication;
import com.gasstation.util.Const;
import com.guanyin.gasstation.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

public class LoginActivity extends Activity implements OnClickListener {

	private EditText et_username;
	private EditText et_password;

	private TextView tv_login;

	private ProgressDialog dialog;

	private String TAG = "LoginActivity";

	private MyApplication app;

	private Context Context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		if (app == null) {
			
			app = MyApplication.getInstance();
		}

		initviews();

		tv_login.setOnClickListener(this);
	}

	private void initviews() {
		et_username = (EditText) findViewById(R.id.input_username);
		et_password = (EditText) findViewById(R.id.input_password);

		tv_login = (TextView) findViewById(R.id.tv_login);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_login:
			if (dialog != null && dialog.isShowing()) {
				return;
			}
			if (et_username.getText().toString().length() == 0) {
				Const.showToast(this, "用户名不能为空！");
				et_username.requestFocus();
				break;
			}

			if (et_password.getText().toString().length() == 0) {
				Const.showToast(this, "密码不能为空！");
				et_username.requestFocus();
				break;
			}
			dialog = ProgressDialog.show(this, null, "正在登录，请稍候...");

			JSONObject jsonObj = new JSONObject();
			try {
				jsonObj.put("station_user", et_username.getText().toString()
						.trim());
				jsonObj.put("password", et_password.getText().toString().trim());
			} catch (org.json.JSONException e) {
			}
			OkHttpUtils.post().url(Const.serverurl)
					.addParams("route", Const.apiRouteLogin)
					.addParams("jsonText", jsonObj.toString()).build()
					.execute(new StringCallback() {
						@Override
						public void onError(Call request, Exception e, int arg1) {
							dialog.dismiss();
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
									JSONObject res = jsonObj
											.getJSONObject("data");
									// 在这里初始化my application的shared prefence时出错
									Editor editor = app.sp.edit();
									editor.putString("station_id",
											res.getString("id"));
									editor.putString("station_name",
											res.getString("station_name"));
									editor.putString("toltal_sum",
											res.getString("toltal_sum"));
									editor.putString("token",
											res.getString("token"));
									editor.apply();
									Const.showToast(Context, "保存数据成功！");
									Intent intent = new Intent(
											Context,
											PendingActivity.class);
									startActivity(intent);
									finish();
								} else {
									Const.showToast(LoginActivity.this, "登录失败:"
											+ jsonObj.getJSONObject("status")
													.getString("error_desc"));
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							dialog.dismiss();
						}

					});

			break;

		default:
			break;
		}
	}
}
