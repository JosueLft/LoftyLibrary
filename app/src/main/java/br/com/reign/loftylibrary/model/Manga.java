package br.com.reign.loftylibrary.model;

public class Manga extends Work {
    private String title;
    private String description;
    private String genre;
    private String status;
    private String siteURL;
    private String workCover;
    private long date;

    public Manga() {}

    public Manga(String description, String genre, String status, String workCover) {
        this.description = description;
        this.genre = genre;
        this.status = status;
        this.workCover = workCover;
    }



    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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
    public String getSiteURL() {
        return siteURL;
    }
    public void setSiteURL(String siteURL) {
        this.siteURL = siteURL;
    }
    public String getWorkCover() {
        return workCover;
    }
    public void setWorkCover(String workCover) {
        this.workCover = workCover;
    }
    public long getDate() {
        return date;
    }
    public void setDate(long date) {
        this.date = date;
    }
}