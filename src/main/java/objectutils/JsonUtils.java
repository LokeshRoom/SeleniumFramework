package objectutils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Creator: Lokesh.kk
 * Created on: 2/19/2020
 **/
public class JsonUtils {

    public static JSONObject parseStringToJson(String jsonBody) throws Exception {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonBody);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return jsonObject;
    }

    public static HashMap<String, String> parseJSONToHashMap(JSONObject jsonObject) throws Exception {
        HashMap<String, String> parsedObject = new HashMap<>();
        try {
            Map<String, Object> mapObject = jsonObject.toMap();
            for (String key : mapObject.keySet()) {
                if (mapObject.get(key) == null) {
                    parsedObject.put(key, "");
                } else {
                    parsedObject.put(key, mapObject.get(key).toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return parsedObject;
    }

    public static HashMap<String, String> parseJSONStringToHashMap(String jsonObjectString) throws Exception {
        return parseJSONToHashMap(parseStringToJson(jsonObjectString));
    }
}
