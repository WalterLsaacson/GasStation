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

import com.gasstation.application.MyApplication;
import com.gasstation.customAdapter.MyGridViewAdapter;
import com.gasstation.data.ReguelInfo;
import com.gasstation.util.Const;
import com.guanyin.gasstation.MainActivity;
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
			final View loginView = inflater.inflate(R.layout.logout_dialog, null);
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
					intent = new Intent(Context, MainActivity.class);
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

	// private void voiceTongzhi() {
	// Const.showToast(Context, "开始语音");
	// // 1.创建 SpeechSynthesizer 对象, 第二个参数：
	// // 本地合成时传 InitListener
	// SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(Context,
	// null);
	// // 2.合成参数设置，详见《科大讯飞MSC API手册(Android)》
	// // SpeechSynthesizer 类
	// // 设置发音人（更多在线发音人，用户可参见 附录12.2
	// mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
	// mTts.setParameter(SpeechConstant.SPEED, "50");// 设置语速
	// mTts.setParameter(SpeechConstant.VOLUME, "80");// 设置音量，范围
	// // 0~100
	// mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
	// // 设置云端
	// // 设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
	// // 保存在 SD 卡需要在 AndroidManifest.xml 添加写
	// // SD 卡权限
	// // 仅支持保存为 pcm 格式， 如果不需要保存合成音频，注释该行代码
	// mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
	//
	// // 合成监听器
	// SynthesizerListener mSynListener = new SynthesizerListener() {
	// // 会话结束回调接口，没有错误时， error为null
	// public void onCompleted(SpeechError error) {
	// }
	//
	// // 缓冲进度回调
	// // percent为缓冲进度0~100，
	// // beginPos为缓冲音频在文本中开始位置，
	// // endPos表示缓冲音频在
	// // 文本中结束位置， info为附加信息。
	// public void onBufferProgress(int percent, int beginPos, int endPos,
	// String info) {
	// }
	//
	// // 开始播放
	// public void onSpeakBegin() {
	// }
	//
	// // 暂停播放
	// public void onSpeakPaused() {
	// }
	//
	// // 播放进度回调
	// // percent为播放进度0~100,beginPos为播放音频在文本中开始位置，
	// // endPos表示播放音频在文
	// // 本中结束位置.
	// public void onSpeakProgress(int percent, int beginPos, int endPos) {
	// }
	//
	// // 恢复播放回调接口
	// public void onSpeakResumed() {
	// }
	//
	// @Override
	// public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
	// }
	//
	// };
	// // 3.开始合成
	// mTts.startSpeaking("测试语音。 test voice", mSynListener);
	//
	// }

}
