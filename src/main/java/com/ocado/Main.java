package com.ocado;

import com.ocado.basket.BasketSplitter;
import com.ocado.basket.ConfigLoader;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        var path = "src\\main\\resources\\config.json";
        BasketSplitter splitter = new BasketSplitter(path);
        var result = splitter.split(List.of("xd", "Fond - Chocolate", "Chocolate - Unsweetened", "Nut - Almond, Blanched, Whole", "Haggis", "Mushroom - Porcini Frozen", "Cake - Miini Cheesecake Cherry", "Sauce - Mint", "Longan", "Bag Clear 10 Lb", "Nantucket - Pomegranate Pear", "Puree - Strawberry", "Numi - Assorted Teas", "Apples - Spartan", "Garlic - Peeled", "Cabbage - Nappa", "Bagel - Whole White Sesame", "Tea - Apple Green Tea"));
        System.out.println(result);
        System.out.println(result.size());
    }
}
