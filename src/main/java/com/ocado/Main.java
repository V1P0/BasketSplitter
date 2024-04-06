package com.ocado;

import com.ocado.basket.BasketSplitter;
import com.ocado.basket.logic.ConfigLoader;
import com.ocado.basket.logic.DeliveryItemsOptimizer;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try {
            var path = "src\\main\\resources\\config.json";
            var itemList = List.of("Fond - Chocolate", "Chocolate - Unsweetened", "Nut - Almond, Blanched, Whole", "Haggis", "Mushroom - Porcini Frozen", "Cake - Miini Cheesecake Cherry", "Sauce - Mint", "Longan", "Bag Clear 10 Lb", "Nantucket - Pomegranate Pear", "Puree - Strawberry", "Numi - Assorted Teas", "Apples - Spartan", "Garlic - Peeled", "Cabbage - Nappa", "Bagel - Whole White Sesame", "Tea - Apple Green Tea");
            itemList = new ConfigLoader().loadDeliveryOptions(path).keySet().stream().toList();
            var startTime = System.currentTimeMillis();
            BasketSplitter splitter = new BasketSplitter(path);
            var result = splitter.split(itemList);
            System.out.println(System.currentTimeMillis() - startTime);
            System.out.println(result);
            System.out.println(result.size());
            startTime = System.currentTimeMillis();
            DeliveryItemsOptimizer optimizer = new DeliveryItemsOptimizer(new ConfigLoader().loadDeliveryOptions(path));
            result = optimizer.optimizeDeliveryItems(itemList, new HashSet<>(){
                @Override
                public boolean contains(Object key) {
                    return true;
                }
            });
            //remove the empty deliveries
            result.values().removeIf(List::isEmpty);
            System.out.println(System.currentTimeMillis() - startTime);
            System.out.println(result);
            System.out.println(result.size());


            // set of all delivery options from each item
            var allDeliveryOptions = new ConfigLoader().loadDeliveryOptions(path).values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
            System.out.println(allDeliveryOptions.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
