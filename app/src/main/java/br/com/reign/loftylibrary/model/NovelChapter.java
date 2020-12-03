package br.com.reign.loftylibrary.model;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class NovelChapter extends Chapter {

    public NovelChapter() {}

    public NovelChapter(String chapterTitle, String contentChapter, String cover, long date) {
        super(chapterTitle, contentChapter, cover, date);
    }

    public NovelChapter(int pageNumber, String contentChapter) {
        super(pageNumber, contentChapter);
    }

    public NovelChapter(String chapterTitle) {
        super(chapterTitle);
    }

    @NonNull
    @Override
    public String toString() {
        return "\n" + "Work: " + getWorkTitle() + "\n" +
                "Chapter: " + getChapterTitle() + "\n" +
                "Cover: " + getCover() + "\n" +
                "Date: " + getDate() + "\n" +
                "Translate: " + getTranslatedBy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NovelChapter)) return false;
        NovelChapter that = (NovelChapter) o;
        return getWorkTitle().equals(that.getWorkTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorkTitle());
    }
}