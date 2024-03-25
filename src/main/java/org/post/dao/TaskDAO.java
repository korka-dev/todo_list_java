package org.post.dao;

import org.post.config.Settings;
import org.post.database.Connexion;
import org.post.models.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

public class TaskDAO {
    private final String tableName;
    private final Connection connection;


    public TaskDAO(String tableName) throws SQLException {
        this.tableName = tableName;
        try {
            this.connection = Connexion.getConnexion();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (!tableExists()) createTableSQL();

    }

    public boolean tableExists() throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[]{"TABLE"});

        return resultSet.next();
    }

    public void createTableSQL() {
        String tablesql = "CREATE TABLE " + this.tableName +
                "(" + "id INT AUTO_INCREMENT PRIMARY KEY," +
                "title VARCHAR(255) NOT NULL," +
                "category VARCHAR(255)," +
                "content TEXT," +
                "dateCompleted DATETIME," +
                "dateCreation DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "author_id INT," +
                "FOREIGN KEY(author_id) REFERENCES users(id))";

        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(tablesql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Boolean insertTask(String title, String content, String category) throws SQLException {
        String insertSQL = "INSERT INTO posts(title, category, content, author_id)" +
                " VALUES (?, ?, ?, ?)";

        // Création d'un objet PreparedStatement pour l'insertion
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, category);
        preparedStatement.setString(3, content);
//        preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
        preparedStatement.setInt(4, Settings.getCurrentUser().getId());

        // Exécutez la requête d'insertion
        int rowsInserted = preparedStatement.executeUpdate();
        preparedStatement.close();

        return rowsInserted > 0;
    }


    public List<Task> findByStatement(PreparedStatement preparedStatement) throws SQLException {
        List<Task> tasks = new ArrayList<>();

        ResultSet resultSet;

        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String title = resultSet.getString("title");
            String category = resultSet.getString("category");
            String content = resultSet.getString("content");
            Timestamp dateCreation = resultSet.getTimestamp("dateCreation");
            boolean status = resultSet.getTimestamp("dateCompleted") != null;
            int authorId = resultSet.getInt("author_id");


            Task task = new Task(id, title, category, content, dateCreation, authorId, status);
            tasks.add(task);
        }

        resultSet.close();
        preparedStatement.close();

        return tasks;
    }


    public List<Task> getAllTasks() throws SQLException {

        PreparedStatement preparedStatement;

        String sql = "SELECT * FROM posts";
        preparedStatement = this.connection.prepareStatement(sql);

        return findByStatement(preparedStatement);
    }

    public List<Task> getTaskByAuthor() throws SQLException {

        PreparedStatement preparedStatement;

        String sql = "SELECT * FROM posts WHERE author_id = ?";
        preparedStatement = this.connection.prepareStatement(sql);
        preparedStatement.setInt(1, Settings.getCurrentUser().getId());

        return findByStatement(preparedStatement);

    }

    public boolean TaskConfirmation(String taskTitle) throws SQLException {
        String selectSQL = "SELECT * FROM posts WHERE title=?";
        String updateSQL = "UPDATE posts SET dateCompleted=? WHERE title=? AND dateCompleted IS NULL";

        try (PreparedStatement selectStatement = connection.prepareStatement(selectSQL);
             PreparedStatement updateStatement = connection.prepareStatement(updateSQL)) {

            selectStatement.setString(1, taskTitle);
            ResultSet resultSet = selectStatement.executeQuery();

            if (!resultSet.next()) {
                System.out.println("\u001B[31mLa tâche n'existe pas.\u001B[0m");
                return false;
            }

            if (resultSet.getTimestamp("dateCompleted") != null) {
                return false;
            }

            updateStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            updateStatement.setString(2, taskTitle);

            int rowsUpdated = updateStatement.executeUpdate();

            return rowsUpdated > 0;
        }
    }

    public List<Task> filterCompletedTasks(String category) throws SQLException {
        PreparedStatement preparedStatement;

        if (isCategoryExists(category)) {
            String sql = "SELECT * FROM posts WHERE category = ? AND dateCompleted IS NOT NULL";
            preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, category);

            List<Task> completedTasks = findByStatement(preparedStatement);

            preparedStatement.close();

            if (completedTasks.isEmpty()) {
                System.out.println("Aucune tâche complète trouvée dans la catégorie spécifiée.");
            }

            return completedTasks;
        } else {
            System.out.println("La catégorie spécifiée n'existe pas.");
            return new ArrayList<>();
        }
    }

    private boolean isCategoryExists(String category) throws SQLException {
        PreparedStatement preparedStatement;
        String sql = "SELECT COUNT(*) AS count FROM posts WHERE category = ?";
        preparedStatement = this.connection.prepareStatement(sql);
        preparedStatement.setString(1, category);

        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt("count");

        preparedStatement.close();

        return count > 0;
    }


    public Task findById(int taskId) {
        Task task = null;

        try {
            String query = "SELECT * FROM posts WHERE id = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setInt(1, taskId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String category = resultSet.getString("category");
                String content = resultSet.getString("content");
                Timestamp dateCreation = resultSet.getTimestamp("dateCreation");
                int authorId = resultSet.getInt("author_id");
                boolean status = resultSet.getTimestamp("dateCompleted") != null;

                task = new Task(id, title, category, content, dateCreation, authorId, status);
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return task;
    }


    public boolean updateTask(Task updatedTask) {
        try {
            String updateQuery = "UPDATE posts SET title = ?, category = ?, content = ?, dateCreation = ? WHERE id = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(updateQuery);

            preparedStatement.setString(1, updatedTask.getTitle());
            preparedStatement.setString(2, updatedTask.getCategory());
            preparedStatement.setString(3, updatedTask.getContent());
            preparedStatement.setTimestamp(4, new Timestamp(updatedTask.getDateCreation().getTime()));
            preparedStatement.setInt(5, updatedTask.getId());

            int rowsUpdated = preparedStatement.executeUpdate();
            preparedStatement.close();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void modifyTask(int taskId, String newTitle, String newCategory, String newContent) {
        Task existingTask = findById(taskId);

        if (existingTask != null) {
            Task updatedTask = new Task(
                    existingTask.getId(),
                    newTitle,
                    newCategory,
                    newContent,
                    new Timestamp(System.currentTimeMillis()),
                    existingTask.getAuthorId(),
                    existingTask.isStatus()
            );

            if (updateTask(updatedTask)) {
                System.out.println("Tâche mise à jour avec succès.");
            } else {
                System.out.println("Échec de la mise à jour de la tâche.");
            }
        } else {
            System.out.println("La tâche avec l'ID spécifié n'existe pas.");
        }
    }


    public Boolean deleteTask(int taskId) throws SQLException {
        String deleteSQL = "DELETE FROM posts WHERE id = ? AND author_id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
        preparedStatement.setInt(1, taskId);
        preparedStatement.setInt(2, Settings.getCurrentUser().getId());

        int rowsDeleted = preparedStatement.executeUpdate();

        preparedStatement.close();

        return rowsDeleted > 0;
    }
}













