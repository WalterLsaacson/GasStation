package com.gasstation.customAdapter;

import java.util.ArrayList;

import okhttp3.Call;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gasstation.application.MyApplication;
import com.gasstation.data.ConsumeInfo;
import com.gasstation.data.RechargeInfo;
import com.gasstation.user.ConsumeInfoActivity;
import com.gasstation.user.ConsumeRecordActivity;
import com.gasstation.util.Const;
import com.guanyin.gasstation.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

public class MyFragment extends Fragment {

	private String Tag = "MyFragmentConsume";
	private MyApplication app;
	private Context context;
	private ProgressDialog dialog;

	private ListView yeartView;
	private ListView monthView;

	private ArrayList<String> years;
	private ArrayList<String> months;

	private MyListViewAdapter yearAdapter;
	private MyListViewAdapter monthAdapter;

	private ArrayList<RechargeInfo> res = new ArrayList<RechargeInfo>();
	private ArrayList<ConsumeInfo> cons = new ArrayList<ConsumeInfo>();

	private boolean hasData = true;
	private boolean hasInfo = true;

	private String curYear;
	private String curMonth;

	private int curYearPosition;
	private int curMonthPosition;

	// 加载日期的数据
	private ListView dayView;
	public MyRechargeDayAdapter rechargeDayAdapter;
	public MyConsumeDayAdapter conDayAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (app == null) {
			app = MyApplication.getInstance();
		}
		context = getActivity();
		curYear = Const.Date().substring(0, 4);
		curMonth = Const.Date().substring(5, 7);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment, container, false);
		initDatas();
		initViews(view);

		dayInfo();
		return view;
	}

	private void dayInfo() {
		// 点击年份之后加载月份列表
		yeartView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int positionYear, long id) {
				monthAdapter.notifyDataSetChanged();

				// 点击月份之后加载日期的数据
				monthView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int positionMonth, long id) {
						Const.log(Tag, "点击加载每天的数据");
						hasData = true;
						hasInfo = true;
						if (!ConsumeInfoActivity.record) {
							// 这是充值记录
							// 在这里获取每个月具体到每一天的数据
							dialog = ProgressDialog.show(context, null,
									"正在查询充值记录请稍候...");
							Const.showToast(context, "加载充值记录详情");

							final JSONObject jsonObj = new JSONObject();

							try {
								jsonObj.put("id",
										app.sp.getString("station_id", "0"));
								jsonObj.put("date", "2016-0"
										+ (positionMonth + 1));

							} catch (JSONException e) {
								e.printStackTrace();
							}

							OkHttpUtils
									.post()
									.url(Const.serverurl)
									.addParams("route",
											Const.apiRouteRechargeInfo)
									.addParams("token",
											app.sp.getString("token", null))
									.addParams("jsonText", jsonObj.toString())
									.build().execute(new StringCallback() {

										@Override
										public void onError(Call request,
												Exception e, int arg0) {
											// Const.log(Tag, e.getMessage());
											dialog.dismiss();
											Const.showToast(context, "联网失败:"
													+ e.getMessage());
										}

										@Override
										public void onResponse(String response,
												int arg1) {
											Const.log(Tag, "response"
													+ response);
											JSONObject jsonObj = null;
											try {
												jsonObj = new JSONObject(
														response);
											} catch (JSONException e) {
												e.printStackTrace();
											}
											try {
												if (jsonObj
														.getJSONObject("status")
														.getString("succeed")
														.equals("1")) {

													Const.log(Tag, "接收到正确的数据！");
													res.clear();
													if (jsonObj.has("data")) {
														JSONArray result = jsonObj
																.getJSONArray("data");

														if (result != null
																&& result
																		.length() > 0) {
															for (int i = 0; i < result
																	.length(); i++) {
																RechargeInfo card = new RechargeInfo();
																JSONObject obj = result
																		.getJSONObject(i);
																card.id = obj
																		.getString("id");
																card.user_id = obj
																		.getString("user_id");
																card.recharge_card = obj
																		.getString("recharge_card");
																card.recharge_money = obj
																		.getString("recharge_money");
																card.recharge_memeber = obj
																		.getString("recharge_memeber");
																card.telephone = obj
																		.getString("telephone");
																card.add_time = obj
																		.getString("add_time");
																res.add(card);
															}
															Const.log(Tag,
																	"加载数据完毕");
														}
													} else {
														Const.log(Tag,
																"设置hasdata为假");
														hasData = false;
														res.add(new RechargeInfo());
													}

													dialog.dismiss();
													rechargeDayAdapter = new MyRechargeDayAdapter(
															getActivity(), res,
															hasData);
													// if (firstRecharge) {
													// dayView.setAdapter(dayAdapter);
													// firstRecharge = false;
													// } else {
													// dayAdapter
													// .notifyDataSetChanged();
													// }
													dayView.setAdapter(rechargeDayAdapter);
													dialog.dismiss();
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
							// Const.showToast(context, "加载充值信息详情！");
							// // 加载假数据
							// RechargeInfo card = new RechargeInfo();
							// card.id = "1";
							// card.user_id = "33";
							// card.recharge_card = "123";
							// card.recharge_money = "10000";
							// card.recharge_memeber = "关印";
							// card.telephone = "18829282408";
							// card.add_time = "11:11";
							// res.add(card);
							// dayAdapter = new MyRechargeDayAdapter(
							// getActivity(), res);
							// if (firstDay) {
							// dayView.setAdapter(dayAdapter);
							// firstDay = false;
							// } else {
							// dayAdapter.notifyDataSetChanged();
							// }
						} else {
							// 这是消费记录
							// 在这里获取每个月具体到每一天的数据
							dialog = ProgressDialog.show(context, null,
									"正在查询消费记录请稍候...");
							Const.showToast(context, "加载消费信息详情！");
							final JSONObject jsonObj = new JSONObject();

							try {
								jsonObj.put("id",
										app.sp.getString("station_id", "0"));
								jsonObj.put("year", (2016 + positionYear) + "");
								jsonObj.put("month", (positionMonth + 1) + "");

							} catch (JSONException e) {
								e.printStackTrace();
							}

							OkHttpUtils
									.post()
									.url(Const.serverurl)
									.addParams("route", Const.apiRouteMonthInfo)
									.addParams("token",
											app.sp.getString("token", null))
									.addParams("jsonText", jsonObj.toString())
									.build().execute(new StringCallback() {
										@Override
										public void onError(Call request,
												Exception e, int arg2) {
											dialog.dismiss();
											Const.showToast(context, "联网失败:"
													+ e.getMessage());
										}

										@Override
										public void onResponse(String response,
												int arg1) {
											Const.log(Tag, response);
											JSONObject jsonObj = null;
											try {
												jsonObj = new JSONObject(
														response);
											} catch (JSONException e) {
												e.printStackTrace();
											}
											try {
												if (jsonObj
														.getJSONObject("status")
														.getString("succeed")
														.equals("1")) {

													// 在这里更新历史总额和消费总额

													ConsumeInfoActivity
															.setUp("历史总额："
																	+ jsonObj
																			.getJSONObject(
																					"data")
																			.getString(
																					"addup"),
																	" 消费总额："
																			+ jsonObj
																					.getJSONObject(
																							"data")
																					.getString(
																							"addup"));
													Const.log(Tag,
															"------------接收到正确的数据！");
													JSONArray result = jsonObj
															.getJSONObject(
																	"data")
															.getJSONArray(
																	"result");
													cons.clear();
													if (result != null
															&& result.length() > 0) {
														for (int i = 0; i < result
																.length(); i++) {
															ConsumeInfo consInfo = new ConsumeInfo();
															JSONObject obj = result
																	.getJSONObject(i);
															consInfo.car_sum = obj
																	.getString("car_sum");
															consInfo.oil_mass = obj
																	.getString("oil_mass");
															consInfo.price_mass = obj
																	.getString("price_mass");
															consInfo.update_time = obj
																	.getString("update_time");

															cons.add(consInfo);
														}

														Const.log(Tag, "加载数据完毕");

														dialog.dismiss();

													} else {
														Const.log(Tag,
																"hasInfo is null");
														hasInfo = false;
														cons.add(new ConsumeInfo());
													}
													Const.log(Tag, "cons"
															+ cons.toString());

													conDayAdapter = new MyConsumeDayAdapter(
															getActivity(),
															cons, hasInfo);

													dayView.setAdapter(conDayAdapter);
													dialog.dismiss();
												} else {
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
							if (hasInfo) {

								dayView.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int datePosition, long id) {

										Intent intent = new Intent(context,
												ConsumeRecordActivity.class);
										intent.putExtra("date",
												cons.get(datePosition)
														.getTime());
										intent.putExtra("car_sum",
												cons.get(datePosition)
														.getCar_sum());
										intent.putExtra("oil_mass",
												cons.get(datePosition)
														.getOil_mass());
										intent.putExtra("price_mass",
												cons.get(datePosition)
														.getPrice_mass());
										startActivity(intent);
									}
								});
							}

							// // 加载假数据
							// ConsumeInfo consInfo = new ConsumeInfo();
							// consInfo.car_sum = "29";
							// consInfo.oil_mass = "23";
							// consInfo.price_mass = "5120";
							// cons.add(consInfo);
						}
						// conDayAdapter = new
						// MyConsumeDayAdapter(getActivity(),
						// cons);
						// if (firstDay) {
						// dayView.setAdapter(conDayAdapter);
						// firstDay = false;
						// } else {
						// conDayAdapter.notifyDataSetChanged();
						// }
					}

				});

			}
		});
	}

	private void initDatas() {

		years = new ArrayList<String>();
		for (int i = 2016; i < 2018; i++) {
			if (curYear.equals(i + "")) {
				Const.log(Tag, "当年为" + curYear);
				curYearPosition = i - 2016;
			}
			years.add("" + i);
		}
		months = new ArrayList<String>();
		for (int i = 1; i < 13; i++) {
			if (i >= 10) {
				if (curMonth.equals(i + "")) {
					// Const.log(Tag, "当前月为" + curMonth);
					curMonthPosition = i - 1;
				}
			} else {
				if (curMonth.equals("0" + i)) {
					// Const.log(Tag, "当前月为" + curMonth);
					curMonthPosition = i - 1;
				}
			}
			months.add("0" + i);
		}
	}

	private void initViews(View view) {
		yeartView = (ListView) view.findViewById(R.id.lv_year);
		monthView = (ListView) view.findViewById(R.id.lv_month);
		dayView = (ListView) view.findViewById(R.id.lv_day);

		// yeartView.setChoiceMode(android.widget.AbsListView.CHOICE_MODE_SINGLE);
		// monthView.setChoiceMode(android.widget.AbsListView.CHOICE_MODE_SINGLE);

		// 初始化year的list view
		yearAdapter = new MyListViewAdapter(years, getActivity(), "year",
				curYearPosition, curMonthPosition);

		yeartView.setAdapter(yearAdapter);

		// 初始化month的list view
		monthAdapter = new MyListViewAdapter(months, getActivity(), "month",
				curYearPosition, curMonthPosition);

		monthView.setAdapter(monthAdapter);

	}
}
