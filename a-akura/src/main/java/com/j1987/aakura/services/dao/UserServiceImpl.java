package com.j1987.aakura.services.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.j1987.aakura.domain.JRole;
import com.j1987.aakura.domain.JUser;
import com.j1987.aakura.fwk.JUtils;

public class UserServiceImpl implements UserService {

		@PersistenceContext(name=JUtils.DB_PERSISTENCE_UNIT)
		private EntityManager entityManager;
	    

		public UserServiceImpl() {
		// TODO Auto-generated constructor stub
		}
	    
		
		@Override
	    public long  countUsers() {
	        return entityManager.createQuery("SELECT COUNT(u) FROM JUser u", Long.class).getSingleResult();
	    }
	    
	    
	    @Override
	    public List<JUser> findAllUsers() {
	        return entityManager.createQuery("SELECT u FROM JUser u", JUser.class).getResultList();
	    }
	    
	    @Override
	    public List<JUser> findAllUsersWithoutFirstUser() {

	    	return entityManager.createQuery("SELECT u FROM JUser u WHERE u.id <> :id", JUser.class)
	    			.setParameter("id", Long.valueOf("1"))
	    			.getResultList();
	    }
	    
	    
	    @Override
	    public JUser findUserById(Long id) {
	        if (id == null) return null;
	        return entityManager.find(JUser.class, id);
	    }
	    
	    @Override
	    public JUser findUserByIdWithoutFirstUser(Long id) {
	    	
			return entityManager.createQuery("SELECT u FROM JUser u WHERE u.id <> :adminId AND u.id = :id", JUser.class)
			.setParameter("adminId", Long.valueOf("1"))
			.setParameter("id", id)
			.getSingleResult();
	    }
	    
	    
	    @Override
	    public List<JUser> findUserEntries(int firstResult, int maxResults) {
	        return entityManager.createQuery("SELECT u FROM JUser u", JUser.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	    }
	    
	    @Override
	    public List<JUser> findUserEntriesWithoutFirstUser(int firstResult,	int maxResults) {
	    
	    	return entityManager.createQuery("SELECT u FROM JUser u WHERE u.id <> :adminId", JUser.class)
	    			.setParameter("adminId", Long.valueOf("1"))
	    			.setFirstResult(firstResult)
	    			.setMaxResults(maxResults)
	    			.getResultList();
	    }
	    
		@Override
		public List<JUser> findUserByUserNameEquals(String userName) {
	        if (StringUtils.isEmpty(userName)) 
	        	throw new IllegalArgumentException("The 'userName' argument is required");
	        
	        return entityManager.createQuery("SELECT u FROM JUser AS u WHERE u.userName = :userName", JUser.class)
	    		.setParameter("userName", userName)
	    		.getResultList();
		}
		
		@Override
		public List<JRole> findRoleByRoleNameEquals(String dbRoleName) {
			
	        if (StringUtils.isEmpty(dbRoleName)) 
	        	throw new IllegalArgumentException("The 'dbRoleName' argument is required");
	        
	        return entityManager.createQuery("SELECT r FROM JRole AS r WHERE r.name = :dbRoleName", JRole.class)
	    		.setParameter("dbRoleName", dbRoleName)
	    		.getResultList();
			
		}
	    
	    @Transactional
	    @Override
	    public void persist(JUser user) {
	        
	        this.entityManager.persist(user);
	    }
	    
	    @Transactional
	    @Override
	    public void remove(Long id) {

	            JUser attached = findUserById(id);
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
	    public JUser merge(JUser user) {
	        
	        JUser merged = this.entityManager.merge(user);
	        this.entityManager.flush();
	        return merged;
	    }
	    
	    @Transactional(propagation = Propagation.REQUIRED)
		@Override
		public void update(JUser user) {
	    	this.entityManager.createQuery("UPDATE JUser u SET u.userName = :userName, u.lastName = :lastName, "
	    			+ " u.firstName = :firstName, u.email = :email, u.password = :password "
	    			+ " WHERE u.id = :id")
	    	.setParameter("id", user.getId())
	    	.setParameter("userName", user.getUserName())
	    	.setParameter("lastName", user.getLastName())
	    	.setParameter("firstName", user.getFirstName())
	    	.setParameter("email",user.getEmail())
	    	.setParameter("password",user.getPassword())
	    	.executeUpdate();
		}
	    
		public void setEntityManager(EntityManager entityManager) {
			this.entityManager = entityManager;
		}
}
