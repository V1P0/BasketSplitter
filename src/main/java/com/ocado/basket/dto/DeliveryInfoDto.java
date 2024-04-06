package com.ocado.basket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * DeliveryInfoDto class
 * Contains
 * - deliveryName: name of the delivery option
 * - deliveryItems: list of items in the delivery option
 *
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class DeliveryInfoDto {
    private String deliveryName;
    private List<String> deliveryItems;
}
