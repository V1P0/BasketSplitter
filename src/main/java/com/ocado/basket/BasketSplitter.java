package com.ocado.basket;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BasketSplitter {

    private final DeliveryItemsOptimizer optimizer;
    private final DeliveryCountMinimizer deliveryCountMinimizer;

    public BasketSplitter(String absolutePathToConfigFile) {
        var loader = new ConfigLoader();
        Map<String, List<String>> productToDeliveryOptions = loader.loadDeliveryOptions(absolutePathToConfigFile);
        optimizer = new DeliveryItemsOptimizer(productToDeliveryOptions);
        deliveryCountMinimizer = new DeliveryCountMinimizer(productToDeliveryOptions);

    }


    public Map<String, List<String>> split(List<String> items) {
        Set<String> minimalDeliverySet = deliveryCountMinimizer.optimizeBasket(items);
        return optimizer.optimizeDeliveryItems(items, minimalDeliverySet);
    }


}
