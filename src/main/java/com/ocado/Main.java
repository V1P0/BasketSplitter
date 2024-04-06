package com.ocado;

import com.ocado.basket.BasketSplitter;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            var path = "src\\main\\resources\\config.json";
            BasketSplitter splitter = new BasketSplitter(path);
            var result = splitter.split(List.of("Cocoa Butter", "Tart - Raisin And Pecan", "Table Cloth 54x72 White", "Flower - Daisies", "Fond - Chocolate", "Cookies - Englishbay Wht"));
            System.out.println(result);
            System.out.println(result.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
