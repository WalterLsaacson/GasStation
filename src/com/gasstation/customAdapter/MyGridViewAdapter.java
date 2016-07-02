package com.gasstation.customAdapter;

import java.util.ArrayList;

import okhttp3.Call;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gasstation.application.MyApplication;
import com.gasstation.data.ReguelInfo;
import com.gasstation.util.Const;
import com.guanyin.gasstation.R;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

public class MyGridViewAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<ReguelInfo> res;
	// 通知打印和语音提交到服务器
	private ProgressDialog dialog;
	private MyApplication app;
	private String TAG = "MyGridViewAdapter";

	public int delPosition;

	// 百度语音

	public MyGridViewAdapter(Context context, ArrayList<ReguelInfo> res) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (app == null) {
			app = MyApplication.getInstance();
		}
		// 下边这些代码现在需要注释掉
		// if (convertView == null) {
		// convertView = LayoutInflater.from(context).inflate(
		// R.layout.gridview_item, null);
		// TextView tv_license = (TextView) convertView
		// .findViewById(R.id.license);
		// TextView tv_time = (TextView) convertView
		// .findViewById(R.id.item_time);
		// TextView tv_oilgun = (TextView) convertView
		// .findViewById(R.id.oilgun);
		// TextView tv_oilquality = (TextView) convertView
		// .findViewById(R.id.oilquality);
		// TextView tv_price = (TextView) convertView.findViewById(R.id.price);
		// TextView tv_gross = (TextView) convertView.findViewById(R.id.gross);
		// TextView tv_total_price = (TextView) convertView
		// .findViewById(R.id.totalPrice);
		// TextView tv_notification = (TextView) convertView
		// .findViewById(R.id.notification);
		// // 通知按钮
		// TextView tv_print = (TextView) convertView
		// .findViewById(R.id.notification_print);
		// TextView tv_voice = (TextView) convertView
		// .findViewById(R.id.notification);
		// ViewHolder holder = new ViewHolder();
		// holder.tv_license = tv_license;
		// holder.tv_gross = tv_gross;
		// holder.tv_notification = tv_notification;
		// holder.tv_oilgun = tv_oilgun;
		// holder.tv_oilquality = tv_oilquality;
		// holder.tv_price = tv_price;
		// holder.tv_time = tv_time;
		// holder.tv_total_price = tv_total_price;
		// // 通知
		// holder.tv_print = tv_print;
		// holder.tv_voice = tv_voice;
		// convertView.setTag(holder);
		// }
		// // }else{
		// // Log.e("test", position+" is not null");
		// // }
		// ViewHolder holder = (ViewHolder) convertView.getTag();
		// TextView tv_license = holder.tv_license;
		// tv_license.setText(res.get(position).getCarNumber());
		// TextView tv_time = holder.tv_time;
		// tv_time.setText(res.get(position).getInsertTime());
		// TextView tv_oilgun = holder.tv_oilgun;
		// tv_oilgun.setText(res.get(position).getGunNumber());
		// TextView tv_oilquality = holder.tv_oilquality;
		// tv_oilquality.setText(res.get(position).getGasType());
		// TextView tv_price = holder.tv_price;
		// tv_price.setText(res.get(position).getPrice());
		// TextView tv_gross = holder.tv_gross;
		// tv_gross.setText(res.get(position).getOilMass());
		// TextView tv_total_price = holder.tv_total_price;
		// tv_total_price.setText(res.get(position).getTotalPrice());

		convertView = LayoutInflater.from(context).inflate(
				R.layout.gridview_item, null);
		TextView tv_license = (TextView) convertView.findViewById(R.id.license);
		tv_license.setText(res.get(position).getCarNumber());
		TextView tv_time = (TextView) convertView.findViewById(R.id.item_time);
		tv_time.setText(res.get(position).getInsertTime());
		TextView tv_oilgun = (TextView) convertView.findViewById(R.id.oilgun);
		tv_oilgun.setText(res.get(position).getGunNumber());
		TextView tv_oilquality = (TextView) convertView
				.findViewById(R.id.oilquality);
		tv_oilquality.setText(res.get(position).getGasType());
		TextView tv_price = (TextView) convertView.findViewById(R.id.price);
		tv_price.setText(res.get(position).getPrice());
		TextView tv_gross = (TextView) convertView.findViewById(R.id.gross);
		tv_gross.setText(res.get(position).getOilMass());
		TextView tv_total_price = (TextView) convertView
				.findViewById(R.id.totalPrice);
		tv_total_price.setText(res.get(position).getTotalPrice());

		// final LinearLayout deLayout = (LinearLayout) convertView
		// .findViewById(R.id.del_linear);
		String regex = "(.{1})";

		final String voicesString = "请" + res.get(position).getGunNumber()
				+ "给"
				+ res.get(position).getCarNumber().replaceAll(regex, "$1 ")
				+ "加油," + res.get(position).getGasType()
				+ res.get(position).getOilMass() + "升";
		// 通知初始化
		String printState = res.get(position).getPrintState();
		String voiceState = res.get(position).getNoticeState();
		// final TextView tv_print = holder.tv_print;
		final TextView tv_print = (TextView) convertView
				.findViewById(R.id.notification_print);

		// final TextView tv_voice = holder.tv_voice;
		final TextView tv_voice = (TextView) convertView
				.findViewById(R.id.notification);
		if (printState.equals("1")) {
			tv_print.setText("已通知");
			tv_print.setBackgroundResource(R.drawable.btn_card_grey);
			tv_print.setEnabled(false);
		}
		if (voiceState.equals("1")) {
			tv_voice.setText("已通知");
			tv_voice.setBackgroundResource(R.drawable.btn_card_grey);
			tv_voice.setEnabled(false);
		}
		// 通知监听器

		tv_voice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 这是语音的通知
				dialog = ProgressDialog.show(context, null, "正在语音通知...");
				final JSONObject jsonObj = new JSONObject();
				try {
					jsonObj.put("id", app.sp.getString("station_id", "0"));
					jsonObj.put("journal_id", res.get(position).getJournalId());
					jsonObj.put("state", "1");

				} catch (JSONException e) {
					e.printStackTrace();
				}

				OkHttpUtils.post().url(Const.serverurl)
						.addParams("route", Const.apiRouteUpdate)
						.addParams("token", app.sp.getString("token", null))
						.addParams("jsonText", jsonObj.toString()).build()
						.execute(new StringCallback() {
							@Override
							public void onError(Call request, Exception e,
									int arg0) {
								dialog.dismiss();
								Const.showToast(context,
										"联网失败:" + e.getMessage());
							}

							@Override
							public void onResponse(String response, int arg1) {
								Const.log(TAG, "response" + response);
								JSONObject jsonObj = null;
								try {
									jsonObj = new JSONObject(response);
								} catch (JSONException e) {
									e.printStackTrace();
								}
								try {
									if (jsonObj.getJSONObject("status")
											.getString("succeed").equals("1")) {
										Const.log(TAG, "接收到正确的数据！");
										tv_voice.setText("已通知");
										tv_voice.setBackgroundResource(R.drawable.btn_card_grey);
										tv_voice.setEnabled(false);
										if (tv_print.getText().equals("已通知")) {

											// deLayout.setVisibility(View.GONE);
											res.remove(position);
											notifyDataSetChanged();

										}
										dialog.dismiss();
										// 1.创建 SpeechSynthesizer 对象, 第二个参数：
										// 本地合成时传 InitListener
										SpeechSynthesizer mTts = SpeechSynthesizer
												.createSynthesizer(context,
														null);
										// 2.合成参数设置，详见《科大讯飞MSC API手册(Android)》
										// SpeechSynthesizer 类
										// 设置发音人（更多在线发音人，用户可参见 附录12.2
										// aisxying陕西话
										mTts.setParameter(
												SpeechConstant.VOICE_NAME,
												"xiaoyan");
										mTts.setParameter(SpeechConstant.SPEED,
												"30");// 设置语速
										mTts.setParameter(
												SpeechConstant.VOLUME, "80");// 设置音量，范围
																				// 0~100
										mTts.setParameter(
												SpeechConstant.ENGINE_TYPE,
												SpeechConstant.TYPE_CLOUD); // 设置云端
										// 设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
										// 保存在 SD 卡需要在 AndroidManifest.xml 添加写
										// SD 卡权限
										// 仅支持保存为 pcm 格式， 如果不需要保存合成音频，注释该行代码
										mTts.setParameter(
												SpeechConstant.TTS_AUDIO_PATH,
												"./sdcard/iflytek.pcm");

										// 合成监听器
										SynthesizerListener mSynListener = new SynthesizerListener() {
											// 会话结束回调接口，没有错误时， error为null
											public void onCompleted(
													SpeechError error) {
											}

											// 缓冲进度回调
											// percent为缓冲进度0~100，
											// beginPos为缓冲音频在文本中开始位置，
											// endPos表示缓冲音频在
											// 文本中结束位置， info为附加信息。
											public void onBufferProgress(
													int percent, int beginPos,
													int endPos, String info) {
											}

											// 开始播放
											public void onSpeakBegin() {
											}

											// 暂停播放
											public void onSpeakPaused() {
											}

											// 播放进度回调
											// percent为播放进度0~100,beginPos为播放音频在文本中开始位置，
											// endPos表示播放音频在文
											// 本中结束位置.
											public void onSpeakProgress(
													int percent, int beginPos,
													int endPos) {
											}

											// 恢复播放回调接口
											public void onSpeakResumed() {
											}

											@Override
											public void onEvent(int arg0,
													int arg1, int arg2,
													Bundle arg3) {

												// stub

											}

										};
										// 3.开始合成
										mTts.startSpeaking(voicesString,
												mSynListener);

										Const.showToast(context, "语音通知成功！");
									} else {
										dialog.dismiss();
										Const.showToast(
												context,
												"读取数据失败:"
														+ jsonObj
																.getJSONObject(
																		"status")
																.getString(
																		"error_desc"));

										dialog.dismiss();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
								;
							}
						});

			}
		});
		tv_print.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 这是打印的通知
				dialog = ProgressDialog.show(context, null, "正在通知打印...");
				final JSONObject jsonObj = new JSONObject();
				try {
					jsonObj.put("id", app.sp.getString("station_id", "0"));
					jsonObj.put("journal_id", res.get(position).getJournalId());
					jsonObj.put("print", "1");

				} catch (JSONException e) {
					e.printStackTrace();
				}

				OkHttpUtils.post().url(Const.serverurl)
						.addParams("route", Const.apiRouteUpdate)
						.addParams("token", app.sp.getString("token", null))
						.addParams("jsonText", jsonObj.toString()).build()
						.execute(new StringCallback() {
							@Override
							public void onError(Call request, Exception e,
									int arg0) {
								dialog.dismiss();
								Const.showToast(context,
										"联网失败:" + e.getMessage());
							}

							@Override
							public void onResponse(String response, int arg1) {
								Const.log(TAG, "response" + response);
								JSONObject jsonObj = null;
								try {
									jsonObj = new JSONObject(response);
								} catch (JSONException e) {
									e.printStackTrace();
								}
								try {
									if (jsonObj.getJSONObject("status")
											.getString("succeed").equals("1")) {
										Const.log(TAG, "接收到正确的数据！");
										tv_print.setText("已通知");
										tv_print.setBackgroundResource(R.drawable.btn_card_grey);
										tv_print.setEnabled(false);
										if (tv_voice.getText().equals("已通知")) {
											// TODO Auto-generated method
											// Const.dele = true;
											// Const.delPosition = position;
											res.remove(position);
											notifyDataSetChanged();
											// deLayout.setVisibility(View.GONE);
										}
										dialog.dismiss();

										Const.showToast(context, "打印成功！");
									} else {
										dialog.dismiss();
										Const.showToast(
												context,
												"读取数据失败:"
														+ jsonObj
																.getJSONObject(
																		"status")
																.getString(
																		"error_desc"));
										dialog.dismiss();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
								;
							}
						});
			}
		});

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

		// 这里是通知和打印的按钮
		TextView tv_print;
		TextView tv_voice;
	}

}
