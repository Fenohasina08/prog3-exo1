package com.hei.prog3td2.app;

import com.hei.prog3td2.service.DataRetriever;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever retriever = new DataRetriever();

        System.out.println("=========================================");
        System.out.println("TEST DU TD JDBC - FILTRAGE ET PAGINATION");
        System.out.println("=========================================\n");

        System.out.println("1. TEST getAllCategories()");
        System.out.println("--------------------------");
        List<com.hei.prog3td2.model.Category> categories = retriever.getAllCategories();
        System.out.println("Nombre de catégories : " + categories.size());
        categories.forEach(c -> System.out.println("  - " + c.getName() + " (id: " + c.getId() + ")"));

        System.out.println("\n2. TEST getProductList(page, size)");
        System.out.println("----------------------------------");

        System.out.println("\na) page=1, size=10 :");
        retriever.getProductList(1, 10).forEach(System.out::println);

        System.out.println("\nb) page=1, size=5 :");
        retriever.getProductList(1, 5).forEach(System.out::println);

        System.out.println("\nc) page=1, size=3 :");
        retriever.getProductList(1, 3).forEach(System.out::println);

        System.out.println("\nd) page=2, size=2 :");
        retriever.getProductList(2, 2).forEach(System.out::println);

        System.out.println("\n3. TEST getProductsByCriteria (sans pagination)");
        System.out.println("------------------------------------------------");

        System.out.println("\na) productName='Dell', categoryName=null, dates=null :");
        System.out.println("   Résultats attendus : 1 produit (Laptop Dell XPS)");
        List<com.hei.prog3td2.model.Product> results = retriever.getProductsByCriteria("Dell", null, null, null);
        System.out.println("   Nombre trouvé : " + results.size());
        results.forEach(p -> System.out.println("   - " + p.getName() + " (catégorie: " +
                (p.getCategory() != null ? p.getCategory().getName() : "null") + ")"));

        System.out.println("\nb) productName=null, categoryName='info', dates=null :");
        System.out.println("   Résultats attendus : 2 produits (Dell XPS et Ecran Samsung - tous deux Informatique)");
        results = retriever.getProductsByCriteria(null, "info", null, null);
        System.out.println("   Nombre trouvé : " + results.size());
        results.forEach(p -> System.out.println("   - " + p.getName() + " (catégorie: " +
                (p.getCategory() != null ? p.getCategory().getName() : "null") + ")"));

        System.out.println("\nc) productName='iPhone', categoryName='mobile', dates=null :");
        System.out.println("   Résultats attendus : 1 produit (iPhone 13 avec catégorie Mobile)");
        results = retriever.getProductsByCriteria("iPhone", "mobile", null, null);
        System.out.println("   Nombre trouvé : " + results.size());
        results.forEach(p -> System.out.println("   - " + p.getName() + " (catégorie: " +
                (p.getCategory() != null ? p.getCategory().getName() : "null") + ")"));

        System.out.println("\nd) productName=null, categoryName=null, dates=[2024-02-01, 2024-03-01] :");
        Instant minDate = Instant.parse("2024-02-01T00:00:00Z");
        Instant maxDate = Instant.parse("2024-03-01T23:59:59Z");
        System.out.println("   Résultats attendus : 2 produits (iPhone 13 et Casque Sony)");
        results = retriever.getProductsByCriteria(null, null, minDate, maxDate);
        System.out.println("   Nombre trouvé : " + results.size());
        results.forEach(p -> {
            String dateStr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .format(LocalDateTime.ofInstant(p.getCreationDatetime(), ZoneId.systemDefault()));
            System.out.println("   - " + p.getName() + " (date: " + dateStr + ")");
        });

        System.out.println("\ne) productName='Samsung', categoryName='bureau', dates=null :");
        System.out.println("   Résultats attendus : 1 produit (Ecran Samsung avec catégorie Bureau)");
        results = retriever.getProductsByCriteria("Samsung", "bureau", null, null);
        System.out.println("   Nombre trouvé : " + results.size());
        results.forEach(p -> System.out.println("   - " + p.getName() + " (catégorie: " +
                (p.getCategory() != null ? p.getCategory().getName() : "null") + ")"));

        System.out.println("\nf) productName='Sony', categoryName='informatique', dates=null :");
        System.out.println("   Résultats attendus : 0 produit (Casque Sony a catégorie Audio, pas Informatique)");
        results = retriever.getProductsByCriteria("Sony", "informatique", null, null);
        System.out.println("   Nombre trouvé : " + results.size());
        results.forEach(p -> System.out.println("   - " + p.getName()));

        System.out.println("\ng) productName=null, categoryName='audio', dates=[2024-01-01, 2024-12-01] :");
        minDate = Instant.parse("2024-01-01T00:00:00Z");
        maxDate = Instant.parse("2024-12-01T23:59:59Z");
        System.out.println("   Résultats attendus : 1 produit (Casque Sony)");
        results = retriever.getProductsByCriteria(null, "audio", minDate, maxDate);
        System.out.println("   Nombre trouvé : " + results.size());
        results.forEach(p -> System.out.println("   - " + p.getName()));

        System.out.println("\nh) productName=null, categoryName=null, dates=null :");
        System.out.println("   Résultats attendus : 5 produits (tous les produits)");
        results = retriever.getProductsByCriteria(null, null, null, null);
        System.out.println("   Nombre trouvé : " + results.size());
        results.forEach(p -> System.out.println("   - " + p.getName()));

        System.out.println("\n4. TEST getProductsByCriteria (avec pagination)");
        System.out.println("------------------------------------------------");

        System.out.println("\ni) aucun filtre, page=1, size=10 :");
        System.out.println("   Résultats attendus : 5 produits (tous, page 1)");
        results = retriever.getProductsByCriteria(null, null, null, null, 1, 10);
        System.out.println("   Nombre trouvé : " + results.size());
        results.forEach(p -> System.out.println("   - " + p.getName()));

        System.out.println("\nj) productName='Dell', page=1, size=5 :");
        System.out.println("   Résultats attendus : 1 produit (Dell XPS, page 1)");
        results = retriever.getProductsByCriteria("Dell", null, null, null, 1, 5);
        System.out.println("   Nombre trouvé : " + results.size());
        results.forEach(p -> System.out.println("   - " + p.getName()));

        System.out.println("\nk) categoryName='Informatique', page=1, size=10 :");
        System.out.println("   Résultats attendus : 2 produits (Dell XPS et Ecran Samsung, page 1)");
        results = retriever.getProductsByCriteria(null, "Informatique", null, null, 1, 10);
        System.out.println("   Nombre trouvé : " + results.size());
        results.forEach(p -> System.out.println("   - " + p.getName()));

        System.out.println("\n=========================================");
        System.out.println("TESTS TERMINÉS AVEC SUCCÈS !");
        System.out.println("=========================================");
    }
}