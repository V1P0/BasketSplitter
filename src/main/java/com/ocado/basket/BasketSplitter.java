package com.ocado.basket;

import com.ocado.basket.logic.*;
import com.ocado.basket.logic.minimzer.AbstractDeliveryCountMinimizer;
import com.ocado.basket.logic.minimzer.DynamicDeliveryCountMinimizer;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * BasketSplitter class
 * Splits the items into delivery options and optimizes the number of items in each delivery option
 *
 * @version 1.0
 */
public class BasketSplitter {

    private final DeliveryItemsOptimizer optimizer;
    private final AbstractDeliveryCountMinimizer deliveryCountMinimizer;

    /**
     * Constructor
     * @param absolutePathToConfigFile path to the config file
     */
    public BasketSplitter(String absolutePathToConfigFile) {
        var loader = new ConfigLoader();
        Map<String, List<String>> productToDeliveryOptions = loader.loadDeliveryOptions(absolutePathToConfigFile);
        optimizer = new DeliveryItemsOptimizer(productToDeliveryOptions);
        deliveryCountMinimizer = new DynamicDeliveryCountMinimizer(productToDeliveryOptions);

    }


    /**
     * Split the items into delivery options
     * Minimal number of delivery options is used
     * Then Maximize the number of items in the biggest delivery option
     * @param items list of items
     * @return map of delivery options and items
     */
    public Map<String, List<String>> split(List<String> items) {
        Set<String> minimalDeliverySet = deliveryCountMinimizer.optimizeBasket(items);
        return optimizer.optimizeDeliveryItems(items, minimalDeliverySet);
    }


}
