package br.com.reign.loftylibrary.utils;

import java.util.Comparator;

import br.com.reign.loftylibrary.model.Chapter;

public class CompareDate implements Comparator<Chapter> {

    @Override
    public int compare(Chapter chapter1, Chapter chapter2) {
        Long date1 = chapter1.getDate();
        Long date2 = chapter2.getDate();

        return date2.compareTo(date1);
    }
}
