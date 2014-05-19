package com.j1987.aakura.services.dao;

import java.util.List;

import com.j1987.aakura.domain.JActivity;

/**
 * An interface of <code>activity</code> service.This interface allows data access about {@link JActivity}
 * @author Franck Agah
 *
 **/

public interface ActivityService {

	public long countActivities();

	public List<JActivity> findAllActivities();

	public JActivity findActivity(Long id);

	public List<JActivity> findActivityEntries(int firstResult, int maxResults);

	public void persist(JActivity activity);

	public void remove(Long id);

	public void flush();

	public void clear();

	public JActivity merge(JActivity activity);
	
	public void update(JActivity activity);

	public List<JActivity> findActivitiesByCodeEquals(String code);
	
	public List<JActivity> findActivitiesByActivityCodeList(List<String> activityCodeList);

}
