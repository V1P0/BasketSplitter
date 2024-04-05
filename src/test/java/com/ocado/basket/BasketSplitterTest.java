package com.ocado.basket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BasketSplitterTest {

    private BasketSplitter basketSplitter;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary config file
        var tempFile = tempDir.resolve("config.json");
        try (var writer = new PrintWriter(tempFile.toFile())) {
            writer.println("{\"item1\": [\"deliveryOption1\", \"deliveryOption2\"], \"item2\": [\"deliveryOption1\"], \"item3\": [\"deliveryOption2\"]}");
        }

        basketSplitter = new BasketSplitter(tempFile.toString());
    }


    @Test
    void splitShouldReturnCorrectDeliveryOptionsForGivenItems() {
        List<String> items = Arrays.asList("item1", "item2", "item3");
        Map<String, List<String>> expected = Map.of(
                "deliveryOption1", List.of("item1", "item2"),
                "deliveryOption2", List.of("item3")
        );

        Map<String, List<String>> actual = basketSplitter.split(items);

        for (Map.Entry<String, List<String>> entry : expected.entrySet()) {
            String key = entry.getKey();
            List<String> expectedList = entry.getValue();
            List<String> actualList = actual.get(key);
            assertThat(actualList).containsExactlyInAnyOrderElementsOf(expectedList);
        }
    }

    @Test
    void splitShouldReturnEmptyMapWhenNoItemsProvided() {
        List<String> items = List.of();

        Map<String, List<String>> actual = basketSplitter.split(items);

        assertEquals(Map.of(), actual);
    }

    @Test
    void splitShouldThrowExceptionWhenItemWithoutDeliveryOptionsProvided() {
        List<String> items = Arrays.asList("item1", "itemWithoutDeliveryOption");

        assertThrows(IllegalArgumentException.class, () -> basketSplitter.split(items));
    }

    @Test
    void splitShouldHandleLargeInputAndConfigWithRandomDeliveryOptions() {
        // Generate large config
        var tempFile = tempDir.resolve("large_config.json");
        try (var writer = new PrintWriter(tempFile.toFile())) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            for (int i = 0; i < 1000; i++) {
                sb.append("\"item").append(i).append("\": [");
                List<String> deliveryOptions = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    deliveryOptions.add("\"deliveryOption" + j + "\"");
                }
                Collections.shuffle(deliveryOptions);
                List<String> selectedDeliveryOptions = deliveryOptions.subList(0, new Random().nextInt(6) + 5);
                sb.append(String.join(", ", selectedDeliveryOptions));
                sb.append("]");
                if (i != 999) {
                    sb.append(", ");
                }
            }
            sb.append("}");
            writer.println(sb);
        } catch (IOException e) {
            fail("Failed to create large config file", e);
        }

        basketSplitter = new BasketSplitter(tempFile.toString());

        // Generate large input list
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            items.add("item" + i);
        }

        // Execute and assert no exceptions are thrown
        assertDoesNotThrow(() -> basketSplitter.split(items));
    }
}