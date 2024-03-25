package org.post.views;

import org.post.config.Settings;
import org.post.dao.UserDAO;
import org.post.email.EmailSender;
import org.post.redis.Cache;
import org.post.utils.Pair;
import org.post.utils.Printing;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class User {
    static UserDAO userTable;
    static Cache redis;

    static {
        try {
            userTable = new UserDAO("users");
            redis = new Cache(new Settings());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void askCreateUser() throws SQLException {
        System.out.println("\t\t*** Création d'utilisateur ***");
        Scanner scanner = new Scanner(System.in);

        System.out.print("\u001B[36mEntrer votre nom complet >: \u001B[0m");
        String name = scanner.nextLine();

        System.out.print("\u001B[36mEntrer votre email >: \u001B[0m");
        String email = scanner.nextLine();

        if (userTable.getByEmail(email) != null) {
            System.out.println("\u001B[31mL'email existe déjà. Veuillez utiliser un autre email.\u001B[0m");
            return;
        }

        System.out.print("\u001B[36mEntrer votre mot de passe >: \u001B[0m");
        String password = scanner.nextLine();

        System.out.println(" Création d'utilisateur en cours ......");

        String confirmationCode = genererCodeConfirmation(email);
        redis.SetKey(email, confirmationCode);
        envoyerEmailConfirmation(email, name, confirmationCode);

        if (userTable.insertUser(name, email, password, false)) {
            System.out.println("\u001B[32mUtilisateur créé avec succès \uD83D\uDE00\u001B[0m");
        } else {
            System.out.println("\u001B[31mImpossible de créer l'utilisateur.\u001B[0m");
            return;
        }
        System.out.print("\u001B[36mVoulez-vous confirmer le compte maintenant ? (o/n) >: \u001B[0m");
        String confirmationChoice = scanner.nextLine();

        if (confirmationChoice.equalsIgnoreCase("o")) {
            System.out.print("\u001B[36mVeuillez saisir le code de confirmation reçu par email >: \u001B[0m");
            String userInputCode = scanner.nextLine();

            String generatedCode = redis.GetKey(email);

            if (generatedCode == null || !generatedCode.equals(userInputCode)) {
                System.out.println("Code incorrect ou expiré. Veuillez demander un nouveau.");
            } else {
                if (userTable.confirmAccount(email)) {
                    System.out.println("\u001B[32mCompte confirmé avec succès. Vous pouvez maintenant vous connecter.\u001B[0m");
                } else {
                    System.out.println("\u001B[31mImpossible de confirmer le compte.\u001B[0m");
                }
            }
        } else {
            System.out.println("\u001B[33mVous pouvez confirmer votre compte ultérieurement en utilisant la fonction de confirmation.\u001B[0m");

        }
    }

    public static void confirmAccountRequestCode(String email, String name) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        UserDAO userDAO = new UserDAO("users");
        org.post.models.User user = userDAO.getByEmail(email);

        if (user == null) {
            System.out.println("\u001B[31mAucun utilisateur trouvé avec cet email.\u001B[0m");
            return;
        }

        if (user.isActive()) {
            System.out.println("Le compte est déjà confirmé. Vous pouvez vous connecter.");
            return;
        }

        // Générer un code de confirmation
        String confirmationCode = genererCodeConfirmation(email);

        // Stocker le code dans redis
        redis.SetKey(email, confirmationCode);

        envoyerEmailConfirmation(email, name, confirmationCode);

        System.out.print("\u001B[36mVeuillez saisir le code de confirmation reçu par email >: \u001B[0m");
        String userInputCode = scanner.nextLine();

        String generatedCode = redis.GetKey(email);

        if (generatedCode == null || !generatedCode.equals(userInputCode)) {
            System.out.println("Code incorrect ou expiré. Veuillez demander un nouveau.");
            return;
        }

        if (userDAO.confirmAccount(email)) {
            System.out.println("\u001B[32mUtilisateur confirmé avec succès. Vous pouvez maintenant vous connecter. \uD83D\uDE00\u001B[0m");
        } else {
            System.out.println("\u001B[31mImpossible de confirmer votre compte. \uD83D\uDE14\u001B[0m");
        }
    }

    public static void askConfirmUser() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\u001B[36mEntrer l'email à confirmer >: \u001B[0m");
        String email = scanner.nextLine();
        System.out.print("\u001B[36mEntrer le code de confirmation reçu par email (lors de la création) >: \u001B[0m");
        String userInputCode = scanner.nextLine();

        UserDAO userDAO = new UserDAO("users");
        org.post.models.User user = userDAO.getByEmail(email);

        if (user == null) {
            System.out.println("\u001B[31mAucun utilisateur trouvé avec cet email.\u001B[0m");
            return;
        }

        String generatedCode = redis.GetKey(email);
        if (generatedCode == null || !generatedCode.equals(userInputCode)) {
            System.out.println("\u001B[31mCode incorrect ou expiré. Veuillez demander un nouveau.\u001B[0m");
        } else {
            if (userDAO.confirmAccount(email)) {
                System.out.println("\u001B[32mCompte confirmé avec succès. Vous pouvez maintenant vous connecter.\u001B[0m");
            } else {
                System.out.println("\u001B[31mImpossible de confirmer votre compte. \uD83D\uDE14\u001B[0m");
            }
        }
    }

    public static void askGenerateNewCode() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\u001B[36mEntrer votre email >: \u001B[0m");
        String email = scanner.nextLine();

        UserDAO userDAO = new UserDAO("users");
        org.post.models.User user = userDAO.getByEmail(email);

        if (user == null) {
            System.out.println("\u001B[31mAucun utilisateur trouvé avec cet email.\u001B[0m");
            return;
        }

        if (user.isActive()) {
            System.out.println("\u001B[32mVotre compte est déjà actif. Vous n'avez pas besoin d'un nouveau code.\u001B[0m");
            return;
        }
        if (user.isActive()) {
            System.out.println("\u001B[32mVotre compte est déjà actif. Vous n'avez pas besoin d'un nouveau code.\u001B[0m");
            return;
        }

        // Générer un nouveau code de confirmation
        String newConfirmationCode = genererCodeConfirmation(email);

        // Stocker le nouveau code dans Redis
        redis.SetKey(email, newConfirmationCode);

        // Envoyer le nouveau code par email
        envoyerEmailConfirmation(email, user.getName(), newConfirmationCode);

        System.out.println("\u001B[32mNouveau code de confirmation généré et envoyé avec succès.\u001B[0m");
    }


    private static String genererCodeConfirmation(String email) {
        String characters = "0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder codeBuilder = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            codeBuilder.append(randomChar);
        }

        return codeBuilder.toString();
    }

    private static void envoyerEmailConfirmation(String email, String name, String codeConfirmation) {
        String sujet = "Confirmation de création de compte";
        String corpsHtml = String.format("<p>Bonjour %s \nMerci d'avoir créer un compte. Votre code de" + " confirmation est : %s</p>.\nIl expirera dans %d minutes", name, codeConfirmation, new Settings().getRedisExpireMinutes());

        EmailSender.sendHtmlEmail(email, sujet, corpsHtml);
    }


    public static void askGetAllUser() throws SQLException {
        System.out.println("\t\t**** Affichage de tous les utilisateurs ******");

        List<org.post.models.User> users = userTable.getAllUsers();
        System.out.println("\u001B[36m.... " + "(" + users.size() + ") résultats....\u001B[0m");
        Printing.print(users);
    }

    public static void askResetPassword() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("**** Réinitialisation du mot de passe ****");
        System.out.print("\u001B[36mEntrer votre email >: \u001B[0m");
        String email = scanner.nextLine();

        org.post.models.User user = userTable.getByEmail(email);

        if (user != null) {
            System.out.print("\u001B[36mEntrez votre nouveau mot de passe >: \u001B[0m");
            String newPassword = scanner.nextLine();

            if (userTable.resetPassword(email, newPassword)) {
                System.out.println("Mot de passe réinitialisé avec succès.");
            } else {
                System.out.println("Échec de la réinitialisation du mot de passe.");
            }
        } else {
            System.out.println("Aucun utilisateur trouvé avec cet e-mail.");
        }
    }


    public static void askAuthenticate() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\u001B[36mEntrer votre email >: \u001B[0m");
        String email = scanner.nextLine();

        System.out.print("\u001B[36mEntrer votre mot de passe >: \u001B[0m");
        String password = scanner.nextLine();

        Pair<Boolean, org.post.models.User> res = userTable.loginUser(email, password);
        if (res.getKey()) {
            Settings.setCurrentUser(res.getValue());
            System.out.println("Authentification avec succès.");
        } else {
            if (userTable.getByEmail(email) != null && !userTable.getByEmail(email).isActive()) {
                System.out.println("Votre compte n'est pas actif. Veuillez activer votre compte pour vous connecter.");
            } else {
                System.out.println("Email ou mot de passe incorrect.");
            }
        }
    }


}
