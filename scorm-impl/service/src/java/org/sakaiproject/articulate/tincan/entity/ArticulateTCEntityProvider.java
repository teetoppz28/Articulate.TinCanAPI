package org.sakaiproject.articulate.tincan.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.articulate.tincan.api.ArticulateTCEntityProviderService;
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

public class ArticulateTCEntityProvider extends AbstractEntityProvider implements RESTful, RequestAware, ArticulateTCConstants {

    private Log log = LogFactory.getLog(ArticulateTCEntityProvider.class);

    private RequestGetter requestGetter;

    @Setter
    private ArticulateTCEntityProviderService articulateTCEntityProviderService;

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

    @EntityCustomAction(action = PATH_ACTION, viewKey = "")
    public ActionReturn action(EntityView view, Map<String, Object> params) {
        HttpServletRequest request = requestGetter.getRequest();

        // tincanapi-lrs/action/{path2}
        String path2 = view.getPathSegment(2);

        // tincanapi-lrs/action/statements
        if (StringUtils.equals(path2, PATH_STATEMENTS)) {
            log.info("Path: tincanapi-lrs/action/statements called");

            String statementJson = articulateTCEntityProviderService.processStatementPayload(request);
            articulateTCEntityProviderService.sendStatementToLRS(statementJson);
        }

        // tincanapi-lrs/action/activities/
        if (StringUtils.equals(path2, PATH_ACTIVITIES)) {
         // tincanapi-lrs/action/activities/{path3}
            String path3 = view.getPathSegment(3);

            // tincanapi-lrs/action/activities/state
            if (StringUtils.equals(path3, PATH_STATE)) {
                log.info("Path: tincanapi-lrs/action/activities/state called. Method: " + view.getMethod());
                if (StringUtils.equalsIgnoreCase(view.getMethod(), "GET") || StringUtils.equalsIgnoreCase((String) params.get("queryString"), PATH_QUERY_PARAM_GET)) {
                    // get the activity state
                    // TODO get activity state data from db
                } else if (StringUtils.equalsIgnoreCase(view.getMethod(), "PUT") || StringUtils.equalsIgnoreCase(view.getMethod(), "POST")){
                    // store the activity state
                    //articulateTCEntityProviderService.processStatePayload(request);
                    // TODO save state data to db
                } else if (StringUtils.equalsIgnoreCase(view.getMethod(), "DELETE")) {
                    // delete the activity state
                    // TODO delete state data from db
                }
            }
        }

        Map<String,Object> map = new HashMap<String, Object>();
        return new ActionReturn(map);
    }

    @Override
    public void setRequestGetter(RequestGetter requestGetter) {
        this.requestGetter = requestGetter;
    }

    /*
     * Inherited methods (not used)
     */

    @Override
    public String createEntity(EntityReference ref, Object entity, Map<String, Object> params) {
        return null;
    }

    @Override
    public void updateEntity(EntityReference ref, Object entity, Map<String, Object> params) {
    }

    @Override
    public Object getEntity(EntityReference ref) {
        return null;
    }

    @Override
    public void deleteEntity(EntityReference ref, Map<String, Object> params) {
    }

    @Override
    public List<?> getEntities(EntityReference ref, Search search) {
        return null;
    }

}
