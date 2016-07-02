package com.guanyin.gasstation;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.gasstation.application.MyApplication;
import com.gasstation.user.LoginActivity;
import com.gasstation.user.PendingActivity;

public class MainActivity extends Activity {

	private Intent intent;

	private Context context = this;

	private MyApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (app == null) {
			app = MyApplication.getInstance();
		}

		Timer timer = new Timer();
		// token 为空时跳转到登录
		if (app.sp.getString("token", "").equals("")) {
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					intent = new Intent(context, LoginActivity.class);
					startActivity(intent);
					finish();
				}
			};
			timer.schedule(task, 2000);
		} else {
			// token不为空时加载详情
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					intent = new Intent(context, PendingActivity.class);
					startActivity(intent);
					finish();
				}
			};
			timer.schedule(task, 2000);
		}
	}
}
