package com.j1987.aakura.domain;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * Log of all significant events in the system
 * @author alain
 *
 */
@RooJavaBean
@RooToString
@RooJpaActiveRecord(table = "J_AUDITLOG", finders = { "findJAuditLogsByEventCategory" })
public class JAuditLog {
	
	private static Logger logger = Logger.getLogger(JAuditLog.class);

    /**
     * name of user who performs the change
     */
    private String userId;

    /**
     * details of the change
     */
    private String description;

    /**
     */
    @NotNull
    @Enumerated
    private JEventCategory eventCategory;

    /**
     * Name of event
     */
    @NotNull
    @Enumerated
    private JEvent event;

    /**
     * Parameter 1
     */
    private String param1;

    /**
     * Parameter 2
     */
    private String param2;
    
    /**
     * 
     */
    private String ipAddress;
    
    
    /**
     * 
     */
    private String hostClient;
    

    /**
     * Date when change was made
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat()
    private Date eventDate;

    // UI convenience
    private String eventDateAsString;
    
    /**
     * New event log
     * @param userId
     * @param description
     * @param eventCategory
     * @param event
     * @param initialValue
     * @param newValue
     */
    public JAuditLog(String userId, String description, JEventCategory eventCategory, JEvent event, String param1, String param2) {
        super();
        this.eventDate = new Date(); //current date always
        this.userId = userId;
        this.description = description;
        this.eventCategory = eventCategory;
        this.event = event;
        this.param1 = param1;
        this.param2 = param2;
    }

	public static List<JAuditLog> findJAuditLogsByEventCategory(JEventCategory eventCategory, int firstResult, int maxResults, Date startDate, Date endDate) {
        if (eventCategory == null) throw new IllegalArgumentException("The eventCategory argument is required");
        TypedQuery<JAuditLog> query = null; 
        
        if (startDate != null && endDate != null) {
            EntityManager em = JAuditLog.entityManager();
            query = em.createQuery("SELECT o FROM JAuditLog AS o WHERE o.eventCategory = :eventCategory AND (o.eventDate BETWEEN :startDate AND  :endDate) ORDER by o.eventDate DESC", JAuditLog.class);
            query.setParameter("eventCategory", eventCategory);
            query.setParameter("startDate", startDate, TemporalType.DATE);
            query.setParameter("endDate", endDate, TemporalType.DATE);
            
		} else if (startDate!= null && endDate == null) {
            EntityManager em = JAuditLog.entityManager();
            query = em.createQuery("SELECT o FROM JAuditLog AS o WHERE o.eventCategory = :eventCategory  AND o.eventDate >= :startDate ORDER by o.eventDate DESC", JAuditLog.class);
            query.setParameter("eventCategory", eventCategory);
            query.setParameter("startDate", startDate, TemporalType.DATE);
			
		} else if (startDate == null && endDate != null) {
            EntityManager em = JAuditLog.entityManager();
            query = em.createQuery("SELECT o FROM JAuditLog AS o WHERE o.eventCategory = :eventCategory AND o.eventDate <= :endDate ORDER by o.eventDate DESC", JAuditLog.class);
            query.setParameter("eventCategory", eventCategory);
            query.setParameter("endDate", endDate, TemporalType.DATE);
            
		} else if (startDate == null && endDate == null) {
            EntityManager em = JAuditLog.entityManager();
            query = em.createQuery("SELECT o FROM JAuditLog AS o WHERE o.eventCategory = :eventCategory ORDER by o.eventDate DESC", JAuditLog.class);
            query.setParameter("eventCategory", eventCategory);
            
		} else {
			logger.error(">>>>  Unknown combination of AuditLogForm properties");
		}
        
        List<JAuditLog> result = new ArrayList<JAuditLog>();
        
        if (firstResult > 0 && maxResults > 0 ) {
        	result = query.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
		} else {
        	result = query.getResultList();
		}
        
        return result;
    }
	
    public static List<JAuditLog> findAllJAuditLogs(JEventCategory eventCategory) {
        return entityManager().createQuery("SELECT o FROM JAuditLog o WHERE o.eventCategory = :eventCategory", JAuditLog.class).setParameter("eventCategory", eventCategory).getResultList();
    }
    
	
    public static long countJAuditLogs(JEventCategory eventCategory) {
        return entityManager().createQuery("SELECT COUNT(o) FROM JAuditLog o WHERE o.eventCategory = :eventCategory", Long.class).setParameter("eventCategory", eventCategory).getSingleResult();
    }
    
    
    
}
