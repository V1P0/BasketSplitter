package com.ocado.basket;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BasketSplitter {

    private final Map<String, List<String>> deliveryOptions;

    public BasketSplitter(String absolutePathToConfigFile) {
        AbstractConfigLoader loader = new ConfigLoader();
        this.deliveryOptions =  loader.loadDeliveryOptions(absolutePathToConfigFile);
    }


    public Map<String, List<String>> split(List<String> items) {
        var itemSet = new HashSet<>(items);
        var splitDeliveries = new HashMap<String, List<String>>();
        while(!itemSet.isEmpty()){
            Map<String, Long> deliveryCounts = countDeliveryOptions(itemSet);
            Map.Entry<String, Long> maxDeliveryOptionEntry = getMaxDeliveryOption(deliveryCounts);
            String maxDeliveryOption = maxDeliveryOptionEntry.getKey();
            List<String> itemsWithMaxDeliveryOption = getItemsWithMaxDeliveryOption(itemSet, maxDeliveryOption);
            splitDeliveries.put(maxDeliveryOption, itemsWithMaxDeliveryOption);
            updateItemSetAndDeliveryCounts(itemSet, itemsWithMaxDeliveryOption, deliveryCounts);
        }
        return splitDeliveries;
    }

    private Map<String, Long> countDeliveryOptions(Set<String> itemSet) {
        return deliveryOptions.entrySet().stream()
                .filter(entry -> itemSet.contains(entry.getKey()))
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private Map.Entry<String, Long> getMaxDeliveryOption(Map<String, Long> deliveryCounts) {
        return Collections.max(deliveryCounts.entrySet(), Map.Entry.comparingByValue());
    }

    private List<String> getItemsWithMaxDeliveryOption(Set<String> itemSet, String maxDeliveryOption) {
        return itemSet.stream()
                .filter(item -> deliveryOptions.get(item).contains(maxDeliveryOption))
                .toList();
    }

    private void updateItemSetAndDeliveryCounts(Set<String> itemSet, List<String> itemsWithMaxDeliveryOption, Map<String, Long> deliveryCounts) {
        itemsWithMaxDeliveryOption.forEach(itemSet::remove);
        itemsWithMaxDeliveryOption.forEach(
                item -> deliveryOptions.get(item).forEach(
                        deliveryOption -> {
                            if (deliveryCounts.containsKey(deliveryOption)) {
                                deliveryCounts.put(deliveryOption, deliveryCounts.get(deliveryOption) - 1);
                            }
                        }));
    }
}
