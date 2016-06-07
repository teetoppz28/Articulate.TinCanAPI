package org.sakaiproject.articulate.tincan.entity;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.articulate.tincan.api.ArticulateTCEntityProviderService;
import org.sakaiproject.articulate.tincan.util.ArticulateTCSecurityUtils;
import org.sakaiproject.articulate.tincan.ArticulateTCConstants;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RESTful;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RequestAware;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.entityprovider.extension.RequestGetter;
import org.sakaiproject.entitybroker.entityprovider.search.Search;
import org.sakaiproject.entitybroker.util.AbstractEntityProvider;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCEntityProvider extends AbstractEntityProvider implements RESTful, RequestAware, ArticulateTCConstants {

    @Setter
    private ArticulateTCEntityProviderService articulateTCEntityProviderService;

    private RequestGetter requestGetter;

    public void init() {
    }

    public String getEntityPrefix() {
        return REST_PREFIX;
    }

    public Object getSampleEntity() {
        return REST_PREFIX;
    }

    @Override
    public String[] getHandledInputFormats() {
        return Formats.ALL_KNOWN_FORMATS;
    }

    public String[] getHandledOutputFormats() {
        return new String[] {Formats.HTML};
    }

    /**
     * PATH: {PREFIX}/statements
     * Pass through to put a statement to the configured LRS
     * 
     * @param view
     * @param params
     * @return
     */
    @EntityCustomAction(action = PATH_STATEMENTS, viewKey = "")
    public ActionReturn actionStatements(EntityView view, Map<String, Object> params) {
        ArticulateTCSecurityUtils.securityCheck();
        HttpServletRequest request = requestGetter.getRequest();

        String retVal = "";

        articulateTCEntityProviderService.postStatementPayload(request);

        return new ActionReturn(retVal);
    }

    /**
     * PATH: {PREFIX}/activities/{state}
     * Gets or saves activity data
     * 
     * @param view
     * @param params
     * @return
     */
    @EntityCustomAction(action = PATH_ACTIVITIES, viewKey = "")
    public ActionReturn actionActivitiesState(EntityView view, Map<String, Object> params) {
        ArticulateTCSecurityUtils.securityCheck();
        HttpServletRequest request = requestGetter.getRequest();

        String retVal = "";

        // {PREFIX}/activities/{path2}
        String path2 = view.getPathSegment(2);

        // {PREFIX}/activities/state
        if (StringUtils.equals(path2, PATH_STATE)) {
            if (StringUtils.equalsIgnoreCase(view.getMethod(), "GET") || StringUtils.equalsIgnoreCase((String) params.get("queryString"), PATH_QUERY_PARAM_GET)) {
                // get the activity state
                retVal = articulateTCEntityProviderService.getActivityStatePayload(request);
            } else if (StringUtils.equalsIgnoreCase(view.getMethod(), "PUT") || StringUtils.equalsIgnoreCase(view.getMethod(), "POST")){
                // store the activity state
                articulateTCEntityProviderService.postActivityStatePayload(request);
            }
        }

        return new ActionReturn(retVal);
    }

    @Override
    public void setRequestGetter(RequestGetter requestGetter) {
        this.requestGetter = requestGetter;
    }

    /*
     * Inherited methods (not implemented)
     */

    public String createEntity(EntityReference ref, Object entity, Map<String, Object> params) {
        return "Not implemented.";
    }

    public void updateEntity(EntityReference ref, Object entity, Map<String, Object> params) {
    }

    public Object getEntity(EntityReference ref) {
        return "Not implemented.";
    }

    public void deleteEntity(EntityReference ref, Map<String, Object> params) {
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        return null;
    }

}
