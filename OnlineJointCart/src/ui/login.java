package ui;

import java.util.Scanner;
import dao.UserDAO;
import ui.Menu;

public class login {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        UserDAO dao = new UserDAO();

        while (true) {

            System.out.println("\n===== WELCOME =====");
            System.out.println("1. Login");
            System.out.println("2. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:

                    System.out.print("Enter Username: ");
                    String username = sc.nextLine();

                    System.out.print("Enter Password: ");
                    String password = sc.nextLine();

                    int userId = dao.loginUser(username, password);

                    if (userId != -1) {

                        System.out.println("Login Successful!");
                        System.out.println("User ID: " + userId);

                        System.out.print("Enter Group ID (shared cart): ");
                        int groupId = sc.nextInt();

                        int ownerId = 1; // fixed owner for now

                        Menu.start(userId, groupId, ownerId);

                    } else {
                        System.out.println("Invalid Username or Password!");
                    }

                    break;

                case 2:
                    System.out.println("Exiting...");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}