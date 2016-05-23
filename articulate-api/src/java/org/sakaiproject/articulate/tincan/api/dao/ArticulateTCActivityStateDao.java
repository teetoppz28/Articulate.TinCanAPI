package org.sakaiproject.articulate.tincan.api.dao;

import java.util.List;

import org.sakaiproject.articulate.tincan.model.ArticulateTCRequestPayload;
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
     * Use this one, prefarrably, over the load() method
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
     * Finds activity rows for a given site ID (context)
     * 
     * @param context the site or group id
     * @param deleted should we return the deleted rows as well?
     * @return
     */
    List<ArticulateTCActivityState> findByContext(String context, boolean deleted);

    /**
     * Finds the row with the given {@link ArticulateTCRequestPayload} object
     * 
     * @param articulateTCRequestPayload
     * @return
     */
    ArticulateTCActivityState findOne(ArticulateTCRequestPayload articulateTCRequestPayload);

    /**
     * Finds the row with the given unique key (user ID, site ID, content package ID)
     * 
     * @param userId
     * @param siteId
     * @param constantPackageId
     * @return
     */
    ArticulateTCActivityState findOneByUniqueKey(String userId, String siteId, Long constantPackageId);

    /**
     * Softly delete the state row in the db
     * (Mark it as "deleted", don't remove it)
     * 
     * @param articulateTCActivityState
     */
    void remove(ArticulateTCActivityState articulateTCActivityState);

}
