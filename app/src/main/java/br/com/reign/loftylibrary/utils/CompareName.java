package br.com.reign.loftylibrary.utils;

import java.util.Comparator;

import br.com.reign.loftylibrary.model.Chapter;

public class CompareName implements Comparator<Chapter> {

    @Override
    public int compare(Chapter chapter1, Chapter chapter2) {
        String title1 = chapter1.getWorkTitle();
        String title2 = chapter2.getWorkTitle();

        return title1.compareTo(title2);
    }

}
