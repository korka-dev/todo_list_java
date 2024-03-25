package org.post.models;

import java.io.Serializable;

public class User implements Serializable {
    int id;
    String name;
    String email;
    Boolean active = false;

    public User() {
    }

    public User(int id, String name, String email, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.active = active;
    }

    public String getName() {
        return name;
    }


    public boolean isActive() {
        return active;
    }

    public int getId() {
        return id;
    }


    @Override
    public String toString() {
        return String.format("| %-5d | %-20s | %-30s | %-6s |", this.id, this.name, this.email, this.active);
    }


}


