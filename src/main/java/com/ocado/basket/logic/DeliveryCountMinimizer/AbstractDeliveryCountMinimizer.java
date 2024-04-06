package com.ocado.basket.logic.DeliveryCountMinimizer;

import java.util.List;
import java.util.Set;

public interface AbstractDeliveryCountMinimizer {
    Set<String> optimizeBasket(List<String> products);
}
