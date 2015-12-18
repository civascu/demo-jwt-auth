package org.imc.demo.auth.jwt.models;

public class User {

    private final String username;
    private final String password;
    private final String email;
    private final String id;

    public User(String id, String userName, String password, String email) {
        this.username = userName;
        this.password = password;
        this.email = email;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }
}
