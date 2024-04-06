package com.ocado.basket.logic.minimzer;

import com.ocado.basket.exceptions.InvalidItemException;

import java.util.*;

public class DynamicDeliveryCountMinimizer implements AbstractDeliveryCountMinimizer {

    private final Map<String, List<String>> productToDeliveryOptions;

    public DynamicDeliveryCountMinimizer(Map<String, List<String>> productToDeliveryOptions) {
        this.productToDeliveryOptions = productToDeliveryOptions;
    }

    // dp find optimal delivery options
    public Set<String> optimizeBasket(List<String> products) {
        Map<Set<String>, Set<String>> cache = new HashMap<>();
        return findOptimalDeliveryOptions(new HashSet<>(products), cache);
    }

    private Set<String> findOptimalDeliveryOptions(Set<String> items, Map<Set<String>, Set<String>> cache) {
        if (items.isEmpty()) {
            return new HashSet<>();
        }
        if (cache.containsKey(items)) {
            return cache.get(items);
        }

        Set<String> minimalDeliveryOptions = Set.of();

        for (String product : items) {
            validateProduct(product);
            minimalDeliveryOptions = findMinimalDeliveryOptionsForProduct(items, product, minimalDeliveryOptions, cache);
        }

        cache.put(items, minimalDeliveryOptions);
        return minimalDeliveryOptions;
    }

    private void validateProduct(String product) {
        if(!productToDeliveryOptions.containsKey(product)){
            throw new InvalidItemException("Invalid item: " + product + " not found in delivery options");
        }
    }

    private Set<String> findMinimalDeliveryOptionsForProduct(Set<String> items, String product, Set<String> minimalDeliveryOptions, Map<Set<String>, Set<String>> cache) {
        for (String deliveryOption : productToDeliveryOptions.get(product)) {
            Set<String> coveredProducts = findCoveredProducts(items, deliveryOption);
            Set<String> remainingProducts = new HashSet<>(items);
            remainingProducts.removeAll(coveredProducts);
            Set<String> deliveryOptionsForRemaining = findOptimalDeliveryOptions(remainingProducts, cache);

            minimalDeliveryOptions = updateMinimalDeliveryOptions(deliveryOption, deliveryOptionsForRemaining, minimalDeliveryOptions);
        }
        return minimalDeliveryOptions;
    }

    private Set<String> findCoveredProducts(Set<String> items, String deliveryOption) {
        Set<String> coveredProducts = new HashSet<>();
        for (String product : items) {
            if (productToDeliveryOptions.containsKey(product) && productToDeliveryOptions.get(product).contains(deliveryOption)) {
                coveredProducts.add(product);
            }
        }
        return coveredProducts;
    }

    private Set<String> updateMinimalDeliveryOptions(String deliveryOption, Set<String> deliveryOptionsForRemaining, Set<String> minimalDeliveryOptions) {
        if (minimalDeliveryOptions.isEmpty() || deliveryOptionsForRemaining.size() + 1 < minimalDeliveryOptions.size()) {
            minimalDeliveryOptions = new HashSet<>(deliveryOptionsForRemaining);
            minimalDeliveryOptions.add(deliveryOption);
        }
        return minimalDeliveryOptions;
    }
}
