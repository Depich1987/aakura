package com.j1987.aakura.fwk;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;

import com.j1987.aakura.domain.JActivity;
import com.j1987.aakura.domain.JCompany;

public class JUtils {
	
	public static final String DB_PERSISTENCE_UNIT =  "persistenceUnit";
	
	public static final String SECURITY_UNAUTHENTICATED_USER 	= "anonymousUser";
	
	public static final SimpleDateFormat DATE_FORMAT_URL = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	public static final String AUDIT_MSG_CREATE_COMPANY = "Création d'une Entreprise";
	public static final String AUDIT_MSG_UPDATE_COMPANY = "Modification d'une Entreprise";
	
	public static final String AUDIT_MSG_CREATE_ACTIVITY = "Création d'une nouvelle activite";
	public static final String AUDIT_MSG_UPDATE_ACTIVITY = "Modification d'une activite";
	public static final String AUDIT_MSG_DELETE_ACTIVITY = "Suppression d'une activite";
	
	public static final String AUDIT_MSG_CREATE_USER = "Création d'un utilisateur";
	public static final String AUDIT_MSG_UPDATE_USER_DETAILS = "Modification du details d'un utilisateur";
	public static final String AUDIT_MSG_DELETE_USER = "Suppression d'un utilisateur";
	public static final String AUDIT_MSG_ASSIGNROLE_USER = "Assignation de role a un utilisateur";
	public static final String AUDIT_MSG_ASSIGNACTIVITY_USER = "Assignation d'une activite a un utilisateur";
	public static final String AUDIT_MSG_UNASSIGN_USER = "Désassignation d'un utilisateur";
	public static final String AUDIT_MSG_CHANGEPASSWORD_USER = "Modification de Mot de passe d'un utilisateur";
	
	public static final String AUDIT_MSG_CREATE_PAYMENT_OUT = "Enregistrement d'une Depense";
	public static final String AUDIT_MSG_CREATE_PAYMENT_IN = "Enregistrement d'une Recette";
	
	public static final String AUDIT_MSG_UPDATE_PAYMENT_OUT = "Modification d'une Depense";
	public static final String AUDIT_MSG_UPDATE_PAYMENT_IN = "Modification d'une Recette";
	
	public static final String AUDIT_MSG_DELETE_PAYMENT_OUT = "Suppression d'une Depense";
	public static final String AUDIT_MSG_DELETE_PAYMENT_IN = "Suppression d'une Recette";
	
	public static final String AUDIT_MSG_LOGIN = "Connexion utilisateur ";
	public static final String AUDIT_MSG_LOGOUT = "Déconnexion utilisateur ";
	
	public static final String DB_ROLE_ADMIN = "ROLE_ADMIN";
	public static final String DB_ROLE_SUPERVISOR = "ROLE_SUPERVISOR";
	public static final String DB_ROLE_MANAGER = "ROLE_ACTIVITY_MANAGER";
	
	public static final String DB_PAYMENT_OUT = "Depense";
	public static final String DB_PAYMENT_IN = "Recette";
	
	public static final String DB_UI_ROLE_ADMIN = "ADMINISTRATEUR";
	public static final String DB_UI_ROLE_SUPERVISOR = "SUPERVISEUR";
	public static final String DB_UI_ROLE_MANAGER = "GERANT";
	
	public static final String HTTP_SESSION_COMPANY_CODE =  "userCompanyCode";
	public static final String HTTP_SESSION_COMPANY_NAME =  "userCompanyName";
	
	public static final String HTTP_SESSION_ACTIVITY_CODE =  "userActivityCode";
	public static final String HTTP_SESSION_ACTIVITY_NAME =  "userActivityName";

	
	
	/**
	 * Allows to run an SQL script from within our system. 
	 * This is basically done via an Ant built-in Task
	 * */
	public static void executeSql(String sqlFilePath, String driver, String url, String userName, String password) {
	    final class SqlExecuter extends SQLExec {
	        public SqlExecuter() {
	            Project project = new Project();
	            project.init();
	            setProject(project);
	            setTaskType("sql");
	            setTaskName("sql");
	        }
	    }
	    
	    SqlExecuter executer = new SqlExecuter();
	    executer.setSrc(new File(sqlFilePath));
	    executer.setDriver(driver);
	    executer.setUrl(url);
	    executer.setUserid(userName);
	    executer.setPassword(password);
	    executer.execute();
	}
	
    public static Map<String, String> uiRoleNamesMap() {
    	
    	Map<String, String> roleNamesMap = new HashMap<String, String>();

    	roleNamesMap.put(JUtils.DB_ROLE_ADMIN, JUtils.DB_UI_ROLE_ADMIN);
    	roleNamesMap.put(JUtils.DB_ROLE_SUPERVISOR, JUtils.DB_UI_ROLE_SUPERVISOR);
    	roleNamesMap.put(JUtils.DB_ROLE_MANAGER, JUtils.DB_UI_ROLE_MANAGER);
    	
        return roleNamesMap;
    }
    
    public static Map<String, String> dbRoleNamesMap() {
    	
    	Map<String, String> roleNamesMap = new HashMap<String, String>();

    	roleNamesMap.put(JUtils.DB_UI_ROLE_ADMIN, JUtils.DB_ROLE_ADMIN);
    	roleNamesMap.put( JUtils.DB_UI_ROLE_SUPERVISOR, JUtils.DB_ROLE_SUPERVISOR);
    	roleNamesMap.put( JUtils.DB_UI_ROLE_MANAGER, JUtils.DB_ROLE_MANAGER);

        return roleNamesMap;
    }
    
    /**
     * Retrieves a a comma separated String objects from a Collection. 
     * Useful for the view
     * */
	public static <T> String retrieveObjectPropertiesAsString(Collection<T> collection) {
		StringBuffer buffer = new StringBuffer();
		
		for (T obj : collection) {
			String prop = null;
			// Access the property (ex: the name)
			if (obj instanceof JCompany) {
				prop = ((JCompany)obj).getName();
				
			}else if(obj instanceof JActivity){
				prop = ((JActivity)obj).getName();
				
			}
			
			if (buffer.indexOf(prop) == -1) {
				buffer.append(prop).append(", ");
			}
		}
		
		String names = buffer.toString();
		if (!StringUtils.isEmpty(names)) {
			int lastIndex = names.lastIndexOf(", ");
			names = names.substring(0, lastIndex);
		}
		return names;
	}	
	
}