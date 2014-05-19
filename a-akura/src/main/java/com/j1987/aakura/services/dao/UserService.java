package com.j1987.aakura.services.dao;

import java.util.List;

import com.j1987.aakura.domain.JRole;
import com.j1987.aakura.domain.JUser;

public interface UserService {

	public List<JUser> findUserByUserNameEquals(String userName);

	public long countUsers();

	public List<JUser> findAllUsers();
	
	public List<JUser> findAllUsersWithoutFirstUser();

	public JUser findUserById(Long id);
	
	public JUser findUserByIdWithoutFirstUser(Long id);

	public List<JUser> findUserEntries(int firstResult, int maxResults);
	
	public List<JUser> findUserEntriesWithoutFirstUser(int firstResult, int maxResults);

	public void persist(JUser user);

	public void remove(Long id);

	public void flush();

	public void clear();

	public JUser merge(JUser user);
	
	public void update(JUser user);

	public List<JRole> findRoleByRoleNameEquals(String dbRoleName);

}
