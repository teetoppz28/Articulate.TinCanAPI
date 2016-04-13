package org.sakaiproject.scorm.entity.tincanapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.sakaiproject.scorm.service.tincanapi.api.TinCanAPIService;

public class TinCanAPIEntityProvider extends AbstractEntityProvider implements RESTful, RequestAware {

    private Log log = LogFactory.getLog(TinCanAPIEntityProvider.class);

    public static String PREFIX = "tincanapi-lrs";
    private RequestGetter requestGetter;

    private static final String PATH_ACTION = "action";
    private static final String PATH_STATEMENTS = "statements";
    private static final String PATH_ACTIVITIES = "activities";
    private static final String PATH_STATE = "state";

    @Setter
    private TinCanAPIService tinCanAPIService;

    public void init() {
    }

    public String getEntityPrefix() {
        return PREFIX;
    }

    public Object getSampleEntity() {
        return PREFIX;
    }

    @Override
    public String[] getHandledInputFormats() {
        return Formats.ALL_KNOWN_FORMATS;
    }

    public String[] getHandledOutputFormats() {
        return new String[] {Formats.HTML};
    }

    @SuppressWarnings("unchecked")
    @EntityCustomAction(action = PATH_ACTION, viewKey = "")
    public ActionReturn action(EntityView view, Map<String, Object> params) {
        HttpServletRequest request = requestGetter.getRequest();

        // tincanapi-lrs/action/{path2}
        String path2 = view.getPathSegment(2);

        // tincanapi-lrs/action/statements
        if (StringUtils.equals(path2, PATH_STATEMENTS)) {
            log.info("Path: tincanapi-lrs/action/statements called");
            String payload = tinCanAPIService.getRequestPayload(request);
            String contentStr = tinCanAPIService.getContentFromPayload(payload);
            Map<String, Object> statementObj = (Map<String, Object>) tinCanAPIService.getObjectFromJSON(contentStr);
        }

        // tincanapi-lrs/action/activities/
        if (StringUtils.equals(path2, PATH_ACTIVITIES)) {
            String path3 = view.getPathSegment(3);

            // tincanapi-lrs/action/activities/state
            if (StringUtils.equals(path3, PATH_STATE)) {
                log.info("Path: tincanapi-lrs/action/activities/state called. Method: " + view.getMethod());
                if (StringUtils.equalsIgnoreCase(view.getMethod(), "GET")) {
                    // get the activity state
                    // TODO get activity state data from db
                } else if (StringUtils.equalsIgnoreCase(view.getMethod(), "PUT")){
                    // store the activity state
                    // TODO save state data to db
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
