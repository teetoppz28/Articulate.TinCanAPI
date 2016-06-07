package org.sakaiproject.articulate.tincan.api.dao;

import org.sakaiproject.articulate.tincan.model.hibernate.ArticulateTCActivityState;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public interface ArticulateTCActivityStateDao {

    /**
     * Save or update (if the key exists) the state object in the database
     * 
     * @param articulateTCActivityState
     */
    void save(ArticulateTCActivityState articulateTCActivityState);

    /**
     * Get a state row with the given ID
     * Use this one, preferably, over the load() method
     * 
     * @param id
     * @return
     */
    ArticulateTCActivityState get(long id);

    /**
     * Get a state row with the given ID
     * 
     * @param id
     * @return
     */
    ArticulateTCActivityState load(long id);

    /**
     * Finds the row with the given unique key (attempt ID)
     * 
     * @param attemptId
     * @return
     */
    ArticulateTCActivityState findOneByUniqueKey(Long attemptId);

    /**
     * Finds the row with the given unique key (attempt ID)
     * 
     * @param attemptId
     * @param stateId
     * @return
     */
    ArticulateTCActivityState findOneByUniqueKey(Long attemptId, String stateId);

}
