package com.j1987.aakura.web.statistics;

import java.io.Serializable;
import java.math.BigDecimal;

public class ActivityStatistics implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8155893394968550624L;
	
	private String selectedActivities;
	
	private long countPaymentIn;
	
	private long countPaymentOut;
	
	private BigDecimal aggregatePaymentIn = new BigDecimal("0");
	
	private BigDecimal aggregatePaymentOut = new BigDecimal("0");
	
	private String bestActivityName;
	
	private BigDecimal balanceAmount  = new BigDecimal("0");; 

	public ActivityStatistics() {
		// TODO Auto-generated constructor stub
	}

	public String getSelectedActivities() {
		return selectedActivities;
	}

	public void setSelectedActivities(String selectedActivities) {
		this.selectedActivities = selectedActivities;
	}

	public Long getCountPaymentIn() {
		return countPaymentIn;
	}

	public void setCountPaymentIn(Long countPaymentIn) {
		this.countPaymentIn = countPaymentIn;
	}

	public Long getCountPaymentOut() {
		return countPaymentOut;
	}

	public void setCountPaymentOut(Long countPaymentOut) {
		this.countPaymentOut = countPaymentOut;
	}

	public BigDecimal getAggregatePaymentIn() {
		return aggregatePaymentIn;
	}

	public void setAggregatePaymentIn(BigDecimal aggregatePaymentIn) {
		this.aggregatePaymentIn = aggregatePaymentIn;
	}

	public BigDecimal getAggregatePaymentOut() {
		return aggregatePaymentOut;
	}

	public void setAggregatePaymentOut(BigDecimal aggregatePaymentOut) {
		this.aggregatePaymentOut = aggregatePaymentOut;
	}

	public String getBestActivityName() {
		return bestActivityName;
	}

	public void setBestActivityName(String bestActivityName) {
		this.bestActivityName = bestActivityName;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}
	
	
}