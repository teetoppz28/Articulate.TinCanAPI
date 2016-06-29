package org.sakaiproject.articulate.tincan.util;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entity.api.Edit;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdUsedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCContentEntityUtils {

    private static final Logger log = LoggerFactory.getLogger(ArticulateTCContentEntityUtils.class);

    /**
     * Attempt to add collection, if it exists, attempt to edit it instead
     * 
     * @param collectionPath
     * @return
     */
    public static ContentCollectionEdit addOrEditCollection(String collectionPath) {
        try {
            return addCollection(collectionPath);
        } catch (IdUsedException iue) {
            // id exists, edit the collection instead
            log.debug("Collection exists. Getting the EDIT collection object instead for path: {}", collectionPath);
            return editCollection(collectionPath);
        } catch (Exception e) {
            log.error("Error getting the add collection object.", e);
        }

        return null;
    }

    /**
     * Attempt to add collection
     * 
     * @param collectionPath
     * @return
     */
    public static ContentCollectionEdit addCollection(String collectionPath) throws Exception {
        ContentHostingService contentHostingService = (ContentHostingService) ComponentManager.get(ContentHostingService.class);

        return contentHostingService.addCollection(collectionPath);
    }

    /**
     * Attempt to edit collection
     * 
     * @param collectionPath
     * @return
     */
    public static ContentCollectionEdit editCollection(String collectionPath) {
        ContentHostingService contentHostingService = (ContentHostingService) ComponentManager.get(ContentHostingService.class);

        try {
            return contentHostingService.editCollection(collectionPath);
        } catch (Exception e) {
            log.error("Error getting the edit collection object.", e);
        }

        return null;
    }

    /**
     * Attempt to add resource, if it exists, attempt to edit it instead
     * 
     * @param resourcePath
     * @return
     */
    public static ContentResourceEdit addOrEditResource(String resourcePath) {
        try {
            return addResource(resourcePath);
        } catch (IdUsedException e) {
            // id exists, edit the resource instead
            log.debug("Resource exists. Getting the EDIT resource object instead for path: {}", resourcePath);
            return editResource(resourcePath);
        } catch (Exception e) {
            log.error("Error getting the add resource object.", e);
        }

        return null;
    }

    /**
     * Attempt to add resource
     * 
     * @param resourcePath
     * @return
     */
    public static ContentResourceEdit addResource(String resourcePath) throws Exception {
        ContentHostingService contentHostingService = (ContentHostingService) ComponentManager.get(ContentHostingService.class);

        return contentHostingService.addResource(resourcePath);
    }

    /**
     * Attempt to edit resource
     * 
     * @param resourcePath
     * @return
     */
    public static ContentResourceEdit editResource(String resourcePath) {
        ContentHostingService contentHostingService = (ContentHostingService) ComponentManager.get(ContentHostingService.class);

        try {
            return contentHostingService.editResource(resourcePath);
        } catch (Exception e) {
            log.error("Error getting the edit resource object.", e);
        }

        return null;
    }

    /**
     * Add properties to a {@link ContentEntity}
     * 
     * @param editEntity
     * @param keys keys array
     * @param values values array
     * @return
     */
    public static Edit addProperties(Edit editEntity, String[] keys, String[] values) {
        ResourcePropertiesEdit props = editEntity.getPropertiesEdit();

        for (int i = 0; i < keys.length; i++) {
            props.addProperty(keys[i], values[i]);
        }

        return editEntity;
    }

}
