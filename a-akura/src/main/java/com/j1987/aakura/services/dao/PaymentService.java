package com.j1987.aakura.services.dao;

import java.util.Date;
import java.util.List;

import com.j1987.aakura.domain.JActivity;
import com.j1987.aakura.domain.JPayment;

public interface PaymentService {

	public long countPayments();

	public List<JPayment> findAllPayments();

	public JPayment findPayment(Long id);

	public List<JPayment> findPaymentEntries(int firstResult, int maxResults);

	public void persist(JPayment payment);

	public void remove(Long id);

	public void flush();

	public void clear();

	public JPayment merge(JPayment payment);

	public List<JPayment> findAllPaymentsByActivityEquals(JActivity activity);

	public List<JPayment> findPaymentsByActivityEqualsEntries(JActivity activity,int firstResult, int maxResults);

	public List<JPayment> findPaymentsByPaymentDateBetween(Date startDate, Date endDate);

	public List<JPayment> findPaymentsByActivityEqualsAndPaymentDateBetween(JActivity activity, Date startDate, Date endDate);

	public List<JPayment> findPaymentsByActivityEqualsAndPaymentDateBetweenEntries(JActivity activity, Date startDate, Date endDate, int firstResult, int maxResults);

	public List<JPayment> findPaymentsByActivityListAndPaymentDateBetweenEntries(List<JActivity> activities, List<String> paymentTypes, Date startDate, Date endDate, int firstResult, int maxResults);
	
	public List<JPayment> findPaymentsByActivityListAndPaymentDateBetween(List<JActivity> activities, List<String> paymentTypes, Date startDate, Date endDate);
	
	public void update(JPayment payment);

}
