package com.ocado.basket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class DeliveryInfoDto {
    private String deliveryName;
    private List<String> deliveryItems;
}
