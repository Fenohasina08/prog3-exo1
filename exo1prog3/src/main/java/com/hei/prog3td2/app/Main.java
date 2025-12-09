package com.hei.prog3td2.app;

import com.hei.prog3td2.model.Category;
import com.hei.prog3td2.model.Product;
import com.hei.prog3td2.service.DataRetriever;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();

        System.out.println("=== Test 1 : getAllCategories() ===");
        List<Category> categories = dataRetriever.getAllCategories();
        categories.forEach(System.out::println);
        System.out.println();

        System.out.println("=== Test 2 : getProductList(page, size) ===");
        List<Product> productsPage1 = dataRetriever.getProductList(1, 10);
        System.out.println("Page 1, size 10 :");
        productsPage1.forEach(System.out::println);
        System.out.println();

        List<Product> productsPage2 = dataRetriever.getProductList(2, 3);
        System.out.println("Page 2, size 3 :");
        productsPage2.forEach(System.out::println);
        System.out.println();

        System.out.println("=== Test 3 : getProductsByCriteria (sans pagination) ===");

        System.out.println("a) productName='Dell' :");
        List<Product> dellProducts = dataRetriever.getProductsByCriteria("Dell", null, null, null);
        dellProducts.forEach(System.out::println);
        System.out.println();

        System.out.println("b) categoryName='info' :");
        List<Product> infoProducts = dataRetriever.getProductsByCriteria(null, "info", null, null);
        infoProducts.forEach(System.out::println);
        System.out.println();

        System.out.println("c) productName='iPhone', categoryName='mobile' :");
        List<Product> iphoneMobile = dataRetriever.getProductsByCriteria("iPhone", "mobile", null, null);
        iphoneMobile.forEach(System.out::println);
        System.out.println();

        Instant min = LocalDateTime.of(2024, 2, 1, 0, 0).toInstant(ZoneOffset.UTC);
        Instant max = LocalDateTime.of(2024, 3, 1, 0, 0).toInstant(ZoneOffset.UTC);
        System.out.println("d) creationMin=2024-02-01, creationMax=2024-03-01 :");
        List<Product> dateFiltered = dataRetriever.getProductsByCriteria(null, null, min, max);
        dateFiltered.forEach(System.out::println);
        System.out.println();

        System.out.println("e) productName='Samsung', categoryName='bureau' :");
        List<Product> samsungBureau = dataRetriever.getProductsByCriteria("Samsung", "bureau", null, null);
        samsungBureau.forEach(System.out::println);
        System.out.println();

        System.out.println("f) productName='Sony', categoryName='informatique' :");
        List<Product> sonyInfo = dataRetriever.getProductsByCriteria("Sony", "informatique", null, null);
        sonyInfo.forEach(System.out::println);
        System.out.println();

        Instant minAudio = LocalDateTime.of(2024, 1, 1, 0, 0).toInstant(ZoneOffset.UTC);
        Instant maxAudio = LocalDateTime.of(2024, 12, 1, 0, 0).toInstant(ZoneOffset.UTC);
        System.out.println("g) categoryName='audio', creationMin=2024-01-01, creationMax=2024-12-01 :");
        List<Product> audioDate = dataRetriever.getProductsByCriteria(null, "audio", minAudio, maxAudio);
        audioDate.forEach(System.out::println);
        System.out.println();

        System.out.println("h) aucun filtre (tous les produits) :");
        List<Product> all = dataRetriever.getProductsByCriteria(null, null, null, null);
        all.forEach(System.out::println);
        System.out.println();

        System.out.println("=== Test 4 : getProductsByCriteria avec pagination ===");

        System.out.println("i) aucun filtre, page=1, size=10 :");
        List<Product> paginated1 = dataRetriever.getProductsByCriteria(null, null, null, null, 1, 10);
        paginated1.forEach(System.out::println);
        System.out.println();

        System.out.println("j) productName='Dell', page=1, size=5 :");
        List<Product> paginated2 = dataRetriever.getProductsByCriteria("Dell", null, null, null, 1, 5);
        paginated2.forEach(System.out::println);
        System.out.println();

        System.out.println("k) categoryName='informatique', page=1, size=10 :");
        List<Product> paginated3 = dataRetriever.getProductsByCriteria(null, "informatique", null, null, 1, 10);
        paginated3.forEach(System.out::println);
        System.out.println();

        System.out.println("=== Tests terminés ===");
    }
}
