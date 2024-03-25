package org.post.views;

import org.post.config.Settings;
import org.post.utils.Caching;

import java.sql.SQLException;
import java.util.Scanner;

public class Menu {
    public static void printMenu() {
//        String currentUser = Settings.getCurrentUser().getName().toUpperCase();
//
//        System.out.println("******** Welcome, " + currentUser + "! *****");
        System.out.println("**********  MANAGEMENTS TASKS **********");
        System.out.println("------------- USERS --------------------");
        System.out.println(" \t\t1.  Create User");
        System.out.println(" \t\t2.  Confirm User");
        System.out.println(" \t\t3.  Generate new code confirmation");
        System.out.println(" \t\t4.  Change Password");
        System.out.println(" \t\t5.  Authentication");
        System.out.println(" \t\t6.  Get All Users");
        System.out.println("------------- TASKS ---------------------");
        System.out.println(" \t\t7.  Create Task");
        System.out.println(" \t\t8.  Confirm Task");
        System.out.println(" \t\t9.  Modify Task");
        System.out.println(" \t\t10. Get All Tasks");
        System.out.println(" \t\t11. Filter Tasks By Author");
        System.out.println(" \t\t12. Filter Tasks By Category");
        System.out.println(" \t\t13. Delete Task");
        System.out.println("------------------------------------------");
        System.out.println(" \t\t14. Exit");
        System.out.println("*******************************************");


    }


    public static void printAbout() {
        System.out.println("************************************************");
        System.out.println("\t\t🌟 Bienvenue " + Settings.getCurrentUser().getName().toUpperCase() + " 🌟");
        System.out.println("************************************************");
    }


    public static void run() throws SQLException {
//        Settings.setCurrentUser(Caching.loadUser());
//
//        printAbout();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMenu();

            System.out.print("choice >: ");
            int choice = scanner.nextInt();


            switch (choice) {

                case 1:
                    User.askCreateUser();
                    break;
                case 2:
                    User.askConfirmUser();
                    break;
                case 3:
                    User.askGenerateNewCode();
                    break;
                case 4:
                    User.askResetPassword();
                    break;
                case 5:
                    User.askAuthenticate();
                    break;

                case 6:
                    User.askGetAllUser();
                    break;

                case 7:
                    Task.askCreateTask();
                    break;
                case 8:
                    Task.confirmTask();
                    break;

                case 9:
                    Task.askModifyTask();
                    break;

                case 10:
                    Task.askGetAllTasks();
                    break;

                case 11:
                    Task.askGetAllTasksByAuthor();
                    break;

                case 12:
                    Task.askFilterTasksByCategory();
                    break;

                case 13:
                    Task.askDeleteTask();
                    break;


                case 14:
                    System.out.println("Bye bye....");
                    scanner.close();
                    Caching.saveUser(Settings.getCurrentUser());
                    System.exit(0);
                    break;
                default:
                    System.out.println("Entree Invalide");

            }
        }

    }

}
