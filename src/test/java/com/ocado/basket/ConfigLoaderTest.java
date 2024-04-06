package com.ocado.basket;

import com.ocado.basket.exceptions.ConfigLoadException;
import com.ocado.basket.logic.ConfigLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigLoaderTest {

    private ConfigLoader configLoader;

    @BeforeEach
    void setUp() {
        configLoader = new ConfigLoader();
    }

    @Test
    void loadDeliveryOptions_returnsCorrectMap_whenConfigFileIsValid(@TempDir Path tempDir) throws IOException {
        // Create a temporary config file
        var tempFile = tempDir.resolve("config.json");
        try (var writer = new PrintWriter(tempFile.toFile())) {
            writer.println("{\"option1\": [\"value1\", \"value2\"], \"option2\": [\"value3\", \"value4\"]}");
        }

        var expected = Map.of(
                "option1", List.of("value1", "value2"),
                "option2", List.of("value3", "value4")
        );

        var actual = configLoader.loadDeliveryOptions(tempFile.toString());

        assertEquals(expected, actual);
    }

    @Test
    void loadDeliveryOptions_throwsRuntimeException_whenConfigFileDoesNotExist() {
        var exception = assertThrows(ConfigLoadException.class, () -> configLoader.loadDeliveryOptions("nonexistent.json"));
        assertEquals("Failed to load config file", exception.getMessage());
    }

    @Test
    void loadDeliveryOptions_throwsRuntimeException_whenConfigFileIsInvalid(@TempDir Path tempDir) throws IOException {
        // Create a temporary config file
        var tempFile = tempDir.resolve("config.json");
        try (var writer = new PrintWriter(tempFile.toFile())) {
            writer.println("{\"option1\": \"value1\", \"option2\": \"value2\"}");  // Invalid JSON for this use case
        }

        var exception = assertThrows(ConfigLoadException.class, () -> configLoader.loadDeliveryOptions(tempFile.toString()));
        assertEquals("Invalid JSON in config file", exception.getMessage());
    }
}