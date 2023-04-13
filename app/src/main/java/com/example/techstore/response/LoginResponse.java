package com.example.techstore.response;

import java.io.Serializable;

public class LoginResponse implements Serializable{
    String response;
    String role;

    public LoginResponse(String response, String role, String key) {
        this.response = response;
        this.role = role;
        this.key = key;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    String key;
}
