package org.post.views;

import org.post.dao.TaskDAO;
import org.post.utils.Printing;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Task {
    static TaskDAO postTable;

    static {
        try {
            postTable = new TaskDAO("posts");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void askCreateTask() throws SQLException {
        System.out.println("\t\t**** Creation d'une tache ******");
        Scanner scanner = new Scanner(System.in);

        System.out.print("\u001B[36mEntrer le  titre de la tache  >: \u001B[0m");
        String title = scanner.nextLine();

        System.out.print("\u001B[36mEntrer le categorie de la tache >: \u001B[0m");
        String category = scanner.nextLine();

        System.out.print("\u001B[36mEntrer le contenu de la tache >: \u001B[0m");
        String content = scanner.nextLine();


        System.out.println("Creation d'une tache......");


        if (postTable.insertTask(title, category, content))
            System.out.println("Tache crée avec succès.");
        else
            System.out.println("Impossbile de creer une tache");
    }

    public static void askGetAllTasks() throws SQLException {
        System.out.println("\t\t**** Affichage des taches  ******");

        List<org.post.models.Task> posts = postTable.getAllTasks();
        System.out.println(".... " + "(" + posts.size() + ") resultats....");
        Printing.print(posts);

    }

    public static void askGetAllTasksByAuthor() throws SQLException {
        System.out.println("\t\t**** Getting All Posts  ******");
        System.out.println("Printing Posts ....");

        List<org.post.models.Task> posts = postTable.getTaskByAuthor();
        System.out.println(".... " + "(" + posts.size() + ") results....");
        Printing.print(posts);

    }

    public static void confirmTask() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\u001B[36mEntrer le titre de la tâche à compléter >: \u001B[0m");
        String taskTitle = scanner.nextLine();

        if (postTable.TaskConfirmation(taskTitle)) {
            System.out.println("\u001B[32mLa tâche a été complétée avec succès.\u001B[0m");
        } else {
            System.out.println("\u001B[31mLa tâche n'a pas pu être complétée.\u001B[0m");
        }
    }

    public static void askFilterTasksByCategory() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\u001B[36mEntrer le categorie à afficher >: \u001B[0m");
        String category = scanner.nextLine();

        List<org.post.models.Task> filteredTasks = postTable.filterCompletedTasks(category);

        Printing.print(filteredTasks);
    }

    public static void askModifyTask() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("**** Modification de tâche ****");
        System.out.print("\u001B[36mEntrez l'ID de la tâche à modifier >: \u001B[0m");
        int taskId = scanner.nextInt();
        scanner.nextLine();

        org.post.models.Task taskToModify = postTable.findById(taskId);

        if (taskToModify != null) {
            System.out.println("Tâche actuelle :");
            System.out.println(taskToModify.toString());

            System.out.print("\u001B[36mNouveau titre (laissez vide pour ne pas modifier) >: \u001B[0m");
            String newTitle = scanner.nextLine();

            System.out.print("\u001B[36mNouvelle catégorie (laissez vide pour ne pas modifier) >: \u001B[0m");
            String newCategory = scanner.nextLine();


            System.out.print("\u001B[36mNouveau contenu (laissez vide pour ne pas modifier) >: \u001B[0m");
            String newContent = scanner.nextLine();

            postTable.modifyTask(taskToModify.getId(), newTitle, newCategory, newContent);

        } else {
            System.out.println("Aucune tâche trouvée avec cet ID.");
        }
    }

    public static void askDeleteTask() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Suppression de tache ***");

        System.out.print("\u001B[36mEntrez l'ID de la tâche à supprimer >: \u001B[0m");
        int taskIdToDelete = scanner.nextInt();

        if (postTable.deleteTask(taskIdToDelete)) {
            System.out.println("Tâche supprimée avec succès.");
        } else {
            System.out.println("\u001B[31mImpossible de supprimer la tâche ou vous n'êtes pas autorisé.\u001B[0m");

        }
    }



}

