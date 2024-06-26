package com.ocado.basket.logic;

import com.ocado.basket.exceptions.ConfigLoadException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ConfigLoader class
 * Loads the delivery options from a config file
 *
 * @version 1.0
 */
public class ConfigLoader {

    public Map<String, List<String>> loadDeliveryOptions(String absolutePathToConfigFile) {
        Map<String, List<String>> deliveryOptions = new HashMap<>();
        try(var reader = new FileReader(absolutePathToConfigFile)) {
            var config = new JSONObject(new JSONTokener(reader));
            config.keys().forEachRemaining(key-> deliveryOptions.put(key, config.getJSONArray(key).toList().stream().map(Object::toString).toList()));
        }catch (IOException e) {
            throw new ConfigLoadException("Failed to load config file", e);
        }catch (JSONException e) {
            throw new ConfigLoadException("Invalid JSON in config file", e);
        }
        return deliveryOptions;
    }
}
