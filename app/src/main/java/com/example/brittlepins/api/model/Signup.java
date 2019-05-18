package com.example.brittlepins.api.model;

public class Signup {
    private String username;
    private String email;
    private String password;

    public Signup(String username, String email, String password) {
      this.username = username;
      this.email = email;
      this.password = password;
    }
}
