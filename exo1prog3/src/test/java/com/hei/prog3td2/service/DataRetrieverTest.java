package com.hei.prog3td2.service;

import com.hei.prog3td2.model.Category;
import com.hei.prog3td2.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataRetrieverTest {
    private DataRetriever dataRetriever;

    @BeforeEach
    void setUp() {
        dataRetriever = new DataRetriever();
    }

    @Test
    void testGetAllCategories() {
        List<Category> categories = dataRetriever.getAllCategories();

        // Vérifier qu'on a des catégories
        assertFalse(categories.isEmpty());

        // Vérifier la première catégorie
        Category firstCategory = categories.get(0);
        assertNotNull(firstCategory.getId());
        assertNotNull(firstCategory.getName());
    }

    @Test
    void testGetProductList() {
        // Test page 1, size 3
        List<Product> page1 = dataRetriever.getProductList(1, 3);
        assertEquals(3, page1.size());

        // Test page 2, size 2
        List<Product> page2 = dataRetriever.getProductList(2, 2);
        assertTrue(page2.size() <= 2);
    }

    @Test
    void testGetProductsByCriteria_productName() {
        // Test avec nom "Dell"
        List<Product> results = dataRetriever.getProductsByCriteria("Dell", null, null, null);
        assertFalse(results.isEmpty());

        // Vérifier que tous les résultats contiennent "Dell"
        results.forEach(product ->
                assertTrue(product.getName().toLowerCase().contains("dell"))
        );
    }

    @Test
    void testGetProductsByCriteria_categoryName() {
        // Test avec catégorie "Informatique"
        List<Product> results = dataRetriever.getProductsByCriteria(null, "Informatique", null, null);

        // Vérifier que tous les résultats ont une catégorie "Informatique"
        results.forEach(product -> {
            if (product.getCategory() != null) {
                assertTrue(product.getCategoryName().toLowerCase().contains("informatique"));
            }
        });
    }

    @Test
    void testGetProductsByCriteria_dateRange() {
        Instant minDate = Instant.parse("2024-02-01T00:00:00Z");
        Instant maxDate = Instant.parse("2024-03-01T00:00:00Z");

        List<Product> results = dataRetriever.getProductsByCriteria(null, null, minDate, maxDate);

        // Vérifier que toutes les dates sont dans l'intervalle
        results.forEach(product -> {
            assertTrue(product.getCreationDatetime().isAfter(minDate) ||
                    product.getCreationDatetime().equals(minDate));
            assertTrue(product.getCreationDatetime().isBefore(maxDate) ||
                    product.getCreationDatetime().equals(maxDate));
        });
    }

    @Test
    void testGetProductsByCriteria_combined() {
        // Test combiné : nom "iPhone" et catégorie "Téléphonie"
        // (car dans ta DB, la première catégorie de iPhone est "Téléphonie", pas "Mobile")
        List<Product> results = dataRetriever.getProductsByCriteria("iPhone", "Téléphonie", null, null);

        // Doit trouver au moins un résultat
        assertFalse(results.isEmpty(), "Devrait trouver iPhone avec catégorie Téléphonie");

        // Vérifier le contenu
        results.forEach(product -> {
            assertTrue(product.getName().toLowerCase().contains("iphone"));
            if (product.getCategory() != null) {
                assertTrue(product.getCategoryName().toLowerCase().contains("téléphonie"));
            }
        });
    }
    @Test
    void testGetProductsByCriteria_withPagination() {
        // Test avec pagination
        List<Product> results = dataRetriever.getProductsByCriteria(null, null, null, null, 1, 2);
        assertEquals(2, results.size());
    }

    @Test
    void testProductMapping() {
        // Vérifier qu'un produit a les bons attributs
        List<Product> products = dataRetriever.getProductList(1, 1);
        if (!products.isEmpty()) {
            Product product = products.get(0);
            assertNotNull(product.getId());
            assertNotNull(product.getName());
            assertNotNull(product.getCreationDatetime());
            // Note: price n'existe plus dans Product
            // Note: category peut être null
        }
    }

    @Test
    void testCategoryMapping() {
        // Vérifier qu'une catégorie a les bons attributs
        List<Category> categories = dataRetriever.getAllCategories();
        if (!categories.isEmpty()) {
            Category category = categories.get(0);
            assertNotNull(category.getId());
            assertNotNull(category.getName());
            // Note: productId n'existe plus dans Category
        }
    }
}