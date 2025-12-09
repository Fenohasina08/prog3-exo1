package com.hei.prog3td2.service;

import com.hei.prog3td2.model.Category;
import com.hei.prog3td2.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
        assertNotNull(categories);
        assertEquals(7, categories.size());
        Category first = categories.get(0);
        assertEquals(1, first.getId());
        assertEquals("Informatique", first.getName());
        assertEquals(1, first.getProductId());
        Category last = categories.get(6);
        assertEquals(7, last.getId());
        assertEquals("Mobile", last.getName());
        assertEquals(2, last.getProductId());
    }

    @Test
    void testGetProductList_pagination() {
        List<Product> page1 = dataRetriever.getProductList(1, 3);
        assertEquals(3, page1.size());
        assertEquals(1, page1.get(0).getId());
        assertEquals("Laptop Dell XPS", page1.get(0).getName());
        List<Product> page2 = dataRetriever.getProductList(2, 3);
        assertEquals(2, page2.size());
        assertEquals(4, page2.get(0).getId());
        assertEquals("Clavier Logitech", page2.get(0).getName());
        List<Product> page3 = dataRetriever.getProductList(3, 3);
        assertTrue(page3.isEmpty());
    }

    @Test
    void testGetProductsByCriteria_productName() {
        List<Product> dellProducts = dataRetriever.getProductsByCriteria("Dell", null, null, null);
        assertEquals(1, dellProducts.size());
        assertEquals("Laptop Dell XPS", dellProducts.get(0).getName());
        List<Product> samsungProducts = dataRetriever.getProductsByCriteria("Samsung", null, null, null);
        assertEquals(1, samsungProducts.size());
        assertEquals("Ecran Samsung 27\"", samsungProducts.get(0).getName());
        List<Product> noProducts = dataRetriever.getProductsByCriteria("XYZ", null, null, null);
        assertTrue(noProducts.isEmpty());
    }

    @Test
    void testGetProductsByCriteria_categoryName() {
        List<Product> infoProducts = dataRetriever.getProductsByCriteria(null, "info", null, null);
        assertEquals(2, infoProducts.size());
        assertTrue(infoProducts.stream().anyMatch(p -> p.getId() == 1));
        assertTrue(infoProducts.stream().anyMatch(p -> p.getId() == 5));
        List<Product> audioProducts = dataRetriever.getProductsByCriteria(null, "audio", null, null);
        assertEquals(1, audioProducts.size());
        assertEquals("Casque Sony WH1000", audioProducts.get(0).getName());
    }

    @Test
    void testGetProductsByCriteria_dates() {
        Instant min = LocalDateTime.of(2024, 2, 1, 0, 0).toInstant(ZoneOffset.UTC);
        Instant max = LocalDateTime.of(2024, 3, 1, 0, 0).toInstant(ZoneOffset.UTC);
        List<Product> products = dataRetriever.getProductsByCriteria(null, null, min, max);
        assertEquals(2, products.size());
        assertTrue(products.stream().anyMatch(p -> p.getId() == 2));
        assertTrue(products.stream().anyMatch(p -> p.getId() == 3));
    }

    @Test
    void testGetProductsByCriteria_combined() {
        List<Product> iphoneMobile = dataRetriever.getProductsByCriteria("iPhone", "mobile", null, null);
        assertEquals(1, iphoneMobile.size());
        assertEquals("iPhone 13", iphoneMobile.get(0).getName());
        List<Product> samsungBureau = dataRetriever.getProductsByCriteria("Samsung", "bureau", null, null);
        assertEquals(1, samsungBureau.size());
        assertEquals("Ecran Samsung 27\"", samsungBureau.get(0).getName());
        List<Product> sonyInfo = dataRetriever.getProductsByCriteria("Sony", "informatique", null, null);
        assertTrue(sonyInfo.isEmpty());
    }

    @Test
    void testGetProductsByCriteria_withPagination() {
        List<Product> page1 = dataRetriever.getProductsByCriteria(null, "info", null, null, 1, 1);
        assertEquals(1, page1.size());
        assertEquals(1, page1.get(0).getId());
        List<Product> page2 = dataRetriever.getProductsByCriteria(null, "info", null, null, 2, 1);
        assertEquals(1, page2.size());
        assertEquals(5, page2.get(0).getId());
        List<Product> page3 = dataRetriever.getProductsByCriteria(null, "info", null, null, 3, 1);
        assertTrue(page3.isEmpty());
    }

    @Test
    void testProductMapping() {
        List<Product> products = dataRetriever.getProductList(1, 1);
        assertEquals(1, products.size());
        Product product = products.get(0);
        assertEquals(1, product.getId());
        assertEquals("Laptop Dell XPS", product.getName());
        assertEquals(new BigDecimal("4500.00"), product.getPrice());
        assertEquals(LocalDateTime.of(2024, 1, 15, 9, 30).atOffset(ZoneOffset.UTC).toInstant(),
                product.getCreationDatetime());
    }

    @Test
    void testCategoryMapping() {
        List<Category> categories = dataRetriever.getAllCategories();
        Category category = categories.get(0);
        assertEquals(1, category.getId());
        assertEquals("Informatique", category.getName());
        assertEquals(1, category.getProductId());
    }
}
