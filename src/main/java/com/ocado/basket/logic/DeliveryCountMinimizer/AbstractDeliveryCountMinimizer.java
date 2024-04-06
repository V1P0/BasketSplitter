package com.ocado.basket.logic.DeliveryCountMinimizer;

import java.util.List;
import java.util.Set;

/**
 * AbstractDeliveryCountMinimizer interface
 * Optimizes the number of items in each delivery option
 *
 * @version 1.0
 */
public interface AbstractDeliveryCountMinimizer {
    Set<String> optimizeBasket(List<String> products);
}
