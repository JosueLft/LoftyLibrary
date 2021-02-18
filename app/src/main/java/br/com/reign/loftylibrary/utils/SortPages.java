package br.com.reign.loftylibrary.utils;

import java.util.Comparator;

import br.com.reign.loftylibrary.model.MangaChapter;

public class SortPages implements Comparator<MangaChapter> {

    @Override
    public int compare(MangaChapter page1, MangaChapter page2) {
        int pag1 = page1.getPageNumber();
        int pag2 = page2.getPageNumber();

        return (pag1 - pag2);
    }

}