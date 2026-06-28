package dao;

import java.sql.*;
import db.DBConnection;
import model.User;

public class UserDAO {

    // ---------------- REGISTER ----------------
    public boolean registerUser(User user) {

        try {
            Connection con = DBConnection.getConnection();

            String checkSql = "SELECT * FROM users WHERE email=?";
            PreparedStatement checkPs = con.prepareStatement(checkSql);
            checkPs.setString(1, user.getEmail());

            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                System.out.println("Email already exists!");
                return false;
            }

            String sql = "INSERT INTO users(username,email,password) VALUES(?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ---------------- LOGIN (RETURN USER ID) ----------------
    public int loginUser(String username, String password) {

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT user_id FROM users WHERE username=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    // ---------------- VIEW PRODUCTS ----------------
    public void viewProducts() {

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT product_id, product_name, price, stock FROM products";
            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            System.out.println("ID\tNAME\tPRICE\tSTOCK");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("product_id") + "\t" +
                        rs.getString("product_name") + "\t" +
                        rs.getDouble("price") + "\t" +
                        rs.getInt("stock")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- ADD TO CART ----------------
    public void addToCart(int userId, int groupId, String productName, double price, int quantity) {

        try {
            Connection con = DBConnection.getConnection();

            // STEP 1: CHECK IF ITEM ALREADY EXISTS
            String checkSql = "SELECT quantity FROM cart WHERE group_id=? AND product_name=?";
            PreparedStatement checkPs = con.prepareStatement(checkSql);

            checkPs.setInt(1, groupId);
            checkPs.setString(2, productName);

            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                // ITEM EXISTS → UPDATE QUANTITY
                int oldQty = rs.getInt("quantity");
                int newQty = oldQty + quantity;

                String updateSql = "UPDATE cart SET quantity=? WHERE group_id=? AND product_name=?";
                PreparedStatement updatePs = con.prepareStatement(updateSql);

                updatePs.setInt(1, newQty);
                updatePs.setInt(2, groupId);
                updatePs.setString(3, productName);

                updatePs.executeUpdate();

                System.out.println("Quantity updated in cart!");

            } else {
                // ITEM DOES NOT EXIST → INSERT NEW ROW
            	String sql = "INSERT INTO cart(user_id, group_id, product_name, price, quantity) VALUES (?, ?, ?, ?, ?)";
            	PreparedStatement ps = con.prepareStatement(sql);

            	ps.setInt(1, userId);
            	ps.setInt(2, groupId);
            	ps.setString(3, productName);
            	ps.setDouble(4, price);
            	ps.setInt(5, quantity);
                ps.executeUpdate();

                System.out.println("Item added to cart!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ---------------- VIEW CART ----------------
    public void viewCart(int groupId) {

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM cart WHERE group_id=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, groupId);

            ResultSet rs = ps.executeQuery();

            System.out.println("---- YOUR CART ----");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("cart_id") + " | " +
                        rs.getString("product_name") + " | " +
                        rs.getDouble("price") + " | " +
                        rs.getInt("quantity")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- REMOVE FROM CART ----------------
    public void removeFromCart(int userId, int cartId) {

        try {
            Connection con = DBConnection.getConnection();

            // STEP 1: CHECK IF ITEM EXISTS FOR THIS USER
            String checkSql = "SELECT * FROM cart WHERE cart_id=? AND user_id=?";
            PreparedStatement checkPs = con.prepareStatement(checkSql);

            checkPs.setInt(1, cartId);
            checkPs.setInt(2, userId);

            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Item not found in YOUR cart!");
                return;
            }

            // STEP 2: DELETE ITEM
            String sql = "DELETE FROM cart WHERE cart_id=? AND user_id=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, cartId);
            ps.setInt(2, userId);

            ps.executeUpdate();

            System.out.println("✅ Item removed successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- CHECKOUT ----------------
    public void checkout(int userId, int groupId, int ownerId) {

        try {
            Connection con = DBConnection.getConnection();

            // STEP 1: ONLY OWNER CAN CHECKOUT
            if (userId != ownerId) {
                System.out.println("❌ Only owner can perform checkout!");
                return;
            }

            // STEP 2: FETCH CART
            String sql = "SELECT product_name, price, quantity FROM cart WHERE group_id=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, groupId);

            ResultSet rs = ps.executeQuery();

            double total = 0;
            boolean hasData = false;

            System.out.println("\n===== BILL =====");
            System.out.println("NAME\tPRICE\tQTY\tTOTAL");

            while (rs.next()) {
                hasData = true;

                String name = rs.getString("product_name");
                double price = rs.getDouble("price");
                int qty = rs.getInt("quantity");

                double itemTotal = price * qty;
                total += itemTotal;

                System.out.println(name + "\t" + price + "\t" + qty + "\t" + itemTotal);
            }

            // STEP 3: EMPTY CART CHECK
            if (!hasData) {
                System.out.println("❌ Cart is empty!");
                return;
            }

            System.out.println("----------------------");
            System.out.println("GRAND TOTAL: " + total);

            // STEP 4: CLEAR GROUP CART
            String deleteSql = "DELETE FROM cart WHERE group_id=?";
            PreparedStatement ps2 = con.prepareStatement(deleteSql);

            ps2.setInt(1, groupId);
            ps2.executeUpdate();

            System.out.println("✅ Checkout successful!");

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ---------------- EMAIL CHECK ----------------
    public boolean emailExists(String email) {

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM users WHERE email=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}