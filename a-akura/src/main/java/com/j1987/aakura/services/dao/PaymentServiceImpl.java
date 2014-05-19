package com.j1987.aakura.services.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.transaction.annotation.Transactional;

import com.j1987.aakura.domain.JActivity;
import com.j1987.aakura.domain.JPayment;
import com.j1987.aakura.fwk.JUtils;

public class PaymentServiceImpl implements PaymentService {

	@PersistenceContext(name=JUtils.DB_PERSISTENCE_UNIT)
	private EntityManager entityManager;
	
	public PaymentServiceImpl() {
		// TODO Auto-generated constructor stub
	}
    
	@Override
    public long countPayments() {
        return entityManager.createQuery("SELECT COUNT(p) FROM JPayment p", Long.class).getSingleResult();
    }
    
    @Override
    public List<JPayment> findAllPayments() {
        return entityManager.createQuery("SELECT p FROM JPayment p ORDER BY p.creationDate DESC", JPayment.class).getResultList();
    }
    
    
    @Override
    public List<JPayment> findAllPaymentsByActivityEquals(JActivity activity) {
        return entityManager.createQuery("SELECT p FROM JPayment p WHERE p.activity = :activity ORDER BY p.paymentType, p.creationDate DESC", JPayment.class)
        		.setParameter("activity", activity)
        		.getResultList();
    }
    
    @Override
    public List<JPayment> findPaymentsByActivityEqualsEntries(JActivity activity, int firstResult, int maxResults) {
        
    	return entityManager.createQuery("SELECT p FROM JPayment p WHERE p.activity = :activity ORDER BY  p.paymentType , p.creationDate DESC", JPayment.class)
        		.setParameter("activity", activity)
        		.setFirstResult(firstResult)
        		.setMaxResults(maxResults)
        		.getResultList();
    }
    
    @Override
    public List<JPayment> findPaymentsByActivityEqualsAndPaymentDateBetweenEntries(JActivity activity, Date startDate, Date endDate, int firstResult, int maxResults) {
    	
        if (startDate == null) throw new IllegalArgumentException("The startDate argument is required");
        if (endDate == null) throw new IllegalArgumentException("The endDate argument is required");
        
        Date endDateTemp = DateUtils.addDays(endDate, 1);
        
        return entityManager.createQuery("SELECT p FROM JPayment AS p WHERE p.activity = :activity AND p.paymentDate BETWEEN :startDate AND :endDate ORDER BY  p.paymentType, p.creationDate DESC", JPayment.class)
        .setParameter("activity", activity)
        .setParameter("startDate", startDate, TemporalType.DATE)
        .setParameter("endDate", endDateTemp, TemporalType.DATE)
        .setFirstResult(firstResult)
        .setMaxResults(maxResults)
        .getResultList();
    }
    
    
    @Override
    public List<JPayment> findPaymentsByActivityEqualsAndPaymentDateBetween(JActivity activity, Date startDate, Date endDate) {
    	
        if (startDate == null) throw new IllegalArgumentException("The startDate argument is required");
        if (endDate == null) throw new IllegalArgumentException("The endDate argument is required");
        
        Date endDateTemp = DateUtils.addDays(endDate, 1);
        
        return entityManager.createQuery("SELECT p FROM JPayment AS p WHERE p.activity = :activity AND p.paymentDate BETWEEN :startDate AND :endDate ORDER BY p.paymentType, p.creationDate DESC", JPayment.class)
		.setParameter("activity", activity)
		.setParameter("startDate", startDate, TemporalType.DATE)
        .setParameter("endDate", endDateTemp, TemporalType.DATE)
        .getResultList();
    }
    
    @Override
    public List<JPayment> findPaymentsByActivityListAndPaymentDateBetweenEntries(List<JActivity> activities, List<String> paymentTypes, Date startDate, Date endDate,	int firstResult, int maxResults) {


        if (startDate == null) throw new IllegalArgumentException("The startDate argument is required");
        if (endDate == null) throw new IllegalArgumentException("The endDate argument is required");
        
        Date endDateTemp = DateUtils.addDays(endDate, 1);
        
        return entityManager.createQuery("SELECT p FROM JPayment AS p WHERE (p.activity IN :activities) AND (p.paymentType IN :paymentTypes) AND p.paymentDate BETWEEN :startDate AND :endDate ORDER BY p.paymentType, p.creationDate DESC", JPayment.class)
		.setParameter("activities", activities)
		.setParameter("paymentTypes", paymentTypes)
		.setParameter("startDate", startDate, TemporalType.DATE)
        .setParameter("endDate", endDateTemp, TemporalType.DATE)
        .setFirstResult(firstResult)
        .setMaxResults(maxResults)
        .getResultList();
    }
    
    @Override
    public List<JPayment> findPaymentsByActivityListAndPaymentDateBetween(List<JActivity> activities, List<String> paymentTypes, Date startDate, Date endDate) {
        
    	if (startDate == null) throw new IllegalArgumentException("The startDate argument is required");
        if (endDate == null) throw new IllegalArgumentException("The endDate argument is required");
        
        Date endDateTemp = DateUtils.addDays(endDate, 1);
        
        return entityManager.createQuery("SELECT p FROM JPayment AS p WHERE (p.activity IN :activities) AND (p.paymentType IN :paymentTypes)  AND p.paymentDate BETWEEN :startDate AND :endDate ORDER BY p.paymentType,  p.creationDate DESC", JPayment.class)
		.setParameter("activities", activities)
		.setParameter("paymentTypes", paymentTypes)
		.setParameter("startDate", startDate, TemporalType.DATE)
        .setParameter("endDate", endDateTemp, TemporalType.DATE)
        .getResultList();
    }
    
    
    @Override
    public List<JPayment> findPaymentsByPaymentDateBetween(Date startDate, Date endDate) {
    	
        if (startDate == null) throw new IllegalArgumentException("The startDate argument is required");
        if (endDate == null) throw new IllegalArgumentException("The endDate argument is required");
        
        Date endDateTemp = DateUtils.addDays(endDate, 1);
        
        return entityManager.createQuery("SELECT p FROM JPayment AS p WHERE p.paymentDate BETWEEN :startDate AND :endDate ORDER BY p.paymentDate, p.paymentType DESC", JPayment.class)
        .setParameter("startDate", startDate, TemporalType.DATE)
        .setParameter("endDate", endDateTemp, TemporalType.DATE)
        .getResultList();
    }
    
    
    @Override
    public JPayment findPayment(Long id) {
        if (id == null) return null;
        return entityManager.find(JPayment.class, id);
    }
    
    @Override
    public List<JPayment> findPaymentEntries(int firstResult, int maxResults) {
    	
        return this.entityManager.createQuery("SELECT p FROM JPayment p ORDER BY p.paymentDate, p.paymentType DESC", JPayment.class)
        		.setFirstResult(firstResult)
        		.setMaxResults(maxResults)
        		.getResultList();
    }
    
    @Transactional
    @Override
    public void persist(JPayment payment) {
        this.entityManager.persist(payment);
    }
    
    @Transactional
    @Override
    public void remove(Long id) {

        JPayment attached = findPayment(id);
        this.entityManager.remove(attached);
    }
    
    @Transactional
    @Override
    public void flush() {
        this.entityManager.flush();
    }
    
    @Transactional
    @Override
    public void clear() {
        this.entityManager.clear();
    }
    
    @Transactional
    @Override
    public JPayment merge(JPayment payment) {
        JPayment merged = this.entityManager.merge(payment);
        this.entityManager.flush();
        return merged;
    }
    
    @Override
    @Transactional
    public void update(JPayment payment) {
    	
		entityManager
		.createQuery("UPDATE JPayment p SET p.reference = :reference , p.description = :description , p.amount = :amount , p.paymentType = :paymentType , p.paymentDate = :paymentDate , p.modificationDate = :modificationDate , p.activity = :activity , p.modificatedBy = :modificatedBy WHERE p.id = :id")
		.setParameter("reference", payment.getReference())
		.setParameter("description", payment.getDescription())
		.setParameter("amount", payment.getAmount())
		.setParameter("paymentType", payment.getPaymentType())
		.setParameter("paymentDate", payment.getPaymentDate())
		.setParameter("modificationDate", payment.getModificationDate())
		.setParameter("activity", payment.getActivity())
		.setParameter("modificatedBy", payment.getModificatedBy())
		.setParameter("id", payment.getId())
		.executeUpdate();
    	
    }
    
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
