package com.gasstation.user;

public class ReguelInfo {

	private String journalIdString;
	public String memberId;
	public String invoiceCode;
	public String invoiceName;
	public String stationId;
	public String carNumber;
	public String gunNumber;
	public String gasType;
	public String price;
	public String totalPrice;
	public String oilMass;
	public String noticeState;
	public String printState;
	public String insertTime;
	public String journalId;

	public String getJournalId() {
		return journalId;
	}

	public void setJournalId(String journalId) {
		this.journalId = journalId;
	}

	public String getJournalIdString() {
		return journalIdString;
	}

	public void setJournalIdString(String journalIdString) {
		this.journalIdString = journalIdString;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}

	public String getInvoiceName() {
		return invoiceName;
	}

	public void setInvoiceName(String invoiceName) {
		this.invoiceName = invoiceName;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getCarNumber() {
		return carNumber;
	}

	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	public String getGunNumber() {
		return "油    枪：" + gunNumber + "号";
	}

	public void setGunNumber(String gunNumber) {
		this.gunNumber = gunNumber;
	}

	public String getGasType() {
		return "油    品：" + gasType;
	}

	public void setGasType(String gasType) {
		this.gasType = gasType;
	}

	public String getPrice() {
		return "单    价：" + price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getTotalPrice() {
		return "总    价：" + totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getOilMass() {
		return "加油量：" + oilMass;
	}

	public void setOilMass(String oilMass) {
		this.oilMass = oilMass;
	}

	public String getNoticeState() {
		return noticeState;
	}

	public void setNoticeState(String noticeState) {
		this.noticeState = noticeState;
	}

	public String getPrintState() {
		return printState;
	}

	public void setPrintState(String printState) {
		this.printState = printState;
	}

	public String getInsertTime() {

		return insertTime.substring(11, 16);
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}

}
