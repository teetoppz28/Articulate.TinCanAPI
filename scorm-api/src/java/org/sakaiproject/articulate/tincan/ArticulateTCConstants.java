package org.sakaiproject.articulate.tincan;

import org.sakaiproject.scorm.api.ScormConstants;

public interface ArticulateTCConstants extends ScormConstants {

    /**
     * Entity provider prefix
     */
    public final static String REST_PREFIX = "tincanapi-lrs";

    /*
     * RESTful API entity configuration
     */
    public final static String PATH_STATEMENTS = "statements";
    public final static String PATH_ACTIVITIES = "activities";
    public final static String PATH_STATE = "state";
    public final static String PATH_QUERY_PARAM_GET = "method=get";
    public final static String PATH_QUERY_PARAM_DELETE = "method=delete";

    /*
     * Activity statement data keys
     */
    public final static String STATEMENT_DATA_KEY_CONTENT = "content";

    /*
     * Activity state data keys
     */
    public final static String STATE_DATA_ID_SEPARATOR = "::";
    public final static String STATE_DATA_KEY_ID = "id";
    public final static String STATE_DATA_KEY_ACTIVITY_ID = "activityId";
    public final static String STATE_DATA_KEY_CONTENT = "content";
    public final static String STATE_DATA_KEY_AGENT = "agent";
    public final static String STATE_DATA_KEY_STATE_ID = "stateId";
    public final static String STATE_DATA_KEY_SITE_ID = "siteid";
    public final static String STATE_DATA_KEY_ENDPOINT = "endpoint";
    public final static String STATE_DATA_KEY_ACTOR = "actor";
    public final static String STATE_DATA_KEY_USER_ID = "userid";
    public final static String STATE_DATA_KEY_PACKAGE_ID = "packageid";

    public final static String DEFAULT_ENCODING = "UTF-8";

    /*
     * Archive package processing
     */
    public final static String ARCHIVE_DEFAULT_URL_PATH_PREFIX = "/access/content";
    public final static String ARCHIVE_DEFAULT_STORAGE_PATH_PREFIX = "/private/articulate/";
    public final static String ARCHIVE_DEFAULT_LAUNCH_PAGE = "story.html";
    public final static String ARCHIVE_ZIP_MIMETYPE = "application/zip";
    public final static String ARCHIVE_META_XML_FILE = "meta.xml";
    public final static String ARCHIVE_TINCAN_XML_FILE = "tincan.xml";

    /*
     * HTML page
     */
    public final static String HTML_ARTICULATE_TC_CSS = "styles/articulatetc.css";
    public final static String HTML_HEADSCRIPTS = "/library/js/headscripts.js";
    public final static String HTML_BODY_ONLOAD_ADDTL="setMainFrameHeight(window.name)";

    /*
     * Configuration page
     */
    public final static Double CONFIGURATION_DEFAULT_POINTS = 100d;
    public final static int CONFIGURATION_DEFAULT_ATTEMPTS = 10;

}
