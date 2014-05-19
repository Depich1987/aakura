package com.j1987.aakura.web.form;

import java.io.Serializable;

public class FilterPayment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4136886227483711674L;

	private String activityFilter;
	
	public FilterPayment() {
		// TODO Auto-generated constructor stub
	}

	public String getActivityFilter() {
		return activityFilter;
	}

	public void setActivityFilter(String activityFilter) {
		this.activityFilter = activityFilter;
	}

}
