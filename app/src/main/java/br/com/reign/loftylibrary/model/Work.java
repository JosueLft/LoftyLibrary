package br.com.reign.loftylibrary.model;

import androidx.annotation.NonNull;

public class Work {

    private String workTitle;
    private String chapters;
    private String imgCoverWork;
    private String genre;
    private String status;
    private String distributedBy;
    private String description;
    private String type;

    public Work() {}

    public Work(String workTitle, String imgCoverWork, String genre, String status, String distributedBy) {
        this.workTitle = workTitle;
        this.imgCoverWork = imgCoverWork;
        this.genre = genre;
        this.status = status;
        this.distributedBy = distributedBy;
    }

    public Work(String workTitle, String imgCoverWork, String description, String distributedBy, String genre, String status, String type) {
        this.workTitle = workTitle;
        this.imgCoverWork = imgCoverWork;
        this.description = description;
        this.distributedBy = distributedBy;
        this.genre = genre;
        this.status = status;
        this.type = type;
    }

    public Work(String workTitle, String imgCoverWork, String distributedBy, String genre, String status, String type) {
        this.workTitle = workTitle;
        this.imgCoverWork = imgCoverWork;
        this.distributedBy = distributedBy;
        this.genre = genre;
        this.status = status;
        this.type = type;
    }



    @NonNull
    @Override
    public String toString() {
        return "\nTitle: " + workTitle + "\n" +
                "Cover: " + imgCoverWork + "\n" +
                "Genre: " + genre + "\n" +
                "Status: " + status  + "\n";
    }

    public String getWorkTitle() {
        return workTitle;
    }
    public void setWorkTitle(String workTitle) {
        this.workTitle = workTitle;
    }
    public String getChapters() {
        return chapters;
    }
    public void setChapters(String chapters) {
        this.chapters = chapters;
    }
    public String getImgCoverWork() {
        return imgCoverWork;
    }
    public void setImgCoverWork(String imgCoverWork) {
        this.imgCoverWork = imgCoverWork;
    }
    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getDistributedBy() {
        return distributedBy;
    }
    public void setDistributedBy(String distributedBy) {
        this.distributedBy = distributedBy;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
