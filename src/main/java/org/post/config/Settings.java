package org.post.config;

import org.post.models.User;

public class Settings {

    private final String mysqlUser;
    private final String mysqlPassword;
    private final String mysqlHost;
    private final String mysqlPort;
    private final String mysqlDatabase;

    private final String smtpUser;
    private final String smtpPassword;
    private final String smtpHost;
    private final int smtpPort;

    private final String redisHost;
    private final int redisPort;

    private static org.post.models.User currentUser;
    public static String cacheFileName = "current_user.ser";

    public Settings() {
        mysqlUser = System.getenv("MYSQL_USER");
        mysqlPassword = System.getenv("MYSQL_PASSWORD");
        mysqlHost = System.getenv("MYSQL_HOST");
        mysqlPort = System.getenv("MYSQL_PORT");
        mysqlDatabase = System.getenv("MYSQL_DATABASE");

        smtpUser = System.getenv("SMTP_USER");
        smtpPassword = System.getenv("SMTP_PASSWORD");
        smtpHost = System.getenv("SMTP_HOST");
        smtpPort = Integer.parseInt(System.getenv("SMTP_PORT"));

        redisHost = System.getenv("REDIS_HOST");
        redisPort = Integer.parseInt(System.getenv("REDIS_PORT"));

    }


    public String getUrl() {
        return String.format("jdbc:mysql://%s:%s/%s", mysqlHost, mysqlPort, mysqlDatabase);
    }

    public String getUser() {
        return mysqlUser;
    }

    public static User getCurrentUser() {
        if (currentUser == null)
            throw new RuntimeException("Aucun utilisateur authentifié. Pensez à vous connecter pour obtenir l'accès");
        return currentUser;
    }

//    public int getId(){
//        return  id;
//    }


    public static void setCurrentUser(org.post.models.User newUser) {
        currentUser = newUser;
    }

    public String getPassword() {
        return mysqlPassword;
    }


    public String getSmtpUser() {
        return smtpUser;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public String getEmailSender() {
        return "korka-dev@sanutech.com";
    }

    public String getRedisHost() {
        return redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public int getRedisExpireMinutes() {
        return 5;
    }


}
