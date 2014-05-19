package com.j1987.aakura.services.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.j1987.aakura.domain.JCompany;
import com.j1987.aakura.fwk.JUtils;

public class CompanyServiceImpl implements CompanyService {
	
	@PersistenceContext(name=JUtils.DB_PERSISTENCE_UNIT)
	private EntityManager entityManager;
	
	public CompanyServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public JCompany findCompanyById(Long id) {
		
		if (id == null) return null;
        return entityManager.find(JCompany.class, id);
	}

	@Override
	public List<JCompany> findAllCompanies() {
		return entityManager.createQuery("SELECT c FROM JCompany c", JCompany.class).getResultList();
	}

	@Override
	@Transactional
	public JCompany merge(JCompany company) {
		
		JCompany merged = this.entityManager.merge(company);
        this.entityManager.flush();
        return merged;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(JCompany company) {
		
    	this.entityManager.createQuery("UPDATE JCompany c SET c.name = :name,  c.description = :description WHERE c.id = :id")
    	.setParameter("id", company.getId())
    	.setParameter("name", company.getName())
    	.setParameter("description",company.getDescription())
    	.executeUpdate();

	}

	@Override
	@Transactional
	public void flush() {
		this.entityManager.flush();

	}

	@Override
	@Transactional
	public void clear() {
		this.entityManager.clear();

	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
