package br.com.reign.loftylibrary.model;

public class User {

    private final String id;
    private final String name;
    private final String profileUrl;

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