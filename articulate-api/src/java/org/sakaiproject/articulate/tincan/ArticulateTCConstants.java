package org.sakaiproject.articulate.tincan;

import java.text.DecimalFormat;

import org.sakaiproject.scorm.api.ScormConstants;

interface ArticulateTCConstants extends ScormConstants {

    /**
     * Entity provider prefix
     */
    final static String REST_PREFIX = "tincanapi-lrs";

    /*
     * RESTful API entity configuration
     */
    final static String PATH_STATEMENTS = "statements";
    final static String PATH_ACTIVITIES = "activities";
    final static String PATH_STATE = "state";
    final static String PATH_QUERY_PARAM_GET = "method=get";
    final static String PATH_QUERY_PARAM_DELETE = "method=delete";

    /*
     * Activity statement data keys
     */
    final static String STATEMENT_DATA_KEY_CONTENT = "content";

    /*
     * Activity state data keys
     */
    final static String STATE_DATA_ID_SEPARATOR = "::";
    final static String STATE_DATA_KEY_ID = "id";
    final static String STATE_DATA_KEY_ACTIVITY_ID = "activityId";
    final static String STATE_DATA_KEY_CONTENT = "content";
    final static String STATE_DATA_KEY_AGENT = "agent";
    final static String STATE_DATA_KEY_STATE_ID = "stateId";
    final static String STATE_DATA_KEY_SITE_ID = "siteid";
    final static String STATE_DATA_KEY_ENDPOINT = "endpoint";
    final static String STATE_DATA_KEY_ACTOR = "actor";
    final static String STATE_DATA_KEY_USER_ID = "userid";
    final static String STATE_DATA_KEY_PACKAGE_ID = "packageid";

    final static String DEFAULT_ENCODING = "UTF-8";

    /*
     * Archive package processing
     */
    final static String ARCHIVE_DEFAULT_URL_PATH_PREFIX = "/access/content";
    final static String ARCHIVE_DEFAULT_STORAGE_PATH_PREFIX = "/private/articulate/";
    final static String ARCHIVE_DEFAULT_LAUNCH_PAGE = "story.html";
    final static String ARCHIVE_ZIP_MIMETYPE = "application/zip";
    final static String ARCHIVE_META_XML_FILE = "meta.xml";
    final static String ARCHIVE_TINCAN_XML_FILE = "tincan.xml";
    final static String ARCHIVE_META_ATTR_ID = "id";
    final static String ARCHIVE_META_ATTR_TITLE = "title";

    /*
     * HTML page
     */
    final static String HTML_ARTICULATE_TC_CSS = "styles/articulatetc.css";
    final static String HTML_HEADSCRIPTS = "/library/js/headscripts.js";
    final static String HTML_BODY_ONLOAD_ADDTL="setMainFrameHeight(window.name)";

    /*
     * Configuration page
     */
    final static Double CONFIGURATION_DEFAULT_POINTS = 100d;
    final static int CONFIGURATION_DEFAULT_ATTEMPTS = 10;
    final static boolean CONFIGURATION_DEFAULT_IS_GRADED = false;
    final static String CONFIGURATION_DEFAULT_APP_CONTENT_TYPE = "Articulate TinCanAPI";
    final static int CONFIGURATION_DEFAULT_NUMBER_OF_TRIES_UNLIMITED = -1;
    final static boolean CONFIGURATION_DEFAULT_ACTIVITY_STATE_DELETED = false;
    final static String CONFIGURATION_RECORD_SCORE_TYPE_BEST = "BEST";
    final static String CONFIGURATION_RECORD_SCORE_TYPE_LATEST = "LATEST";

    /*
     * Grading
     */
    final static DecimalFormat GRADE_DECIMAL_FORMAT = new DecimalFormat("#.##");

}
