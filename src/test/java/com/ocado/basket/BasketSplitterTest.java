package com.ocado.basket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}