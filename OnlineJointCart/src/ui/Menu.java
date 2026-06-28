package ui;
import ui.Menu;
import java.util.Scanner;
import java.sql.*;

import dao.UserDAO;
import db.DBConnection;   // make sure this exists in your project

public class Menu {

    public static void start(int userId, int groupId, int ownerId) {

        Scanner sc = new Scanner(System.in);
        UserDAO dao = new UserDAO();

        while (true) {

            System.out.println("\n===== MENU =====");
            System.out.println("1. View Products");
            System.out.println("2. Add to Cart");
            System.out.println("3. View Cart");
            System.out.println("4. Checkout");
            System.out.println("5. Remove from cart");
            System.out.println("6. Exit");

            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    dao.viewProducts();
                    break;

                case 2: {

                    System.out.print("Enter Product ID: ");
                    int pid = sc.nextInt();

                    System.out.print("Enter Quantity: ");
                    int qty = sc.nextInt();

                    try {
                        Connection con = DBConnection.getConnection();

                        String sql = "SELECT product_name, price FROM products WHERE product_id=?";
                        PreparedStatement ps = con.prepareStatement(sql);

                        ps.setInt(1, pid);

                        ResultSet rs = ps.executeQuery();

                        if (!rs.next()) {
                            System.out.println("Product not found!");
                            break;
                        }

                        String name = rs.getString("product_name");
                        double price = rs.getDouble("price");

                        System.out.println("DEBUG: Adding -> " + name + " | " + price);

                        dao.addToCart(userId,groupId, name, price, qty);

                        con.close();

                    } catch (Exception e) {
                        System.out.println("ERROR in Add to Cart:");
                        e.printStackTrace();
                    }

                    break;
                }

                case 3:
                    dao.viewCart(groupId);
                    break;

                case 4:
                    dao.checkout(userId,groupId, ownerId);
                    break;

                case 5:
                	System.out.print("Enter Cart ID (from View Cart): ");
                    int cartId = sc.nextInt();
                    dao.removeFromCart(userId, cartId);
                    break;

                case 6:
                    System.out.println("Exiting...");
                    return;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}