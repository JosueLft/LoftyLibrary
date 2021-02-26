package br.com.reign.loftylibrary.model;

public class User {

    private String id;
    private String name;
    private String profileUrl;

    public User() {}

    public User(String id, String name, String profileUrl) {
        this.id = id;
        this.name = name;
        this.profileUrl = profileUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
}