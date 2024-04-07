package com.ocado.basket.logic;

import com.ocado.basket.exceptions.InvalidItemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.Collectors;

class ItemsCountMaximizerTest {

    private ItemsCountMaximizer maximizer;
    private Map<String, List<String>> deliveryOptions;

    @BeforeEach
    void setUp() {
        deliveryOptions = new HashMap<>();
        maximizer = new ItemsCountMaximizer(deliveryOptions);
    }

    @Test
    void optimizeDeliveryItems_emptyItemSet_returnsEmptyMap() {
        List<String> itemSet = new ArrayList<>();
        Set<String> optimalDeliveries = new HashSet<>();
        Map<String, List<String>> result = maximizer.optimizeDeliveryItems(itemSet, optimalDeliveries);
        assertTrue(result.isEmpty());
    }

    @Test
    void optimizeDeliveryItems_singleItemSingleDeliveryOption_returnsSingleDeliveryOption() {
        deliveryOptions.put("item1", Collections.singletonList("deliveryOption1"));
        List<String> itemSet = Collections.singletonList("item1");
        Set<String> optimalDeliveries = new HashSet<>(Collections.singletonList("deliveryOption1"));
        Map<String, List<String>> result = maximizer.optimizeDeliveryItems(itemSet, optimalDeliveries);
        assertEquals(Collections.singletonMap("deliveryOption1", Collections.singletonList("item1")), result);
    }

    @Test
void optimizeDeliveryItems_multipleItemsSameDeliveryOption_returnsSingleDeliveryOption() {
    deliveryOptions.put("item1", Collections.singletonList("deliveryOption1"));
    deliveryOptions.put("item2", Collections.singletonList("deliveryOption1"));
    List<String> itemSet = Arrays.asList("item1", "item2");
    Set<String> optimalDeliveries = new HashSet<>(Collections.singletonList("deliveryOption1"));
    Map<String, List<String>> result = maximizer.optimizeDeliveryItems(itemSet, optimalDeliveries);
    Map<String, Set<String>> resultSet = result.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> new HashSet<>(e.getValue())));
    Map<String, Set<String>> expectedSet = new HashMap<>();
    expectedSet.put("deliveryOption1", new HashSet<>(Arrays.asList("item1", "item2")));
    assertEquals(expectedSet, resultSet);
}

    @Test
    void optimizeDeliveryItems_multipleItemsDifferentDeliveryOptions_returnsMultipleDeliveryOptions() {
        deliveryOptions.put("item1", Collections.singletonList("deliveryOption1"));
        deliveryOptions.put("item2", Collections.singletonList("deliveryOption2"));
        List<String> itemSet = Arrays.asList("item1", "item2");
        Set<String> optimalDeliveries = new HashSet<>(Arrays.asList("deliveryOption1", "deliveryOption2"));
        Map<String, List<String>> result = maximizer.optimizeDeliveryItems(itemSet, optimalDeliveries);
        Map<String, List<String>> expected = new HashMap<>();
        expected.put("deliveryOption1", Collections.singletonList("item1"));
        expected.put("deliveryOption2", Collections.singletonList("item2"));
        assertEquals(expected, result);
    }

    @Test
    void optimizeDeliveryItems_itemNotInDeliveryOptions_throwsInvalidItemException() {
        deliveryOptions.put("item1", Collections.singletonList("deliveryOption1"));
        List<String> itemSet = Arrays.asList("item1", "item2");
        Set<String> optimalDeliveries = new HashSet<>(Collections.singletonList("deliveryOption1"));
        assertThrows(InvalidItemException.class, () -> maximizer.optimizeDeliveryItems(itemSet, optimalDeliveries));
    }
}