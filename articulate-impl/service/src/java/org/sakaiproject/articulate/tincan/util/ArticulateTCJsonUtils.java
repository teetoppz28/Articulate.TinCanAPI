package org.sakaiproject.articulate.tincan.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * Utility class for JSON-specific functionality
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCJsonUtils {

    private static Log log = LogFactory.getLog(ArticulateTCJsonUtils.class);

    /**
     * Creates a JSON string from the given object
     * 
     * @param fromObj the object holding to be converted to JSON
     * @return the JSON string representing the object
     */
    public static String parseToJson(Object fromObj) {
        if (fromObj == null) {
            throw new IllegalArgumentException("Object to parse into JSON cannot be null.");
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        // serialize null values
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();
        String rv = gson.toJson(fromObj, fromObj.getClass());

        return rv;
    }

    /**
     * Creates a list of POJOs from the given JSON string
     * 
     * @param jsonStr the JSON to parse
     * @return a list of maps of the POJOs created
     */
    public static List<Object> parseFromJsonArray(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            throw new IllegalArgumentException("JSON string cannot be blank.");
        }

        Gson gson = new Gson();
        List<Object> rv = new ArrayList<>();

        try {
            JsonArray jsonArray = (JsonArray) getJsonElement(jsonStr);
            for (JsonElement jsonElement : jsonArray) {
                Map<?, ?> obj = gson.fromJson(jsonElement, HashMap.class);
                rv.add(obj);
            }
        } catch (Exception e) {
            log.error("Error getting JSON Array.", e);
        }

        return rv;
    }

    /**
     * Creates a map of the POJO from the given JSON string
     * 
     * @param jsonStr the JSON to parse
     * @return a Map of the POJO created
     */
    public static Map<?, ?> parseFromJsonObject(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            throw new IllegalArgumentException("JSON string cannot be blank.");
        }

        Gson gson = new Gson();

        try {
            return gson.fromJson(getJsonElement(jsonStr), HashMap.class);
        } catch (Exception e) {
            log.error("Error getting JSON Object.", e);
        }

        return new HashMap<String, Object>();
    }

    /**
     * Gets the JsonElement from the given string
     * 
     * @param jsonStr
     * @return
     */
    private static JsonElement getJsonElement(String jsonStr) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.fromJson(jsonStr, JsonElement.class);

         if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
         } else if (jsonElement.isJsonArray()) {
             return jsonElement.getAsJsonArray();
         }

         return null;
    }

}
