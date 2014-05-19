package com.j1987.aakura.services.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.j1987.aakura.domain.JActivity;
import com.j1987.aakura.fwk.JUtils;

public class ActivityServiceImpl implements ActivityService {

	@PersistenceContext(name=JUtils.DB_PERSISTENCE_UNIT)
	private EntityManager entityManager;
	
	public ActivityServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	 
	@Override
    public  long countActivities() {
        return entityManager.createQuery("SELECT COUNT(o) FROM JActivity o", Long.class).getSingleResult();
    }
    
    @Override
    public  List<JActivity> findAllActivities() {
        return entityManager.createQuery("SELECT o FROM JActivity o", JActivity.class).getResultList();
    }
    
    @Override
    public  JActivity findActivity(Long id) {
        if (id == null) return null;
        return entityManager.find(JActivity.class, id);
    }
    
    @Override
    public List<JActivity> findActivitiesByCodeEquals(String code) {
    	
        if (code == null || code.length() == 0) throw new IllegalArgumentException("The code argument is required");
       
        return entityManager.createQuery("SELECT o FROM JActivity AS o WHERE o.code = :code", JActivity.class)
        		.setParameter("code", code)
        		.getResultList();
        
    }
    
    @Override
    public  List<JActivity> findActivityEntries(int firstResult, int maxResults) {
        return entityManager.createQuery("SELECT o FROM JActivity o", JActivity.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Override
    public List<JActivity> findActivitiesByActivityCodeList(List<String> activityCodeList) {
    	
    	return entityManager.createQuery("SELECT o FROM JActivity AS o WHERE (o.code IN :activityCodeList)", JActivity.class)
        		.setParameter("activityCodeList", activityCodeList)
        		.getResultList();
    }
    
    @Transactional
    @Override
    public void persist(JActivity activity) {
       
        this.entityManager.persist(activity);
    }
    
    @Transactional
    @Override
    public void remove(Long id) {

            JActivity attached = findActivity(id);
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
    public JActivity merge(JActivity activity) {
        
        JActivity merged = this.entityManager.merge(activity);
        this.entityManager.flush();
        return merged;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void update(JActivity activity) {
    	
    	this.entityManager.createQuery("UPDATE JActivity a SET a.name = :name, a.code = :code, a.description = :description, a.company = :company WHERE a.id = :id")
    	.setParameter("id", activity.getId())
    	.setParameter("name", activity.getName())
    	.setParameter("code", activity.getCode())
    	.setParameter("description",activity.getDescription())
    	.setParameter("company",activity.getCompany())
    	.executeUpdate();
    	
    }
	
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
