package com.ocado.basket;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class DeliveryInfo {
    private String deliveryName;
    private List<String> deliveryItems;
}
