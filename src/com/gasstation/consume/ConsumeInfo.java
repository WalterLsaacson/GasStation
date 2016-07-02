package com.gasstation.consume;

public class ConsumeInfo {
	// "status": {
	// "succeed": "1"
	// },
	// "data": {
	// "toltal_sum": "110000.00",
	// "addup": "130000.00",
	// "consumeup": "2000.00",
	// "result": [
	// {
	// "station_id": "2",
	// "car_sum": "4",
	// "oil_mass": "44.0000",
	// "price_mass": "220.0000",
	// "update_time": "2016-06-02 16:52:19"
	// }
	// ]
	// }
	// }
	public String car_sum;
	public String oil_mass;
	public String price_mass;
	public String update_time;

	public String getCar_sum() {
		return car_sum;
	}

	public String getUpdate_time() {

		return update_time.subSequence(8, 11) + "号：";

	}

	public String getTime() {
		return update_time;
	}

	public String getOil_mass() {
		return oil_mass;
	}

	public String getPrice_mass() {
		return price_mass;
	}
}
