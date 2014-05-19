package com.j1987.aakura.web.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReportFilterForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4623403080959414252L;
	
	private List<String> activityCodeList = new ArrayList<String>();
	private	Boolean paymentIn = true;
	private	Boolean paymentOut = true;
	
	private String format;
	
	private String startDate;

	private String endDate;

	public ReportFilterForm() {
		// TODO Auto-generated constructor stub
	}

	public List<String> getActivityCodeList() {
		return activityCodeList;
	}

	public void setActivityCodeList(List<String> activityCodeList) {
		this.activityCodeList = activityCodeList;
	}

	public Boolean getPaymentIn() {
		return paymentIn;
	}

	public void setPaymentIn(Boolean paymentIn) {
		this.paymentIn = paymentIn;
	}

	public Boolean getPaymentOut() {
		return paymentOut;
	}

	public void setPaymentOut(Boolean paymentOut) {
		this.paymentOut = paymentOut;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	
}
