package com.ocado.basket;

import java.util.List;
import java.util.Map;

public interface AbstractConfigLoader {
    Map<String, List<String>> loadDeliveryOptions(String absolutePathToConfigFile);
}
