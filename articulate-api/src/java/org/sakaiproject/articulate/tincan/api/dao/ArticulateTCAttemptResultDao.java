package org.sakaiproject.articulate.tincan.api.dao;

import java.util.List;

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
     * Finds the completed attempt row(s) with the given attempt ID
     * 
     * @param attemptId the attempt ID
     * @return
     */
    List<ArticulateTCAttemptResult> findByAttemptId(long attemptId);

    /**
     * Finds the incompleted attempt row(s) with the given attempt ID
     * 
     * @param attemptId the attempt ID
     * @return
     */
    List<ArticulateTCAttemptResult> findByAttemptIdIncomplete(long attemptId);

    /**
     * Finds the row(s) with the given attempt ID
     * 
     * @param attemptId the attempt ID
     * @param onlyCompleted only retrieve completed attempts?
     * @return
     */
    List<ArticulateTCAttemptResult> findByAttemptId(long attemptId, boolean onlyCompleted);

    /**
     * Finds the row with the given attempt ID and the attempt number
     * 
     * @param attemptId the attempt ID
     * @param attemptNumber the attempt number
     * @return
     */
    ArticulateTCAttemptResult findByAttemptIdNumber(long attemptId, long attemptNumber);

    /**
     * Is the attempt completed?
     * 
     * @param attemptId the attempt ID
     * @return true, if completed (has a date completed value)
     */
    boolean isAttemptComplete(long attemptId);

}
