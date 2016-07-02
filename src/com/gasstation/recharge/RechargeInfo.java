package com.gasstation.recharge;

public class RechargeInfo {

	// "id": "3",
	// "user_id": "1",
	// "recharge_card": "11111",
	// "recharge_money": "800.00",
	// "recharge_memeber": "李丽丽",
	// "telephone": "111111111",
	// "add_time": "2016-05-01 10:02:24"
	public String id;
	public String user_id;
	public String recharge_card;
	public String recharge_money;
	public String recharge_memeber;
	public String telephone;
	public String add_time;

	public String getRecharge_money() {
		return recharge_money ;
	}

	public String getRecharge_memeber() {
		return "充值人：" + recharge_memeber;
	}

	public String getTelephone() {
		return "电话：" + telephone;
	}
}
