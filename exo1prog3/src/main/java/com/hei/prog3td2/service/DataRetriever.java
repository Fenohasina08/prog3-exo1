package com.hei.prog3td2.service;

import com.hei.prog3td2.db.DBConnection;
import com.hei.prog3td2.model.Category;
import com.hei.prog3td2.model.Product;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id, name FROM product_category ORDER BY id"; // Pas de DISTINCT

        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(new Category(
                        rs.getInt("id"),
                        rs.getString("name")
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

        // ICI AUSSI : selon le PDF, on doit filtrer par catégorie si on veut être strict
        // Mais getProductList n'a pas de paramètres de filtre, donc on garde la logique actuelle
        String sql = """
            SELECT p.*, 
                   pc.id as cat_id, 
                   pc.name as cat_name
            FROM product p
            LEFT JOIN product_category pc ON p.id = pc.product_id
            WHERE pc.id = (
                SELECT MIN(id) FROM product_category 
                WHERE product_id = p.id
            )
            ORDER BY p.id
            LIMIT ? OFFSET ?
            """;

        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, size);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Category category = null;
                    if (rs.getObject("cat_id") != null) {
                        category = new Category(
                                rs.getInt("cat_id"),
                                rs.getString("cat_name")
                        );
                    }

                    Product product = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getTimestamp("creation_datetime").toInstant(),
                            category
                    );

                    products.add(product);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getProductsByCriteria(
            String productName, String categoryName,
            Instant creationMin, Instant creationMax
    ) {
        List<Product> products = new ArrayList<>();

        // Requête principale
        StringBuilder sql = new StringBuilder("""
        SELECT p.*, 
               pc.id as cat_id, 
               pc.name as cat_name
        FROM product p
        LEFT JOIN product_category pc ON p.id = pc.product_id
        WHERE pc.id = (
            SELECT MIN(id) FROM product_category 
            WHERE product_id = p.id
        )
        """);

        List<Object> params = new ArrayList<>();

        // FILTRE STRICT selon PDF : productName doit aussi vérifier une catégorie
        if (productName != null && !productName.isBlank()) {
            sql.append(" AND p.name ILIKE ?");
            sql.append(" AND EXISTS (");
            sql.append("   SELECT 1 FROM product_category pc2");
            sql.append("   WHERE pc2.product_id = p.id");
            sql.append("   AND pc2.name ILIKE ?");
            sql.append(" )");
            params.add("%" + productName + "%");
            // Pour quel nom de catégorie ? Le PDF ne dit pas...
            // On met le même que categoryName si fourni, sinon on met "%" pour "n'importe quelle catégorie"
            if (categoryName != null && !categoryName.isBlank()) {
                params.add("%" + categoryName + "%");
            } else {
                params.add("%"); // Accepte n'importe quelle catégorie
            }
        }

        // FILTRE categoryName
        if (categoryName != null && !categoryName.isBlank()) {
            // On ne l'ajoute que si productName n'a pas déjà ajouté la condition
            if (productName == null || productName.isBlank()) {
                sql.append(" AND EXISTS (");
                sql.append("   SELECT 1 FROM product_category pc2");
                sql.append("   WHERE pc2.product_id = p.id");
                sql.append("   AND pc2.name ILIKE ?");
                sql.append(" )");
                params.add("%" + categoryName + "%");
            }
        }

        // Gestion des dates (inchangé)
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
                    Category category = null;
                    if (rs.getObject("cat_id") != null) {
                        category = new Category(
                                rs.getInt("cat_id"),
                                rs.getString("cat_name")
                        );
                    }

                    Product product = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getTimestamp("creation_datetime").toInstant(),
                            category
                    );

                    products.add(product);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public List<Product> getProductsByCriteria(
            String productName, String categoryName,
            Instant creationMin, Instant creationMax,
            int page, int size
    ) {
        List<Product> products = new ArrayList<>();
        int offset = (page - 1) * size;

        StringBuilder sql = new StringBuilder("""
        SELECT p.*, 
               pc.id as cat_id, 
               pc.name as cat_name
        FROM product p
        LEFT JOIN product_category pc ON p.id = pc.product_id
        WHERE pc.id = (
            SELECT MIN(id) FROM product_category 
            WHERE product_id = p.id
        )
        """);

        List<Object> params = new ArrayList<>();

        // MÊME LOGIQUE que la méthode sans pagination
        if (productName != null && !productName.isBlank()) {
            sql.append(" AND p.name ILIKE ?");
            sql.append(" AND EXISTS (");
            sql.append("   SELECT 1 FROM product_category pc2");
            sql.append("   WHERE pc2.product_id = p.id");
            sql.append("   AND pc2.name ILIKE ?");
            sql.append(" )");
            params.add("%" + productName + "%");
            if (categoryName != null && !categoryName.isBlank()) {
                params.add("%" + categoryName + "%");
            } else {
                params.add("%");
            }
        }

        if (categoryName != null && !categoryName.isBlank()) {
            if (productName == null || productName.isBlank()) {
                sql.append(" AND EXISTS (");
                sql.append("   SELECT 1 FROM product_category pc2");
                sql.append("   WHERE pc2.product_id = p.id");
                sql.append("   AND pc2.name ILIKE ?");
                sql.append(" )");
                params.add("%" + categoryName + "%");
            }
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
        params.add(size);
        params.add(offset);

        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Category category = null;
                    if (rs.getObject("cat_id") != null) {
                        category = new Category(
                                rs.getInt("cat_id"),
                                rs.getString("cat_name")
                        );
                    }

                    Product product = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getTimestamp("creation_datetime").toInstant(),
                            category
                    );

                    products.add(product);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }
}