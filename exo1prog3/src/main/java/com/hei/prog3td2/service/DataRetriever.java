package com.hei.prog3td2.service;

import com.hei.prog3td2.db.DBConnection;
import com.hei.prog3td2.model.Category;
import com.hei.prog3td2.model.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {


    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM product_category ORDER BY id";

        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("product_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }


    public List<Product> getProductList(int page, int size) {
        List<Product> products = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = "SELECT * FROM product ORDER BY id LIMIT ? OFFSET ?";

        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, size);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }


    public List<Product> getProductsByCriteria(String productName, String categoryName,
                                               Instant creationMin, Instant creationMax) {
        List<Product> products = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT DISTINCT p.*
                FROM product p
                LEFT JOIN product_category c ON p.id = c.product_id
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (productName != null && !productName.isBlank()) {
            sql.append(" AND p.name ILIKE ?");
            params.add("%" + productName + "%");
        }

        if (categoryName != null && !categoryName.isBlank()) {
            sql.append(" AND c.name ILIKE ?");
            params.add("%" + categoryName + "%");
        }

        if (creationMin != null) {
            sql.append(" AND p.creation_datetime >= ?");
            params.add(Timestamp.from(creationMin));
        }

        if (creationMax != null) {
            sql.append(" AND p.creation_datetime <= ?");
            params.add(Timestamp.from(creationMax));
        }

        sql.append(" ORDER BY p.id");

        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }


    public List<Product> getProductsByCriteria(String productName, String categoryName,
                                               Instant creationMin, Instant creationMax,
                                               int page, int size) {
        List<Product> products = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT DISTINCT p.*
                FROM product p
                LEFT JOIN product_category c ON p.id = c.product_id
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (productName != null && !productName.isBlank()) {
            sql.append(" AND p.name ILIKE ?");
            params.add("%" + productName + "%");
        }

        if (categoryName != null && !categoryName.isBlank()) {
            sql.append(" AND c.name ILIKE ?");
            params.add("%" + categoryName + "%");
        }

        if (creationMin != null) {
            sql.append(" AND p.creation_datetime >= ?");
            params.add(Timestamp.from(creationMin));
        }

        if (creationMax != null) {
            sql.append(" AND p.creation_datetime <= ?");
            params.add(Timestamp.from(creationMax));
        }

        sql.append(" ORDER BY p.id LIMIT ? OFFSET ?");
        int offset = (page - 1) * size;
        params.add(size);
        params.add(offset);

        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }


    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getBigDecimal("price"),
                rs.getTimestamp("creation_datetime").toInstant()
        );
    }
}
