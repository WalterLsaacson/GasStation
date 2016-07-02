package com.gasstation.util;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.iflytek.cloud.SpeechUtility;

public class MyApplication extends Application {

	private final String TAG = "MyApplication";
	/** MyApplication实例 */
	private static MyApplication instance;

	public SharedPreferences sp;

	// 账户的数据
	public String token;

	// 账户余额o
	public static double sBanance;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		SpeechUtility.createUtility(instance, "appid=575cc426");
		init();

		initData();
	}

	private void initData() {
		token = sp.getString("token", "");
	}

	public static MyApplication getInstance() {
		return instance;
	}

	public void init() {
		sp = getSharedPreferences("user_message", MODE_PRIVATE);
		Const.showToast(this, sp.getString("token", "没有token"));
		if (Const.debug)
			Log.e(TAG, "init()");
	}
}
