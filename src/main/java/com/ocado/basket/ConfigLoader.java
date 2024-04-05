package com.ocado.basket;

import com.ocado.basket.exceptions.ConfigLoadException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigLoader {

    public Map<String, List<String>> loadDeliveryOptions(String absolutePathToConfigFile) {
        var deliveryOptions = new HashMap<String, List<String>>();
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
