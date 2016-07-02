package com.gasstation.consume;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gasstation.recharge.RechargeActivity;
import com.gasstation.user.PendingActivity;
import com.gasstation.util.Const;
import com.gasstation.util.MyApplication;
import com.guanyin.gasstation.R;

public class ConsumeInfoActivity extends Activity implements OnClickListener {

	// private Context Context = this;
	private RadioButton rb_consumeRecord;
	private RadioButton rb_rechargeRecord;

	private TextView tv_recharge;
	private TextView tv_back;

	private static TextView tv_record_price;
	private static TextView tv_his_price;
	private TextView tv_cur_price;

	private MyApplication app;

	private Intent intent;
	// false代表充值记录，true代表消费记录
	public static boolean record = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_comsum_info);

		if (app == null) {
			app = MyApplication.getInstance();
		}
		Const.firstLoadMonth = true;
		Const.firstLoadYear = true;
		// 初始化组件
		initView();

		// 写一个方法来设置消费总额和历史总额
		// setUp();
		tv_cur_price.setText("当前余额：" + app.sp.getString("toltal_sum", null));

		// 充值按钮的跳转
		tv_recharge.setOnClickListener(this);
		tv_back.setOnClickListener(this);

		rb_consumeRecord.setChecked(true);
		// rb_consumeRecord.performClick();
		MyFragment consumeFragment = new MyFragment();
		FragmentManager consumeManager = getFragmentManager();
		FragmentTransaction consumeTransaction = consumeManager
				.beginTransaction();
		consumeTransaction.replace(R.id.frame_fragment, consumeFragment);
		consumeTransaction.commit();
		// Const.firstLoadMonth = false;
		// Const.firstLoadYear = false;
		rb_consumeRecord.setOnClickListener(this);
		rb_rechargeRecord.setOnClickListener(this);
	}

	public static void setUp(String hist_price, String record_price) {
		tv_his_price.setText(hist_price);
		tv_record_price.setText(record_price);
	}

	private void initView() {
		rb_consumeRecord = (RadioButton) findViewById(R.id.rb_consume_record);
		rb_rechargeRecord = (RadioButton) findViewById(R.id.rb_recharge_record);
		tv_recharge = (TextView) findViewById(R.id.tv_recharge);
		tv_back = (TextView) findViewById(R.id.tv_back);

		tv_record_price = (TextView) findViewById(R.id.tv_record_price);
		tv_his_price = (TextView) findViewById(R.id.tv_his_price);
		tv_cur_price = (TextView) findViewById(R.id.tv_cur_price);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_recharge:
			intent = new Intent(this, RechargeActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_back:
			intent = new Intent(this, PendingActivity.class);
			startActivity(intent);
			break;
		case R.id.rb_consume_record:
			// Const.showToast(Context, "消费记录");
			record = true;
			MyFragment consumeFragment = new MyFragment();
			FragmentManager consumeManager = getFragmentManager();
			FragmentTransaction consumeTransaction = consumeManager
					.beginTransaction();
			consumeTransaction.replace(R.id.frame_fragment, consumeFragment);
			consumeTransaction.commit();
			break;
		case R.id.rb_recharge_record:
			// Const.showToast(Context, "充值记录");
			record = false;
			MyFragment rechargeFragment = new MyFragment();
			FragmentManager rechargeManager = getFragmentManager();
			FragmentTransaction rechargeTransaction = rechargeManager
					.beginTransaction();
			rechargeTransaction.replace(R.id.frame_fragment, rechargeFragment);
			rechargeTransaction.commit();
			break;
		default:
			break;
		}
	}

}
