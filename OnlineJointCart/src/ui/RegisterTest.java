package ui;

import java.util.Scanner;
import dao.UserDAO;
import model.User;

public class RegisterTest{

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        UserDAO dao = new UserDAO();

        System.out.println("===== USER REGISTRATION =====");

        System.out.print("Enter Username: ");
        String username = sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        User user = new User(username, email, password);

        boolean result = dao.registerUser(user);

        if (result) {
            System.out.println("User Registered Successfully!");
        } else {
            System.out.println("Registration Failed! (Email may already exist)");
        }

        sc.close();
    }
}