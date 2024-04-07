package com.ocado.basket.logic.DeliveryCountMinimizer;

import com.ocado.basket.exceptions.InvalidItemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class DynamicDeliveryCountMinimizerTest {

    private DynamicDeliveryCountMinimizer minimizer;
    private Map<String, List<String>> productToDeliveryOptions;

    @BeforeEach
    void setUp() {
        productToDeliveryOptions = new HashMap<>();
        minimizer = new DynamicDeliveryCountMinimizer(productToDeliveryOptions);
    }

    @Test
    void optimizeBasket_emptyBasket_returnsEmptySet() {
        List<String> products = new ArrayList<>();
        Set<String> result = minimizer.optimizeBasket(products);
        assertTrue(result.isEmpty());
    }

    @Test
    void optimizeBasket_singleProductSingleDeliveryOption_returnsSingleDeliveryOption() {
        productToDeliveryOptions.put("product1", Collections.singletonList("deliveryOption1"));
        List<String> products = Collections.singletonList("product1");
        Set<String> result = minimizer.optimizeBasket(products);
        assertEquals(Collections.singleton("deliveryOption1"), result);
    }

    @Test
    void optimizeBasket_multipleProductsSameDeliveryOption_returnsSingleDeliveryOption() {
        productToDeliveryOptions.put("product1", Collections.singletonList("deliveryOption1"));
        productToDeliveryOptions.put("product2", Collections.singletonList("deliveryOption1"));
        List<String> products = Arrays.asList("product1", "product2");
        Set<String> result = minimizer.optimizeBasket(products);
        assertEquals(Collections.singleton("deliveryOption1"), result);
    }

    @Test
    void optimizeBasket_multipleProductsDifferentDeliveryOptions_returnsMultipleDeliveryOptions() {
        productToDeliveryOptions.put("product1", Collections.singletonList("deliveryOption1"));
        productToDeliveryOptions.put("product2", Collections.singletonList("deliveryOption2"));
        List<String> products = Arrays.asList("product1", "product2");
        Set<String> result = minimizer.optimizeBasket(products);
        assertEquals(new HashSet<>(Arrays.asList("deliveryOption1", "deliveryOption2")), result);
    }

    @Test
    void optimizeBasket_productNotInDeliveryOptions_throwsInvalidItemException() {
        productToDeliveryOptions.put("product1", Collections.singletonList("deliveryOption1"));
        List<String> products = Arrays.asList("product1", "product2");
        assertThrows(InvalidItemException.class, () -> minimizer.optimizeBasket(products));
    }

    @Test
    void optimizeBasket_largeDataSet_returnsOptimizedDeliveryOptions() {
        for (int i = 1; i <= 100; i++) {
            productToDeliveryOptions.put("product" + i, Arrays.asList("deliveryOption1", "deliveryOption2", "deliveryOption" + (i%11)));
        }
        List<String> products = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            products.add("product" + i);
        }
        Set<String> result = minimizer.optimizeBasket(products);
        assertTrue(result.size() <= 100);
    }
}