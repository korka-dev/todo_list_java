package org.post.models;

import java.sql.Timestamp;

public class Task {

    int id;
    String title;
    String category;
    String content;

    Timestamp dateCreation;

    int authorId;

    boolean status;


    public Task(int id, String title, String category, String content, Timestamp dateCreation, int authorId, boolean status) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.content = content;
        this.dateCreation = dateCreation;
        this.authorId = authorId;
        this.status = status;
    }

    public int getId(){
        return id;
    }

    public int getAuthorId() {
        return authorId;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public boolean isStatus() {
        return status;
    }

    public String toString() {
        String statusIcon = (status) ? "✔" : "✘";

        return String.format("| %-5d | %-20s | %-20s | %-20s | %-20s | %-5d | %s",
                id, title, category, content, dateCreation, authorId, statusIcon);
    }




}
