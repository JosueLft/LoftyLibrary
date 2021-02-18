package br.com.reign.loftylibrary.model;

import java.util.Objects;

public class Chapter {

    private String workTitle;
    private String chapterTitle;
    private String contentChapter;
    private String cover;
    private long date;
    private String translatedBy;
    private int pageNumber;
    private String type;

    public Chapter() {}

    public Chapter(String chapterTitle, String contentChapter, String cover, long date) {
        this.chapterTitle = chapterTitle;
        this.contentChapter = contentChapter;
        this.cover = cover;
        this.date = date;
    }

    public Chapter(int pageNumber, String contentChapter) {
        this.pageNumber = pageNumber;
        this.contentChapter = contentChapter;
    }

    public Chapter(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getWorkTitle() {
        return workTitle;
    }
    public void setWorkTitle(String workTitle) {
        this.workTitle = workTitle;
    }
    public String getChapterTitle() {
        return chapterTitle;
    }
    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }
    public String getContentChapter() {
        return contentChapter;
    }
    public void setContentChapter(String contentChapter) {
        this.contentChapter = contentChapter;
    }
    public String getCover() {
        return cover;
    }
    public void setCover(String cover) {
        this.cover = cover;
    }
    public long getDate() {
        return date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public String getTranslatedBy() {
        return translatedBy;
    }
    public void setTranslatedBy(String translatedBy) {
        this.translatedBy = translatedBy;
    }
    public int getPageNumber() {
        return pageNumber;
    }
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chapter)) return false;
        Chapter that = (Chapter) o;
        return getWorkTitle().equals(that.getWorkTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorkTitle());
    }
}
