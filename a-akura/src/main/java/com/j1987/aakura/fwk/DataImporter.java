package com.j1987.aakura.fwk;

import org.springframework.stereotype.Component;

/**
 * <tt>ASDataImporter</tt> helps import data within the application. 
 * As a Singleton, this process happens only once when the first and only instance is loaded  
 * 
 * @author cbalde
 *
 */
@Component
public class DataImporter  {
	
	private static DataImporter sInstance;
	
	
	private DataImporter() {
	}
	
	private DataImporter(String fileName, String driver, String url, String userName, String password) {
		JUtils.executeSql(fileName, driver, url, userName, password);
	}
	
	/**
	 * Singleton access from outside
	 * */
	public static DataImporter getInstance(String fileName, String driver, String url, String userName, String password) {
		if (sInstance == null) {
			sInstance = new DataImporter(fileName, driver, url, userName, password);
		}
		return sInstance;
	}
	
}
