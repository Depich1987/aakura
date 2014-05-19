package com.j1987.aakura.services.dao;

import java.util.List;

import com.j1987.aakura.domain.JCompany;


/**
 * An interface of <code>company</code> service.This interface allows data access about {@link JCompany}
 * @author Franck Agah
 *
 **/


public interface CompanyService {
	
	public JCompany findCompanyById(Long id);
	
	public List<JCompany> findAllCompanies();
	
	public JCompany merge(JCompany company);
	
	public void update(JCompany company);
	
	public void flush();

	public void clear();
}