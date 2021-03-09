package br.com.reign.loftylibrary.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class MangaChapter extends Chapter {



    public MangaChapter() {}

    public MangaChapter(String chapterTitle, String contentChapter, String cover, long date) {
        super(chapterTitle, contentChapter, cover, date);
    }

    public MangaChapter(int pageNumber, String contentChapter) {
        super(pageNumber, contentChapter);
    }

    public MangaChapter(String chapterTitle) {
        super(chapterTitle);
    }

    @NonNull
    @Override
    public String toString() {
        return "\n" + "work_name: " + getWorkTitle() + "\n" +
                "Chapter Title: " + getChapterTitle() + "\n" +
                "current_date: " + getDate() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MangaChapter)) return false;
        MangaChapter that = (MangaChapter) o;
        return getWorkTitle().equals(that.getWorkTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorkTitle());
    }
}