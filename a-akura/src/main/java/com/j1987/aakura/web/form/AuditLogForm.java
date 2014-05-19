package com.j1987.aakura.web.form;

import java.io.Serializable;
import java.util.Date;

public class AuditLogForm implements Serializable {

	private static final long serialVersionUID = 6045215175583750131L;

	private Date startDate;
	private Date endDate;
	
	// HACK
	private String startDateAsString;
	private String endDateAsString;
	
	public AuditLogForm() {
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStartDateAsString() {
		return startDateAsString;
	}

	public void setStartDateAsString(String startDateAsString) {
		this.startDateAsString = startDateAsString;
	}

	public String getEndDateAsString() {
		return endDateAsString;
	}

	public void setEndDateAsString(String endDateAsString) {
		this.endDateAsString = endDateAsString;
	}

}
