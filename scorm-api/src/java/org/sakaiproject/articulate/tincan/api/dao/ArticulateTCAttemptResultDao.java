package org.sakaiproject.articulate.tincan.api.dao;

import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCAttemptResult;

public interface ArticulateTCAttemptResultDao {

    /**
     * Save or update (if the key exists) the object in the database
     * 
     * @param articulateTCAttemptResult
     */
    void save(ArticulateTCAttemptResult articulateTCAttemptResult);

    /**
     * Get a row with the given ID
     * Use this one, preferably, over the load() method
     * 
     * @param id
     * @return
     */
    ArticulateTCAttemptResult get(long id);

    /**
     * Get a row with the given ID
     * 
     * @param id
     * @return
     */
    ArticulateTCAttemptResult load(long id);

    /**
     * Finds the row with the given attempt ID
     * 
     * @param attemptId the attempt ID
     * @return
     */
    ArticulateTCAttemptResult findByAttemptId(long attemptId);

    /**
     * Is the attempt completed?
     * 
     * @param attemptId the attempt ID
     * @return true, if completed (has a date completed value)
     */
    boolean isAttemptComplete(long attemptId);

}
