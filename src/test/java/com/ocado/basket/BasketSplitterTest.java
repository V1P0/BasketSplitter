package com.ocado.basket;

import com.ocado.basket.exceptions.InvalidItemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            writer.println("{\"item1\": [\"deliveryOption1\", \"deliveryOption2\"], \"item2\": [\"deliveryOption1\", \"deliveryOption2\"], \"item3\": [\"deliveryOption1\", \"deliveryOption3\"], \"item4\": [\"deliveryOption1\", \"deliveryOption3\"], \"item5\": [\"deliveryOption3\"], \"item6\": [\"deliveryOption2\"]}");
        }

        basketSplitter = new BasketSplitter(tempFile.toString());
    }


    @Test
    void split_returnsCorrectDeliveryOptions_forGivenItems() {
        List<String> items = Arrays.asList("item1", "item2", "item3", "item4", "item5", "item6");
        Map<String, List<String>> expected = Map.of(
                "deliveryOption2", List.of("item1", "item2", "item6"),
                "deliveryOption3", List.of("item3", "item4", "item5")
        );

        var actual = basketSplitter.split(items);

        assertEquals(expected.size(), actual.size());

        for (Map.Entry<String, List<String>> entry : expected.entrySet()) {
            String key = entry.getKey();
            List<String> expectedList = entry.getValue();
            List<String> actualList = actual.get(key);
            assertThat(actualList).containsExactlyInAnyOrderElementsOf(expectedList);
        }
    }

    @Test
    void split_returnsEmptyMap_whenNoItemsProvided() {
        List<String> items = List.of();

        Map<String, List<String>> actual = basketSplitter.split(items);

        assertEquals(Map.of(), actual);
    }

    @Test
    void split_throwsException_whenItemWithoutDeliveryOptionsProvided() {
        List<String> items = Arrays.asList("item1", "itemWithoutDeliveryOption");

        assertThrows(InvalidItemException.class, () -> basketSplitter.split(items));
    }

    @Test
    void split_handlesLargeInputAndConfigWithRandomDeliveryOptions() {
        var tempFile = generateLargeConfig();

        // Generate large input list
        List<String> items = IntStream.range(0, 100)
                              .mapToObj(i -> "item" + i)
                              .collect(Collectors.toList());


        basketSplitter = new BasketSplitter(tempFile.toString());

        assertDoesNotThrow(() -> basketSplitter.split(items));
    }

    private Path generateLargeConfig() {
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
        return tempFile;
    }

    @Test
    void split_returnsEmptyMap_whenConfigFileIsEmpty() throws IOException {
        // Create an empty config file
        var tempFile = tempDir.resolve("empty_config.json");
        try (var writer = new PrintWriter(tempFile.toFile())) {
            writer.println("{}");
        }

        basketSplitter = new BasketSplitter(tempFile.toString());

        List<String> items = Arrays.asList("item1", "item2", "item3");

        assertThrows(InvalidItemException.class, () -> basketSplitter.split(items));
    }
}