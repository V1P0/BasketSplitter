package com.ocado.basket;

import com.ocado.basket.exceptions.InvalidItemException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DeliveryItemsOptimizer {
    private final Map<String, List<String>> deliveryOptions;

    public DeliveryItemsOptimizer(Map<String, List<String>> deliveryOptions) {
        this.deliveryOptions = deliveryOptions;
    }

    // greedy maximize number of items in single delivery
    public Map<String, List<String>> optimizeDeliveryItems(List<String> itemSet, Set<String> optimalDeliveries) {
        Set<String> remainingItems = new HashSet<>(itemSet);
        Map<String, List<String>> splitDeliveries = new HashMap<>();
        Map<String, Long> deliveryCounts = countDeliveryOptions(remainingItems, optimalDeliveries);
        while(!deliveryCounts.isEmpty()){
            DeliveryInfo maxDeliveryOption = computeMaxDeliveryOption(deliveryCounts, remainingItems);
            splitDeliveries.put(maxDeliveryOption.getDeliveryName(), maxDeliveryOption.getDeliveryItems());
            maxDeliveryOption.getDeliveryItems().forEach(remainingItems::remove);
            deliveryCounts = updateDeliveryCounts(deliveryCounts, maxDeliveryOption, optimalDeliveries);
        }
        if(!remainingItems.isEmpty()){
            throw new InvalidItemException("Invalid items: " + remainingItems);
        }
        return splitDeliveries;
    }

    public Map<String, Long> countDeliveryOptions(Set<String> itemSet, Set<String> optimalDeliveries) {
        return deliveryOptions.entrySet().stream()
                .filter(entry -> itemSet.contains(entry.getKey()))
                .flatMap(entry -> entry.getValue().stream())
                .filter(optimalDeliveries::contains)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    public DeliveryInfo computeMaxDeliveryOption(Map<String, Long> deliveryCounts, Set<String> itemSet){
        Map.Entry<String, Long> maxDeliveryOptionEntry = getMaxDeliveryOption(deliveryCounts);
        String maxDeliveryOption = maxDeliveryOptionEntry.getKey();
        List<String> itemsWithMaxDeliveryOption = getItemsWithMaxDeliveryOption(itemSet, maxDeliveryOption);
        return new DeliveryInfo(maxDeliveryOption, itemsWithMaxDeliveryOption);
    }

    private Map.Entry<String, Long> getMaxDeliveryOption(Map<String, Long> deliveryCounts) {
        return Collections.max(deliveryCounts.entrySet(), Map.Entry.comparingByValue());
    }

    private List<String> getItemsWithMaxDeliveryOption(Set<String> itemSet, String maxDeliveryOption) {
        return itemSet.stream()
                .filter(deliveryOptions::containsKey)
                .filter(item -> deliveryOptions.get(item).contains(maxDeliveryOption))
                .toList();
    }

    public Map<String, Long> updateDeliveryCounts(Map<String, Long> deliveryCounts, DeliveryInfo maxDelivery, Set<String> optimalDeliveries) {
        Map<String, Long> newDeliveryCounts = new HashMap<>(deliveryCounts);
        maxDelivery.getDeliveryItems().forEach(
                item -> deliveryOptions.get(item).stream().filter(optimalDeliveries::contains).forEach(
                        deliveryOption -> newDeliveryCounts.put(deliveryOption, newDeliveryCounts.get(deliveryOption) - 1)));
        newDeliveryCounts.remove(maxDelivery.getDeliveryName());
        return newDeliveryCounts;
    }
}
