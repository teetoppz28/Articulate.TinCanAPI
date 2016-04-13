package org.sakaiproject.scorm.service.tincanapi.impl.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Utility class for JSON-specific functionality
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class TinCanAPIJsonUtils {

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
        JsonArray jsonArray = gson.fromJson(jsonStr, JsonElement.class).getAsJsonArray();
        List<Object> rv = new ArrayList<Object>(jsonArray.size());
        for (JsonElement jsonElement : jsonArray) {
            Map<?, ?> obj = gson.fromJson(jsonElement, HashMap.class);
            rv.add(obj);
        }

        return rv;
    }

    /**
     * Creates a list of POJOs from the given JSON string
     * 
     * @param jsonStr the JSON to parse
     * @return a Map of the POJO created
     */
    public static Map<?, ?> parseFromJsonObject(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            throw new IllegalArgumentException("JSON string cannot be blank.");
        }

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonStr, JsonElement.class).getAsJsonObject();
        Map<?,?> obj = gson.fromJson(jsonObject, HashMap.class);

        return obj;
    }
}
